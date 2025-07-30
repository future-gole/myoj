package com.doublez.judge.config;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import com.doublez.common.core.enums.ProgramType;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.core.command.PullImageResultCallback;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class MultiLanguageDockerSandBoxPool {

    private final DockerClient dockerClient;
    private final String hostCodeBaseDir; // 宿主机上存放所有代码的根目录 (通过构造函数注入)

    // 存储每种语言的配置 (使用 EnumMap 提高效率)
    private final Map<ProgramType, LanguageConfig> languageConfigs;
    // 为每种语言创建一个容器队列
    private final Map<ProgramType, BlockingQueue<String>> containerQueues;
    // 从 containerId 映射到其语言类型，方便归还
    private final Map<String, ProgramType> containerLanguageMap;
    // 从 containerId 映射到容器名，用于查找代码目录
    private final Map<String, String> containerNameMap;

    public MultiLanguageDockerSandBoxPool(DockerClient dockerClient, String hostCodeBaseDir, Map<ProgramType, LanguageConfig> languageConfigs) {
        this.dockerClient = dockerClient;
        this.hostCodeBaseDir = hostCodeBaseDir;
        this.languageConfigs = new EnumMap<>(languageConfigs);

        this.containerQueues = new EnumMap<>(ProgramType.class);
        this.containerLanguageMap = new ConcurrentHashMap<>();
        this.containerNameMap = new ConcurrentHashMap<>();

        // 确保代码根目录存在
        if (!FileUtil.exist(this.hostCodeBaseDir)) {
            FileUtil.mkdir(this.hostCodeBaseDir);
        }

        // 根据配置为每种语言初始化一个阻塞队列
        this.languageConfigs.forEach((lang, config) ->
                containerQueues.put(lang, new ArrayBlockingQueue<>(config.poolSize()))
        );
    }

    /**
     * 初始化所有语言的容器池
     */
    public void initPools() {
        log.info("------  初始化所有语言的容器池开始  ------");
        languageConfigs.forEach((language, config) -> {
            log.info("------  开始创建 {} 语言的容器 (数量: {})  ------", language, config.poolSize());
            for (int i = 0; i < config.poolSize(); i++) {
                String containerName = config.containerNamePrefix() + "-" + i;
                createContainer(language, containerName);
            }
            log.info("------  {} 语言的容器创建完毕  -----", language);
        });
        log.info("------  所有容器池初始化完毕  ------");
    }

    /**
     * 根据语言获取一个可用的容器
     */
    public String getContainer(ProgramType language) {
        BlockingQueue<String> queue = containerQueues.get(language);
        if (queue == null) {
            throw new IllegalArgumentException("不支持的语言类型或未配置: " + language);
        }
        try {
            log.info("请求获取 {} 语言的容器，当前可用 {} 个", language, queue.size());
            return queue.take(); // take() 会在队列为空时阻塞，直到有可用容器
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("获取容器时被中断", e);
        }
    }

    /**
     * 将容器归还到池中
     */
    public void returnContainer(String containerId) {
        ProgramType language = containerLanguageMap.get(containerId);
        if (language == null) {
            log.error("无法归还容器：未找到ID为 {} 的容器记录", containerId);
            return;
        }
        BlockingQueue<String> queue = containerQueues.get(language);
        if (queue != null) {
            boolean offered = queue.offer(containerId); // offer() 不会阻塞
            if (offered) {
                log.info("容器 {} ({}) 已归还，当前可用 {} 个", containerId, language, queue.size());
            } else {
                log.warn("归还容器 {} ({}) 失败，队列已满", containerId, language);
            }
        }
    }

    /**
     * 根据容器ID获取其在宿主机上对应的代码目录
     * @return 宿主机上的绝对路径
     */
    public String getHostCodeDir(String containerId) {
        String containerName = containerNameMap.get(containerId);
        if (containerName == null) {
            log.error("无法找到容器ID {} 对应的代码目录", containerId);
            throw new IllegalArgumentException("无效的容器ID: " + containerId);
        }
        return hostCodeBaseDir + File.separator + containerName;
    }

    private void createContainer(ProgramType language, String containerName) {
        LanguageConfig config = languageConfigs.get(language);

        // 1. 检查容器是否已存在
        List<Container> containerList = dockerClient.listContainersCmd().withShowAll(true).withNameFilter(List.of(containerName)).exec();
        if (!CollectionUtil.isEmpty(containerList)) {
            Container container = containerList.get(0);
            String containerId = container.getId();
            log.info("容器 {} ({}) 已存在.", containerName, containerId);
            if ("created".equals(container.getState()) || "exited".equals(container.getState())) {
                log.info("正在启动已存在的容器 {}...", containerId);
                dockerClient.startContainerCmd(container.getId()).exec();
            }
            addContainerToPool(containerId, containerName, language);
            return;
        }

        // 2. 如果不存在，则创建新容器
        log.info("容器 {} 不存在，开始创建新容器...", containerName);
        pullImageIfNeeded(config.imageName());

        // 为每个容器创建宿主机上的挂载目录
        String hostCodePath = createHostDirForContainer(containerName);
        // 使用 LanguageConfig 自身的方法来构建 HostConfig
        HostConfig hostConfig = config.buildHostConfig(hostCodePath);

        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(config.imageName())
                .withName(containerName)
                .withHostConfig(hostConfig)
                .withAttachStderr(true)
                .withAttachStdout(true)
                .withTty(true); // 保持 TTY 打开以使容器持续运行

        CreateContainerResponse createContainerResponse = containerCmd.exec();
        String containerId = createContainerResponse.getId();
        log.info("容器 {} 创建成功，ID为 {}", containerName, containerId);

        dockerClient.startContainerCmd(containerId).exec();
        log.info("容器 {} 启动成功", containerId);

        addContainerToPool(containerId, containerName, language);
    }

    private void addContainerToPool(String containerId, String containerName, ProgramType language) {
        containerQueues.get(language).add(containerId);
        containerLanguageMap.put(containerId, language);
        containerNameMap.put(containerId, containerName);
    }

    private void pullImageIfNeeded(String imageName) {
        if (dockerClient.listImagesCmd().withImageNameFilter(imageName).exec().isEmpty()) {
            log.info("本地未找到镜像 {}，开始拉取...", imageName);
            try {
                dockerClient.pullImageCmd(imageName)
                        .exec(new PullImageResultCallback())
                        .awaitCompletion();
                log.info("镜像 {} 拉取成功。", imageName);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("拉取镜像时被中断", e);
            }
        } else {
            log.info("镜像 {} 已存在于本地。", imageName);
        }
    }

    // 为每个容器创建宿主机上的挂载目录
    private String createHostDirForContainer(String containerName) {
        String containerHostPath = this.hostCodeBaseDir + File.separator + containerName;
        if (!FileUtil.exist(containerHostPath)) {
            FileUtil.mkdir(containerHostPath);
        }
        return containerHostPath;
    }
}
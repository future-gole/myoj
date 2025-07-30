package com.doublez.judge.config;

import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Volume;

/**
 * 语言沙箱配置类 (Java 16+ record)
 * 这是一个不可变的数据载体，封装了创建一种特定语言沙箱所需的所有配置。
 * 核心优点：将 HostConfig 的构建逻辑内聚到配置类自身，主类无需关心构建细节。
 */
public record LanguageConfig(
        String imageName,
        String containerNamePrefix,
        String volumeDir, // 容器内的挂载点
        long memoryLimit,
        long memorySwapLimit,
        long cpuLimit,
        int poolSize
) {
    /**
     * 根据当前配置和指定的宿主机路径，构建 Docker 的 HostConfig。
     * @param hostCodePath 宿主机上为单个容器分配的、用于挂载的代码目录
     * @return 配置好的 HostConfig 对象
     */
    public HostConfig buildHostConfig(String hostCodePath) {
        HostConfig hostConfig = new HostConfig();
        // 设置挂载目录，将宿主机的代码目录映射到容器内的指定目录
        hostConfig.setBinds(new Bind(hostCodePath, new Volume(volumeDir)));

        // 限制docker容器使用资源
        hostConfig.withMemory(memoryLimit);
        hostConfig.withMemorySwap(memorySwapLimit);
        hostConfig.withCpuCount(cpuLimit);

        // 安全设置
        hostConfig.withNetworkMode("none");  // 禁用网络
        hostConfig.withReadonlyRootfs(true); // 禁止在root目录写文件

        return hostConfig;
    }
}
package com.doublez.judge.config;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class SandboxConfig {

    @Value("${sandbox.docker.host:tcp://localhost:2375}")
    private String dockerHost;

    /**
     * 创建 DockerClient 的 Bean
     * 这样 DockerClient 就可以在任何需要的地方被注入
     * @return DockerClient 实例
     */
    @Bean
    public DockerClient dockerClient() {
        // DockerClient 的标准创建方式
        // 1. 创建配置，从 @Value 注入的 dockerHost
        DefaultDockerClientConfig clientConfig = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(dockerHost)
                .build();

        // 2. 使用新的方式创建 HTTP Client
        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(clientConfig.getDockerHost())
                .sslConfig(clientConfig.getSSLConfig())
                // 在这里可以添加更多精细化配置
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();

        // 3. 使用 DockerClientImpl 将配置和 HTTP Client 组装起来
        return DockerClientImpl.getInstance(clientConfig, httpClient);
    }

    /**
     * 创建我们最终的沙箱池 Bean
     * Spring 会自动将已经存在的 DockerClient Bean 和我们定义的 SandboxProperties Bean 注入到方法参数中
     * @param dockerClient Spring 容器中已有的 DockerClient Bean
     * @param properties Spring 容器中已有的 SandboxProperties Bean
     * @return 配置完成的 MultiLanguageDockerSandBoxPool 实例
     */
    @Bean(destroyMethod = "") // 可选：如果未来有销毁逻辑，可指定方法名
    public MultiLanguageDockerSandBoxPool multiLanguageDockerSandBoxPool(DockerClient dockerClient, SandboxProperties properties) {
        // 从配置属性类中获取参数，创建沙箱池
        MultiLanguageDockerSandBoxPool pool = new MultiLanguageDockerSandBoxPool(
                dockerClient,
                properties.getHostCodeBaseDir(),
                properties.getLanguages()
        );
        // 调用初始化方法，填充容器池
        pool.initPools();
        return pool;
    }
}
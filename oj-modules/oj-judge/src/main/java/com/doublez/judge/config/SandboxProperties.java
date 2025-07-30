package com.doublez.judge.config;

import com.doublez.common.core.enums.ProgramType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "sandbox") // 绑定配置文件中前缀为 "sandbox" 的属性
public class SandboxProperties {

    /**
     * 宿主机上存放所有代码的根目录
     */
    private String hostCodeBaseDir = System.getProperty("user.dir") + File.separator + "code_pool";

    /**
     * 各种语言的详细配置
     * Key 是 ProgramType 枚举的名称 (大小写不敏感，例如 JAVA, cpp, go-lang)
     * Value 是对应的 LanguageConfig
     */
    private Map<ProgramType, LanguageConfig> languages;
}
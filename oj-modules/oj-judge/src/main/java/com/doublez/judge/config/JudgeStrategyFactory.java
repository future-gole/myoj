package com.doublez.judge.config;

import com.doublez.common.core.enums.ResultCode;
import com.doublez.common.security.exception.ServiceException;
import com.doublez.judge.template.AbstractJudgeTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class JudgeStrategyFactory {

    // Spring 会自动将所有 AbstractJudgeTemplate 类型的 Bean 注入到这个 Map
    // Key 是 Bean 的名字 (e.g., "java", "python"), Value 是 Bean 实例
    @Autowired
    private Map<String, AbstractJudgeTemplate> judgeStrategies;

    public AbstractJudgeTemplate getStrategy(String language) {
        AbstractJudgeTemplate strategy = judgeStrategies.get(language);
        if (strategy == null) {
            throw new ServiceException(ResultCode.FAILED_NOT_SUPPORT_PROGRAM);
        }
        return strategy;
    }
}
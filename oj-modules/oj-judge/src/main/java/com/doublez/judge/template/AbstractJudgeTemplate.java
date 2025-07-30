package com.doublez.judge.template;

import cn.hutool.core.io.FileUtil;
import com.doublez.common.core.constants.Constants;
import com.doublez.common.core.enums.CodeRunStatus;
import com.doublez.judge.config.MultiLanguageDockerSandBoxPool;
import com.doublez.judge.domain.CompileResult;
import com.doublez.judge.domain.SandBoxExecuteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
public abstract class AbstractJudgeTemplate {

    @Autowired
    protected MultiLanguageDockerSandBoxPool sandBoxPool;

    // 模板方法，定义了判题的完整流程骨架
    public final SandBoxExecuteResult judge(String userCode, List<String> inputList,Long timeLimit) {
        // 1. 准备环境
        String containerId = prepareEnvironment();

        // 2. 创建用户代码文件
        String userCodePath = createUserCodePath(containerId);
        File userCodeFile = createUserCodeFile(userCode, userCodePath);

        try {
            // 3. 编译代码
            CompileResult compileResult = compileCodeByDocker(containerId, userCodePath); // 传递所需参数
            if (!compileResult.isCompiled()) {
                // 如果编译失败，也需要清理文件和容器
                return SandBoxExecuteResult.fail(CodeRunStatus.COMPILE_FAILED, compileResult.getExeMessage());
            }
            // 4. 运行代码
            return executeCodeByDocker(containerId, inputList,timeLimit); // 传递所需参数
        } finally {
            // 5. 清理环境
            deleteUserCodeFile(userCodeFile);
            cleanupEnvironment(containerId);
        }
    }

    private void deleteUserCodeFile(File userCodeFile) {
        if (userCodeFile != null && userCodeFile.exists()) {
            FileUtil.del(userCodeFile);
        }
    }

    /**
     * 创建用户代码文件
     * @param userCode
     */
    private File createUserCodeFile(String userCode, String userCodePath) {
        if (FileUtil.exist(userCodePath)) {
            FileUtil.del(userCodePath);
        }
        return FileUtil.writeString(userCode, userCodePath, Constants.UTF8);
    }

    private void cleanupEnvironment(String containerId) {
        // 只有在 containerId 有效时才归还
        if (containerId != null) {
            sandBoxPool.returnContainer(containerId);
        }
    }

    // --- 抽象方法 (钩子)，由子类实现 ---

    /**
     * 编译代码，不同语言实现不同
     */
    protected abstract CompileResult compileCodeByDocker(String containerId, String userCodePath);

    /**
     * 运行代码，不同语言的运行命令和参数不同
     */
    protected abstract SandBoxExecuteResult executeCodeByDocker(String containerId, List<String> inputList,Long timeLimit);


    /**
     * 准备环境
     * @return 容器id
     */
    protected abstract String prepareEnvironment();

    protected abstract String createUserCodePath(String containerId);
}
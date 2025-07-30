package com.doublez.judge.service.impl;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.doublez.common.core.constants.JudgeConstants;
import com.doublez.common.core.enums.CodeRunStatus;
import com.doublez.common.core.enums.ProgramType;
import com.doublez.judge.callback.DockerStartResultCallback;
import com.doublez.judge.callback.StatisticsCallback;
import com.doublez.judge.domain.CompileResult;
import com.doublez.judge.domain.SandBoxExecuteResult;
import com.doublez.judge.template.AbstractJudgeTemplate;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.StatsCmd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service("java")
public class JavaJudgeStrategy extends AbstractJudgeTemplate {

    @Autowired
    private DockerClient dockerClient;

    @Override
    protected CompileResult compileCodeByDocker(String containerId, String userCodePath) {
        String cmdId = createExecCmd(JudgeConstants.DOCKER_JAVAC_CMD, null, containerId);
        //挂载一个回调实时捕获命令的输出（标准输出和标准错误）。
        DockerStartResultCallback resultCallback = new DockerStartResultCallback();
        CompileResult compileResult = new CompileResult();
        try {
            dockerClient.execStartCmd(cmdId)
                    .exec(resultCallback)
                    .awaitCompletion();//阻塞并且等待，因为必须要等待编译结束
            if (CodeRunStatus.FAILED.equals(resultCallback.getCodeRunStatus())) {
                compileResult.setCompiled(false);
                compileResult.setExeMessage(resultCallback.getErrorMessage());
            } else {
                compileResult.setCompiled(true);
            }
            return compileResult;
        } catch (InterruptedException e) {
            //此处可以直接抛出 已做统一异常处理
            throw new RuntimeException(e);
        }
    }

    @Override
    protected SandBoxExecuteResult executeCodeByDocker(String containerId, List<String> inputList,Long timeLimit) {
        List<String> outList = new ArrayList<>(); //记录输出结果
        long maxMemory = 0L;  //最大占用内存
        long maxUseTime = 0L; //最大运行时间
        //执行用户代码
        for (String inputArgs : inputList) {
            //创建命令
            String cmdId = createExecCmd(JudgeConstants.DOCKER_JAVA_EXEC_CMD, inputArgs, containerId);
            //执行代码
            StopWatch stopWatch = new StopWatch();        //执行代码后开始计时
            //执行情况监控
            StatsCmd statsCmd = dockerClient.statsCmd(containerId); //启动监控
            StatisticsCallback statisticsCallback = statsCmd.exec(new StatisticsCallback());
            stopWatch.start();
            //用于接收统计信息的回调
            DockerStartResultCallback resultCallback = new DockerStartResultCallback();
            try {
                dockerClient.execStartCmd(cmdId)
                        .exec(resultCallback)
                        .awaitCompletion(timeLimit, TimeUnit.SECONDS);
                if (CodeRunStatus.FAILED.equals(resultCallback.getCodeRunStatus())) {
                    //未通过所有用例返回结果
                    return SandBoxExecuteResult.fail(CodeRunStatus.NOT_ALL_PASSED);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            stopWatch.stop();  //结束时间统计
            statsCmd.close();  //结束docker容器执行统计
            long userTime = stopWatch.getLastTaskTimeMillis(); //执行耗时
            maxUseTime = Math.max(userTime, maxUseTime);       //记录最大的执行用例耗时
            Long memory = statisticsCallback.getMaxMemory();
            if (memory != null) {
                maxMemory = Math.max(maxMemory, statisticsCallback.getMaxMemory()); //记录最大的执行用例占用内存
            }
            outList.add(resultCallback.getMessage().trim());   //记录输出结果
        }

        return getSanBoxResult(inputList, outList, maxMemory, maxUseTime); //封装结果
    }

    @Override
    protected String prepareEnvironment() {
        return sandBoxPool.getContainer(ProgramType.JAVA);
    }

    @Override
    protected String createUserCodePath(String containerId) {
        String codeDir = sandBoxPool.getHostCodeDir(containerId);
        return codeDir + File.separator + JudgeConstants.USER_CODE_JAVA_CLASS_NAME;
    }

    private String createExecCmd(String[] javaCmdArr, String inputArgs, String containerId) {
        if (!StrUtil.isEmpty(inputArgs)) {
            //当入参不为空时拼接入参
            String[] inputArray = inputArgs.split(" "); //入参
            javaCmdArr = ArrayUtil.append(JudgeConstants.DOCKER_JAVA_EXEC_CMD, inputArray);
        }
        ExecCreateCmdResponse cmdResponse = dockerClient.execCreateCmd(containerId)
                .withCmd(javaCmdArr)
                .withAttachStderr(true)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .exec();
        return cmdResponse.getId();
    }

    private SandBoxExecuteResult getSanBoxResult(List<String> inputList, List<String> outList,
                                                 long maxMemory, long maxUseTime) {
        if (inputList.size() != outList.size()) {
            //输入用例数量 不等于 输出用例数量  属于执行异常
            return SandBoxExecuteResult.fail(CodeRunStatus.NOT_ALL_PASSED, outList, maxMemory, maxUseTime);
        }
        return SandBoxExecuteResult.success(CodeRunStatus.SUCCEED, outList, maxMemory, maxUseTime);
    }
}

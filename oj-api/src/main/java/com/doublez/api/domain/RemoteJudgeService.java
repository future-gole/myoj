package com.doublez.api.domain;

import com.doublez.api.domain.domain.dto.JudgeSubmitDTO;
import com.doublez.api.domain.domain.vo.UserQuestionResultVO;
import com.doublez.common.core.constants.Constants;
import com.doublez.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "RemoteJudgeService", value = Constants.JUDGE_SERVICE)
public interface RemoteJudgeService {

    @PostMapping("/judge/doJudgeJavaCode")
    R<UserQuestionResultVO> doJudgeJavaCode(@RequestBody JudgeSubmitDTO judgeSubmitDTO);
}

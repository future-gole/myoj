package com.doublez.friend.rabbit;

import com.doublez.api.domain.dto.JudgeSubmitDTO;
import com.doublez.common.core.constants.RabbitMQConstants;
import com.doublez.common.core.enums.ResultCode;
import com.doublez.common.security.exception.ServiceException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JudgeProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    public void produceMsg(JudgeSubmitDTO judgeSubmitDTO) {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConstants.OJ_WORK_QUEUE, judgeSubmitDTO);
        } catch (Exception e) {
            log.error("生产者发送消息异常", e);
            throw new ServiceException(ResultCode.FAILED_RABBIT_PRODUCE);
        }
    }
}

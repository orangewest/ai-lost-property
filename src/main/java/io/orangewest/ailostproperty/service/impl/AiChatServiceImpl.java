package io.orangewest.ailostproperty.service.impl;

import io.orangewest.ailostproperty.assistant.AiAssistant;
import io.orangewest.ailostproperty.component.ChatFlow;
import io.orangewest.ailostproperty.dao.LostRegisterRepository;
import io.orangewest.ailostproperty.pojo.entity.LostRegisterEntity;
import io.orangewest.ailostproperty.pojo.output.IntentionOutput;
import io.orangewest.ailostproperty.pojo.output.LostPropertyOutput;
import io.orangewest.ailostproperty.service.AiChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AiChatServiceImpl implements AiChatService {

    @Autowired
    private AiAssistant aiAssistant;

    @Autowired
    private LostRegisterRepository lostRegisterRepository;

    @Override
    @ChatFlow
    public String chat(String userId, String message) {
        // 判断用户的意图
        IntentionOutput intention = aiAssistant.getIntention(userId, message);
        log.info("intention:{}", intention);
        String output = intention.getOutput();
        switch (intention.getIntention()) {
            case 1:
                // 丢失信息登记
                output = registerLost(userId, message);
                break;
            case 2:
                // 找到失物登记
                break;
            case 3:
                // 失物查询
                break;
        }
        return output;
    }

    private String registerLost(String userId, String message) {
        LostPropertyOutput lostPropertyOutput = aiAssistant.registerLost(userId, message);
        log.info("lostPropertyOutput:{}", lostPropertyOutput);
        if (lostPropertyOutput.getCompleted()) {
            LostRegisterEntity lostRegisterEntity = new LostRegisterEntity();
            BeanUtils.copyProperties(lostPropertyOutput, lostRegisterEntity);
            lostRegisterRepository.save(lostRegisterEntity);
        }
        return lostPropertyOutput.getOutput();
    }

}

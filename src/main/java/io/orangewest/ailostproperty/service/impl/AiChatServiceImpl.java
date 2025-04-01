package io.orangewest.ailostproperty.service.impl;

import com.alibaba.dashscope.utils.JsonUtils;
import io.orangewest.ailostproperty.assistant.AiAssistant;
import io.orangewest.ailostproperty.assistant.LostPropertyAssistant;
import io.orangewest.ailostproperty.component.ChatFlow;
import io.orangewest.ailostproperty.dao.ChatHistoryRepository;
import io.orangewest.ailostproperty.dao.LostPropertyRepository;
import io.orangewest.ailostproperty.dao.LostRegisterRepository;
import io.orangewest.ailostproperty.pojo.dto.ChatHistoryVo;
import io.orangewest.ailostproperty.pojo.entity.LostPropertyEntity;
import io.orangewest.ailostproperty.pojo.entity.LostRegisterEntity;
import io.orangewest.ailostproperty.pojo.output.IntentionOutput;
import io.orangewest.ailostproperty.pojo.output.LostPropertyOutput;
import io.orangewest.ailostproperty.service.AiChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AiChatServiceImpl implements AiChatService {

    @Autowired
    private AiAssistant aiAssistant;

    @Autowired
    private LostRegisterRepository lostRegisterRepository;

    @Autowired
    private ChatHistoryRepository chatHistoryRepository;

    @Autowired
    private LostPropertyAssistant lostPropertyAssistant;

    @Autowired
    private LostPropertyRepository lostPropertyRepository;

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
                output = registerLostProperty(userId, message);
                break;
            case 3:
                // 失物查询
                break;
        }
        return output;
    }

    @Override
    public Page<ChatHistoryVo> queryChatHistory(String userId, Pageable page) {
        return chatHistoryRepository.findAllBySessionId(userId, page)
                .map(chatHistory -> ChatHistoryVo.of(chatHistory.getRole().getRole(), chatHistory.getContent(), chatHistory.getCreatedDate()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearChatHistory(String userId) {
        chatHistoryRepository.deleteBySessionId(userId);
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

    private String registerLostProperty(String userId, String message) {
        StringBuilder sb = new StringBuilder();
        lostPropertyAssistant.registerLostProperty(userId, message)
                .doOnNext(sb::append)
                .blockLast();
        log.info("registerLostProperty sb:{}", sb);
        LostPropertyOutput lostPropertyOutput = JsonUtils.fromJson(sb.toString(), LostPropertyOutput.class);
        if (lostPropertyOutput.getCompleted()) {
            LostPropertyEntity lostPropertyEntity = new LostPropertyEntity();
            BeanUtils.copyProperties(lostPropertyOutput, lostPropertyEntity);
            lostPropertyRepository.save(lostPropertyEntity);
        }
        return lostPropertyOutput.getOutput();
    }

}

package io.orangewest.ailostproperty.service;

import io.orangewest.ailostproperty.pojo.dto.ChatHistoryVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AiChatService {

    String chat(String userId, String message);

    Page<ChatHistoryVo> queryChatHistory(String userId, Pageable page);

    void clearChatHistory(String userId);
}

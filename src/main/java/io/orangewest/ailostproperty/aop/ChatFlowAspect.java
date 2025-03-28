package io.orangewest.ailostproperty.aop;

import io.orangewest.ailostproperty.dao.ChatHistoryRepository;
import io.orangewest.ailostproperty.pojo.entity.ChatHistoryEntity;
import io.orangewest.ailostproperty.pojo.enums.ChatRole;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ChatFlowAspect {

    @Autowired
    private ChatHistoryRepository chatHistoryRepository;

    @Pointcut("@annotation(io.orangewest.ailostproperty.component.ChatFlow)")
    public void pointcut() {

    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        String sessionId = (String) args[0];
        String message = (String) args[1];
        log.info("sessionId:{}, message:{}", sessionId, message);
        saveChatHistory(sessionId, message, ChatRole.USER);
        Object result = joinPoint.proceed();
        log.info("sessionId:{}, aiMessage:{}", sessionId, result);
        saveChatHistory(sessionId, result.toString(), ChatRole.ASSISTANT);
        return result;
    }

    private void saveChatHistory(String sessionId, String message, ChatRole chatRole) {
        ChatHistoryEntity chatHistory = new ChatHistoryEntity();
        chatHistory.setSessionId(sessionId);
        chatHistory.setRole(chatRole);
        chatHistory.setContent(message);
        chatHistoryRepository.save(chatHistory);
    }
}

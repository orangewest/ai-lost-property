package io.orangewest.ailostproperty.assistant;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import reactor.core.publisher.Flux;

import static io.orangewest.ailostproperty.assistant.ChatConfig.QAQ_CHAT_BEAN;

@AiService(wiringMode = AiServiceWiringMode.EXPLICIT, streamingChatModel = QAQ_CHAT_BEAN,
        tools = {"chatHistoryTools", "lostPropertyTools", "lostRegisterTools"})
public interface LostPropertyAssistant {

    /**
     * 登记失物信息
     */
    @SystemMessage(fromResource = "/message/system/registerLostProperty.txt")
    @UserMessage("当前sessionId：{{sessionId}}；用户当前消息：{{message}}")
    Flux<String> registerLostProperty(@V("sessionId") String id, @V("message") String message);

    /**
     * 查询失物信息
     */
    @SystemMessage(fromResource = "/message/system/queryLostProperty.txt")
    @UserMessage("当前sessionId：{{sessionId}}；用户当前消息：{{message}}")
    Flux<String> queryLostProperty(@V("sessionId") String id, @V("message") String message);

}

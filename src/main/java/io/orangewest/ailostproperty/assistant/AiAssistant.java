package io.orangewest.ailostproperty.assistant;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import io.orangewest.ailostproperty.pojo.output.IntentionOutput;
import io.orangewest.ailostproperty.pojo.output.LostPropertyOutput;

@AiService(wiringMode = AiServiceWiringMode.EXPLICIT, chatModel = "qwenChatModel",
        streamingChatModel = "qwenStreamingChatModel", tools = {"chatHistoryTools", "lostRegisterTools"})
public interface AiAssistant {

    /**
     * 获取用户意图
     */
    @SystemMessage(fromResource = "/message/system/getIntention.txt")
    @UserMessage("当前sessionId：{{sessionId}}；用户当前消息：{{message}}")
    IntentionOutput getIntention(@V("sessionId") String sessionId, @V("message") String message);

    /**
     * 注册失物信息
     */
    @SystemMessage(fromResource = "/message/system/registerLost.txt")
    @UserMessage("当前sessionId：{{sessionId}}；用户当前消息：{{message}}")
    LostPropertyOutput registerLost(@V("sessionId") String id, @V("message") String message);

}

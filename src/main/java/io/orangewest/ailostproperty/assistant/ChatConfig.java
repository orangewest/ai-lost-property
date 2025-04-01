package io.orangewest.ailostproperty.assistant;

import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatConfig {

    public static final String QAQ_CHAT_BEAN = "qwenQaQStreamingChatModel";

    @Bean(QAQ_CHAT_BEAN)
    public StreamingChatLanguageModel qwenStreamingChatModel() {
        return QwenStreamingChatModel.builder()
                .apiKey(System.getenv("ALI_AI_KEY"))
                .modelName("qwq-32b")
                .build();
    }


}

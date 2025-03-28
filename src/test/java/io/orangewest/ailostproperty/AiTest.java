package io.orangewest.ailostproperty;


import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.community.model.dashscope.WanxImageModel;
import dev.langchain4j.data.image.Image;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

public class AiTest {

    @Test
    void test_deepseek_chat() {
        QwenChatModel chatModel = QwenChatModel.builder()
                .apiKey(System.getenv("ALI_AI_KEY"))
                .modelName("deepseek-r1")
                .build();

        System.out.println(chatModel.chat("你是谁？"));
    }

    @Test
    void test_deepseek_stream_chat() throws InterruptedException {
        QwenStreamingChatModel chatModel = QwenStreamingChatModel.builder()
                .apiKey(System.getenv("ALI_AI_KEY"))
                .modelName("deepseek-r1")
                .build();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        chatModel.chat("你是谁？", new StreamingChatResponseHandler() {
            @Override
            public void onPartialResponse(String s) {
                System.out.println(s);
            }

            @Override
            public void onCompleteResponse(ChatResponse chatResponse) {
                System.out.println("完成。。。");
                countDownLatch.countDown();
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("出错了。。。");
            }
        });
        countDownLatch.await();
    }

    @Test
    void test_image_generate(){
        WanxImageModel imageModel = WanxImageModel.builder()
                .apiKey(System.getenv("ALI_AI_KEY"))
                .modelName("wanx2.1-t2i-turbo")
                .build();
        Response<Image> generate = imageModel.generate("给我生成一张猫的图片");
        System.out.println(generate.content().url());
    }

    @Test
    void test_image_recognize(){
        QwenChatModel chatModel = QwenChatModel.builder()
                .apiKey(System.getenv("ALI_AI_KEY"))
                .modelName("qwen2.5-vl-32b-instruct")
                .build();
        UserMessage userMessage = UserMessage.from(
                TextContent.from("这个照片上面描述了什么？"),
                ImageContent.from("https://dashscope-result-wlcb-acdr-1.oss-cn-wulanchabu-acdr-1.aliyuncs.com/1d/76/20250325/b0fe3396/c09ada40-37f1-4748-9d32-82a373c79534151007983.png?Expires=1742978890&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=gOV4MUh2glEKgk2PNXJZBOEENDI%3D")
        );
        ChatResponse chatResponse = chatModel.chat(userMessage);
        System.out.println(chatResponse.aiMessage().toString());
    }

    @Test
    void test_ai_service(){
        ChatLanguageModel model = QwenChatModel.builder()
                .apiKey(System.getenv("ALI_AI_KEY"))
                .modelName("qwen2.5-7b-instruct-1m")
                .build();
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
                // 对话记忆
                .chatMemory(new MessageWindowChatMemory.Builder()
                        .id("test")
                        .maxMessages(10)
                        .chatMemoryStore(new InMemoryChatMemoryStore())
                        .build())
                // function call
                .tools(new Tools())
                .build();
        String message = assistant.chat("你好，我叫张三，今年20岁。");
        System.out.println(message);
        message = assistant.chat("我是谁，我是几班的？");
        System.out.println(message);
    }


    interface Assistant {

        String chat(String userMessage);

    }

    static class Tools {

        @Tool("获取用户所在的班级")
        public String getUserClass(
                @P("用户姓名") String userName) {
            System.out.println("用户姓名是："+userName);
            return "三班";
        }

    }

}

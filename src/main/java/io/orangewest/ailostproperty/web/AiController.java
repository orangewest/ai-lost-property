package io.orangewest.ailostproperty.web;

import io.orangewest.ailostproperty.service.AiChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiChatService aiChatService;

    @GetMapping(value = "/chat-stream", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=utf-8")
    public Flux<String> chatStream(@RequestParam(value = "message", defaultValue = "Hello") String message,
                                   @RequestParam(value = "userId", defaultValue = "111") String userId) {
        return Flux.just(aiChatService.chat(userId, message));
    }

}

package io.orangewest.ailostproperty.web;

import io.orangewest.ailostproperty.pojo.dto.ChatHistoryVo;
import io.orangewest.ailostproperty.service.AiChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping(value = "/chat-history")
    public PagedModel<ChatHistoryVo> queryChatHistory(@RequestParam(value = "userId", defaultValue = "111") String userId,
                                                      @PageableDefault(direction = Sort.Direction.DESC, sort = "id") Pageable page) {
        return new PagedModel<>(aiChatService.queryChatHistory(userId, page));
    }

    @PostMapping(value = "/clear-chat-history/{userId}")
    public void clearChatHistory(@PathVariable("userId") String userId) {
        aiChatService.clearChatHistory(userId);
    }

}

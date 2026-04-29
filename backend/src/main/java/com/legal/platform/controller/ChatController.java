package com.legal.platform.controller;

import com.legal.platform.common.Result;
import com.legal.platform.entity.ChatMessage;
import com.legal.platform.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatMessageService chatMessageService;

    @GetMapping("/conversations")
    public Result<List<Map<String, Object>>> getConversationList(@RequestParam Long userId) {
        return chatMessageService.getConversationList(userId);
    }

    @GetMapping("/history")
    public Result<List<ChatMessage>> getChatHistory(
            @RequestParam Long userId,
            @RequestParam Long targetId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        return chatMessageService.getChatHistory(userId, targetId, page, size);
    }

    @PostMapping("/send")
    public Result<ChatMessage> sendMessage(@RequestBody ChatMessage message) {
        return chatMessageService.sendMessage(message);
    }

    @PutMapping("/read")
    public Result<Boolean> markAsRead(@RequestParam Long userId, @RequestParam Long targetId) {
        return chatMessageService.markAsRead(userId, targetId);
    }

    @GetMapping("/unread-count")
    public Result<Integer> getUnreadCount(@RequestParam Long userId) {
        return chatMessageService.getUnreadCount(userId);
    }

    @MessageMapping("/chat.send/{toUserId}")
    public void handleWebSocketMessage(@DestinationVariable Long toUserId, @Payload ChatMessage message, SimpMessageHeaderAccessor headerAccessor) {
        message.setToUserId(toUserId);
        chatMessageService.sendMessage(message);
    }
}

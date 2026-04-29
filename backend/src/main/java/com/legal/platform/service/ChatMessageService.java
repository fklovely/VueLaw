package com.legal.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.legal.platform.common.Result;
import com.legal.platform.entity.ChatMessage;

import java.util.List;
import java.util.Map;

public interface ChatMessageService extends IService<ChatMessage> {
    Result<List<Map<String, Object>>> getConversationList(Long userId);
    Result<List<ChatMessage>> getChatHistory(Long userId, Long targetId, Integer page, Integer size);
    Result<ChatMessage> sendMessage(ChatMessage message);
    Result<Boolean> markAsRead(Long userId, Long targetId);
    Result<Integer> getUnreadCount(Long userId);
}

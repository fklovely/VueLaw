package com.legal.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.legal.platform.common.Result;
import com.legal.platform.entity.ChatMessage;
import com.legal.platform.entity.SysUser;
import com.legal.platform.mapper.ChatMessageMapper;
import com.legal.platform.mapper.SysUserMapper;
import com.legal.platform.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements ChatMessageService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public Result<List<Map<String, Object>>> getConversationList(Long userId) {
        List<ChatMessage> messages = this.list(new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getFromUserId, userId)
                .or()
                .eq(ChatMessage::getToUserId, userId)
                .orderByDesc(ChatMessage::getCreateTime));

        Map<Long, Map<String, Object>> conversationMap = new LinkedHashMap<>();
        for (ChatMessage msg : messages) {
            Long targetId = msg.getFromUserId().equals(userId) ? msg.getToUserId() : msg.getFromUserId();
            if (!conversationMap.containsKey(targetId)) {
                SysUser targetUser = sysUserMapper.selectById(targetId);
                Map<String, Object> conversation = new HashMap<>();
                conversation.put("targetId", targetId);
                conversation.put("targetName", targetUser != null ? targetUser.getRealName() : "未知用户");
                conversation.put("targetAvatar", targetUser != null ? targetUser.getAvatar() : null);
                conversation.put("lastMessage", msg.getContent());
                conversation.put("lastTime", msg.getCreateTime());
                conversation.put("messageType", msg.getMessageType());

                LambdaQueryWrapper<ChatMessage> unreadWrapper = new LambdaQueryWrapper<>();
                unreadWrapper.eq(ChatMessage::getFromUserId, targetId)
                        .eq(ChatMessage::getToUserId, userId)
                        .eq(ChatMessage::getIsRead, 0);
                conversation.put("unreadCount", this.count(unreadWrapper));

                conversationMap.put(targetId, conversation);
            }
        }

        return Result.success(new ArrayList<>(conversationMap.values()));
    }

    @Override
    public Result<List<ChatMessage>> getChatHistory(Long userId, Long targetId, Integer page, Integer size) {
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w
                .and(w1 -> w1.eq(ChatMessage::getFromUserId, userId).eq(ChatMessage::getToUserId, targetId))
                .or(w2 -> w2.eq(ChatMessage::getFromUserId, targetId).eq(ChatMessage::getToUserId, userId))
        );
        wrapper.orderByDesc(ChatMessage::getCreateTime);
        wrapper.last("LIMIT " + size + " OFFSET " + (page - 1) * size);

        List<ChatMessage> messages = this.list(wrapper);
        Collections.reverse(messages);
        return Result.success(messages);
    }

    @Override
    public Result<ChatMessage> sendMessage(ChatMessage message) {
        message.setIsRead(0);
        message.setCreateTime(LocalDateTime.now());
        this.save(message);
        
        ChatMessage savedMessage = this.getById(message.getId());
        
        messagingTemplate.convertAndSendToUser(
                message.getToUserId().toString(),
                "/queue/messages",
                savedMessage
        );

        return Result.success(savedMessage);
    }

    @Override
    public Result<Boolean> markAsRead(Long userId, Long targetId) {
        LambdaUpdateWrapper<ChatMessage> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ChatMessage::getFromUserId, targetId)
                .eq(ChatMessage::getToUserId, userId)
                .eq(ChatMessage::getIsRead, 0)
                .set(ChatMessage::getIsRead, 1);
        this.update(wrapper);
        return Result.success(true);
    }

    @Override
    public Result<Integer> getUnreadCount(Long userId) {
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatMessage::getToUserId, userId)
                .eq(ChatMessage::getIsRead, 0);
        return Result.success((int) this.count(wrapper));
    }
}

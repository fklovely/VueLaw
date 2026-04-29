package com.legal.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.legal.platform.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
}

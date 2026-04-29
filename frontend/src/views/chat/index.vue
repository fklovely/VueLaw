<template>
  <div class="chat-container">
    <div class="conversation-list">
      <div class="conversation-header">
        <h3>消息</h3>
        <el-badge :value="totalUnread" :hidden="totalUnread === 0">
          <i class="el-icon-message"></i>
        </el-badge>
      </div>
      <div class="conversation-items">
        <div
          v-for="conv in conversations"
          :key="conv.targetId"
          :class="['conversation-item', { active: currentTargetId === conv.targetId }]"
          @click="selectConversation(conv)"
        >
          <el-avatar :src="conv.targetAvatar" :size="48">{{ conv.targetName?.charAt(0) }}</el-avatar>
          <div class="conversation-info">
            <div class="conversation-name">{{ conv.targetName }}</div>
            <div class="conversation-last">{{ conv.lastMessage }}</div>
          </div>
          <div class="conversation-meta">
            <div class="conversation-time">{{ formatTime(conv.lastTime) }}</div>
            <el-badge :value="conv.unreadCount" :hidden="conv.unreadCount === 0" />
          </div>
        </div>
        <el-empty v-if="conversations.length === 0" description="暂无会话" />
      </div>
    </div>
    <div class="chat-main">
      <template v-if="currentTargetId">
        <div class="chat-header">
          <span class="chat-target-name">{{ currentTargetName }}</span>
        </div>
        <div class="chat-messages" ref="messageContainer">
          <div
            v-for="msg in messages"
            :key="msg.id"
            :class="['message-item', { 'message-self': msg.fromUserId === currentUserId }]"
          >
            <el-avatar v-if="msg.fromUserId !== currentUserId" :size="36">{{ currentTargetName?.charAt(0) }}</el-avatar>
            <div class="message-content">
              <div class="message-bubble" v-if="msg.messageType === 1">{{ msg.content }}</div>
              <div class="message-bubble message-image" v-else-if="msg.messageType === 2">
                <el-image :src="msg.fileUrl" :preview-src-list="[msg.fileUrl]" style="max-width: 200px; max-height: 200px;" />
              </div>
              <div class="message-time">{{ formatTime(msg.createTime) }}</div>
            </div>
            <el-avatar v-if="msg.fromUserId === currentUserId" :size="36">{{ userInfo?.realName?.charAt(0) }}</el-avatar>
          </div>
        </div>
        <div class="chat-input">
          <el-input
            v-model="inputMessage"
            placeholder="输入消息..."
            @keyup.enter.native="handleSend"
            :disabled="sending"
          >
            <el-upload
              slot="prepend"
              :action="uploadUrl"
              :headers="uploadHeaders"
              :on-success="handleImageUpload"
              :show-file-list="false"
              accept="image/*"
            >
              <el-button type="text" icon="el-icon-picture"></el-button>
            </el-upload>
            <el-button slot="append" type="primary" @click="handleSend" :loading="sending">发送</el-button>
          </el-input>
        </div>
      </template>
      <el-empty v-else description="选择一个会话开始聊天" />
    </div>
  </div>
</template>

<script>
import { getConversationList, getChatHistory, sendMessage, markAsRead, getUnreadCount } from '@/api/chat'
import SockJS from 'sockjs-client'
import { Client } from '@stomp/stompjs'

export default {
  name: 'ChatPage',
  data() {
    return {
      conversations: [],
      messages: [],
      currentTargetId: null,
      currentTargetName: '',
      inputMessage: '',
      sending: false,
      stompClient: null,
      totalUnread: 0,
      currentPage: 1,
      hasMore: true,
      uploadUrl: '/api/upload',
      uploadHeaders: {}
    }
  },
  computed: {
    currentUserId() {
      return this.$store.getters.userInfo?.userId
    },
    userInfo() {
      return this.$store.getters.userInfo
    }
  },
  created() {
    const token = this.$store.getters.token
    if (token) {
      this.uploadHeaders = { Authorization: token }
    }
    this.loadConversations()
    this.connectWebSocket()
  },
  beforeDestroy() {
    if (this.stompClient) {
      this.stompClient.deactivate()
    }
  },
  methods: {
    async loadConversations() {
      try {
        const res = await getConversationList(this.currentUserId)
        this.conversations = res.data || []
        this.calculateTotalUnread()
      } catch (error) {
        console.error(error)
      }
    },
    calculateTotalUnread() {
      this.totalUnread = this.conversations.reduce((sum, conv) => sum + (conv.unreadCount || 0), 0)
    },
    async selectConversation(conv) {
      this.currentTargetId = conv.targetId
      this.currentTargetName = conv.targetName
      this.currentPage = 1
      this.messages = []
      await this.loadMessages()
      if (conv.unreadCount > 0) {
        await markAsRead(this.currentUserId, this.currentTargetId)
        conv.unreadCount = 0
        this.calculateTotalUnread()
      }
    },
    async loadMessages() {
      try {
        const res = await getChatHistory(this.currentUserId, this.currentTargetId, 1, 50)
        this.messages = res.data || []
        this.$nextTick(() => {
          this.scrollToBottom()
        })
      } catch (error) {
        console.error(error)
      }
    },
    async handleSend() {
      if (!this.inputMessage.trim() || this.sending) return
      this.sending = true
      try {
        const message = {
          fromUserId: this.currentUserId,
          toUserId: this.currentTargetId,
          messageType: 1,
          content: this.inputMessage.trim()
        }
        await sendMessage(message)
        this.inputMessage = ''
        await this.loadMessages()
        await this.loadConversations()
      } catch (error) {
        console.error(error)
      } finally {
        this.sending = false
      }
    },
    handleImageUpload(res) {
      if (res.code === 200) {
        this.sendImageMessage(res.data.url)
      }
    },
    async sendImageMessage(url) {
      try {
        const message = {
          fromUserId: this.currentUserId,
          toUserId: this.currentTargetId,
          messageType: 2,
          content: '图片',
          fileUrl: url
        }
        await sendMessage(message)
        await this.loadMessages()
        await this.loadConversations()
      } catch (error) {
        console.error(error)
      }
    },
    connectWebSocket() {
      this.stompClient = new Client({
        webSocketFactory: () => new SockJS('/api/ws'),
        reconnectDelay: 5000,
        onConnect: () => {
          this.stompClient.subscribe('/user/' + this.currentUserId + '/queue/messages', (message) => {
            const msg = JSON.parse(message.body)
            if (msg.fromUserId === this.currentTargetId) {
              this.messages.push(msg)
              this.$nextTick(() => {
                this.scrollToBottom()
              })
              markAsRead(this.currentUserId, this.currentTargetId)
            }
            this.loadConversations()
          })
        },
        onStompError: (frame) => {
          console.error('STOMP error:', frame)
        }
      })
      this.stompClient.activate()
    },
    scrollToBottom() {
      const container = this.$refs.messageContainer
      if (container) {
        container.scrollTop = container.scrollHeight
      }
    },
    formatTime(time) {
      if (!time) return ''
      const date = new Date(time)
      const now = new Date()
      const diff = now - date
      if (diff < 60000) return '刚刚'
      if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
      if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
      return date.toLocaleDateString()
    }
  }
}
</script>

<style lang="scss" scoped>
.chat-container {
  display: flex;
  height: calc(100vh - 120px);
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
}

.conversation-list {
  width: 300px;
  border-right: 1px solid #eee;
  display: flex;
  flex-direction: column;
}

.conversation-header {
  padding: 16px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;

  h3 {
    margin: 0;
    font-size: 18px;
  }
}

.conversation-items {
  flex: 1;
  overflow-y: auto;
}

.conversation-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  cursor: pointer;
  transition: background 0.2s;

  &:hover {
    background: #f5f7fa;
  }

  &.active {
    background: #ecf5ff;
  }
}

.conversation-info {
  flex: 1;
  margin-left: 12px;
  overflow: hidden;
}

.conversation-name {
  font-weight: 500;
  margin-bottom: 4px;
}

.conversation-last {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.conversation-meta {
  text-align: right;
  margin-left: 8px;
}

.conversation-time {
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}

.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.chat-header {
  padding: 16px;
  border-bottom: 1px solid #eee;

  .chat-target-name {
    font-size: 16px;
    font-weight: 500;
  }
}

.chat-messages {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  background: #f5f7fa;
}

.message-item {
  display: flex;
  margin-bottom: 16px;

  &.message-self {
    justify-content: flex-end;

    .message-bubble {
      background: #409eff;
      color: #fff;
    }

    .message-time {
      text-align: right;
    }
  }
}

.message-content {
  max-width: 60%;
  margin: 0 8px;
}

.message-bubble {
  padding: 10px 14px;
  background: #fff;
  border-radius: 8px;
  word-break: break-word;

  &.message-image {
    padding: 4px;
    background: transparent;
  }
}

.message-time {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.chat-input {
  padding: 16px;
  border-top: 1px solid #eee;
}
</style>

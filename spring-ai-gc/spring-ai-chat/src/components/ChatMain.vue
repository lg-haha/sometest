<script setup lang="ts">
import { ref, nextTick, watch } from 'vue'
import type { Conversation, ModelInfo, ChatMessage } from '../types'
import { streamChat } from '../api/chat'
import MessageBubble from './MessageBubble.vue'
import ModelSelector from './ModelSelector.vue'

const props = defineProps<{
  conversation: Conversation | null
  models: ModelInfo[]
  isStreaming: boolean
}>()

const emit = defineEmits<{
  'update:isStreaming': [value: boolean]
}>()

const inputText = ref('')
const messageListRef = ref<HTMLDivElement>()
let abortController: AbortController | null = null

// 当前选中的模型
const currentModel = ref(props.conversation?.model || 'deepseek')

watch(() => props.conversation, (conv) => {
  if (conv) {
    currentModel.value = conv.model
  }
  scrollToBottom()
}, { immediate: true })

function scrollToBottom() {
  nextTick(() => {
    if (messageListRef.value) {
      messageListRef.value.scrollTop = messageListRef.value.scrollHeight
    }
  })
}

async function handleSend() {
  const text = inputText.value.trim()
  if (!text || !props.conversation || props.isStreaming) return

  const userMessage: ChatMessage = {
    id: crypto.randomUUID(),
    role: 'user',
    content: text,
    timestamp: Date.now()
  }
  props.conversation.messages.push(userMessage)

  // 设置会话标题（取第一条消息的前 20 个字符）
  if (props.conversation.messages.filter(m => m.role === 'user').length === 1) {
    props.conversation.title = text.slice(0, 20) + (text.length > 20 ? '...' : '')
  }

  const assistantMessage: ChatMessage = {
    id: crypto.randomUUID(),
    role: 'assistant',
    content: '',
    timestamp: Date.now(),
    model: currentModel.value
  }
  props.conversation.messages.push(assistantMessage)
  // 通过响应式数组获取 Proxy 包装后的对象引用，确保后续修改能触发视图更新
  const assistantIdx = props.conversation.messages.length - 1
  props.conversation.model = currentModel.value

  inputText.value = ''
  emit('update:isStreaming', true)
  scrollToBottom()

  abortController = streamChat(
    {
      message: text,
      conversationId: props.conversation.id,
      model: currentModel.value
    },
    (chunk) => {
      props.conversation.messages[assistantIdx].content += chunk
      scrollToBottom()
    },
    (error) => {
      props.conversation.messages[assistantIdx].content = `请求失败: ${error.message}`
      emit('update:isStreaming', false)
      abortController = null
    },
    () => {
      emit('update:isStreaming', false)
      abortController = null
    }
  )
}

function handleKeydown(e: KeyboardEvent) {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    handleSend()
  }
}

function handleStop() {
  if (abortController) {
    abortController.abort()
    abortController = null
    emit('update:isStreaming', false)
  }
}
</script>

<template>
  <div class="chat-main">
    <!-- 顶部工具栏 -->
    <div class="chat-header">
      <ModelSelector
        :model="currentModel"
        :models="models"
        @update:model="currentModel = $event"
      />
      <el-button
        v-if="isStreaming"
        type="danger"
        size="small"
        @click="handleStop"
      >
        <el-icon><VideoPause /></el-icon>
        停止
      </el-button>
    </div>

    <!-- 消息列表 -->
    <div ref="messageListRef" class="message-list">
      <div v-if="!conversation" class="empty-state">
        <el-icon :size="48" color="#c0c4cc"><ChatDotRound /></el-icon>
        <p>选择或新建一个会话开始聊天</p>
      </div>
      <template v-else>
        <MessageBubble
          v-for="msg in conversation.messages"
          :key="msg.id"
          :message="msg"
        />
        <div v-if="isStreaming" class="typing-indicator">
          <span></span><span></span><span></span>
        </div>
      </template>
    </div>

    <!-- 输入区域 -->
    <div class="input-area">
      <el-input
        v-model="inputText"
        type="textarea"
        :autosize="{ minRows: 1, maxRows: 6 }"
        placeholder="输入消息，Enter 发送，Shift+Enter 换行"
        :disabled="!conversation || isStreaming"
        @keydown="handleKeydown"
      />
      <el-button
        type="primary"
        :icon="Promotion"
        :disabled="!inputText.trim() || !conversation || isStreaming"
        @click="handleSend"
      />
    </div>
  </div>
</template>

<script lang="ts">
import { Promotion } from '@element-plus/icons-vue'
export default {
  components: { Promotion }
}
</script>

<style scoped>
.chat-main {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.chat-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-bottom: 1px solid #e4e7ed;
  background-color: #fff;
}

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background-color: #fafafa;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #909399;
  gap: 16px;
}

.empty-state p {
  font-size: 14px;
}

.input-area {
  display: flex;
  align-items: flex-end;
  gap: 8px;
  padding: 12px 16px;
  border-top: 1px solid #e4e7ed;
  background-color: #fff;
}

.input-area .el-button {
  flex-shrink: 0;
  height: 40px;
  width: 40px;
  padding: 0;
}

.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 8px 14px;
  margin-bottom: 16px;
  width: fit-content;
}

.typing-indicator span {
  width: 6px;
  height: 6px;
  background-color: #c0c4cc;
  border-radius: 50%;
  animation: typing 1.4s infinite;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 60%, 100% { transform: translateY(0); }
  30% { transform: translateY(-6px); }
}
</style>

<script setup lang="ts">
import { computed } from 'vue'
import { marked } from 'marked'
import type { ChatMessage } from '../types'

const props = defineProps<{
  message: ChatMessage
}>()

const rendered = computed(() => {
  if (props.message.role === 'user') {
    return props.message.content.replace(/\n/g, '<br>')
  }
  return marked.parse(props.message.content)
})

const isUser = computed(() => props.message.role === 'user')
</script>

<template>
  <div class="message-bubble" :class="{ 'is-user': isUser }">
    <div class="bubble-avatar">
      <el-avatar :size="36" :style="{ backgroundColor: isUser ? '#409eff' : '#67c23a' }">
        <el-icon><User v-if="isUser" /><Monitor v-else /></el-icon>
      </el-avatar>
    </div>
    <div class="bubble-body">
      <div class="bubble-content" v-html="rendered" />
      <div v-if="!isUser && message.model" class="bubble-model">
        {{ message.model }}
      </div>
    </div>
  </div>
</template>

<style scoped>
.message-bubble {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.message-bubble.is-user {
  flex-direction: row-reverse;
}

.bubble-avatar {
  flex-shrink: 0;
}

.bubble-body {
  max-width: 70%;
}

.bubble-content {
  padding: 10px 14px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.6;
  word-break: break-word;
}

.is-user .bubble-content {
  background-color: #409eff;
  color: #fff;
  border-top-right-radius: 4px;
}

.message-bubble:not(.is-user) .bubble-content {
  background-color: #f4f4f5;
  color: #303133;
  border-top-left-radius: 4px;
}

.bubble-model {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  text-align: right;
}

.is-user .bubble-model {
  text-align: left;
}

/* Markdown 样式 */
.bubble-content :deep(pre) {
  background-color: #1e1e1e;
  color: #d4d4d4;
  padding: 12px;
  border-radius: 6px;
  overflow-x: auto;
  font-size: 13px;
}

.bubble-content :deep(code) {
  background-color: #f0f0f0;
  padding: 2px 6px;
  border-radius: 3px;
  font-size: 13px;
}

.is-user .bubble-content :deep(code) {
  background-color: rgba(255, 255, 255, 0.2);
}

.bubble-content :deep(pre code) {
  background-color: transparent;
  padding: 0;
}

.bubble-content :deep(p) {
  margin: 0 0 8px;
}

.bubble-content :deep(p:last-child) {
  margin-bottom: 0;
}

.bubble-content :deep(ul), .bubble-content :deep(ol) {
  margin: 4px 0;
  padding-left: 20px;
}

.bubble-content :deep(blockquote) {
  margin: 8px 0;
  padding: 4px 12px;
  border-left: 3px solid #dcdfe6;
  color: #606266;
}
</style>

<script setup lang="ts">
import type { Conversation } from '../types'

defineProps<{
  conversations: Conversation[]
  currentId: string | null
}>()

const emit = defineEmits<{
  create: []
  select: [id: string]
  delete: [id: string]
}>()
</script>

<template>
  <div class="chat-sidebar">
    <div class="sidebar-header">
      <el-button type="primary" style="width: 100%" @click="emit('create')">
        <el-icon><Plus /></el-icon>
        新建会话
      </el-button>
    </div>
    <div class="sidebar-list">
      <div
        v-for="conv in conversations"
        :key="conv.id"
        class="conversation-item"
        :class="{ active: conv.id === currentId }"
        @click="emit('select', conv.id)"
      >
        <div class="conv-title">
          <el-icon><ChatDotRound /></el-icon>
          <span>{{ conv.title || '新会话' }}</span>
        </div>
        <el-button
          class="conv-delete"
          type="danger"
          :icon="Delete"
          circle
          size="small"
          @click.stop="emit('delete', conv.id)"
        />
      </div>
      <div v-if="conversations.length === 0" class="empty-tip">
        暂无会话
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Delete } from '@element-plus/icons-vue'
export default {
  components: { Delete }
}
</script>

<style scoped>
.chat-sidebar {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.sidebar-header {
  padding: 16px;
  border-bottom: 1px solid #e4e7ed;
}

.sidebar-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.conversation-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s;
  margin-bottom: 2px;
}

.conversation-item:hover {
  background-color: #f5f7fa;
}

.conversation-item.active {
  background-color: #ecf5ff;
}

.conv-title {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  overflow: hidden;
}

.conv-title span {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 14px;
}

.conv-delete {
  opacity: 0;
  transition: opacity 0.2s;
}

.conversation-item:hover .conv-delete {
  opacity: 1;
}

.empty-tip {
  text-align: center;
  color: #909399;
  padding: 40px 0;
  font-size: 14px;
}
</style>

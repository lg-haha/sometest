<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import type { Conversation, ModelInfo } from './types'
import { getModels } from './api/chat'
import ChatSidebar from './components/ChatSidebar.vue'
import ChatMain from './components/ChatMain.vue'

const conversations = ref<Conversation[]>([])
const currentConversationId = ref<string | null>(null)
const models = ref<ModelInfo[]>([])
const isStreaming = ref(false)

const currentConversation = computed(() =>
  conversations.value.find(c => c.id === currentConversationId.value) || null
)

onMounted(async () => {
  try {
    models.value = await getModels()
  } catch (e) {
    console.error('获取模型列表失败', e)
  }
})

function createConversation() {
  const conv: Conversation = {
    id: crypto.randomUUID(),
    title: '',
    messages: [],
    model: models.value.length > 0 ? models.value[0].code : 'deepseek'
  }
  conversations.value.unshift(conv)
  currentConversationId.value = conv.id
}

function selectConversation(id: string) {
  currentConversationId.value = id
}

function deleteConversation(id: string) {
  const index = conversations.value.findIndex(c => c.id === id)
  if (index !== -1) {
    conversations.value.splice(index, 1)
    if (currentConversationId.value === id) {
      currentConversationId.value = conversations.value.length > 0
        ? conversations.value[0].id
        : null
    }
  }
}
</script>

<template>
  <el-container class="app-container">
    <el-aside width="280px" class="app-aside">
      <ChatSidebar
        :conversations="conversations"
        :current-id="currentConversationId"
        @create="createConversation"
        @select="selectConversation"
        @delete="deleteConversation"
      />
    </el-aside>
    <el-main class="app-main">
      <ChatMain
        :conversation="currentConversation"
        :models="models"
        :is-streaming="isStreaming"
        @update:is-streaming="isStreaming = $event"
      />
    </el-main>
  </el-container>
</template>

<style scoped>
.app-container {
  height: 100vh;
  overflow: hidden;
}

.app-aside {
  border-right: 1px solid #e4e7ed;
  background-color: #fff;
  overflow: hidden;
}

.app-main {
  padding: 0;
  overflow: hidden;
}
</style>

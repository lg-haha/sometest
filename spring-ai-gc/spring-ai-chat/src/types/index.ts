/** 聊天消息 */
export interface ChatMessage {
  id: string
  role: 'user' | 'assistant'
  content: string
  timestamp: number
  model?: string
}

/** 会话 */
export interface Conversation {
  id: string
  title: string
  messages: ChatMessage[]
  model: string
}

/** 聊天请求 */
export interface ChatRequest {
  message: string
  conversationId: string
  model: string
}

/** 可用模型 */
export interface ModelInfo {
  code: string
  name: string
  description: string
}

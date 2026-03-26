import type { ChatRequest, ModelInfo } from '../types'

const API_BASE = '/api/chat'

/** 获取可用模型列表 */
export async function getModels(): Promise<ModelInfo[]> {
  const res = await fetch(`${API_BASE}/models`)
  return res.json()
}

/** 同步聊天（备用） */
export async function sendChat(request: ChatRequest): Promise<string> {
  const res = await fetch(`${API_BASE}/send`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(request)
  })
  return res.text()
}

/**
 * 流式聊天 - SSE
 * 使用 fetch + ReadableStream 读取 POST SSE 流
 */
export function streamChat(
  request: ChatRequest,
  onChunk: (text: string) => void,
  onError: (error: Error) => void,
  onComplete: () => void
): AbortController {
  const controller = new AbortController()

  let buffer = ''

  fetch(`${API_BASE}/stream`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(request),
    signal: controller.signal
  }).then(async (response) => {
    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: ${response.statusText}`)
    }
    const reader = response.body!.getReader()
    const decoder = new TextDecoder()

    while (true) {
      const { done, value } = await reader.read()
      if (done) break
      buffer += decoder.decode(value, { stream: true })

      const lines = buffer.split('\n')
      // 最后一个元素可能是不完整的行，保留在缓冲区
      buffer = lines.pop() || ''

      for (const line of lines) {
        if (line.startsWith('data:')) {
          // SSE 标准：data: 后面的空格应被去掉，内容本身保留
          const content = line.slice(5).replace(/^ /, '')
          if (content) {
            onChunk(content)
          }
        }
      }
    }
    // 处理缓冲区中最后剩余的数据
    if (buffer.startsWith('data:')) {
      const content = buffer.slice(5).replace(/^ /, '')
      if (content) {
        onChunk(content)
      }
    }
    onComplete()
  }).catch((err) => {
    if (err.name !== 'AbortError') {
      onError(err)
    }
  })

  return controller
}

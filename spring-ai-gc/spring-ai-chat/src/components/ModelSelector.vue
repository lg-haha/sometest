<script setup lang="ts">
import type { ModelInfo } from '../types'

defineProps<{
  models: ModelInfo[]
  model: string
}>()

const emit = defineEmits(['update:model'])
</script>

<template>
  <el-select
    :model-value="model"
    @update:model-value="emit('update:model', $event)"
    size="small"
    style="width: 200px"
  >
    <el-option
      v-for="m in models"
      :key="m.code"
      :label="m.name"
      :value="m.code"
    >
      <div style="display: flex; justify-content: space-between; align-items: center;">
        <span>{{ m.name }}</span>
        <span style="color: #909399; font-size: 12px;">{{ m.code }}</span>
      </div>
    </el-option>
  </el-select>
</template>

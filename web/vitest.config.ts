import { defineConfig } from 'vitest/config'
import react from '@vitejs/plugin-react'
import path from 'path'

// 独立的 Vitest 配置：与 vite.config.ts 分开，避免 vitest/config 的插件类型在 tsc 生产构建中冲突。
// Vitest 会优先加载本文件；vite build 仍用 vite.config.ts。
export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: './src/test/setup.ts',
    css: false,
  },
})

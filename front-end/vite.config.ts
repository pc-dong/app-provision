import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from "path";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    // 指定服务器应该监听哪个 IP 地址。 如果将此设置为 0.0.0.0 或者 true 将监听所有地址，包括局域网和公网地址。
    // 默认为 'localhost'，即仅能本机访问
    host: '0.0.0.0',
    // 启动端口
    port: 3000,
    // 设为 true 时若端口已被占用则会直接退出，而不是尝试下一个可用端口。
    strictPort: false,
    // HMR 连接配置（用于 HMR websocket 必须使用不同的 http 服务器地址的情况，或者禁用 hmr 模块），一般省略
    hmr: {
      host: '127.0.0.1',
      port: 3000
    },
    // 参数类型：boolean | string，配置启动时时候自动打开网页，是字符串时表示打开某个特定路径
    open: true,
    // 自定义代理规则，用来配合后端服务进行接口调用等。
    // 默认使用 [http-proxy](https://github.com/http-party/node-http-proxy) 模块，完整配置见官方仓库
    proxy: {
      // 字符串简写写法
      // 选项写法
      '/api': {
        target: 'http://localhost:8088',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
})

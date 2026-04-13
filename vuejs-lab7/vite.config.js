import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
    plugins: [vue()],
    server: {
        port: 5173,  // Cổng mặc định của Vite — Spring Boot cho phép CORS cổng này
    }
})
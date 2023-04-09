import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import ElementPlus from 'element-plus'
import 'element-plus/theme-chalk/index.css'
import router from "./router";

const app = createApp(App)
ElementPlus.install(app)
router.install(app)
app.mount('#app')

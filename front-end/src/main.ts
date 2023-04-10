import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import ElementPlus from 'element-plus'
import 'element-plus/theme-chalk/index.css'
import router from "./router";
import * as Icons from "@element-plus/icons-vue";

const app = createApp(App)

for (let i in Icons) {
    app.component(i, Icons[i]);
}
ElementPlus.install(app)
router.install(app)
app.mount('#app')

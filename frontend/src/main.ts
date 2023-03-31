import { createApp } from 'vue'
import App from './App.vue'
import router from "./router";
import 'element-plus/dist/index.css'
import ElementPlus from 'element-plus'
import featureFlagStore from "@/stores/FeatureFlagStore";


const app = createApp(App)
ElementPlus.install(app)
featureFlagStore.install(app)
app.use(router)
app.mount('#app')
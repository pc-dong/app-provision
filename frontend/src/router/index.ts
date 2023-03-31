import FeatureFlag from '@/views/FeatureFlag.vue'
import FeatureConfig from '@/views/FeatureConfig.vue'
import HomePage from '@/views/HomePage.vue'

import {createRouter, createWebHistory} from "vue-router";
const router = createRouter({
    history: createWebHistory(),
    routes: [
        { path: '/', component: HomePage },
        { path: '/feature', component: FeatureFlag },
        { path: '/config', component: FeatureConfig },
    ]
})

export default router
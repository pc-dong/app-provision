import {createRouter, createWebHistory} from "vue-router";

const routes = [] as any[]

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach((to, from, next) => {
    if(to.meta.title) {
        document.title = '组件化AB Testing平台 - ' + to.meta.title
    }
})

export default router
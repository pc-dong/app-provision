import { createRouter, createWebHistory } from "vue-router";

const routes = [
  {
    path: "/",
    redirect: "/home",
  },
  {
    path: "/home",
    name: "主页",
    meta: {
      title: "主页",
    },
    component: () => import("../view/Home.vue"),
    redirect: "/index",
    children: [
      {
        path: "/index",
        meta: {
          title: "首页",
        },
        component: () => import("../view/Welcome.vue"),
      },
      {
        path: "/feature-flag",
        meta: {
          title: "Feature Flag",
        },
        component: () => import("../view/featureflag/Index.vue"),
      },
      {
        path: "/feature-flag/edit",
        meta: {
          title: "Feature Flag",
        },
        component: () => import("../view/featureflag/Edit.vue"),
      },
      {
        path: "/feature-config",
        meta: {
          title: "Feature Config",
        },
        component: () => import("../view/featureflag/FeatureConfigList.vue"),
      },
      {
        path: "/feature-config/edit",
        meta: {
          title: "Feature Config",
        },
        component: () => import("../view/featureflag/FeatureConfigEdit.vue"),
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  return next();
});

export default router;

<template>
  <el-container class="layout-container-demo" style="height: 100%">
    <el-aside :width="asideWidth" style="height: 100%"  ref="aside">
      <el-scrollbar>
        <el-menu :default-openeds="['1','2']">
          <el-menu-item index="1-1"><router-link to="/"><el-icon><House /></el-icon>Home</router-link></el-menu-item>

          <el-sub-menu index="1">
            <template #title>
              <el-icon><Setting /></el-icon>FeatureFlag
            </template>
            <el-menu-item index="1-1"><router-link to="/feature">FeatureFlag</router-link></el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="2">
            <template #title>
              <el-icon><Aim /></el-icon>A/B Testing
            </template>
            <el-menu-item index="1-1">Experiment</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-scrollbar>
    </el-aside>

    <el-container style="height: 100%">
      <el-header style="text-align: right; font-size: 12px" ref="header">
        <div class="toolbar">
          <el-dropdown>
            <el-icon style="margin-right: 8px; margin-top: 1px"
            ><ArrowDown
            /></el-icon>
          </el-dropdown>
          <span>Tom</span>
        </div>
      </el-header>
      <el-main>
<!--        <el-scrollbar>-->
        <router-view/>
<!--        </el-scrollbar>-->
      </el-main>
    </el-container>
  </el-container>
</template>

<script lang="tsx">
export default {
  name: "MainPage",
  inheritAttrs: false,
  customOptions: {}
}
</script>

<script lang="tsx" setup>
import { Setting, Aim, ArrowDown, House} from '@element-plus/icons-vue'
import {
  ElIcon,
} from 'element-plus'

const asideWidth = "200"
</script>

<style scoped>
.layout-container-demo .el-header {
  position: relative;
  background-color: var(--el-color-primary-light-7);
  color: var(--el-text-color-primary);
}
.layout-container-demo .el-aside {
  color: var(--el-text-color-primary);
  background: var(--el-color-primary-light-8);
}
.layout-container-demo .el-menu {
  border-right: none;
}
.layout-container-demo .el-main {
  padding: 0;
}
.layout-container-demo .toolbar {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  right: 20px;
}
</style>

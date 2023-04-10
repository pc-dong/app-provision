<template>
  <div>
    <el-container class="home-container">
      <el-header>
        <el-row>
          <el-col :span="4">
            <p class="system-name">组件化AB测试平台</p>
          </el-col>
          <el-col :offset="12" :span="8" style="min-width: 150px">
          </el-col>
        </el-row>
      </el-header>

      <el-container style="overflow: auto">
        <!-- 左侧菜单 -->
        <el-aside>
          <el-menu router :default-active="activePath" class="el-menu-vertical-demo">
            <el-menu-item index="/index">
              <el-icon><House /></el-icon>
              <span>首页</span>
            </el-menu-item>
            <el-menu-item index="/feature-flag">
              <el-icon><Opportunity /></el-icon>
              <span>Feature Flag</span>
            </el-menu-item>
          </el-menu>
        </el-aside>
        <el-container>
          <el-main>
            <router-view />
          </el-main>
        </el-container>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import {ref} from "vue";

const activePath = ref<string>('')
</script>


<style scoped>
.home-container {
  position: absolute;
  height: 100%;
  top: 0px;
  left: 0px;
  width: 100%;
  background: #f2f3f5;
}

.el-header {
  background: #2661ef;
  padding: 0 10px;
  overflow: hidden;
}

.system-name {
  color: #fff;
  font-size: 18px;
}

.el-aside {
  background: white;
  width: auto !important;
}

.el-menu-vertical-demo:not(.el-menu--collapse) {
  width: 200px;
  min-height: 400px;
}

.el-footer {
  color: #cccccc;
  text-align: center;
  line-height: 60px;
}

.el-footer:hover {
  color: #2661ef;
}

.toggle-button {
  background-color: #d9e0e7;
  font-size: 18px;
  line-height: 24px;
  color: #fff;
  text-align: center;
  letter-spacing: 0.2em;
  cursor: pointer;
  color: black;
}

.el-menu-vertical-demo:not(.el-menu--collapse) {
  width: 200px;
  min-height: 400px;
}

.el-menu-item.is-active {
  color: #fff !important;
  font-size: 15px;
  font-weight: bold;
  background-color: #2661ef !important;
  border-radius: 2px;
  height: 50px;
  line-height: 50px;
  box-sizing: border-box;
  margin: 2px 5px 0px 2px;
}
</style>
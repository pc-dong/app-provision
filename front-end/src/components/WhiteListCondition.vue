<template>
  <div>
    <el-upload
      ref="upload"
      :limit="1"
      accept="text/plain"
      :before-upload="beforeUpload"
      :auto-upload="true"
    >
      <template #trigger>
        <el-button type="primary">select file</el-button>
      </template>
      <el-button type="default" @click="clearWhitelist">clear</el-button>
      <el-collapse v-if="whiteList && whiteList.length > 0">
        <el-collapse-item :title="'文件内容'" :name="0">
          <el-scrollbar
            style="max-height: 400px; min-height: 40px"
            :key="whiteList"
          >
            <p v-for="item in whiteList" :key="item">{{ item }}</p>
          </el-scrollbar>
        </el-collapse-item>
      </el-collapse>
    </el-upload>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { ElMessage, UploadInstance } from "element-plus";

const props = defineProps({
  whiteList: {
    type: Array,
    required: false,
  },
});

const upload = ref<UploadInstance>();
const whiteList = ref(props.whiteList);
const emit = defineEmits(["change"]);

const beforeUpload = (file: File) => {
  const reader = new FileReader();

  reader.onload = () => {
    const fileContent = reader.result;
    whiteList.value = fileContent.split("\n");

    // 校验whiteList中记录由字母数字-和_组成，且长度在 3 - 100之间
    if (whiteList.value) {
      for (let i = 0; i < whiteList.value.length; i++) {
        const value = whiteList.value[i] as string;
        if (!value || !/^[a-zA-Z0-9_-]{3,100}$/.test(value)) {
          upload.value?.clearFiles();
          ElMessage.error("文件内容格式不正确");
          return false;
        }
      }
    }

    emit("change", whiteList.value);
  };

  reader.readAsText(file);
  return false; // 返回true表示立即上传文件
};

const clearWhitelist = () => {
  whiteList.value = [];
  upload.value?.clearFiles();
  emit("change", whiteList.value);
};
</script>

<style scoped></style>

<template>
  <div class="container">
    <el-row :gutter="20">
      <el-col span="4">
        <el-form-item label="Key" prop="key">
          <el-input v-model="item.key" autocomplete="off" />
        </el-form-item>
      </el-col>
      <el-col span="4">
        <el-form-item label="Value" prop="key">
          <el-input v-model="item.value" autocomplete="off" />
        </el-form-item>
      </el-col>
      <el-col span="4">
        <el-form-item>
          <el-button type="warning" @click="deleteItem()"
            ><el-icon> <Delete /> </el-icon
          ></el-button>
        </el-form-item>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";

const props = defineProps({
  index: {
    type: Number,
    required: true,
  },
  item: {
    type: Object,
    required: true,
  },
});

const emit = defineEmits(["deleteItem"]);
const deleteItem = () => {
  emit("deleteItem", props.index);
};

const item = ref(props.item);
</script>

<style scoped></style>

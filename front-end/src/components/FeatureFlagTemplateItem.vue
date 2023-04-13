<template>
  <div class="container">
    <el-card>
      <el-form-item label="Key" prop="key">
        <el-input ref="keyInput" v-model="item.key" />
      </el-form-item>
      <el-form-item label="名称" prop="name">
        <el-input v-model="item.name" />
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input v-model="item.description" />
      </el-form-item>
      <el-form-item label="数据类型" prop="dataType">
        <el-select v-model="item.dataType" @change="handleDataTypeChange">
          <el-option
            v-for="item in Object.keys(TemplateDataType)"
            :key="item"
            :label="item"
            :value="item"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="Default Value" prop="defaultValue">
        <el-input v-model="item.defaultValue" />
      </el-form-item>
      <el-form-item>
        <el-button type="warning" @click="deleteItem()"
          ><el-icon> <Delete /> </el-icon
        ></el-button>
        <el-button
          v-if="shouldShowSubItems()"
          type="primary"
          @click="addSubItems"
          ><el-icon> <Plus /> </el-icon
        ></el-button>
      </el-form-item>
      <el-form-item
        v-if="shouldShowSubItems()"
        label="Sub Items"
        prop="subItems"
      >
        <feature-flag-template-item
          v-for="(subItem, ind) in item.subItems"
          :key="subItem.index"
          :index="ind"
          :item="subItem"
          @deleteItem="deleteSubItem"
        />
      </el-form-item>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { TemplateDataType } from "../domain/FeatureFlags";

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

const item = ref(props.item);

const deleteItem = () => {
  emit("deleteItem", props.index);
};

const deleteSubItem = (index: number) => {
  item.value.subItems.splice(index, 1);
};

let templateItemIndex = 0;

const addSubItems = () => {
  item.value.subItems = item.value.subItems || [];
  item.value.subItems.push({
    index: templateItemIndex++,
    key: "",
    name: "",
    description: "",
    dataType: "STRING",
    defaultValue: "",
    subItems: [],
  });
};

const keyInput = ref();

let tempItems = [] as any[];

const shouldShowSubItems = () => {
  return ["OBJECT", "LIST_OBJECT"].includes(item.value.dataType);
};

const handleDataTypeChange = () => {
  if (shouldShowSubItems()) {
    if (tempItems.length == 0) {
      tempItems.push({
        index: templateItemIndex++,
        key: "",
        name: "",
        description: "",
        dataType: "",
        defaultValue: "",
      });
    }
    item.value.subItems = tempItems;
  } else {
    tempItems = item.value.subItems || [];
    item.value.subItems = [];
  }
};
</script>

<style scoped>
div.container {
  margin: 0;
  padding: 0;
}

.el-form-item {
  display: inline-block !important;
}

.el-card {
  margin: 0;
  padding: 0;
}

.el-form-item__content {
  width: 20px;
}
</style>

<template>
  <div class="template-object">
    <el-row>
      <el-col :span="23 + (shouldShowDeleteButton() ? 0 : 1)">
        <el-form-item
          v-if="shouldShowKey()"
          label="Key"
          prop="key"
          :rules="keyRules"
        >
          <el-input ref="keyInput" v-model="item.key" @change="updateItem" />
        </el-form-item>
        <el-form-item
          v-if="shouldShowKey()"
          label="名称"
          :prop="name"
          :rules="nameRules"
        >
          <el-input v-model="item.name" @change="updateItem" />
        </el-form-item>
        <el-form-item v-if="shouldShowKey()" label="描述" prop="description">
          <el-input v-model="item.description" @change="updateItem" />
        </el-form-item>
        <el-form-item label="数据类型" prop="dataType" :rules="dataTypeRules">
          <el-select v-model="item.dataType" @change="handleDataTypeChange">
            <el-option
              v-for="item in Object.keys(TemplateDataType)"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item
          v-if="shouldShowDefaultValue()"
          label="默认值"
          prop="defaultValue"
          :rules="defaultValueRules"
        >
          <el-input v-model="item.defaultValue" />
        </el-form-item>
        <el-form-item></el-form-item>

        <el-form-item v-if="shouldShowSubItems()" label=" " prop="subItems">
          <feature-flag-template-item
            v-for="(subItem, ind) in item.items"
            :key="subItem"
            :index="ind"
            :item="subItem"
            :parentItem="item"
            @change="
              item.items[ind] = $event;
              updateItem();
            "
            @deleteItem="deleteSubItem"
          />
        </el-form-item>
      </el-col>
      <el-col v-if="shouldShowDeleteButton()" :span="1">
        <el-button type="warning" @click="deleteItem()">
          <el-icon>
            <Delete />
          </el-icon>
        </el-button>
      </el-col>
    </el-row>
    <el-button v-if="shouldShowAddButton()" type="primary" @click="addSubItems">
      <el-icon>
        <Plus />
      </el-icon>
    </el-button>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { FeatureFlagTemplate, TemplateDataType } from "../domain/FeatureFlags";

const props = defineProps({
  index: {
    type: Number,
    required: true,
  },
  item: {
    type: Object,
    required: true,
  },
  parentItem: {
    type: Object,
    required: false,
  },
});

const emit = defineEmits(["deleteItem", "change"]);

const updateItem = () => {
  emit("change", item.value);
};

const item = ref(props.item as FeatureFlagTemplate);
const parentItem = ref(props.parentItem);

const deleteItem = () => {
  emit("deleteItem", props.index);
};

const shouldShowKey = () => {
  return (
    parentItem.value && parentItem.value.dataType == TemplateDataType.OBJECT
  );
};

const shouldShowDefaultValue = () => {
  return [
    TemplateDataType.STRING,
    TemplateDataType.NUMBER,
    TemplateDataType.BOOLEAN,
  ].includes(item.value.dataType);
};

const shouldShowDeleteButton = () => {
  return (
    parentItem.value &&
    parentItem.value.dataType == TemplateDataType.OBJECT &&
    parentItem.value.items.length > 1
  );
};

const shouldShowSubItems = () => {
  return [TemplateDataType.OBJECT, TemplateDataType.ARRAY].includes(
    item.value.dataType
  );
};

const shouldShowAddButton = () => {
  return item.value && item.value.dataType == TemplateDataType.OBJECT;
};

const deleteSubItem = (index: number) => {
  item.value.items = item.value.items || [];
  item.value.items.splice(index, 1);
  updateItem();
};

let templateItemIndex = 0;

const addSubItems = () => {
  item.value.items?.push({
    key: "",
    name: "",
    description: "",
    dataType: TemplateDataType.BOOLEAN,
    defaultValue: "false",
    items: [],
  });
};

const keyInput = ref();

let tempItems = [] as any[];

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
    item.value.items = tempItems;
  } else {
    tempItems = item.value.items || [];
    item.value.items = [];
  }

  updateItem();
};

const keyRules = [
  {
    validator: () => {
      if (
        !(
          parentItem.value &&
          parentItem.value.dataType == TemplateDataType.OBJECT
        )
      ) {
        return Promise.resolve();
      }

      if (!item.value?.key) {
        return Promise.reject("请输入Key");
      }

      if (!/^[a-zA-Z][a-zA-Z0-9_]*$/g.test(item.value.key)) {
        return Promise.reject("由字母、数字、下划线组成，且以字母开头");
      }

      if (item.value?.key.length < 1 || item.value?.key.length > 50) {
        return Promise.reject("长度在 1 到 50 个字符");
      }

      return Promise.resolve();
    },
    trigger: "blur",
  },
];

const nameRules = [
  {
    validator: () => {
      if (
        !(
          parentItem.value &&
          parentItem.value.dataType == TemplateDataType.OBJECT
        )
      ) {
        return Promise.resolve();
      }

      if (!item.value?.name) {
        return Promise.reject("请输入Key");
      }

      if (item.value?.name.length < 1 || item.value?.name.length > 50) {
        return Promise.reject("长度在 1 到 50 个字符");
      }

      return Promise.resolve();
    },
    trigger: "blur",
  },
];

const defaultValueRules = [
  {
    required: false,
  },
  {
    validator: () => {
      if (item.value.dataType == TemplateDataType.NUMBER) {
        if (isNaN(item.value?.defaultValue as any)) {
          return Promise.reject("Please enter a number");
        }
      } else if (item.value.dataType == TemplateDataType.BOOLEAN) {
        if (!["true", "false"].includes(item.value?.defaultValue as string)) {
          return Promise.reject("Please enter true or false");
        }
      }

      return Promise.resolve();
    },
    trigger: "blur",
  },
];

const dataTypeRules = [
  {
    validator: () => {
      if (!item.value?.dataType) {
        return Promise.reject("请选择数据类型");
      }

      return Promise.resolve();
    },
    trigger: "blur",
  },
];
</script>

<style scoped>
div.container {
  padding: 5px;
  background-color: #fafcff;
  width: 100% !important;
}

.template-list {
  background-color: #f5f7fa;
  padding: 10px;
  margin-bottom: 10px;
  width: 100%;
}

.template-object {
  background-color: #ebedf0;
  margin-bottom: 1px;
  margin-right: 0px;
  margin-left: 0px;
  padding-top: 10px;
  padding-left: 0px;
  padding-right: 0px;
  width: 100% !important;
  /*边框为实线*/
  border: 2px solid #dcdfe6;
}

.el-form-item {
  /*display: inline-block !important;*/
  margin-bottom: 15px !important;
}
</style>

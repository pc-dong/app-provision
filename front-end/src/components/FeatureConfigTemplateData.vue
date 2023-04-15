<template>
  <div class="data-container">
    <el-form-item :label="templ.name || ''" :tip="templ.description">
      <template v-if="templ.dataType === TemplateDataType.BOOLEAN">
        <el-switch v-model="item" @change="updateItem" />
      </template>
      <template v-else-if="templ.dataType === TemplateDataType.NUMBER">
        <el-input-number
          v-model="item"
          :precision="2"
          :step="0.1"
          @change="updateItem"
        />
      </template>
      <template v-else-if="templ.dataType === TemplateDataType.STRING">
        <el-input v-model="item" @change="updateItem" />
      </template>

      <template v-else-if="templ.dataType === TemplateDataType.ARRAY">
        <div class="template-list">
          <template v-for="(itP, cIndex) in item" :key="itP">
            <div class="template-object">
              <el-row :gutter="4">
                <el-col :span="22">
                  <feature-config-template-data
                    :key="itP"
                    :templ="templ.items[0]"
                    :index="Number(cIndex)"
                    :parentTemplate="templ"
                    :item="itP"
                    @change="updateArrayItem"
                  />
                </el-col>
                <el-col :span="2">
                  <el-button @click="item.splice(cIndex, 1)" type="warning"
                    ><el-icon><Delete /></el-icon>
                  </el-button>
                </el-col>
              </el-row>
            </div>
          </template>
          <el-button @click="addSubItem" type="primary"
            ><el-icon><Plus /></el-icon>
          </el-button>
        </div>
      </template>

      <template v-else-if="templ.dataType === TemplateDataType.OBJECT">
        <div class="template-object" style="width: 100%">
          <feature-config-template-data
            v-for="it in templ.items || []"
            :key="item[it.key]"
            :templ="it"
            :parentTemplate="templ"
            :item="item[it.key]"
            @change="updateObjectItem"
          />
        </div>
      </template>

      <template v-else>
        <el-input v-model="item" type="textarea" />
      </template>
    </el-form-item>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import {
  TemplateDataType,
  getTemplateDefaultValue,
  FeatureFlagTemplate,
} from "../domain/FeatureFlags";

const props = defineProps({
  templ: {
    type: Object,
    required: true,
  },
  parentTemplate: {
    type: Object,
    required: false,
  },
  index: {
    type: Number,
    required: false,
  },
  item: {
    type: Object,
    required: true,
  },
});

const templ = ref(props.templ as FeatureFlagTemplate);
const parentTemplate = ref(props.parentTemplate as FeatureFlagTemplate);
const index = ref(props.index);
const item = ref(props.item);

const emit = defineEmits(["change"]);

const addSubItem = () => {
  item.value = item.value || [];
  item.value.push(
    getTemplateDefaultValue(
      templ.value?.items ? templ.value?.items[0] : undefined
    )
  );

  updateItem();
};
const updateItem = () => {
  emit("change", templ.value.key, item.value, index.value);
};

const updateObjectItem = (key: string, value: any) => {
  console.log("updateObjectItem:" + key + "," + value + "," + index.value);
  item.value[key] = value;
  updateItem();
};

const updateArrayItem = (key: string, value: any, index: number) => {
  item.value[index] = value;
  updateItem();
};
</script>

<style scoped>
.data-container {
  /*padding: 20px;*/
  /*background-color: #fafcff;*/
  width: 100%;
}
.template-object {
  background-color: #ebedf0;
  margin-bottom: 10px;
  padding: 10px;
  /*边框为实线*/
  border: 2px solid #dcdfe6;
}

.template-list {
  background-color: #f5f7fa;
  padding: 10px;
  margin-bottom: 10px;
  width: 100%;
}
</style>

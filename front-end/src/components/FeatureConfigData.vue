<template>
  <div class="data-container">
    <template v-if="featureFlag?.description.dataType === DataType.BOOLEAN">
      <el-switch style="display: flex" v-model="item" @change="updateData" />
    </template>
    <template v-else-if="featureFlag?.description.dataType === DataType.NUMBER">
      <el-input-number
        style="display: block"
        v-model="item"
        :precision="2"
        :step="0.1"
        @change="updateData"
      />
    </template>
    <template
      v-else-if="
        [DataType.STRING, DataType.JSON_STRING].includes(
          featureFlag?.description.dataType
        )
      "
    >
      <el-input v-model="item" type="textarea" @change="updateData" />
    </template>
    <template v-else>
      <feature-config-template-data
        v-for="it in featureFlag.description.template?.items || []"
        :key="'data' + it.key"
        :templateItem="it"
        :item="item[it.key]"
        @change="updateSubItem"
      />
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { DataType } from "../domain/FeatureFlags";
import FeatureConfigTemplateData from "./FeatureConfigTemplateData.vue";

const props = defineProps({
  featureFlag: {
    type: Object,
    required: true,
  },
  item: {
    required: true,
  },
});

const item = ref(props.item);

const emit = defineEmits(["change"]);

const updateData = () => {
  emit("change", item.value);
};

const updateSubItem = (key: string, value: any) => {
  (item.value as any)[key] = value;
  emit("change", item.value);
};
</script>

<style scoped>
.data-container {
  padding: 20px;
  background-color: #fafcff;
  width: 100%;
}
</style>

<template>
  <div style="width: 100%">
    <el-tree-select
      style="width: 100%"
      v-model="item"
      :data="data"
      multiple
      :render-after-expand="false"
      show-checkbox
      check-strictly
      check-on-click-node
      @change="handleChange"
    />
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { Districts } from "../domain/Districts";

const districts = new Districts();
const data = ref(districts.getProvinceTree());

const props = defineProps({
  item: {
    type: Array,
    required: false,
  },
});

const item = ref(props.item);

const emit = defineEmits(["change"]);

const handleChange = (value: string[]) => {
  emit("change", item.value);
};

defineExpose({ item });
</script>

<style scoped></style>

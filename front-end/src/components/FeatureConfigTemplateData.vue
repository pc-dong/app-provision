<template>
  <div>
    <el-form-item :label="template.name" :tip="template.description">
      <template v-if="template.dataType === TemplateDataType.BOOLEAN">
        <el-switch v-model="item" @change="updateItem" />
      </template>
      <template v-else-if="template.dataType === TemplateDataType.NUMBER">
        <el-input-number
          v-model="item"
          :precision="2"
          :step="0.1"
          @change="updateItem"
        />
      </template>
      <template v-else-if="template.dataType === TemplateDataType.STRING">
        <el-input v-model="item" type="textarea" @change="updateItem" />
      </template>
      <template v-else-if="template.dataType === TemplateDataType.LIST_STRING">
        <el-card>
          <template v-for="(it, index) in item" :key="index">
            <el-row>
              <el-input
                v-model="item[index]"
                type="textarea"
                @change="updateItem"
              />
              <el-button
                @click="
                  item.splice(index, 1);
                  updateItem();
                "
                type="warning"
                >Delete
              </el-button>
            </el-row>
          </template>
          <el-button
            @click="
              item.push('');
              updateItem();
            "
            type="primary"
            >Add
          </el-button>
        </el-card>
      </template>
      <template v-else-if="template.dataType === TemplateDataType.LIST_NUMBER">
        <el-card>
          <template v-for="(it, index) in item" :key="index">
            <el-row>
              <el-input-number
                v-model="item[index]"
                :precision="2"
                :step="0.1"
                @change="updateItem"
              />
              <el-button
                @click="
                  item.splice(index, 1);
                  updateItem();
                "
                type="warning"
                >Delete
              </el-button>
            </el-row>
          </template>
          <el-button
            @click="
              item.push(0);
              updateItem();
            "
            type="primary"
            >Add
          </el-button>
        </el-card>
      </template>
      <template v-else-if="template.dataType === TemplateDataType.LIST_OBJECT">
        <el-card>
          <template v-for="(itP, index) in item" :key="itP">
            <el-row>
              <feature-config-template-data
                v-for="it in template.subItems || []"
                :key="it.key"
                :templateItem="it"
                :item="itP[it.key]"
                @change="
                  (key, value) => {
                    itP[key] = value;
                    updateItem();
                  }
                "
              />
              <el-button
                @click="
                  item.splice(index, 1);
                  updateItem();
                "
                type="warning"
                >Delete
              </el-button>
            </el-row>
          </template>
          <el-button
            @click="
              item.push({});
              updateItem();
            "
            type="primary"
            >Add
          </el-button>
        </el-card>
      </template>

      <template v-else-if="template.dataType === TemplateDataType.OBJECT">
        <el-card>
          <feature-config-template-data
            v-for="(it, index) in template.subItems || []"
            :key="index"
            :templateItem="it"
            :item="item[it.key]"
            @change="
              (key, value) => {
                item[key] = value;
                updateItem();
              }
            "
          />
        </el-card>
      </template>

      <template v-else>
        <el-input v-model="item" type="textarea" />
      </template>
    </el-form-item>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { TemplateDataType } from "../domain/FeatureFlags";

const props = defineProps({
  templateItem: {
    type: Object,
    required: true,
  },
  item: {
    type: Object,
    required: true,
  },
});

const template = ref(props.templateItem);
const item = ref(props.item);

const emit = defineEmits(["change"]);

const updateItem = () => {
  emit("change", template.value.key, item.value);
};
</script>

<style scoped></style>

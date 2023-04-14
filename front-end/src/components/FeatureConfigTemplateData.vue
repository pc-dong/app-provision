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
        <el-input v-model="item" @change="updateItem" />
      </template>
      <template v-else-if="template.dataType === TemplateDataType.LIST_STRING">
        <div class="template-list">
          <template v-for="(it, index) in item" :key="index">
            <div class="template-object">
              <el-row :gutter="10">
                <el-col :span="22" style="min-width: 400px">
                  <el-input
                    v-model="item[index]"
                    @change="updateItem"
                    type="textarea"
                  />
                </el-col>
                <el-col :span="2">
                  <el-button
                    @click="
                      item.splice(index, 1);
                      updateItem();
                    "
                    type="warning"
                    ><el-icon><Delete /></el-icon>
                  </el-button>
                </el-col>
              </el-row>
            </div>
          </template>
          <el-button
            @click="
              item.push('');
              updateItem();
            "
            type="primary"
            ><el-icon><Plus /></el-icon>
          </el-button>
        </div>
      </template>
      <template v-else-if="template.dataType === TemplateDataType.LIST_NUMBER">
        <div class="template-list">
          <template v-for="(it, index) in item" :key="index">
            <div class="template-object" style="">
              <el-row :gutter="20">
                <el-col :span="6">
                  <el-input-number
                    v-model="item[index]"
                    :precision="1"
                    :step="0.1"
                    @change="updateItem"
                  />
                </el-col>
                <el-col :span="18">
                  <el-button
                    @click="
                      item.splice(index, 1);
                      updateItem();
                    "
                    type="warning"
                    ><el-icon><Delete /></el-icon>
                  </el-button>
                </el-col>
              </el-row>
            </div>
          </template>
          <el-button
            @click="
              item.push(0);
              updateItem();
            "
            type="primary"
            ><el-icon><Plus /></el-icon>
          </el-button>
        </div>
      </template>
      <template v-else-if="template.dataType === TemplateDataType.LIST_OBJECT">
        <div class="template-list">
          <template v-for="(itP, index) in item" :key="itP">
            <div class="template-object">
              <el-row :gutter="4">
                <el-col :span="22">
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
                </el-col>
                <el-col :span="2">
                  <el-button
                    @click="
                      item.splice(index, 1);
                      updateItem();
                    "
                    type="warning"
                    ><el-icon><Delete /></el-icon>
                  </el-button>
                </el-col>
              </el-row>
            </div>
          </template>
          <el-button
            @click="
              item.push(getJSONDefaultValue(template.subItems));
              updateItem();
            "
            type="primary"
            ><el-icon><Plus /></el-icon>
          </el-button>
        </div>
      </template>

      <template v-else-if="template.dataType === TemplateDataType.OBJECT">
        <div class="template-object">
          <el-row>
            <el-col :span="24">
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
            </el-col>
          </el-row>
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
import { TemplateDataType, getJSONDefaultValue } from "../domain/FeatureFlags";

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

<style scoped>
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

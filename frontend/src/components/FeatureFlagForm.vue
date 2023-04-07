<template>
  <el-alert
    v-if="showAlert"
    :title="alertMessage"
    :type="alertType"
  />
  <el-dialog
    v-model="dialogFormVisible"
    title="Feature Flag"
  >
    <el-form
      :model="formData"
      :rules="formRules"
      ref="form"
      label-width="140px"
    >
      <el-form-item
        label="Feature Key"
        prop="featureKey"
      >
        <el-input
          v-model="formData.featureKey"
          autocomplete="off"
        />
      </el-form-item>
      <el-form-item
        label="Feature Name"
        prop="description.name"
      >
        <el-input
          v-model="formData.description.name"
          autocomplete="off"
        />
      </el-form-item>
      <el-form-item
        label="Feature Description"
        prop="description.description"
      >
        <el-input
          v-model="formData.description.description"
          autocomplete="off"
        />
      </el-form-item>
      <el-form-item
        label="Data Type"
        prop="description.dataType"
      >
        <el-select
          v-model="formData.description.dataType"
          :onchange="handleDataTypeChange"
        >
          <el-option
            v-for="item in Object.keys(DataType)"
            :key="item"
            :label="item"
            :value="item"
          />
        </el-select>
      </el-form-item>
      <el-form-item
        label="Default Value"
        prop="description.defaultValue"
      >
        <el-input v-model="formData.description.defaultValue" />
      </el-form-item>
      <!-- 如果dataType选择了JSON，则可以增加一个Config Template配置项-->
      <el-form-item
        v-if="formData.description.dataType === 'JSON'"
        label="Config Template"
        prop="description.template"
      >
        <el-input v-model="formData.description.template" />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="resetForm">Reset</el-button>
        <el-button
          type="primary"
          @click="submitForm"
        >
          Confirm
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script lang="ts">
export default {
  name: "FeatureFlagForm",
  inheritAttrs: false,
  customOptions: {}
}
</script>
<script lang="ts" setup>
import { defineEmits } from 'vue'
import {onMounted, reactive, ref} from 'vue'
import {ElForm, ElFormItem, ElSelect, ElOption, ElInput, ElButton} from 'element-plus'
import {useStore} from "vuex";

const featureFlagStore = useStore();


const dialogFormVisible = ref(false)
const formType = ref('ADD')

const emits = defineEmits(['formSubmitted'])

interface ConfigTemplate {
  items: ConfigTemplateItem[]
}

interface ConfigTemplateItem {
  key: string,
  name: string,
  description: string,
  dataType: ConfigTemplateItemDataType,
  defaultValue: object,
  subItems: ConfigTemplateItem[]
}

enum ConfigTemplateItemDataType {
  BOOLEAN = 'BOOLEAN',
  STRING = 'STRING',
  NUMBER = 'NUMBER',
  OBJECT = 'OBJECT',
  LIST_STRING = 'LIST_STRING',
  LIST_NUMBER = 'LIST_NUMBER',
  LIST_OBJECT = 'LIST_OBJECT'
}

enum DataType {
  BOOLEAN = 'BOOLEAN',
  STRING = 'STRING',
  NUMBER = 'NUMBER',
  JSON_STRING = 'JSON_STRING',
  JSON = 'JSON'
}

interface FormData {
  featureKey: string,
  description: {
    name: string,
    description: string,
    dataType: string,
    defaultValue: string,
    status: string,
    template?: ConfigTemplate
  }
}

const formData = ref<FormData>({
  featureKey: '',
  description: {
    name: '',
    description: '',
    dataType: 'BOOLEAN',
    defaultValue: 'false',
    status: 'PUBLISHED',
    template: null as unknown as ConfigTemplate
  }
})

const formRules = {
  'description.dataType': [
    {required: true, message: 'Please select data type', trigger: 'submit'}
  ],
  featureKey: [
    {required: true, message: 'Please enter feature key', trigger: 'submit'}
  ],
  'description.name': [
    {required: true, message: 'Please enter feature name', trigger: 'submit'}
  ],
  'description.defaultValue': [
    {required: true, message: 'Please enter default value', trigger: 'submit'},
    {
      validator: (rule, value) => {
        const dataType = formData.value.description.dataType
        switch (dataType) {
          case 'BOOLEAN':
            if (!['true', 'false'].includes(value)) {
              return Promise.reject('Please enter true or false')
            }
            break
          case 'STRING':
            if (!value) {
              return Promise.reject('Please enter string value')
            }
            break
          case 'NUMBER':
            if (isNaN(Number(value))) {
              return Promise.reject('Please enter number value')
            }
            break
          case 'JSON_STRING':
            try {
              JSON.parse(value)
            } catch (e) {
              return Promise.reject('Please enter valid JSON string')
            }
            break
          case 'JSON':
            try {
              JSON.parse(value)
            } catch (e) {
              return Promise.reject('Please enter valid JSON object')
            }
            break
        }
        return Promise.resolve()
      },
      trigger: 'submit'
    }
  ]
}

const showAlert = ref(false)

const alertMessage = ref('')
const alertType = ref('success')
const alert = (message: string, type: string) => {
  alertMessage.value = message;
  showAlert.value = true;
  alertType.value = type || 'success';
}

const form = ref<ElForm>(null as ElForm)

const submitForm = () => {
  form.value.validate(async (valid) => {
    if (valid) {
      console.log('Form is valid')
      try {
        if(formType.value == 'ADD') {
          await featureFlagStore.dispatch('submitAddForm', formData.value)
        } else {
          await featureFlagStore.dispatch('submitEditForm', formData.value)
        }
        alert('操作成功！', 'success')
        emits('formSubmitted')
      } catch (error: any) {
          console.log(error);
          alert('操作失败，请稍后重试！', 'error')
      }
    } else {
      console.log('Form is invalid')
      return false
    }
  })
}

const resetForm = () => {
  formData.value = {
    featureKey: '',
    description: {
      name: '',
      description: '',
      dataType: 'BOOLEAN',
      defaultValue: 'false',
      status: 'PUBLISHED',
      template: null as unknown as ConfigTemplate
    }
  }
}

// eslint-disable-next-line no-undef
defineExpose({
  dialogFormVisible,
  formType,
  resetForm,
  formData
})

const handleDataTypeChange = (value: string) => {
  if (value === 'JSON') {
    formData.value.description.template = {
      items: []
    }
  } else {
    formData.value.description.template = null as unknown as ConfigTemplate
  }
}

onMounted(() => {
  // console.log('props', props.showForm)
})

</script>

<style scoped>
.el-alert {
  margin: 20px 0 0;
}
.el-alert:first-child {
  margin: 0;
}
</style>
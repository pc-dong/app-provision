<template>
  <div>
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>{{title}}</span>
          <el-button size="mini" style="float:right" @click="router.go(-1)">返回</el-button>
        </div>
      </template>
      <el-form
          :model="featureFlag"
          :rules="formRules"
          ref="form"
          label-width="140px"
      >
        <el-form-item
            label="Feature Key"
            prop="featureKey"
        >
          <el-input
              v-model="featureFlag.featureKey"
              autocomplete="off"
          />
        </el-form-item>
        <el-form-item
            label="Feature Name"
            prop="description.name"
        >
          <el-input
              v-model="featureFlag.description.name"
              autocomplete="off"
          />
        </el-form-item>
        <el-form-item
            label="Feature Description"
            prop="description.description"
        >
          <el-input
              v-model="featureFlag.description.description"
              autocomplete="off"
          />
        </el-form-item>
        <el-form-item
            label="Data Type"
            prop="description.dataType"
        >
          <el-select
              v-model="featureFlag.description.dataType"
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
          <el-input v-model="featureFlag.description.defaultValue" />
        </el-form-item>
        <!-- 如果dataType选择了JSON，则可以增加一个Config Template配置项-->
        <el-form-item
            v-if="featureFlag.description.dataType === 'JSON'"
            label="Config Template"
            prop="description.template"
        >
          <el-input v-model="featureFlag.description.template" />
        </el-form-item>
        <el-form-item>
          <el-button type="default" @click="resetForm()">重置</el-button>
          <el-button type="primary" @click="submitForm()">提交</el-button>

        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import {onBeforeMount, ref} from "vue";
import {useRoute, useRouter} from "vue-router";
import {ElForm, ElFormItem, ElSelect, ElOption, ElInput, ElButton, ElMessage} from 'element-plus'
import {FeatureFlag, FeatureFlags} from "../../domain/FeatureFlags";
import {DataType} from "../../domain/FeatureFlags";

const route = useRoute();
const router = useRouter();
const featureFlags = new FeatureFlags();
const featureFlag = ref({} as FeatureFlag);

const title = ref("Feature Flag")
const formType = ref("add")
const originalFeatureFlag = ref({} as FeatureFlag)

onBeforeMount(async () => {
  title.value = (route.query.type == "edit" ? "编辑" : "新增") + "Feature Flag";
  formType.value = route.query.type == "edit" ? "edit" : "add";
  featureFlag.value = {description: {status: "PUBLISHED"}} as FeatureFlag;

  if (route.query.featureKey) {
    const res = await featureFlags.fetch(route.query.featureKey.toString());
    featureFlag.value = res;
    originalFeatureFlag.value = res;
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
        const dataType = featureFlag.value.description.dataType
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

const handleDataTypeChange = (value: string) => {
  if (value === 'JSON') {
    featureFlag.value.description.template = {
      items: []
    }
  } else {
    featureFlag.value.description.template = null as any
  }
}

const form = ref<ElForm>(null as ElForm)

const submitForm = async () => {
  form.value.validate(async (valid) => {
    if (valid) {
      console.log('Form is valid')
      try {
        if(formType.value.toLowerCase() == 'add') {
          await featureFlags.create(featureFlag.value)
        } else {
          await featureFlags.update(featureFlag.value)
        }
        ElMessage.success('操作成功！')
        setTimeout(() => {
          router.push({path: '/feature-flag'})
        }, 1000)
      } catch (error: any) {
        ElMessage.error('操作失败，请稍后重试！')
      }
    } else {
      return false
    }
  })
}

const resetForm = () => {
  if(formType.value.toLowerCase() == "add") {
    form.value.resetFields()
  } else {
    form.value.resetFields()
    featureFlag.value = originalFeatureFlag.value
  }
}

</script>

<style scoped>

</style>
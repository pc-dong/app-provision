<template>
  <div>
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span class="title">{{ title }}</span>
          <el-button size="mini" style="float: right" @click="router.go(-1)"
            >返回
          </el-button>
        </div>
      </template>
      <el-form
        :model="featureFlag"
        :rules="formRules"
        ref="form"
        label-width="100px"
      >
        <el-form-item label="Key" prop="featureKey">
          <el-input v-model="featureFlag.featureKey" autocomplete="off" />
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="featureFlag.name" autocomplete="off" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="featureFlag.description" autocomplete="off" />
        </el-form-item>

        <el-form-item label="配置模版" prop="template">
          <feature-flag-template-item
            :key="featureFlag.template"
            :index="0"
            :item="featureFlag.template"
            @change="featureFlag.template = $event"
          />
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
import { onBeforeMount, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElForm, ElFormItem, ElInput, ElButton, ElMessage } from "element-plus";
import {
  FeatureFlag,
  FeatureFlags,
  FeatureFlagTemplate,
  TemplateDataType,
} from "../../domain/FeatureFlags";
import FeatureFlagTemplateItem from "../../components/FeatureFlagTemplateItem.vue";

const route = useRoute();
const router = useRouter();
const featureFlags = new FeatureFlags();
const featureFlag = ref({} as FeatureFlag);

const title = ref("Feature Flag");
const formType = ref("add");
const originalFeatureFlag = ref({} as FeatureFlag);

onBeforeMount(async () => {
  title.value = (route.query.type == "edit" ? "编辑" : "新增") + "Feature Flag";
  formType.value = route.query.type == "edit" ? "edit" : "add";
  featureFlag.value = {
    status: "PUBLISHED",
    template: {
      dataType: TemplateDataType.BOOLEAN,
      defaultValue: "false",
      items: [] as FeatureFlagTemplate[],
    },
  } as FeatureFlag;

  if (route.query.featureKey) {
    const res = await featureFlags.fetch(route.query.featureKey.toString());
    featureFlag.value = res;
    originalFeatureFlag.value = res;
  }
});

const formRules = {
  featureKey: [
    { required: true, message: "Please enter feature key", trigger: "submit" },
    // 由字母、数字、下划线组成，且以字母开头
    {
      pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/,
      message: "由字母、数字、下划线组成，且以字母开头",
      trigger: "submit",
    },
  ],
  name: [
    { required: true, message: "Please enter feature name", trigger: "submit" },
  ],
} as any;

const form = ref<ElForm>(null);

const submitForm = async () => {
  console.log(JSON.stringify(featureFlag.value));
  form.value.validate(async (valid: boolean) => {
    if (valid) {
      try {
        if (formType.value.toLowerCase() == "add") {
          await featureFlags.create(featureFlag.value);
        } else {
          await featureFlags.update(featureFlag.value);
        }
        ElMessage.success("操作成功！");
        setTimeout(() => {
          router.push({ path: "/feature-flag" });
        }, 1000);
      } catch (error: any) {
        ElMessage.error("操作失败，请稍后重试！");
      }
    } else {
      return false;
    }
  });
};

const resetForm = () => {
  if (formType.value.toLowerCase() == "add") {
    form.value.resetFields();
  } else {
    featureFlag.value = originalFeatureFlag.value;
  }
};
</script>

<style scoped></style>

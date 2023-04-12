<template>
  <div>
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>{{ title }}</span>
          <el-button size="mini" style="float: right" @click="router.go(-1)"
            >返回
          </el-button>
        </div>
      </template>
      <el-form
        :model="featureConfig"
        :rules="formRules"
        ref="form"
        label-width="140px"
      >
        <el-form-item label="配置标题" prop="title">
          <el-input v-model="featureConfig.title" autocomplete="off" />
        </el-form-item>
        <el-form-item label="配置描述" prop="description">
          <el-input v-model="featureConfig.description" autocomplete="off" />
        </el-form-item>
        <el-form-item label="配置内容" prop="data">
          <el-input
            v-model="featureConfig.data"
            autocomplete="off"
            type="textarea"
          />
        </el-form-item>
        <el-form-item label="埋点数据" prop="trackingData">
          <el-input
            v-model="featureConfig.trackingData"
            autocomplete="off"
            type="textarea"
          />
        </el-form-item>
        <el-form-item label="生效时间" prop="timeRange">
          <el-date-picker
            v-model="featureConfig.timeRange.startDate"
            autocomplete="off"
            placeholder="Select start date and time"
            type="datetime"
          />
          -
          <el-date-picker
            v-model="featureConfig.timeRange.endDate"
            autocomplete="off"
            placeholder="Select end date and time"
            type="datetime"
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
import { FeatureConfig, FeatureConfigs } from "../../domain/FeatureConfigs";

const route = useRoute();
const router = useRouter();
const featureConfigs = new FeatureConfigs();
const featureConfig = ref({} as FeatureConfig);

const title = ref("Feature Config");
const formType = ref("add");
const originalFeatureConfig = ref({} as FeatureConfig);
const featureKey = ref("");
const id = ref("");
onBeforeMount(async () => {
  title.value =
    (route.query.type == "edit" ? "编辑" : "新增") + "Feature Config";
  formType.value = route.query.type == "edit" ? "edit" : "add";
  featureKey.value = "" + route.query.featureKey;
  featureConfig.value = { timeRange: {} } as FeatureConfig;

  if (route.query.id) {
    const res = await featureConfigs.fetchById(route.query.id.toString());
    id.value = res.id as string;
    featureConfig.value = res;
    originalFeatureConfig.value = res;
  }
});

const formRules = {
  title: [{ required: true, message: "Please enter title", trigger: "submit" }],
  data: [{ required: true, message: "Please enter data", trigger: "submit" }],
};

const form = ref<ElForm>(null);

const submitForm = async () => {
  form?.value?.validate(async (valid: boolean) => {
    if (valid) {
      console.log("Form is valid");
      try {
        console.log(featureKey.value);
        featureConfig.value.type = featureKey.value;
        if (formType.value.toLowerCase() == "add") {
          await featureConfigs.create(featureConfig.value);
        } else {
          await featureConfigs.update(id.value, featureConfig.value);
        }
        ElMessage.success("操作成功！");
        setTimeout(() => {
          router.push({
            path: "/feature-config",
            query: { featureKey: featureKey.value },
          });
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
    form.value.resetFields();
    featureConfig.value = originalFeatureConfig.value;
  }
};
</script>

<style scoped></style>

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
          <FeatureConfigTemplateData
            :key="featureConfig.data"
            :templ="featureFlag.template"
            :item="featureConfig.data"
            @change="updateData"
          />
        </el-form-item>
        <el-form-item label="生效时间" prop="timeRange">
          <el-date-picker
            v-model="featureConfig.timeRange.startDate"
            autocomplete="off"
            placeholder="Select start date and time"
            value-format="YYYY-MM-DD HH:mm:ss"
            type="datetime"
          />
          -
          <el-date-picker
            v-model="featureConfig.timeRange.endDate"
            autocomplete="off"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="Select end date and time"
            type="datetime"
          />
        </el-form-item>
        <el-form-item label="埋点数据" prop="trackingData">
          <tracking-data-item
            v-for="(item, index) in trackingData"
            :key="item.index"
            :item="item"
            :index="index"
            @deleteItem="deleteTrackingData(index)"
          />
          <el-button type="primary" @click="addTrackingData">
            <el-icon>
              <Plus />
            </el-icon>
          </el-button>
        </el-form-item>
        <el-form-item
          label="白名单"
          prop="customerCriteriaCondition.whiteListCondition
                .whiteList"
        >
          <WhiteListCondition
            :key="
              featureConfig.customerCriteriaCondition.whiteListCondition
                .whiteList
            "
            :white-list="
              featureConfig.customerCriteriaCondition.whiteListCondition
                .whiteList
            "
            @change="updateWhiteList"
          />
        </el-form-item>
        <el-form-item label="地理位置">
          <LocationCondition
            ref="locationCondition"
            :item="
              featureConfig.customerCriteriaCondition.locationCondition.adCodes
            "
            @change="updateLocation"
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
import { ElButton, ElForm, ElFormItem, ElInput, ElMessage } from "element-plus";
import { FeatureConfig, FeatureConfigs } from "../../domain/FeatureConfigs";
import { FeatureFlag, getDefaultValue } from "../../domain/FeatureFlags";
import TrackingDataItem from "../../components/TrackingDataItem.vue";
import FeatureConfigTemplateData from "../../components/FeatureConfigTemplateData.vue";
import WhiteListCondition from "../../components/WhiteListCondition.vue";
import LocationCondition from "../../components/LocationCondition.vue";

const route = useRoute();
const router = useRouter();
const featureConfigs = new FeatureConfigs();
const featureConfig = ref({} as FeatureConfig);
const featureFlag = ref({} as FeatureFlag);

const title = ref("Feature Config");
const formType = ref("add");
const originalFeatureConfig = ref({} as FeatureConfig);
const featureKey = ref("");
const id = ref("");
const trackingData = ref([] as any[]);
let trackingDataIndex = 0;
const locationCondition = ref({} as any);
onBeforeMount(async () => {
  title.value =
    (route.query.type == "edit" ? "编辑" : "新增") + "Feature Config";
  formType.value = route.query.type == "edit" ? "edit" : "add";
  featureKey.value = "" + route.query.featureKey;

  const featureFlagString = decodeURI(route.query.featureFlag as string);
  featureFlag.value = JSON.parse(featureFlagString) as FeatureFlag;

  featureConfig.value = {
    timeRange: {},
    trackingData: [],
    data: getDefaultValue(featureFlag.value),
    customerCriteriaCondition: {
      whiteListCondition: { whiteList: [] as string[] },
      locationCondition: { adCodes: [] as string[] },
    },
  } as FeatureConfig;

  if (route.query.id) {
    const res = await featureConfigs.fetchById(route.query.id.toString());
    id.value = res.id as string;
    featureConfig.value = res;
    console.log(JSON.stringify(res));
    if (!featureConfig.value.customerCriteriaCondition) {
      featureConfig.value.customerCriteriaCondition = {};
    }

    if (!featureConfig.value.customerCriteriaCondition.whiteListCondition) {
      featureConfig.value.customerCriteriaCondition.whiteListCondition = {
        whiteList: [],
      };
    }

    if (!featureConfig.value.customerCriteriaCondition.locationCondition) {
      featureConfig.value.customerCriteriaCondition.locationCondition = {
        adCodes: [],
      };
    }

    originalFeatureConfig.value = featureConfig.value;
    locationCondition.value.item =
      featureConfig.value.customerCriteriaCondition.locationCondition.adCodes;
  }

  trackingData.value = featureConfig.value.trackingData
    ? Object.keys(featureConfig.value.trackingData).map((key) => {
        return {
          index: trackingDataIndex++,
          key: key,
          value: featureConfig.value.trackingData[key],
        };
      })
    : [];
});

const formRules = {
  title: [{ required: true, message: "Please enter title", trigger: "submit" }],
  data: [{ required: true, message: "Please enter data", trigger: "submit" }],
};

const form = ref<ElForm>(null);

const deleteTrackingData = (index: number) => {
  trackingData.value.splice(index, 1);
};

const addTrackingData = () => {
  trackingData.value.push({
    index: trackingDataIndex++,
    key: "",
    value: "",
  });
};

const updateData = (key: string, value: any) =>
  (featureConfig.value.data = value);

const updateWhiteList = (whiteList: string[]) => {
  if (!featureConfig.value.customerCriteriaCondition) {
    featureConfig.value.customerCriteriaCondition = {};
  }

  if (!featureConfig.value.customerCriteriaCondition.whiteListCondition) {
    featureConfig.value.customerCriteriaCondition.whiteListCondition = {
      whiteList: whiteList || [],
    };
  }

  featureConfig.value.customerCriteriaCondition.whiteListCondition.whiteList =
    whiteList || [];
};

const updateLocation = (value: string[]) => {
  if (!featureConfig.value.customerCriteriaCondition) {
    featureConfig.value.customerCriteriaCondition = {};
  }

  if (!featureConfig.value.customerCriteriaCondition.locationCondition) {
    featureConfig.value.customerCriteriaCondition.locationCondition = {
      adCodes: value || [],
    };
  }

  featureConfig.value.customerCriteriaCondition.locationCondition.adCodes =
    value || [];
};

const submitForm = async () => {
  form?.value?.validate(async (valid: boolean) => {
    if (valid) {
      try {
        featureConfig.value.type = featureKey.value;
        featureConfig.value.trackingData = trackingData.value.reduce(
          (acc, cur) => {
            acc[cur.key] = cur.value;
            return acc;
          },
          {} as any
        );
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
    featureConfig.value = originalFeatureConfig.value;
  }
};
</script>

<style scoped></style>

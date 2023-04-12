<template>
  <div>
    <el-card>
      <el-card-header style="padding: 5px">
        <div class="card-header">
          <span class="title"> FeatureKey: {{ type }}</span>
          <el-button size="mini" style="float: right" @click="router.go(-1)"
            >返回
          </el-button>
        </div>
      </el-card-header>
      <el-divider />
      <el-input
        v-model="searchForm.key"
        clearable
        placeholder="请输入configKey或名称"
        class="input-with-select"
        @clear="searchData"
      >
        <template #append>
          <el-button icon="Search" @click="searchData" />
        </template>
      </el-input>
      <el-button
        type="primary"
        @click="
          () =>
            router.push({
              path: '/feature-config/edit',
              query: { type: 'add', featureKey: type },
            })
        "
      >
        <el-icon><Plus /></el-icon>新增
      </el-button>
      <el-table :data="tableData" border style="width: 100%; margin-top: 50px">
        <el-table-column label="id" prop="id" width="320" />
        <el-table-column prop="type" label="Feature Key" width="180" />
        <el-table-column prop="title" label="配置标题" width="180" />
        <el-table-column prop="status" label="状态" width="180" />
        <el-table-column label="操作">
          <template #default="scope">
            <el-button
              v-if="scope.row?.staticStatus !== 'PUBLISHED'"
              type="warning"
              size="small"
              @click="publish(scope.row.id)"
              >发布</el-button
            >
            <el-button
              v-if="scope.row?.staticStatus === 'PUBLISHED'"
              type="warning"
              size="small"
              @click="disable(scope.row.id)"
              >撤销</el-button
            >
            <el-button
              type="danger"
              size="small"
              @click="deleteData(scope.row.id)"
              >删除</el-button
            >

            <el-button
              size="small"
              @click="
                () =>
                  router.push({
                    path: '/feature-config/edit',
                    query: { featureKey: type, type: 'edit', id: scope.row.id },
                  })
              "
            >
              编辑
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页 -->
      <el-pagination
        style="margin-top: 20px"
        :current-page="searchForm.page"
        :page-size="searchForm.size"
        :page-sizes="[10, 20, 30, 40]"
        layout="->,total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onBeforeMount, onMounted, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { useRoute } from "vue-router";

import { FeatureConfig, FeatureConfigs } from "../../domain/FeatureConfigs";
import { ElMessage } from "element-plus";

const route = useRoute();

const type = ref();
onBeforeMount(() => {
  console.log("onBeforeMount");
  type.value = route.query.featureKey;
});

const router = useRouter();

const featureConfigs = new FeatureConfigs();
const total = ref(0);

const searchForm = reactive({
  page: 1,
  size: 10,
  key: "",
});

const tableData = ref([] as FeatureConfig[]);

const deleteData = (id: string) => {
  console.log("deleteUser");
  featureConfigs
    .delete(id)
    .then(() => {
      ElMessage.success("删除成功");
      searchData();
    })
    .catch((err) => {
      console.log(err);
      ElMessage.error("删除失败，请重试");
    });
};

const searchData = () => {
  console.log("searchData");
  featureConfigs
    .fetchAll(
      type.value,
      searchForm.page > 1 ? searchForm.page - 1 : 0,
      searchForm.size,
      searchForm.key
    )
    .then((res) => {
      tableData.value = res.content;
      total.value = res.total;
      searchForm.page = res.page + 1;
    })
    .catch((err) => {
      console.log(err);
      ElMessage.error("获取数据失败，请重试");
    });
};

const publish = (featureKey: string) => {
  console.log("publish");
  featureConfigs
    .publish(featureKey)
    .then(() => {
      ElMessage.success("发布成功");
      searchData();
    })
    .catch((err) => {
      console.log(err);
      ElMessage.error("发布失败，请重试");
    });
};

const disable = (featureKey: string) => {
  console.log("publish");
  featureConfigs
    .disable(featureKey)
    .then(() => {
      ElMessage.success("操作成功");
      searchData();
    })
    .catch((err) => {
      console.log(err);
      ElMessage.error("操作失败，请重试");
    });
};

const handleSizeChange = () => {
  console.log("handleSizeChange");
  searchData();
};

const handleCurrentChange = () => {
  console.log("handleCurrentChange");
  searchData();
};

onMounted(() => {
  searchData();
});
</script>

<style scoped>
.input-with-select {
  width: 400px;
  float: left;
}

span.title {
  float: left;
  font-size: 15px;
  font-family: "Arial Black";
}
</style>

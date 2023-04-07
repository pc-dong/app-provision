<template>
  <div
    ref="tableWrapper"
    style="height: 100%"
  >
    <div class="toolbar">
      <el-button
        class="add-btn"
        type="default"
        @click="handleAdd"
      >
        <el-icon>
          <Plus />
        </el-icon>
        新增
      </el-button>
    </div>

    <el-table
      :data="tableData"
      :height="tableHeight"
      style="width: 100%"
    >
      <el-table-column
        prop="featureKey"
        label="Feature Key"
      >
        <template #default="{ row }">
          <router-link to="/config">
            {{ row.featureKey }}
          </router-link>
        </template>
      </el-table-column>
      <el-table-column
        prop="description.name"
        label="名称"
      />
      <el-table-column
        prop="description.status"
        label="状态"
      />
      <el-table-column
        label="操作"
      >
        <template #default="{ row }">
          <el-button
            type="primary"
            size="small"
            @click="handleEdit(row)"
          >
            编辑
          </el-button>
          <el-button
            type="success"
            v-if="row?.description?.status != 'PUBLISHED'"
            size="small"
            @click="handlePublish(row)"
          >
            发布
          </el-button>
          <el-button
            type="warning"
            v-if="row?.description?.status == 'PUBLISHED'"
            size="small"
            @click="handleRevoke(row)"
          >
            撤销
          </el-button>
          <el-button
            type="danger"
            size="small"
            @click="handleDelete(row)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      :total="total"
      :current-page="featureFlagStore.state.page"
      :page-size="featureFlagStore.state.pageSize"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      layout="total, sizes, prev, pager, next, jumper"
      :pager-count="7"
    />
    <feature-flag-form
      ref="featureFlagForm"
      @formSubmitted="handleFormSubmitted"
    />
    <!--    <el-dialog v-model="dialogFormVisible" title="Feature Flag">-->
    <!--      <el-form :model="featureFlagStore.state.addForm">-->
    <!--        <el-form-item-->
    <!--            label="Feature Key"-->
    <!--            :label-width="formLabelWidth">-->
    <!--          <el-input-->
    <!--              v-model="featureFlagStore.state.addForm.featureKey"-->
    <!--              autocomplete="off"-->
    <!--          />-->
    <!--        </el-form-item>-->
    <!--        <el-form-item-->
    <!--            label="Feature Name"-->
    <!--            :label-width="formLabelWidth"-->
    <!--        >-->
    <!--          <el-input-->
    <!--              v-model="featureFlagStore.state.addForm.description.name"-->
    <!--              autocomplete="off"-->
    <!--          />-->
    <!--        </el-form-item>-->
    <!--        <el-form-item-->
    <!--            label="Feature Description"-->
    <!--            :label-width="formLabelWidth"-->
    <!--        >-->
    <!--          <el-input-->
    <!--              v-model="featureFlagStore.state.addForm.description.description"-->
    <!--              autocomplete="off"-->
    <!--          />-->
    <!--        </el-form-item>-->
    <!--        <el-form-item-->
    <!--            label="Data Type"-->
    <!--            :label-width="formLabelWidth"-->
    <!--        >-->
    <!--          <el-select-->
    <!--              v-model="featureFlagStore.state.addForm.description.dataType"-->
    <!--              placeholder="Please select a zone"-->
    <!--          >-->
    <!--            <el-option-->
    <!--                label="BOOLEAN"-->
    <!--                value="BOOLEAN"-->
    <!--            />-->
    <!--            <el-option-->
    <!--                label="STRING"-->
    <!--                value="STRING"-->
    <!--            />-->
    <!--            <el-option-->
    <!--                label="NUMBER"-->
    <!--                value="NUMBER"-->
    <!--            />-->
    <!--            <el-option-->
    <!--                label="JSON STRING"-->
    <!--                value="JSON_STRING"-->
    <!--            />-->
    <!--            <el-option-->
    <!--                label="JSON"-->
    <!--                value="JSON"-->
    <!--            />-->
    <!--          </el-select>-->
    <!--        </el-form-item>-->
    <!--        <el-form-item-->
    <!--            label="Default Value"-->
    <!--            :label-width="formLabelWidth"-->
    <!--        >-->
    <!--          <el-input-->
    <!--              v-model="featureFlagStore.state.addForm.description.defaultValue"-->
    <!--              autocomplete="off"-->
    <!--          />-->
    <!--        </el-form-item>-->
    <!--      </el-form>-->
    <!--      <template #footer>-->
    <!--        <span class="dialog-footer">-->
    <!--          <el-button @click="dialogFormVisible = false">Cancel</el-button>-->
    <!--          <el-button-->
    <!--              type="primary"-->
    <!--              @click="handleSubmit"-->
    <!--          >-->
    <!--            Confirm-->
    <!--          </el-button>-->
    <!--        </span>-->
    <!--      </template>-->
    <!--    </el-dialog>-->
  </div>
</template>

<script lang="tsx">
export default {
  name: "FeatureFlag",
  inheritAttrs: false,
  customOptions: {}
}
</script>

<script lang="tsx" setup>
import {ref, onMounted, computed} from 'vue';
import {ElTable, ElTableColumn, ElButton, ElPagination} from 'element-plus';
import {Plus} from '@element-plus/icons-vue'
import {useStore} from "vuex";
import FeatureFlagForm from "@/components/FeatureFlagForm.vue";

const featureFlagStore = useStore();
// 定义表格高度
const tableWrapper = ref();
const tableHeight = ref(0);

// 定义表格数据和分页信息
const tableData = computed(() => featureFlagStore.state.featureFlags);
const total = computed(() => featureFlagStore.state.total);
// 从 http 接口获取表格数据

const fetchData = () => {
  try {
    console.log("fetchData()")
    featureFlagStore.dispatch('fetchFeatureFlags');
  } catch (error) {
    console.error(error);
  }
};
// const dialogFormVisible = ref(false)
const featureFlagForm = ref()

// 处理表格容器尺寸变化
const handleResize = () => {
  if (tableWrapper.value) {
    const wrapperHeight = tableWrapper.value?.height;
    const paginationHeight = 40; // 分页组件的高度
    tableHeight.value = wrapperHeight - paginationHeight;
  }
};

// 处理分页组件的尺寸和页码改变
const handleSizeChange = () => {
  fetchData();
};

const handleCurrentChange = () => {
  fetchData();
};

const formFlag = ref("ADD")
// 处理表格操作按钮点击事件
const handleEdit = (row) => {
  featureFlagForm.value.formType = 'EDIT'
  featureFlagForm.value.formData = row
  featureFlagForm.value.dialogFormVisible = true
};

const handlePublish = async (row) => {
  console.log('发布', row);
  try {
    console.log("fetchData()")
    await featureFlagStore.dispatch('publishFeatureFlag', row.featureKey);
    await fetchData()
  } catch (error) {
    console.error(error);
  }
};

const handleRevoke = async (row) => {
  console.log('撤销', row);
  try {
    console.log("fetchData()")
    await featureFlagStore.dispatch('disableFeatureFlag', row.featureKey);
    await fetchData()
  } catch (error) {
    console.error(error);
  }
};

const handleDelete = async (row) => {
  console.log('删除', row);
  try {
    console.log("fetchData()")
    await featureFlagStore.dispatch('deleteFeatureFlag', row.featureKey);
    await fetchData()
  } catch (error) {
    console.error(error);
  }
};

const handleAdd = () => {
  featureFlagForm.value.formType = 'ADD'
  featureFlagForm.value.dialogFormVisible = true
  featureFlagForm.value.resetForm();
  // featureFlagStore.commit('resetAddForm')
  console.log('新增');
};

const handleFormSubmitted = () => {
  featureFlagForm.value.dialogFormVisible = false
  fetchData();
}

onMounted(() => {
  tableWrapper.value.height = window.innerHeight - 200
  fetchData();
  window.addEventListener('resize', handleResize);
  handleResize();
});
</script>

<style>
.toolbar {
  text-align: right;
  margin-bottom: 10px;
  margin-top: 10px;
}

.add-btn {
  min-width: 100px;
}
</style>
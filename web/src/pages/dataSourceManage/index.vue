<template>
  <div class="content">
    <a-row :gutter="[16, 16]">
      <a-col :span="24">
        <TopLine
          @create="handleCreate"
          :loading="loading"
          :sourceTypeList="sourceTypeList"
          @search="handleSearch"
        />
      </a-col>
      <a-col :span="24"><div class="line"></div></a-col>
      <a-col :span="24">
        <a-table
          :columns="columns"
          :data-source="dataSourceList"
          :loading="loading"
          rowKey="id"
        >
          <template #tags="{ text: tags }">
            <span>
              <a-tag
                v-if="tags"
                v-for="tag in tags.split(',')"
                :key="tag"
                :color="
                  tag === 'loser'
                    ? 'volcano'
                    : tag.length > 5
                    ? 'geekblue'
                    : 'green'
                "
              >
                {{ tag.toUpperCase() }}
              </a-tag>
            </span>
          </template>
          <template #status="{ text: expire }">
            <span> {{ expire ? "已过期" : "可用" }} </span>
          </template>
          <template #dataSourceTypeId="{ text: dataSourceTypeId }">
            <span>
              {{
                (
                  sourceTypeList.find(
                    (item) => item.id == dataSourceTypeId
                  ) || { option: "无" }
                ).name
              }}
            </span>
          </template>
          <template #action="row">
            <a-space>
              <a-button
                size="small"
                @click="handleEdit(row.text.id, row.text.dataSourceTypeId)"
                type="link"
                >{{
                  $t("dataSource.table.list.columns.actions.editButton")
                }}</a-button
              >
              <a-button
                size="small"
                v-show="!row.text.expire"
                type="link"
                @click="handleExpire(row.text.id)"
                >{{
                  $t("dataSource.table.list.columns.actions.expireButton")
                }}</a-button
              >
              <a-button size="small" @click="handleTestConnect(row)" type="link">{{
                $t("dataSource.table.list.columns.actions.testConnectButton")
              }}</a-button>
              <a-button size="small" @click="handleDelete(row)" type="link">{{
                $t("dataSource.table.list.columns.actions.deleteButton")
              }}</a-button>
            </a-space>
          </template>
          <template #version="{ text }">
            <a-button
              size="small"
              @click="handleOpenVersionModal(text.id)"
              type="link"
              >{{ text.versionId }}</a-button
            >
          </template>
          <template #modifyTime="{ text }">
            {{ text && dateFormat(text) }}
          </template>
        </a-table>
      </a-col>
    </a-row>
    <select-type-modal
      @select="handleSelectType"
      v-model:visible="selectTypeModalVisible"
      :sourceTypeList="sourceTypeList"
    />
    <version-modal
      v-model:visible="versionModalCfg.visible"
      :id="versionModalCfg.id"
    />
    <edit-modal
      v-model:visible="modalCfg.visible"
      :id="modalCfg.id"
      :type="modalCfg.type"
      :mode="modalCfg.mode"
      @finish="handleModalFinish"
    />
  </div>
</template>

<script>
import TopLine from "./components/topLine.vue";
import SelectTypeModal from "./components/selectTypeModal.vue";
import EditModal from "./components/editModal.vue";
import VersionModal from "./components/versionModal.vue";
import { useI18n } from "@fesjs/fes";
import { computed } from "vue";
import {
  getDataSourceList,
  deleteDataSource,
  expireDataSource,
  getDataSourceTypes,
  testDataSourceConnect,
} from "@/common/service";
import { message } from "ant-design-vue";
import { dateFormat } from "@/common/utils";

const data = [];
export default {
  components: {
    TopLine,
    SelectTypeModal,
    EditModal,
    VersionModal,
  },
  setup() {
    const { t } = useI18n({ useScope: "global" });
    let columns = computed(() => [
      {
        title: t("dataSource.table.list.columns.title.name"),
        dataIndex: "name",
        align: "center",
      },
      {
        title: t("dataSource.table.list.columns.title.type"),
        dataIndex: "dataSourceTypeId",
        align: "center",
        slots: { customRender: "dataSourceTypeId" },
      },
      {
        title: t("dataSource.table.list.columns.title.status"),
        dataIndex: "expire",
        align: "center",
        slots: { customRender: "status" },
      },
      {
        title: t("dataSource.table.list.columns.title.tags"),
        align: "center",
        dataIndex: "labels",
        slots: { customRender: "tags" },
      },
      {
        title: t("dataSource.table.list.columns.title.version"),
        align: "center",
        slots: { customRender: "version" },
      },
      {
        title: t("dataSource.table.list.columns.title.describe"),
        align: "center",
        dataIndex: "desc",
      },
      {
        title: t("dataSource.table.list.columns.title.updatetim"),
        align: "center",
        dataIndex: "modifyTime",
        slots: { customRender: "modifyTime" },
      },
      {
        title: t("dataSource.table.list.columns.title.creator"),
        align: "center",
        dataIndex: "createUser",
      },
      {
        title: t("dataSource.table.list.columns.title.updater"),
        align: "center",
        dataIndex: "modifyUser",
      },
      {
        title: t("dataSource.table.list.columns.title.action"),
        align: "center",
        slots: { customRender: "action" },
      },
    ]);
    return {
      columns,
    };
  },

  data() {
    return {
      // 时间格式化
      dateFormat,
      // 数据源列表
      dataSourceList: data,
      // 数据源类型选择弹窗
      selectTypeModalVisible: false,
      // 是否加载中
      loading: false,
      // 数据源类型列表
      sourceTypeList: [],
      // 编辑弹框
      modalCfg: {
        mode: "",
        id: "",
        type: "",
        visible: false,
      },
      // 版本弹窗
      versionModalCfg: {
        id: "",
        visible: false,
      },
    };
  },
  methods: {
    // 处理过期
    async handleExpire(id) {
      await expireDataSource(id);
      message.success("操作成功");
      this.getDataSourceList();
    },
    // 处理搜索
    handleSearch(data) {
      this.getDataSourceList(data);
    },
    // 打开版本弹框
    handleOpenVersionModal(id) {
      this.versionModalCfg = {
        visible: true,
        id: id,
      };
    },
    // 处理选择类型
    handleSelectType(item) {
      this.selectTypeModalVisible = false;
      this.modalCfg = {
        mode: "create",
        id: "",
        type: item.id,
        visible: true,
      };
    },
    // 处理删除
    async handleDelete(row) {
      await deleteDataSource(row.text.id);
      message.success("删除成功");
      this.getDataSourceList();
    },
    // 处理编辑
    handleEdit(id, typeid) {
      this.modalCfg = {
        mode: "edit",
        id: id,
        type: typeid,
        visible: true,
      };
    },
    // 打开创建弹窗
    handleCreate() {
      this.selectTypeModalVisible = true;
    },
    // 弹框操作完成
    handleModalFinish() {
      this.getDataSourceList();
    },
    // 测试链接
    async handleTestConnect(row) {
      await testDataSourceConnect(row.text.id, row.text.versionId);
      message.success("连接成功");
    },
    // 获取列表数据
    async getDataSourceList(data) {
      this.loading = true;
      let { list } = await getDataSourceList(data);
      this.loading = false;
      this.dataSourceList = list;
    },
    // 获取数据源类型
    async getTypeData() {
      let { list } = await getDataSourceTypes();
      this.sourceTypeList = list;
    },
  },
  mounted() {
    this.getDataSourceList();
    this.getTypeData();
  },
};
</script>
<style scoped lang="less">
.content {
  padding: 16px 24px;
  box-sizing: border-box;
  background: #fff;
  min-height: calc(100vh - 48px);
}
.top-line {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.line {
  height: 1px;
  width: calc(100% + 48px);
  background-color: #dee4ec;
  margin-left: -24px;
}
</style>

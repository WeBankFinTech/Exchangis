<template>
  <div class="content">
    <a-row :gutter="[16, 16]">
      <a-col :span="24"> <TopLine @create="handleCreate" /> </a-col>
      <a-col :span="24">
        <a-table :columns="columns" :data-source="dataSourceListData" rowKey="id">
          <template #tags="{ text: tags }">
            <span>
              <a-tag v-for="tag in tags" :key="tag" :color="tag === 'loser' ? 'volcano' : tag.length > 5 ? 'geekblue' : 'green'">
                {{ tag.toUpperCase() }}
              </a-tag>
            </span>
          </template>
          <template #action="row">
            <a-space>
              <a-button size="small" @click="handleEdit">{{ $t("dataSource.table.list.columns.actions.editButton") }}</a-button>
              <a-button size="small">{{ $t("dataSource.table.list.columns.actions.expireButton") }}</a-button>
              <a-button size="small">{{ $t("dataSource.table.list.columns.actions.testConnectButton") }}</a-button>
              <a-button size="small" @click="handleDelete(row)">{{ $t("dataSource.table.list.columns.actions.deleteButton") }}</a-button>
            </a-space>
          </template>
          <template #version>
            <a-button size="small" @click="handleOpenVersionModal" type="link">V0002</a-button>
          </template>
        </a-table>
      </a-col>
    </a-row>
    <select-type-modal @select="handleSelectType" v-model:visible="selectTypeModalVisible" />
    <version-modal v-model:visible="versionModalVisible" />
    <edit-modal v-model:visible="modalCfg.visible" :id="modalCfg.id" :type="modalCfg.type" :mode="modalCfg.mode" @finish="handleModalFinish" />
  </div>
</template>

<script>
import TopLine from "./components/top_line.vue";
import SelectTypeModal from "./components/select_type_modal.vue";
import EditModal from "./components/edit_modal.vue";
import VersionModal from "./components/version_modal.vue";
import { useI18n } from "@fesjs/fes";
import { computed } from "vue";
import { getDataSourceList, deleteDataSource } from "@/common/service";
import { message } from "ant-design-vue";

const data = [
  {
    name: "数据源名称",
    type: "MySql",
    updater: "admin",
    creator: "admin",
    describe: "这是一个数据源",
    tags: ["nice", "developer"],
    updatetim: "2020-2-17 10:00",
    status: "可用",
    power: "只读数据源",
    colony: "BDP测试",
    id: "1",
  },
];
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
        dataIndex: "type",
        align: "center",
      },
      {
        title: t("dataSource.table.list.columns.title.colony"),
        dataIndex: "colony",
        align: "center",
      },
      {
        title: t("dataSource.table.list.columns.title.status"),
        align: "center",
        dataIndex: "status",
      },
      {
        title: t("dataSource.table.list.columns.title.power"),
        align: "center",
        dataIndex: "power",
      },
      {
        title: t("dataSource.table.list.columns.title.tags"),
        align: "center",
        dataIndex: "tags",
        slots: { customRender: "tags" },
      },
      {
        title: t("dataSource.table.list.columns.title.version"),
        align: "center",
        dataIndex: "version",
        slots: { customRender: "version" },
      },
      {
        title: t("dataSource.table.list.columns.title.describe"),
        align: "center",
        dataIndex: "describe",
      },
      {
        title: t("dataSource.table.list.columns.title.updatetim"),
        align: "center",
        dataIndex: "updatetim",
      },
      {
        title: t("dataSource.table.list.columns.title.creator"),
        align: "center",
        dataIndex: "creator",
      },
      {
        title: t("dataSource.table.list.columns.title.updater"),
        align: "center",
        dataIndex: "updater",
      },
      {
        title: t("dataSource.table.list.columns.title.action"),
        align: "center",
        slots: { customRender: "action" },
      },
    ]);
    return { columns };
  },
  data() {
    return {
      dataSourceList: data,
      selectTypeModalVisible: false,
      versionModalVisible: false,
      loading: false,
      modalCfg: {
        mode: "",
        id: "",
        type: "",
        visible: false,
      },
      pageCfg: {
        current: 1,
        pageSize: 11,
      },
    };
  },
  methods: {
    // 打开版本弹框
    handleOpenVersionModal() {
      this.versionModalVisible = true;
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
      await deleteDataSource(row.record.id);
      message.success("成功");
    },
    // 处理编辑
    handleEdit() {
      this.modalCfg = {
        mode: "edit",
        id: "12321",
        type: "",
        visible: true,
      };
    },
    handleCreate() {
      this.selectTypeModalVisible = true;
    },
    handleModalFinish() {
      this.modalCfg.visible = false;
      this.getDataSourceList();
    },
    async getDataSourceList() {
      this.loading = true;
      let { list } = await getDataSourceList();
      this.loading = false;
      this.dataSourceList = list;
    },
  },
  computed: {
    dataSourceListData() {
      let strIndex = Math.max(this.pageCfg.current - 1, 0) * this.pageCfg.pageSize;
      return this.dataSourceList.slice(strIndex, strIndex + this.pageCfg.pageSize);
    },
  },
  mounted() {
    this.getDataSourceList();
  },
};
</script>
<style scoped lang="less">
.content {
  padding: 16px;
  box-sizing: border-box;
}
.top-line {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>

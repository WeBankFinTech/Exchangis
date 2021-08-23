<template>
  <div class="content">
    <a-row :gutter="[16, 16]">
      <a-col :span="24"> <TopLine @create="handleCreate" /> </a-col>
      <a-col :span="24">
        <a-table :columns="columns" :data-source="data">
          <template #tags="{ text: tags }">
            <span>
              <a-tag v-for="tag in tags" :key="tag" :color="tag === 'loser' ? 'volcano' : tag.length > 5 ? 'geekblue' : 'green'">
                {{ tag.toUpperCase() }}
              </a-tag>
            </span>
          </template>
          <template #action>
            <a-space>
              <a-button size="small" @click="handleEdit">{{ $t("dataSource.table.list.columns.actions.editButton") }}</a-button>
              <a-button size="small">{{ $t("dataSource.table.list.columns.actions.expireButton") }}</a-button>
              <a-button size="small">{{ $t("dataSource.table.list.columns.actions.testConnectButton") }}</a-button>
              <a-button size="small">{{ $t("dataSource.table.list.columns.actions.deleteButton") }}</a-button>
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
    <edit-modal v-model:visible="modalCfg.visible" />
  </div>
</template>

<script>
import TopLine from "./components/top_line.vue";
import SelectTypeModal from "./components/select_type_modal.vue";
import EditModal from "./components/edit_modal.vue";
import VersionModal from "./components/version_modal.vue";

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
  },
];
export default {
  components: {
    TopLine,
    SelectTypeModal,
    EditModal,
    VersionModal,
  },

  data() {
    return {
      data,
      columns: [
        {
          title: this.$t("dataSource.table.list.columns.title.name"),
          dataIndex: "name",
          align: "center",
          key: "name",
        },
        {
          title: this.$t("dataSource.table.list.columns.title.type"),
          dataIndex: "type",
          align: "center",
          key: "type",
        },
        {
          title: this.$t("dataSource.table.list.columns.title.colony"),
          dataIndex: "colony",
          align: "center",
          key: "colony",
        },
        {
          title: this.$t("dataSource.table.list.columns.title.status"),
          key: "status",
          align: "center",
          dataIndex: "status",
        },
        {
          title: this.$t("dataSource.table.list.columns.title.power"),
          align: "center",
          key: "power",
          dataIndex: "power",
        },
        {
          title: this.$t("dataSource.table.list.columns.title.tags"),
          align: "center",
          key: "tags",
          dataIndex: "tags",
          slots: { customRender: "tags" },
        },
        {
          title: this.$t("dataSource.table.list.columns.title.version"),
          align: "center",
          dataIndex: "version",
          key: "version",
          slots: { customRender: "version" },
        },
        {
          title: this.$t("dataSource.table.list.columns.title.describe"),

          align: "center",
          key: "describe",
          dataIndex: "describe",
        },
        {
          title: this.$t("dataSource.table.list.columns.title.updatetim"),

          align: "center",
          key: "updatetim",
          dataIndex: "updatetim",
        },
        {
          title: this.$t("dataSource.table.list.columns.title.creator"),

          align: "center",
          key: "creator",
          dataIndex: "creator",
        },
        {
          title: this.$t("dataSource.table.list.columns.title.updater"),
          align: "center",
          key: "updater",
          dataIndex: "updater",
        },
        {
          title: this.$t("dataSource.table.list.columns.title.action"),
          align: "center",
          key: "action",
          slots: { customRender: "action" },
        },
      ],
      selectTypeModalVisible: false,
      versionModalVisible: false,
      modalCfg: {
        mode: "",
        id: "",
        visible: false,
      },
    };
  },
  methods: {
    handleOpenVersionModal() {
      this.versionModalVisible = true;
    },
    handleSelectType(val) {
      this.selectTypeModalVisible = false;
      this.modalCfg = {
        mode: "create",
        id: "",
        visible: true,
      };
    },
    handleEdit() {
      this.modalCfg = {
        mode: "edit",
        id: "12321",
        visible: true,
      };
    },
    handleCreate() {
      this.selectTypeModalVisible = true;
    },
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

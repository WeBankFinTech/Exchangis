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
              <a-button size="small" @click="handleEdit">编辑</a-button>
              <a-button size="small">过期</a-button>
              <a-button size="small">测试连接</a-button>
            </a-space>
          </template>
          <template #version>
            <a-button size="small" @click="handleOpenVersionModal" type="link">V0002</a-button>
          </template>
        </a-table>
      </a-col>
    </a-row>
    <select-type-modal @select="handleSelectType" v-model:visible="selectTypeModalVisible" />
    <version-modal v-model:visible="VersionModalVisible" />
    <edit-modal v-model:visible="modalCfg.visible" />
  </div>
</template>

<script>
import TopLine from "./components/top_line.vue";
import SelectTypeModal from "./components/select_type_modal.vue";
import EditModal from "./components/edit_modal.vue";
import VersionModal from "./components/version_modal.vue";
const columns = [
  {
    title: "数据源名称",
    dataIndex: "name",
    align: "center",
    key: "name",
  },
  {
    title: "数据源类型",
    dataIndex: "type",
    align: "center",
    key: "type",
  },
  {
    title: "可用集群",
    dataIndex: "colony",
    align: "center",
    key: "colony",
  },
  {
    title: "状态",
    key: "status",
    align: "center",
    dataIndex: "status",
  },
  {
    title: "权限设置",
    align: "center",
    key: "power",
    dataIndex: "power",
  },
  {
    title: "标签",
    align: "center",
    key: "tags",
    dataIndex: "tags",
    slots: { customRender: "tags" },
  },
  {
    title: "版本",
    align: "center",
    dataIndex: "version",
    key: "action",
    slots: { customRender: "version" },
  },
  {
    title: "描述",
    align: "center",
    key: "describe",
    dataIndex: "describe",
  },
  {
    title: "修后修改时间",
    align: "center",
    key: "updatetim",
    dataIndex: "updatetim",
  },
  {
    title: "创建者",
    align: "center",
    key: "creator",
    dataIndex: "creator",
  },
  {
    title: "最后修改者",
    align: "center",
    key: "updater",
    dataIndex: "updater",
  },
  {
    title: "操作",
    align: "center",
    key: "action",
    slots: { customRender: "action" },
  },
];

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
      columns,
      selectTypeModalVisible: false,
      VersionModalVisible: false,
      modalCfg: {
        mode: "",
        id: "",
        visible: false,
      },
    };
  },
  methods: {
    handleOpenVersionModal() {
      this.VersionModalVisible = true;
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

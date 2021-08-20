<template>
  <div class="content">
    <a-row :gutter="[16, 16]">
      <!-- 搜索行 -->
      <a-col :span="24">
        <div class="title-line">
          <span class="title">
            <a-title style="margin-bottom: 0" :level="4">项目列表</a-title>
          </span>
          <a-input-search placeholder="输入项目名搜索" style="width: 200px" @search="handleOnSearch" />
        </div>
      </a-col>
      <!-- 创建卡片 -->
      <a-col :span="6"> <project-create-card @action="handleCreateCardAction" /> </a-col>
      <!-- 视图卡片 -->
      <a-col :span="6" v-for="item in projectList" :key="item.id">
        <project-view-card @delete="handleOnDelteProject" @edit="handleOnEditProject" :name="item.name" :describe="item.describe" :id="item.id" :tags="item.tags" :icon="item.icon" />
      </a-col>
      <!-- 分页行 -->
      <a-col :span="24">
        <div class="pagination-line">
          <a-pagination :total="50" show-less-items />
        </div>
      </a-col>
    </a-row>
    <!-- 创建表单 -->
    <edit-modal v-model:visible="modalCfg.visible" :mode="modalCfg.mode" :id="modalCfg.id" @finish="handleModalFinish" />
  </div>
</template>

<script>
import { useI18n } from "@fesjs/fes";
import { ApartmentOutlined, PlusOutlined } from "@ant-design/icons-vue";
import ProjectCreateCard from "./components/project_create_card.vue";
import ProjectViewCard from "./components/project_view_card.vue";
import EditModal from "./components/edit_modal.vue";
import { getProjectList } from "@/common/service";
export default {
  components: {
    ProjectViewCard,
    ProjectCreateCard,
    EditModal,
  },
  setup() {
    const { t } = useI18n();
    console.log(t);
    return { t };
  },
  data() {
    return {
      modalCfg: {
        mode: "",
        id: "",
        visible: false,
      },
      projectList: [],
    };
  },
  methods: {
    // 模态框操作完成
    handleModalFinish() {},
    // 新建卡片点击
    handleCreateCardAction() {
      this.modalCfg = {
        visible: true,
        mode: "create",
      };
    },
    // 处理搜索
    handleOnSearch(value) {
      alert(value);
    },
    // 删除项目
    handleOnDelteProject(id) {
      console.log(id);
    },
    // 编辑项目
    handleOnEditProject(id) {
      this.modalCfg = {
        visible: true,
        mode: "edit",
        id: id,
      };
    },
  },
  async mounted() {
    let { list } = await getProjectList();
    this.projectList = list.map((item) => ({
      id: item.id,
      name: item.name,
      describe: item.description,
      tags: item.tags.split(","),
    }));
  },
};
</script>
<style scoped lang="less">
.content {
  padding: 16px;
  box-sizing: border-box;
}
.title-line {
  display: flex;
  justify-content: space-between;
  align-items: center;
  .title {
    border-left: 6px solid #1890ff;
    padding-left: 6px;
  }
}
.pagination-line {
  display: flex;
  justify-content: flex-end;
}
</style>

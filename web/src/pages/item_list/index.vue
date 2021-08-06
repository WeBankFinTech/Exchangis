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
      <a-col :span="6"> <ItemCreateCard @action="handleCreateCardAction" /> </a-col>
      <!-- 视图卡片 -->
      <a-col :span="6" v-for="item in itemList" :key="item.id">
        <item-view-card @delete="handleOnDelteItem" @edit="handleOnEditItem" :name="item.name" :describe="item.describe" :id="item.id" :tags="item.tags" :icon="item.icon" />
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
import { Card, Tag, Row, Col, Menu, Dropdown, MenuItem, Pagination, Modal, InputSearch, TypographyTitle } from "ant-design-vue";
import { ApartmentOutlined, PlusOutlined } from "@ant-design/icons-vue";
import ItemCreateCard from "./components/item_create_card.vue";
import ItemViewCard from "./components/item_view_card.vue";
import EditModal from "./components/edit_modal.vue";
export default {
  components: {
    aRow: Row,
    aCol: Col,
    aPagination: Pagination,
    aInputSearch: InputSearch,
    aTitle: TypographyTitle,
    ItemViewCard,
    ItemCreateCard,
    EditModal,
  },
  data() {
    return {
      modalCfg: {
        mode: "",
        id: "",
        visible: false,
      },
      itemList: [
        {
          id: "1",
          name: "测试项目1",
          icon: null,
          describe: "我是项目描述",
          tags: ["标签1", "标签2"],
        },
        {
          id: "2",
          name: "测试项目2",
          icon: null,
          describe: "我是项目描述",
          tags: ["标签1", "标签2"],
        },
        {
          id: "3",
          name: "测试项目3",
          icon: null,
          describe: "我是项目描述我是项目描述我是项目描述我是项目描述我是项目描述我是项目描述我是项目描述",
          tags: ["标签1", "标签2"],
        },
      ],
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
    handleOnDelteItem(id) {
      console.log(id);
    },
    // 编辑项目
    handleOnEditItem(id) {
      this.modalCfg = {
        visible: true,
        mode: "edit",
        id: id,
      };
    },
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

<template>
  <a-card size="small" bordered>
    <template v-slot:title> <icon-apartment-outlined style="color: #1890ff" /> {{ name }} </template>
    <template v-slot:extra>
      <a-dropdown placement="bottomCenter" trigger="click">
        <a class="ant-dropdown-link"> 操作 </a>
        <template v-slot:overlay>
          <a-menu>
            <a-menu-item @click="$emit('edit', id)"> 编辑 </a-menu-item>
            <a-menu-item @click="$emit('delete', id)"> 删除 </a-menu-item>
          </a-menu>
        </template>
      </a-dropdown>
    </template>
    <div class="content-box">
      <p class="describe">{{ describe }}</p>
    </div>
    <div>
      <a-tag v-for="(tag, index) in tags" :key="index"> {{ tag }} </a-tag>
    </div>
  </a-card>
</template>

<script>
import { Card, Tag, Row, Col, Menu, Dropdown, MenuItem, Pagination, InputSearch } from "ant-design-vue";
import { ApartmentOutlined } from "@ant-design/icons-vue";
export default {
  name: "ItemViewCard",
  components: {
    aCard: Card,
    aTag: Tag,
    aDropdown: Dropdown,
    aMenu: Menu,
    aMenuItem: MenuItem,
    iconApartmentOutlined: ApartmentOutlined,
  },
  props: {
    id: {
      type: String,
      required: true,
    },
    // 图标
    icon: null,
    // 项目名称
    name: {
      type: String,
      required: true,
      default: "",
    },
    // 项目描述
    describe: {
      type: String,
      default: "",
    },
    // tag
    tags: {
      type: Array,
      default: [],
    },
  },
  emits: {
    // 删除
    delete: null,
    // 编辑
    edit: null,
  },
};
</script>

<style scoped lang="less">
.content-box {
  height: 90px;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  .describe {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}
</style>

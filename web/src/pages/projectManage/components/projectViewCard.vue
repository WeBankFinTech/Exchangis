<template>
  <a-card class="view-card" size="small" bordered>
    <template v-slot:title> <icon-apartment-outlined style="color: #1890ff" /> {{ name }} </template>
    <template v-slot:extra>
      <a-dropdown placement="bottomCenter" trigger="click">
        <a class="ant-dropdown-link"> {{ $t("projectManage.viewCard.actionTitle") }} </a>
        <template v-slot:overlay>
          <a-menu>
            <a-menu-item @click="$emit('edit', id)"> {{ $t("projectManage.viewCard.action.edit") }} </a-menu-item>
            <a-menu-item @click="$emit('delete', id)"> {{ $t("projectManage.viewCard.action.delete") }} </a-menu-item>
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
import { ApartmentOutlined } from "@ant-design/icons-vue";
export default {
  name: "ProjectViewCard",
  components: {
    iconApartmentOutlined: ApartmentOutlined,
  },
  props: {
    id: {
      type: String,
      required: true,
    },
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
  emits: ["delete", "edit"],
};
</script>

<style scoped lang="less">
.view-card {
  height: 180px;
}
.content-box {
  height: 100%;
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

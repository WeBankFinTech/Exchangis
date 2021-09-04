<template>
  <a-card class="create-card" @click="$emit('action')">
    <div class="card-body">
      <icon-plus-outlined class="icon" /> <span class="text">{{ $t("projectManage.createCardText") }}</span>
    </div>
  </a-card>
</template>

<script>
import { PlusOutlined } from "@ant-design/icons-vue";
export default {
  name: "ProjectCreateCard",
  components: {
    iconPlusOutlined: PlusOutlined,
  },
  emits: ["action"],
};
</script>

<style scoped lang="less">
.create-card {
  height: 180px;
  position: relative;
  cursor: pointer;
  box-sizing: border-box;
  border: 1px dashed #999999;
}
.create-card:hover {
  .icon {
    color: #1890ff;
  }
  .text {
    color: #1890ff;
  }
}
.card-body {
  position: absolute;
  left: 0;
  top: 0;
  right: 0;
  bottom: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  .icon {
    margin-right: 12px;
    font-size: 34px;
    color: #999999;
    transition: color 200ms;
  }
  .text {
    color: #999999;
    font-size: 26px;
    transition: color 200ms;
  }
}
</style>

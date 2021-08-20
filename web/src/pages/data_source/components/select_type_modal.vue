<template>
  <a-modal footer="" :visible="visible" title="请选择数据源类型" @cancel="$emit('update:visible', false)">
    <div style="display: flex; justify-content: flex-end; margin-bottom: 16px">
      <a-input size="small" style="width: 200px" placeholder="输入关键字搜索数据源" @change="(e) => (searchVal = e.target.value)" />
    </div>
    <div>
      <div style="height: 400px; overflow: hidden auto">
        <div v-for="(group, index) of options" :key="index">
          <div class="group_name">{{ group.group_name }}</div>
          <a-row :gutter="[16, 16]">
            <a-col :span="6" v-for="item of group.items" :key="item.value" v-show="item.label.search(searchVal) !== -1">
              <span class="logo" @click="$emit('select', item)" style="background-image: url(https://liangx-gallery.oss-cn-beijing.aliyuncs.com/mydb.mytb@primary.png)"> </span>
              <div style="text-align: center">{{ item.label }}</div>
            </a-col>
          </a-row>
        </div>
      </div>
    </div>
  </a-modal>
</template>

<script>
const options = [
  {
    group_name: "关系型数据库",
    items: [
      {
        label: "mysql",
        logo: "",
        value: 1,
      },
    ],
  },
  {
    group_name: "大数据存储",
    items: [
      {
        label: "hive",
        logo: "",
        value: 1,
      },
      {
        label: "presto",
        logo: "",
        value: 1,
      },
    ],
  },
  {
    group_name: "文件系统",
    items: [
      {
        label: "HDFS",
        logo: "",
        value: 1,
      },
    ],
  },
  {
    group_name: "无结构化存储",
    items: [
      {
        label: "ElsticSearch",
        logo: "",
        value: 1,
      },
    ],
  },
  {
    group_name: "消息队列",
    items: [
      {
        label: "kafka",
        logo: "",
        value: 1,
      },
    ],
  },
  {
    group_name: "实时流",
    items: [
      {
        label: "mysql Binlog",
        logo: "",
        value: 1,
      },
    ],
  },
];
export default {
  name: "select_type_modal",
  props: {
    visible: {
      type: Boolean,
    },
  },
  emits: {
    select: null,
    "update:visible": null,
  },
  data() {
    return { options, searchVal: "" };
  },
};
</script>

<style scoped lang="less">
.group_name {
  border-left: 3px solid #1890ff;
  padding-left: 6px;
  margin: 12px 0;
}
.logo {
  width: 100%;
  padding-bottom: 100%;
  overflow: hidden;
  display: inline-block;
  height: 0;
  background-size: 100% 100%;
  cursor: pointer;
}
</style>

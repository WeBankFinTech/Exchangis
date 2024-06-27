<template>
  <a-modal footer="" :visible="visible" :title="$t(`dataSource.sourceTypeModal.title`)" @cancel="$emit('update:visible', false)">
    <div style="display: flex; justify-content: flex-end; margin-bottom: 16px">
      <a-input style="width: 200px" :placeholder="$t(`dataSource.sourceTypeModal.searchInputPlaceholder`)" v-model:value="searchVal" />
    </div>
    <div>
      <div style="height: 400px; overflow: hidden auto">
        <div v-for="(group, index) of options" :key="index">
          <div class="group_name">{{ group.group_name }}</div>
          <a-row :gutter="[16, 16]">
            <a-col :span="6" v-for="item of group.items" :key="item.id" v-show="item.name.search(searchVal) !== -1">
              <span class="logo" @click="$emit('select', item)" :style="`background-image: url(${require('@/images/dataSourceTypeIcon/' + item.name + '.png')})`"> </span>
              <div style="text-align: center">{{ item.name }}</div>
            </a-col>
          </a-row>
        </div>
      </div>
    </div>
  </a-modal>
</template>

<script>
import { getDataSourceTypes } from "@/common/service";

export default {
  name: "selectTypeModal",
  props: {
    visible: {
      type: Boolean,
    },
    sourceTypeList: {
      type: Array,
      default: [],
    },
  },
  emits: ["select", "update:visible"],
  data() {
    return { searchVal: "" };
  },
  watch: {
    visible: {
      immediate: true,
      handler: function(cur) {
        if(cur) {
          this.searchVal = '';
        }
      }
    }
  },
  computed: {
    options() {
      let options = {};
      for (const item of this.sourceTypeList) {
        if (options[item.classifier] === undefined) options[item.classifier] = [item];
        else options[item.classifier].push(item);
      }
      return Object.entries(options).map(([group_name, items]) => ({ group_name, items }));
    },
  },
};
</script>

<style scoped lang="less">
@import "../../../common/content.less";
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

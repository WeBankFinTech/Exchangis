<template>
  <div class="top-line">
    <span>
      <a-select @change="changeType" style="width: 120px" :placeholder="$t('dataSource.topLine.searchBar.dataTypePlaceholder')">
        <a-select-option value="jack">Jack</a-select-option>
        <a-select-option value="lucy">Lucy</a-select-option>
      </a-select>
      <a-select @change="changeCreator" style="width: 120px" :placeholder="$t('dataSource.topLine.searchBar.creatorPlaceholder')">
        <a-select-option value="jack">Jack</a-select-option>
        <a-select-option value="lucy">Lucy</a-select-option>
      </a-select>
      <a-input @change="changeName" style="width: 120px" :placeholder="$t('dataSource.topLine.searchBar.namePlaceholder')" />
      <a-button type="primary" @click="$emit('search', seartParams)">
        <template v-slot:icon> <icon-searchOutlined /></template>
        {{ $t("dataSource.topLine.searchBar.searchButtonText") }}
      </a-button>
    </span>
    <a-space>
      <a-button type="primary" @click="$emit('create')">
        <template v-slot:icon> <icon-plusOutlined /></template>
        新建数据源
      </a-button>
      <a-button>
        <template v-slot:icon> <icon-importOutlined /></template>
        批量导入数据源
      </a-button>
      <a-button>
        <template v-slot:icon> <icon-exportOutlined /></template>
        批量导出数据源
      </a-button>
    </a-space>
  </div>
</template>

<script>
import { PlusOutlined, ExportOutlined, ImportOutlined, SearchOutlined } from "@ant-design/icons-vue";
export default {
  components: {
    iconPlusOutlined: PlusOutlined,
    iconExportOutlined: ExportOutlined,
    iconImportOutlined: ImportOutlined,
    iconSearchOutlined: SearchOutlined,
  },
  emits: {
    search: null,
    export: null,
    import: null,
    create: null,
  },
  data() {
    return {
      seartParams: {
        type: "",
        creator: "",
        name: "",
      },
    };
  },
  methods: {
    changeName(val) {
      this.seartParams.name = val.target.value;
    },
    changeCreator(val) {
      this.seartParams.creator = val;
    },
    changeType(val) {
      this.seartParams.type = val;
    },
  },
};
</script>

<style scoped lang="less"></style>

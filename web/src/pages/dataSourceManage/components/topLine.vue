<template>
  <div class="top-line">
    <span>
      <a-select @change="changeType" style="width: 120px" :allowClear="true" :placeholder="$t('dataSource.topLine.searchBar.dataTypePlaceholder')">
        <a-select-option v-for="item of sourceTypeList" :value="item.id" :key="item.id">{{ item.name }}</a-select-option>
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
        {{ $t("dataSource.topLine.createDataSourceButton") }}
      </a-button>
    </a-space>
  </div>
</template>

<script>
import { PlusOutlined, ExportOutlined, ImportOutlined, SearchOutlined } from "@ant-design/icons-vue";
import { getDataSourceTypes } from "@/common/service";

export default {
  name: "topLine",
  components: {
    iconPlusOutlined: PlusOutlined,
    iconExportOutlined: ExportOutlined,
    iconImportOutlined: ImportOutlined,
    iconSearchOutlined: SearchOutlined,
  },
  props: ["sourceTypeList"],
  emits: ["search", "export", "import", "create"],
  data() {
    return {
      seartParams: {
        typeId: "",
        name: "",
      },
      typeOptions: [],
    };
  },
  methods: {
    changeName(val) {
      this.seartParams.name = val.target.value;
    },
    changeType(val) {
      this.seartParams.typeId = Number(val);
    },
  },
};
</script>

<style scoped lang="less"></style>

<!--
 * @Description: 
 * @Author: sueRim
 * @Date: 2022-05-13 10:19:27
-->
<template>
  <div class="top-line">
    <span>
      <a-select v-model:value="seartParams.typeId"
                style="width: 140px;"
                class="top-line-select"
                :allowClear="true"
                :placeholder="$t('dataSource.topLine.searchBar.dataTypePlaceholder')">
        <a-select-option v-for="item of sourceTypeList" :value="Number(item.id)" :key="item.id">{{ item.name }}</a-select-option>
      </a-select>
      <a-input v-model:value="seartParams.name"
               class="top-line-input"
               style="width: 220px;margin-right: 20px" :placeholder="$t('dataSource.topLine.searchBar.namePlaceholder')" />
      <a-button :loading="loading" type="primary" @click="$emit('search', seartParams)">
        <template v-slot:icon> <icon-searchOutlined /></template>
        {{ $t("dataSource.topLine.searchBar.searchButtonText") }}
      </a-button>
      <a-button style="margin-left: 10px" :loading="loading" type="primary" @click="clearParams">
        清空
      </a-button>
    </span>
    <a-space>
      <a-button :loading="loading" type="primary" @click="$emit('encrypt')">
        {{ $t("加密插件") }}
      </a-button>
      <a-button :loading="loading" type="primary" @click="$emit('create')">
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
  props: ["sourceTypeList", "loading"],
  emits: ["search", "export", "import", "create"],
  data() {
    return {
      seartParams: {
        typeId: undefined,
        name: "",
      }
    };
  },
  methods: {
    clearParams() {
      this.seartParams = {
        typeId: undefined,
        name: ""
      }
      this.$emit('search', this.seartParams)
    }
  }
};
</script>

<style scoped lang="less">
  .top-line-select {
    :deep(.ant-select-selector) {
      border-radius: 4px 0 0 4px !important;
      border-right:none !important;
    }
  }

  :deep(.top-line-input.ant-input) {
    border-radius: 0 4px 4px 0;
  }

</style>

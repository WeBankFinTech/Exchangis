<template>
  <div class="container">
    <div class="tools-bar">
      <span @click="modalCfg.visible = true"><SettingOutlined />配置</span>
      <span><CaretRightOutlined />执行</span>
      <span><SaveOutlined />保存</span>
      <span><HistoryOutlined />执行历史</span>
    </div>
    <div class="jd-content">
      <div class="jd_left">
        <DatabaseFilled /><span>子任务列表</span><PlusSquareOutlined />
        <div v-for="(item, idx) in list" :key="idx">
          <div>
            {{ item.subjobName }}<CopyOutlined @click="copySub(item)" />
            <DeleteOutlined />
          </div>
          <div>xxxxxx</div>
          <div><ArrowDownOutlined /></div>
          <div>xxxxxx</div>
        </div>
      </div>
      <div class="jd_right">
        <CheckCircleOutlined />
        <div>
          <!-- <DataSource /> -->
          <Tree />
        </div>
      </div>
    </div>
    <config-modal
      v-model:visible="modalCfg.visible"
      :id="modalCfg.id"
      @finish="handleModalFinish"
    />
    <copy-modal
      v-model:visible="modalCopy.visible"
      :origin="copyObj"
      @finish="handleModalCopy"
    />
  </div>
</template>
<script>
import { toRaw } from "vue";
import {
  SettingOutlined,
  CaretRightOutlined,
  SaveOutlined,
  HistoryOutlined,
  DatabaseFilled,
  PlusSquareOutlined,
  CopyOutlined,
  DeleteOutlined,
  ArrowDownOutlined,
  CheckCircleOutlined,
} from "@ant-design/icons-vue";
import configModal from "./configModal";
import copyModal from "./copyModal";
import { getJobInfo } from "@/common/service";
import { jobInfo } from "../mock"
import DataSource from "./dataSource";
import Tree from "./tree.vue";
export default {
  components: {
    SettingOutlined,
    CaretRightOutlined,
    SaveOutlined,
    HistoryOutlined,
    DatabaseFilled,
    PlusSquareOutlined,
    CopyOutlined,
    DeleteOutlined,
    ArrowDownOutlined,
    CheckCircleOutlined,
    configModal,
    copyModal,
    DataSource,
    Tree,
  },
  data() {
    return {
      name: "",
      modalCfg: {
        id: "",
        visible: false,
      },
      modalCopy: {
        visible: false,
      },
      jobData: {},
      copyObj: {},
      list: [],
    };
  },
  props: {
    curTab: Object,
  },
  watch: {
    curTab(n, o) {
      this.init();
    },
  },
  mounted() {
    this.init();
  },
  methods: {
    init() {
      this.name = this.curTab.jobName;
      this.getInfo()
    },
    async getInfo() {
      try {
        //await getJobInfo(this.curTab.id);
        jobInfo.content.subJobs.forEach(item => {
          item.engineType = jobInfo.engineType
        })
        this.jobData = jobInfo
        this.list = this.jobData.content.subJobs
        console.log(this.jobData)
      } catch (error) {}
    },
    handleModalFinish() {},
    handleModalCopy(data) {
      console.log(data)
    },
    copySub(item) {
      this.copyObj = item;
      this.modalCopy.visible = true;
    },
  },
};
</script>
<style scoped lang="less">
.container {
  .jd-content {
    display: flex;
    .jd_left {
      flex: 1;
    }
    .jd_right {
      flex: 4;
    }
  }
}
</style>

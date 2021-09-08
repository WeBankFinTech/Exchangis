<template>
    <div class="container">
        <div class="tools-bar">
          <span @click="modalCfg.visible=true"><SettingOutlined />配置</span>
          <span><CaretRightOutlined />执行</span>
          <span><SaveOutlined />保存</span>
          <span><HistoryOutlined />执行历史</span>
        </div>
        <div class="jd-content">
          <div class="jd_left">
            <DatabaseFilled /><span>子任务列表</span><PlusSquareOutlined />
            <div>
              <div>任务1<CopyOutlined /> <DeleteOutlined /></div>
              <div>xxxxxx</div>
              <div> <ArrowDownOutlined /> </div>
              <div>xxxxxx</div>
            </div>
          </div>
          <div class="jd_right">
            <CheckCircleOutlined />数据源
          </div>
        </div>
      <config-modal v-model:visible="modalCfg.visible" :id="modalCfg.id" @finish="handleModalFinish" />
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
    CheckCircleOutlined
  } from "@ant-design/icons-vue";
  import configModal from './configModal'
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
      configModal
    },
    data() {
      return {
        name: '',
        modalCfg: {
          id: '',
          visible: false,
        }
      };
    },
    props: {
      curTab: Object
    },
    watch:{
      curTab(n, o){
        this.init()
      }
    },
    mounted() {
      this.init()
    },
    methods: {
      init(){
        this.name = this.curTab.jobName
        console.log(this.curTab, this.name)
      },
      handleModalFinish(){

      }
    }
  }
</script>
<style scoped lang="less">
.container {
    .jd-content{
        display: flex;
        .jd_left{
          flex: 1;
        }
        .jd_right{
          flex: 4;
        }
    }
}
</style>
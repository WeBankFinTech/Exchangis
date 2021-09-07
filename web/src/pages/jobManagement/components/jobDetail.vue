<template>
    <div class="container">
        <SettingOutlined />配置
        <CaretRightOutlined />执行
        <SaveOutlined />保存
        <HistoryOutlined />执行历史
    </div>
</template>
<script>
  import { toRaw } from "vue";
  import {
    SettingOutlined,
    CaretRightOutlined,
    SaveOutlined,
    HistoryOutlined
  } from "@ant-design/icons-vue";
  export default {
    components: {
      SettingOutlined,
      CaretRightOutlined,
      SaveOutlined,
      HistoryOutlined
    },
    data() {
      return {
        name: ''
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
      }
    }
  }
</script>
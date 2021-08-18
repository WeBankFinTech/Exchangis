<template>
  <div class="content">
    <job-list v-show="choosedTab === 'jobList'"/>
  </div>
</template>
<script>
import Button from "ant-design-vue/lib/Button";
import "ant-design-vue/lib/button/style/css";
import JobList from "./components/jobList.vue";
import JobDetail from "./components/jobDetail.vue";
export default {
  components: {
    Button,
    JobList,
    JobDetail
  },
  FesData() {
    console.log("data");
    return {
      name: "jobManagement",
    };
  },
  FesReady() {
    console.log("ready");
  },
  data() {
    return {
      name: "jobManagement11",
      choosedTab: "jobList"
    };
  },
  mounted() {
    console.log("mounted");
  },
};
</script>
<style scoped lang="less">
.content {
  padding: 16px;
  box-sizing: border-box;
}
.title-line {
  display: flex;
  justify-content: space-between;
  align-items: center;
  .title {
    border-left: 6px solid #1890ff;
    padding-left: 6px;
  }
}
.pagination-line {
  display: flex;
  justify-content: flex-end;
}
</style>
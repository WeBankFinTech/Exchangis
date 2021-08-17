<template>
  <div class="container">
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

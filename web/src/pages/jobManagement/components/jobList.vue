<template>
  <div class="content">
    <div class="formWrap">
      <a-input-search
        v-model:value="search"
        placeholder="input search text"
        style="width: 200px"
        @search="handleSearch"
      />
      <a-button
        type="primary"
        style="width: 160px; margin-left: 30px"
        @click="addJob"
      >
        <template #icon> <PlusOutlined /></template>{{t('job.action.createJob')}}
      </a-button>
      <a-button type="primary" style="width: 160px; margin-left: 30px">
        <template #icon> <DownloadOutlined /></template>{{t('job.action.import')}}
      </a-button>
    </div>
    <div>
      <a-tabs v-model:activeKey="activeKey">
        <a-tab-pane key="1">
          <template #tab>
            <span>
              <ApiOutlined />
              {{t('job.type.offline')}}
            </span>
          </template>
          <div class="cardWrap">
            <div v-for="(item, index) in jobList" :key="index" class="card">
              <job-card
                :jobData="item"
                @showJobDetail="showJobDetail"
                @handleJobCopy="handleJobCopy"
              />
            </div>
          </div>
        </a-tab-pane>
        <a-tab-pane key="2">
          <template #tab>
            <span>
              <NodeIndexOutlined />
              {{t('job.type.stream')}}
            </span>
          </template>
          <div class="cardWrap">
            <div v-for="(item, index) in jobList" :key="index" class="card">
              <job-card
                :jobData="item"
                @showJobDetail="showJobDetail"
                @handleJobCopy="handleJobCopy"
              />
            </div>
          </div>
        </a-tab-pane>
      </a-tabs>
    </div>
    <CreateJob
      :visible="visible"
      :editData="editJobData"
      @handleJobAction="handleJobAction"
    />
  </div>
</template>
<script>
import {
  DownloadOutlined,
  NodeIndexOutlined,
  ApiOutlined,
  PlusOutlined,
} from '@ant-design/icons-vue';
import { useI18n } from "@fesjs/fes";
import CreateJob from './createJob';
import JobCard from './job_card';

export default {
  components: {
    DownloadOutlined,
    NodeIndexOutlined,
    ApiOutlined,
    PlusOutlined,
    CreateJob,
    JobCard,
  },
  data() {
    const { t } = useI18n({ useScope: "global" });
    return {
      t,
      search: '1222',
      userName: 'safdsaf',
      activeKey: '1',
      visible: false,
      loading: false,
      editJobData: {},
      jobList: [
        {
          id: 1, // 任务id
          projectId: 1, // 所属项目id
          jobName: '任务名1',
          jobType: 'OFFLINE',
          engineType: 'DataX', // 执行引擎
          jobLabels: 'renwu, hello, hello',
          jobDesc: '任务描述',
        },
        {
          id: 2, // 任务id
          projectId: 1, // 所属项目id
          jobName: '任务名2',
          jobType: 'STREAM',
          engineType: 'Sqoop', // 执行引擎
          jobLabels: 'renwu, hello, hello',
          jobDesc:
            '任务描述ets how a flex item will grow or shrink to fit the space available in itsets how a flex item will grow or shrink to fit the space available in its',
        },
      ],
    };
  },
  methods: {
    handleSearch(element) {
      const value = element.target.value;
      this.search = value;
    },
    addJob() {
      console.log(122);
      this.visible = true;
    },
    handleJobAction(status) {
      this.visible = false;
      this.editJobData = {};
      console.log(status);
    },
    handleJobCopy(data) {
      this.visible = true;
      console.log(data);
      this.editJobData = data;
    },
    showJobDetail(data) {
      console.log(data);
      this.$emit('showJobDetail', data);
    },
  },
};
</script>
<style scoped lang="less">
.cardWrap {
  display: flex;
  flex-wrap: wrap;
  padding-bottom: 30px;
  .card {
    margin: 10px 20px 10px 0px;
  }
}
</style>

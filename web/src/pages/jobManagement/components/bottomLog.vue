<template>
    <!-- 执行日志  jd-bottom -->
    <div class="jd-bottom jd-bottom-log" v-show="visibleLog">
      <div class="jd-bottom-top jd-bottom-log-top" >
        <CloseOutlined
          style="
            color: rgba(0, 0, 0, 0.45);
            font-size: 12px;
            position: absolute;
            right: 24px;
            top: 18px;
            cursor: pointer;
          "
          @click="onCloseLog"
        />
      </div>
      <div class="jd-bottom-content log-bottom-content">
        <a-tabs default-active-key="1" class="exec-info-tab">
          <a-tab-pane key="1" tab="运行情况">
            <div v-if="jobProgress.tasks" class="job-progress-percent job-progress-wrap">
              <span>总进度<span style="font-size: 11px;color:rgba(0,0,0,0.5)">({{statusMap[jobStatus]}})</span></span>
              <a-tooltip :title="jobProgress.title">
                <a-progress :percent="jobProgress.percent" />
              </a-tooltip>
            </div>
            <div v-if="jobProgress.tasks && jobProgress.tasks.Running" class="job-progress-wrap">
              <span class="job-progress-title">正在运行</span>
              <div class="job-progress-body">
                <div class="job-progress-percent" v-for="progress in jobProgress.tasks.Running" :key="progress.name">
                  <span class="job-progress-expand-icon" @click="getTaskInfo(progress)">
                    <RightOutlined v-if="openMetricsId !== progress.taskId" />
                    <DownOutlined v-else/>
                  </span>
                  <span :title="progress.name" style="color:#2e92f7;cursor: pointer;" @click="getTaskInfo(progress)">{{ progress.name }}</span>
                  <a-progress :percent="progress.progress * 100" />
                  <metrics :metricsInfo="metricsInfo" :progress="progress" v-if="openMetricsId === progress.taskId && metricsInfo[progress.taskId]" style="margin-left: 100px"></metrics>
                </div>
              </div>
            </div>
            <div v-if="jobProgress.tasks && jobProgress.tasks.Scheduled" class="job-progress-wrap">
              <span class="job-progress-title">准备中</span>
              <div class="job-progress-body">
                <div class="job-progress-percent" v-for="(progress, index) in jobProgress.tasks.Scheduled">
                  <span :title="progress.name">{{ progress.name }}</span><a-progress :percent="0" />
                </div>
              </div>
            </div>
            <div v-if="jobProgress.tasks && jobProgress.tasks.Inited" class="job-progress-wrap">
              <span class="job-progress-title">初始化</span>
              <div class="job-progress-body">
                <div class="job-progress-percent" v-for="(progress, index) in jobProgress.tasks.Inited">
                  <span :title="progress.name">{{ progress.name }}</span><a-progress :percent="0" />
                </div>
              </div>
            </div>
            <div v-if="jobProgress.tasks && jobProgress.tasks.Failed" class="job-progress-wrap">
              <span class="job-progress-title" style="color:#ff4d4f">失败</span>
              <div class="job-progress-body">
                <div class="job-progress-percent" v-for="(progress, index) in jobProgress.tasks.Failed">
                  <span class="job-progress-expand-icon" @click="getTaskInfo(progress)">
                    <RightOutlined v-if="openMetricsId !== progress.taskId" />
                    <DownOutlined v-else/>
                  </span>
                  <span :title="progress.name" style="color:#2e92f7;cursor: pointer;" @click="getTaskInfo(progress)">{{ progress.name }}</span>
                  <a-progress :percent="progress.progress * 100" />
                  <metrics :metricsInfo="metricsInfo" :progress="progress" v-if="openMetricsId === progress.taskId && metricsInfo[progress.taskId]" style="margin-left: 100px"></metrics>
                </div>
              </div>
            </div>
            <div v-if="jobProgress.tasks && jobProgress.tasks.Cancelled" class="job-progress-wrap">
              <span class="job-progress-title" style="color:#ff4d4f">终止</span>
              <div class="job-progress-body">
                <div class="job-progress-percent" v-for="(progress, index) in jobProgress.tasks.Cancelled">
                  <span class="job-progress-expand-icon" @click="getTaskInfo(progress)">
                    <RightOutlined v-if="openMetricsId !== progress.taskId" />
                    <DownOutlined v-else/>
                  </span>
                  <span :title="progress.name" style="color:#2e92f7;cursor: pointer;" @click="getTaskInfo(progress)">{{ progress.name }}</span>
                  <a-progress :percent="progress.progress * 100" />
                  <metrics :metricsInfo="metricsInfo" :progress="progress" v-if="openMetricsId === progress.taskId && metricsInfo[progress.taskId]" style="margin-left: 100px"></metrics>
                </div>
              </div>
            </div>
            <div v-if="jobProgress.tasks && jobProgress.tasks.Success" class="job-progress-wrap">
              <span class="job-progress-title">成功</span>
              <div class="job-progress-body">
                <div class="job-progress-percent" v-for="(progress, index) in jobProgress.tasks.Success">
                  <span class="job-progress-expand-icon" @click="getTaskInfo(progress)">
                    <RightOutlined v-if="openMetricsId !== progress.taskId" />
                    <DownOutlined v-else/>
                  </span>
                  <span :title="progress.name" style="color:#2e92f7;cursor: pointer;" @click="getTaskInfo(progress)">{{ progress.name }}</span>
                  <a-progress :percent="progress.progress * 100" />
                  <metrics :metricsInfo="metricsInfo" :progress="progress" v-if="openMetricsId === progress.taskId && metricsInfo[progress.taskId]" style="margin-left: 100px"></metrics>
                </div>
              </div>
            </div>
          </a-tab-pane>
          <a-tab-pane key="2" tab="实时日志" force-render>
            <execution-log :param="logParams" :isShow="visibleLog"></execution-log>
          </a-tab-pane>
        </a-tabs>
      </div>
    </div>
</template>
<script>
import { toRaw, h, defineAsyncComponent } from "vue";
import {
  DatabaseFilled,
  CloseOutlined,
  DownOutlined,
  RightOutlined
} from "@ant-design/icons-vue";
import {
  getJobTasks,
  getProgress,
  getMetrics,
  getJobStatus
} from "@/common/service";
import { message, notification } from "ant-design-vue";
import executionLog from './executionLog'
import metrics from './metricsInfo'
import { moveUpDown } from "../../../common/utils";

export default {
  components: {
    DatabaseFilled,
    CloseOutlined,
    DownOutlined,
    RightOutlined,
    executionLog,
    metrics
  },
  data() {
    return {
      visibleLog: false,
      jobExecutionId: this.jobId,

      tasklist: [],
      progressTimer: null,
      jobProgress: {},
      metricsInfo: {},
      openMetricsId: '',
      jobStatus: '',
      statusMap: {
        'Inited': '初始化',
        'Scheduled': '准备',
        'Running': '运行',
        'WaitForRetry': '等待重试',
        'Cancelled': '取消',
        'Failed': '失败',
        'Partial_Success': '部分成功',
        'Success': '成功',
        'Undefined': '未定义',
        'Timeout': '超时'
      }
    };
  },
  props: {
    jobId: Number,
  },
  created() {
    this.init();
  },
  beforeUnmount() {
    clearInterval(this.progressTimer)
  },
  watch: {
    jobId(val) {
      this.jobExecutionId = val;
      this.getTasks()
      this.getJobProgressWithPoll()
    }
  },
  computed: {
    logParams(){
      return {
        list: this.tasklist,
        id: this.jobExecutionId,
        sync: true
      }
    }
  },
  methods: {
    init() {
      this.tasklist = []
      this.visibleLog = true
      this.$nextTick(()=> {
        moveUpDown('.jd-bottom-log', '.sync-history-wrap', '.jd-bottom-log-top')
      })
      this.getTasks()
      this.getJobProgressWithPoll()
    },
    // 获取tasklist
    getTasks() {
      getJobTasks(this.jobExecutionId)
        .then(res => {
          this.tasklist = res.tasks || res.result
        })
        .catch(err => {
          message.error("查询任务列表失败");
        })
    },
    clearProgressTimer() {
      if (this.progressTimer) {
        clearInterval(this.progressTimer);
      }
    },
    getJobProgressWithPoll() {
      this.clearProgressTimer();
      this.getJobProgress()
      this.progressTimer = setInterval(() => {
        this.getJobProgress()
      }, 1000*5)
    },
    getJobProgress() {
      const unfinishedStatusList = ['Inited', 'Scheduled', 'Running', 'WaitForRetry']
      getJobStatus(this.jobExecutionId)
        .then(res => {
          this.jobStatus = res.status
          if (res.allTaskStatus && unfinishedStatusList.indexOf(res.status) === -1) {
            this.clearProgressTimer();
          }
        })
        .catch(err => {
          message.error("查询job状态失败");
          this.clearProgressTimer();
        })
      getProgress(this.jobExecutionId)
        .then(res => {
          if (res.job && res.job.tasks) {
            res.job.successTasks = res.job.tasks.Success?.length || 0
            res.job.initedTasks = res.job.tasks.Inited?.length || 0
            res.job.runningTasks = res.job.tasks.Running?.length || 0
            res.job.totalTasks = this.tasklist.length
            res.job.successPercent = res.job.successTasks * 100 / res.job.totalTasks
            res.job.percent = res.job.progress * 100 //(res.job.successTasks + res.job.runningTasks) * 100 / res.job.totalTasks
            res.job.title = `${res.job.successTasks}成功,${res.job.runningTasks}正在运行,${res.job.initedTasks}正在准备`
          }
          this.jobProgress = res.job
        })
        .catch(err => {
          message.error("查询进度失败");
          this.clearProgressTimer();
        })
    },
    getTaskInfo(progress) {
      if (this.openMetricsId !== progress.taskId) {
        this.openMetricsId = progress.taskId
        getMetrics(progress.taskId, this.jobExecutionId)
          .then(res => {
            this.metricsInfo[res.task.taskId] = res.task?.metrics
          })
          .catch(err => {
            message.error("查询任务指标失败");
          })
      } else {
        this.openMetricsId = ''
      }
    },
    onCloseLog() {
      if (this.progressTimer) {
        clearInterval(this.progressTimer)
      }
      this.visibleLog = false;
      this.$emit("onCloseLog")
    }
  },
};
</script>
<style scoped lang="less">
@import "../../../common/content.less";
.jd-bottom {
  overflow: auto;
  width: calc(100% - 200px);
  position: fixed;
  height: 30%;
  bottom: 0;
  background-color: white;
  box-shadow: 0px -2px 4px rgba(0, 0, 0, 0.05);
  .jd-bottom-top {
    width: calc(100% - 200px);
    height: 43px;
    position: fixed;
    bottom: 30%;
    background-color: #f8f9fc;
    padding: 8px 24px;
    font-family: PingFangSC-Medium;
    font-size: 16px;
    color: rgba(0, 0, 0, 0.85);
    font-weight: 500;
  }
  &.jd-bottom-log {
    height: 33%;
    .jd-bottom-top {
      bottom: 33%;
    }
  }


  &-content {
    padding: 18px 24px;
    .exec-info-tab {
      >:deep(.ant-tabs-bar) {
          position: fixed;
          margin-top: -45px;
        }
      :deep(.ant-tabs-content) {
        padding: 5px 10px;
      }
    }
  }
  .log-bottom-content {
    padding: 0 24px;
  }

  .job-progress-wrap {
    padding: 10px 0;
    border-bottom: 1px dashed rgba(0,0,0,0.2);
  }
  .job-progress-title {
    display: inline-block;
    width: 100px;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }
  .job-progress-body {
    display: inline-block;
    width: calc(100% - 100px);
  }
  .job-progress-percent {
    .job-progress-expand-icon {
      width:15px;
      margin-left: -15px;
      color: rgba(0, 0, 0, 0.45);
      cursor: pointer;
    }
    >span {
      display: inline-block;
      width: 100px;
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
    }
    >div {
      display: inline-block;
      width: calc(100% - 100px);
    }
  }
  .core-block {
    display: inline-block;
    padding: 5px;
    font-size: 12px;
    color: white;
    text-align: center;
    border-radius: 4px;
    margin-right: 20px;
  }
}
</style>

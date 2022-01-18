<template>
    <!-- 执行日志  jd-bottom -->
    <div class="jd-bottom jd-bottom-log" v-if="visibleLog">
      <div class="jd-bottom-top" >
        <!--<span>执行日志</span>-->
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
              <span>总进度</span>
              <a-tooltip :title="jobProgress.title">
                <a-progress :percent="jobProgress.percent" :success-percent="jobProgress.successPercent"/>
              </a-tooltip>
            </div>
            <div v-if="jobProgress.tasks && jobProgress.tasks.running" class="job-progress-wrap">
              <span class="job-progress-title">正在运行</span>
              <div class="job-progress-body">
                <div class="job-progress-percent" v-for="progress in jobProgress.tasks.running" :key="progress.name">
                  <span :title="progress.name" style="color:#2e92f7;cursor: pointer;text-decoration:underline" @click="getTaskInfo(progress)">{{ progress.name }}</span>
                  <a-progress status="active" :percent="progress.progress" />
                  <div class="job-progress-percent" v-if="openMetricsId === progress.taskId && metricsInfo[progress.taskId]" style="margin-left: 100px">
                    <span>资源使用</span>
                    <!--<div style="position: relative;padding-bottom: 20px;margin-bottom: 5px">-->
                    <!--<span style="position: absolute;bottom: 0;left:0;font-size: 12px">CPU使用</span>-->
                    <!--<a-progress type="dashboard" :width="50" :percent="metricsInfo[progress.taskId].resourceUsed.cpu" style="margin-right: 30px"/>-->
                    <!--<span style="position: absolute;bottom: 0;left:80px;font-size: 12px">内存使用</span>-->
                    <!--<a-progress type="dashboard" :width="50" :percent="metricsInfo[progress.taskId].resourceUsed.memory" />-->
                    <!--</div>-->
                    <div style="margin-bottom: 5px">
                      <div class="core-block" style="background-color: #2e92f7">
                        <div>{{metricsInfo[progress.taskId].resourceUsed.cpu}} vcores</div>
                        <div>CPU使用</div>
                      </div>
                      <div class="core-block" style="background-color: #2e92f7">
                        <div>{{metricsInfo[progress.taskId].resourceUsed.memory}} GB</div>
                        <div>内存使用</div>
                      </div>
                    </div>
                    <span>流量情况</span>
                    <div style="position: relative;padding-bottom: 20px;margin-bottom: 5px">
                      <span style="position: absolute;bottom: 0;left:0;font-size: 12px">{{metricsInfo[progress.taskId].traffic.source}}</span>
                      <DatabaseFilled  style="margin-right: 50px;color:#2e92f7;font-size: 25px"/>
                      <span style="position: absolute;bottom: 0;left:67px;font-size: 12px">{{metricsInfo[progress.taskId].traffic.flow}} Records/S</span>
                      <svg class="icon icon-symbol" aria-hidden="true" style="font-size: 40px;margin-right: 50px">
                        <use xlink:href="#icon-lansejiantoudaikuang"></use>
                      </svg>
                      <span style="position: absolute;bottom: 0;left:167px;font-size: 12px">{{metricsInfo[progress.taskId].traffic.sink}}</span>
                      <DatabaseFilled  style="color:#2e92f7;font-size: 25px" />
                    </div>
                    <span>核心指标</span>
                    <div>
                      <div class="core-block" style="background-color: #2e92f7">
                        <div>{{metricsInfo[progress.taskId].indicator.exchangedRecords}}</div>
                        <div>已同步</div>
                      </div>
                      <div class="core-block" style="background-color: #ff4d4f">
                        <div>{{metricsInfo[progress.taskId].indicator.errorRecords}}</div>
                        <div>错误记录</div>
                      </div>
                      <div class="core-block" style="background-color: gold">
                        <div>{{metricsInfo[progress.taskId].indicator.ignoredRecords}}</div>
                        <div>忽略记录</div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div v-if="jobProgress.tasks && jobProgress.tasks.Inited" class="job-progress-wrap">
                <span class="job-progress-title">准备中</span>
                <div class="job-progress-body">
                  <div class="job-progress-percent" v-for="progress in jobProgress.tasks.Inited" :key="progress.name">
                    <span :title="progress.name">{{ progress.name }}</span><a-progress :percent="0" />
                  </div>
                </div>
              </div>
          </a-tab-pane>
          <a-tab-pane key="2" tab="实时日志" force-render>
            <execution-log :param="logParams"></execution-log>
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
} from "@ant-design/icons-vue";
import {
  getJobTasks,
  getProgress,
  getMetrics
} from "@/common/service";
import { message, notification } from "ant-design-vue";
import executionLog from './executionLog'

export default {
  components: {
    DatabaseFilled,
    CloseOutlined,
    executionLog
  },
  data() {
    return {
      visibleLog: false,
      jobExecutionId: this.jobId,

      tasklist: [],
      progressTimer: null,
      jobProgress: {},
      metricsInfo: {},
      openMetricsId: ''
    };
  },
  props: {
    jobId: Number,
  },
  created() {
    this.init();
  },
  watch: {
    jobId(val) {
      this.jobExecutionId = val;
      this.getTasks()
    }
  },
  computed: {
    logParams(){
      return {
        list: this.tasklist,
        id: this.jobExecutionId
      }
    }
  },
  methods: {
    init() {
      this.tasklist = []
      this.visibleLog = true
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
    getJobProgressWithPoll() {
      this.getJobProgress()
      this.progressTimer = setInterval(() => {
        this.getJobProgress()
      }, 1000*5)
    },
    getJobProgress() {
      getProgress(this.jobExecutionId)
        .then(res => {
          if (res.job && res.job.tasks) {
            res.job.successTasks = res.job.tasks.Success?.length || 0
            res.job.initedTasks = res.job.tasks.Inited?.length || 0
            res.job.runningTasks = res.job.tasks.running?.length || 0
            res.job.totalTasks = this.tasklist.length
            res.job.successPercent = res.job.successTasks * 100 / res.job.totalTasks
            res.job.percent = (res.job.successTasks + res.job.runningTasks) * 100 / res.job.totalTasks
            res.job.title = `${res.job.successTasks}成功,${res.job.runningTasks}正在运行,${res.job.initedTasks}正在准备`
          }
          this.jobProgress = res.job
        })
        .catch(err => {
          message.error("查询进度失败");
        })
    },
    getTaskInfo(progress) {
      if (this.openMetricsId !== progress.taskId) {
        this.openMetricsId = progress.taskId
        getMetrics(progress.taskId, this.jobExecutionId)
          .then(res => {
            res.task.taskId = 5
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
  .jd-bottom-top {
    width: calc(100% - 200px);
    height: 48px;
    position: fixed;
    bottom: 30%;
    background-color: #f8f9fc;
    padding: 12px 24px;
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

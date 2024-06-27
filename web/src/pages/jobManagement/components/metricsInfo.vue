<template>
  <div class="job-progress-percent">
    <span>资源使用</span>
    <div style="margin-bottom: 5px">
      <div class="core-block" style="background-color: #2e92f7">
        <div>{{metricsInfo[progress.taskId].resourceUsed.cpu}} vcores</div>
        <div>CPU使用</div>
      </div>
      <div class="core-block" style="background-color: #2e92f7">
        <div>{{metricsInfo[progress.taskId].resourceUsed.memory}} MB</div>
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
        <div>记录数</div>
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
</template>
<script>
  import { defineComponent, h, toRaw, watch, computed, reactive, ref, nextTick, onBeforeUnmount} from "vue"
export default defineComponent({
  props: {
    metricsInfo: Object,
    progress: Object
  },
  emits: ["updateInfo"],
  setup(props, context) {
    const newProps = computed(() => JSON.parse(JSON.stringify(props.progress)))
    watch(newProps, (val, oldVal) => {
    })
    return {
    }
  }
})
</script>

<style scoped lang="less">
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
</style>

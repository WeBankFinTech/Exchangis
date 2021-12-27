<template>
  <div class="home-page-wrap">
    <!-- top -->

    <!-- content -->
    <div class="hp-content">
      <a-row :gutter="[24, 24]">
        <a-col :span="24">
          <div class="hp-content-card" style="border-radius: 6px">
            <div style="padding-bottom: 12px">
              <a-row :gutter="16">
                <a-col :span="4">
                  <div class="hp-top-card">
                    <a-card :bordered="false" style="flex: 1">
                      <div class="home-page-card-item">
                        <div class="home-page-card-img">
                          <svg
                            class="home-page-card-item-svg"
                            style="fill: currentColor; color: #eb7e65"
                          >
                            <use xlink:href="#icon-failure"></use>
                          </svg>
                        </div>
                        <div class="home-page-card-item-txt-t">
                          <span>{{ taskState.FAILED }}</span>
                        </div>
                        <div class="home-page-card-item-txt-b">
                          <span>失败任务</span>
                        </div>
                      </div>
                    </a-card>
                    <div class="hp-top-card-line"></div>
                  </div>
                </a-col>
                <a-col :span="4">
                  <div class="hp-top-card">
                    <a-card :bordered="false" style="flex: 1">
                      <div class="home-page-card-item">
                        <div class="home-page-card-img">
                          <svg
                            class="home-page-card-item-svg"
                            style="fill: currentColor; color: #89c2d9"
                          >
                            <use xlink:href="#icon-running-execution"></use>
                          </svg>
                        </div>
                        <div class="home-page-card-item-txt-t">
                          <span>{{ taskState.RUNNING }}</span>
                        </div>
                        <div class="home-page-card-item-txt-b">
                          <span>运行中</span>
                        </div>
                      </div>
                    </a-card>
                    <div class="hp-top-card-line"></div>
                  </div>
                </a-col>
                <a-col :span="4">
                  <div class="hp-top-card">
                    <a-card :bordered="false" style="flex: 1">
                      <div class="home-page-card-item">
                        <div class="home-page-card-img">
                          <svg
                            class="home-page-card-item-svg"
                            style="fill: currentColor; color: #f7c739"
                          >
                            <use xlink:href="#icon-submitted-success"></use>
                          </svg>
                        </div>
                        <div class="home-page-card-item-txt-t">
                          <span>{{ taskState.BUSY }}</span>
                        </div>
                        <div class="home-page-card-item-txt-b">
                          <span>慢任务</span>
                        </div>
                      </div>
                    </a-card>
                    <div class="hp-top-card-line"></div>
                  </div>
                </a-col>
                <a-col :span="4">
                  <div class="hp-top-card">
                    <a-card :bordered="false" style="flex: 1">
                      <div class="home-page-card-item">
                        <div class="home-page-card-img">
                          <svg
                            class="home-page-card-item-svg"
                            style="fill: currentColor; color: #c9d3e0"
                          >
                            <use xlink:href="#icon-waitting-thread"></use>
                          </svg>
                        </div>
                        <div class="home-page-card-item-txt-t">
                          <span>{{ taskState.IDLE }}</span>
                        </div>
                        <div class="home-page-card-item-txt-b">
                          <span>等待中</span>
                        </div>
                      </div>
                    </a-card>
                    <div class="hp-top-card-line"></div>
                  </div>
                </a-col>
                <a-col :span="4">
                  <div class="hp-top-card">
                    <a-card :bordered="false" style="flex: 1">
                      <div class="home-page-card-item">
                        <div class="home-page-card-img">
                          <svg
                            class="home-page-card-item-svg"
                            style="fill: currentColor; color: #eb7e65"
                          >
                            <use xlink:href="#icon-refresh"></use>
                          </svg>
                        </div>
                        <div class="home-page-card-item-txt-t">
                          <span>{{ taskState.UNLOCK }}</span>
                        </div>
                        <div class="home-page-card-item-txt-b">
                          <span>等待重试</span>
                        </div>
                      </div>
                    </a-card>
                    <div class="hp-top-card-line"></div>
                  </div>
                </a-col>
                <a-col :span="4">
                  <a-card :bordered="false">
                    <div class="home-page-card-item">
                      <div class="home-page-card-img">
                        <svg
                          class="home-page-card-item-svg"
                          style="fill: currentColor; color: #9edaac"
                        >
                          <use xlink:href="#icon-success"></use>
                        </svg>
                      </div>
                      <div class="home-page-card-item-txt-t">
                        <span>{{ taskState.SUCCESS }}</span>
                      </div>
                      <div class="home-page-card-item-txt-b">
                        <span>已完成</span>
                      </div>
                    </div>
                  </a-card>
                </a-col>
              </a-row>
            </div>
          </div>
        </a-col>
        <a-col :span="24">
          <div class="hp-content-card">
            <!-- title -->
            <div class="hp-content-card-title"><span>同步进度</span></div>
            <!-- main -->
            <div class="hp-content-card-main">
              <a-row type="flex">
                <a-col flex="100px"
                  ><span class="hp-content-card-main-span">总进度</span></a-col
                >
                <a-col flex="auto" style="margin-bottom: 16px">
                  <a-tooltip :title="totalTitle">
                    <a-progress
                      :percent="taskProcess.total.percentOfComplete"
                      :success-percent="taskProcess.total.initialized"
                      :strokeWidth="16"
                      :success="successItem"
                      strokeColor="#89C2D9"
                    /> </a-tooltip
                ></a-col>
              </a-row>
              <a-row type="flex">
                <a-col flex="100px"
                  ><span class="hp-content-card-main-span"
                    >同步到BDP</span
                  ></a-col
                >
                <a-col flex="auto" style="margin-bottom: 16px">
                  <a-tooltip :title="bdpTitle">
                    <a-progress
                      :percent="taskProcess.bdp.percentOfComplete"
                      :success-percent="taskProcess.bdp.initialized"
                      :strokeWidth="16"
                      strokeColor="#89C2D9"
                    /> </a-tooltip
                ></a-col>
              </a-row>
              <a-row type="flex">
                <a-col flex="100px"
                  ><span class="hp-content-card-main-span"
                    >同步到ES</span
                  ></a-col
                >
                <a-col flex="auto" style="margin-bottom: 16px">
                  <a-tooltip :title="esTitle">
                    <a-progress
                      :percent="taskProcess.es.percentOfComplete"
                      :success-percent="taskProcess.es.initialized"
                      :strokeWidth="16"
                      strokeColor="#89C2D9"
                    /> </a-tooltip
                ></a-col>
              </a-row>
              <a-row type="flex">
                <a-col flex="100px"
                  ><span class="hp-content-card-main-span"
                    >同步到FPS</span
                  ></a-col
                >
                <a-col flex="auto">
                  <a-tooltip :title="fpsTitle">
                    <a-progress
                      :percent="taskProcess.fps.percentOfComplete"
                      :success-percent="taskProcess.fps.initialized"
                      :strokeWidth="16"
                      strokeColor="#89C2D9"
                    /> </a-tooltip
                ></a-col>
              </a-row>
            </div>
          </div>
        </a-col>
        <a-col :span="24">
          <div class="hp-content-card">
            <!-- title -->
            <div class="hp-content-card-title"><span>流量监控</span></div>
            <!-- main -->
            <div class="hp-content-card-main">
              <div
                ref="myChart_1"
                style="width: 100%; height: 520px"
                id="myChart_1"
              ></div>
            </div>
          </div>
        </a-col>
        <a-col :span="24">
          <div class="hp-content-card">
            <!-- title -->
            <div class="hp-content-card-title"><span>资源使用监控</span></div>
            <!-- main -->
            <div class="hp-content-card-main">
              <div
                ref="myChart_2"
                style="width: 100%; height: 520px"
                id="myChart_2"
                class="my-chart"
              ></div>
              <div
                ref="myChart_3"
                style="width: 100%; height: 520px"
                id="myChart_3"
              ></div>
            </div>
          </div>
        </a-col>
      </a-row>
    </div>
  </div>
</template>

<script>
import { onMounted, reactive, computed, toRefs } from "@vue/runtime-core";
import { toRaw } from "vue";
import { cloneDeep } from "lodash-es";
import {
  getTaskState,
  getTaskProcess,
  getDataSourceFlow,
  getEngineriesSource,
  getEngineriesSourceCpu,
  getEngineriesSourceMem,
} from "@/common/service";
import * as echarts from "echarts";
import SvgIcon from "@/components/svgIcon/index.vue";
import '../../assets/iconfont.js'

let chartList = [];

// 轉換函數 後端二維數組 to echarts 數據
const initOptions = (ori, tar) => {
  const _tar = cloneDeep(tar);
  _tar.legend = ori[0].slice(1);
  _tar.legend.forEach((item) => {
    let o = {};
    o["type"] = "line";
    o["stack"] = "Total";
    o["data"] = [];
    o["name"] = item;
    _tar.series.push(o);
  });
  ori.slice(1).forEach((item) => {
    _tar.xAxis.push(item[0]);
    _tar.series.forEach((o, idx) => {
      o["data"].push(item[idx + 1]);
    });
  });
  return _tar;
};

// echarts 线条颜色
const lineColorLists = [
  { color: "#73A0FA" },
  { color: "#9EDAAC" },
  { color: "#F7C739" },
];
export default {
  setup() {
    const state = reactive({
      taskState: {
        SUCCESS: 0,
        FAILED: 0,
        RUNNING: 0,
        BUSY: 0,
        IDLE: 0,
        UNLOCK: 0,
      },
      taskProcess: {
        total: {},
        bdp: {},
        es: {},
        fps: {},
      },
    });
    const totalTitle = computed(
      () =>
        `${state.taskProcess.total.running}/${state.taskProcess.total.initialized}/${state.taskProcess.total.total}`
    );
    const bdpTitle = computed(
      () =>
        `${state.taskProcess.bdp.running}/${state.taskProcess.bdp.initialized}/${state.taskProcess.bdp.total}`
    );
    const esTitle = computed(
      () =>
        `${state.taskProcess.es.running}/${state.taskProcess.es.initialized}/${state.taskProcess.es.total}`
    );
    const fpsTitle = computed(
      () =>
        `${state.taskProcess.fps.running}/${state.taskProcess.fps.initialized}/${state.taskProcess.fps.total}`
    );
    const init = async () => {
      let _taskState, _taskProcess;
      try {
        _taskState = await getTaskState();
        _taskProcess = await getTaskProcess();
      } catch (err) {
        console.log("homePage error", err);
      }
      // 重点指标
      _taskState.metrices.forEach((item) => {
        if (item.status) {
          state.taskState[item.status]++;
        }
      });
      // 同步进度
      _taskProcess.list.forEach((item) => {
        if (item.key) {
          state.taskProcess[item.key]["percentOfComplete"] =
            item["percentOfComplete"].replaceAll("%", "") - "";
          state.taskProcess[item.key]["total"] = item["total"];
          state.taskProcess[item.key]["initialized"] = item["initialized"];
          state.taskProcess[item.key]["running"] = item["running"];
        }
      });
    };
    onMounted(init());
    return {
      ...toRefs(state),
      totalTitle,
      bdpTitle,
      esTitle,
      fpsTitle,
    };
  },
  data() {
    return {
      engineriesChartData: {
        legend: [],
        xAxis: [],
        series: [],
      },
      cpuChartData: {
        legend: [],
        xAxis: [],
        series: [],
      },
      memChartData: {
        legend: [],
        xAxis: [],
        series: [],
      },
      myInterval: "",
      successItem: {
        strokeColor: "#9EDAAC",
      },
    };
  },
  async mounted() {
    const that = this;
    try {
      await this.initEngineriesChart();
      await this.initCpuChart();
      await this.initMemChart();
    } catch (err) {
      console.log("init chart error", err);
    }
    // that.$nextTick(function () {
    //   // 通过 轮询 去获取 实时数据流
    //   that.myInterval = setInterval(() => {
    //     console.log("chartList", chartList);
    //   }, 500);
    // });
  },
  beforeUnmount() {
    // chartList = [];
    // clearInterval(this.myInterval);
  },
  methods: {
    async initEngineriesChartData() {
      let _dataSourceFlow;
      const that = this;
      try {
        _dataSourceFlow = await getDataSourceFlow();
      } catch (err) {
        console.log("get chart data error:", err);
      }
      const { dataset } = _dataSourceFlow;
      that.engineriesChartData = initOptions(dataset, that.engineriesChartData);
    },
    async initEngineriesChart() {
      const chartDom = this.$refs.myChart_1;
      const that = this;
      try {
        await this.initEngineriesChartData();
      } catch (err) {
        console.log("initEngineriesChartData error", err);
      }
      const chartData = toRaw(that.engineriesChartData);
      chartData.series.forEach((i, idx) => {
        i["lineStyle"] = lineColorLists[idx];
      });
      if (chartDom) {
        const myChart_1 = echarts.init(chartDom);
        const option = {
          tooltip: {
            trigger: "axis",
            backgroundColor: "rgba(0,0,0,0.75)",
            extraCssText: "box-shadow: 0 2px 8px 0; border-radius: 2px;",
            textStyle: {
              fontFamily: "PingFangSC-Regular",
              fontSize: "14px",
              color: "#FFFFFF",
              lineHeight: "22px",
              fontWeight: "400",
            },
          },
          legend: {
            data: chartData.legend,
            icon: "circle",
            textStyle: {
              fontFamily: "PingFangSC - Regular",
              fontSize: "12px",
              color: "rgba(0, 0, 0, 0.45)",
              fontWeight: 400,
            },
            itemHeight: 6,
            itemWidth: 6,
          },
          grid: {
            left: "3%",
            right: "4%",
            bottom: "3%",
            containLabel: true,
          },
          toolbox: {
            feature: {
              saveAsImage: {},
            },
          },
          xAxis: {
            type: "category",
            boundaryGap: false,
            data: chartData.xAxis,
            nameTextStyle: {
              fontFamily: "PingFangSC-Regular",
              fontSize: "12px",
              color: "#000000",
              textAlign: "center",
              fontWeight: "400",
            },
          },
          yAxis: {
            type: "value",
            nameTextStyle: {
              fontFamily: "Helvetica",
              fontSize: "12px",
              color: "#000000",
              textAlign: "right",
              fontWeight: "400",
            },
          },
          series: chartData.series,
        };
        option && myChart_1.setOption(option);
        chartList.push(myChart_1);
      }
    },
    async initCpuChartData() {
      let res;
      const that = this;
      try {
        res = await getEngineriesSourceCpu();
      } catch (err) {
        console.log("get chart data error:", err);
      }
      const { dataset } = res;
      that.cpuChartData = initOptions(dataset, that.cpuChartData);
    },
    async initCpuChart() {
      const chartDom = this.$refs.myChart_2;
      const that = this;
      try {
        await this.initCpuChartData();
      } catch (err) {
        console.log("initCpuChartData error", err);
      }
      const chartData = cloneDeep(that.cpuChartData);
      chartData.series.forEach((i, idx) => {
        i["lineStyle"] = lineColorLists[idx];
      });
      if (chartDom) {
        const myChart_2 = echarts.init(chartDom);
        const option = {
          title: {
            text: "CPU资源使用情况",
            textStyle: {
              fontSize: 14,
              fontFamily: "PingFangSC-Medium",
              color: "rgba(0,0,0,0.65)",
              fontWeight: 500,
            },
          },
          tooltip: {
            trigger: "axis",
            backgroundColor: "rgba(0,0,0,0.75)",
            extraCssText: "box-shadow: 0 2px 8px 0; border-radius: 2px;",
            textStyle: {
              fontFamily: "PingFangSC-Regular",
              fontSize: "14px",
              color: "#FFFFFF",
              lineHeight: "22px",
              fontWeight: "400",
            },
          },
          legend: {
            data: chartData.legend,
            icon: "circle",
            textStyle: {
              fontFamily: "PingFangSC - Regular",
              fontSize: "12px",
              color: "rgba(0, 0, 0, 0.45)",
              fontWeight: 400,
            },
            itemHeight: 6,
            itemWidth: 6,
          },
          grid: {
            left: "3%",
            right: "4%",
            bottom: "3%",
            containLabel: true,
          },
          toolbox: {
            feature: {
              saveAsImage: {},
            },
          },
          xAxis: {
            type: "category",
            boundaryGap: false,
            data: chartData.xAxis,
            nameTextStyle: {
              fontFamily: "PingFangSC-Regular",
              fontSize: "12px",
              color: "#000000",
              textAlign: "center",
              fontWeight: "400",
            },
          },
          yAxis: {
            type: "value",
            nameTextStyle: {
              fontFamily: "Helvetica",
              fontSize: "12px",
              color: "#000000",
              textAlign: "right",
              fontWeight: "400",
            },
          },
          series: chartData.series,
        };
        option && myChart_2.setOption(option);
        chartList.push(myChart_2);
      }
    },
    async initMemChartData() {
      let res;
      const that = this;
      try {
        res = await getEngineriesSourceMem();
      } catch (err) {
        console.log("get chart data error:", err);
      }
      const { dataset } = res;
      that.memChartData = initOptions(dataset, that.memChartData);
    },
    async initMemChart() {
      const chartDom = this.$refs.myChart_3;
      const that = this;
      try {
        await this.initMemChartData();
      } catch (err) {
        console.log("initMemChartData error", err);
      }
      const chartData = cloneDeep(that.memChartData);
      chartData.series.forEach((i, idx) => {
        i["lineStyle"] = lineColorLists[idx];
      });
      if (chartDom) {
        const myChart_3 = echarts.init(chartDom);
        const option = {
          title: {
            text: "MEM资源使用情况",
            textStyle: {
              fontSize: 14,
              fontFamily: "PingFangSC-Medium",
              color: "rgba(0,0,0,0.65)",
              fontWeight: 500,
            },
          },
          tooltip: {
            trigger: "axis",
            backgroundColor: "rgba(0,0,0,0.75)",
            extraCssText: "box-shadow: 0 2px 8px 0; border-radius: 2px;",
            textStyle: {
              fontFamily: "PingFangSC-Regular",
              fontSize: "14px",
              color: "#FFFFFF",
              lineHeight: "22px",
              fontWeight: "400",
            },
          },
          legend: {
            data: chartData.legend,
            icon: "circle",
            textStyle: {
              fontFamily: "PingFangSC - Regular",
              fontSize: "12px",
              color: "rgba(0, 0, 0, 0.45)",
              fontWeight: 400,
            },
            itemHeight: 6,
            itemWidth: 6,
          },
          grid: {
            left: "3%",
            right: "4%",
            bottom: "3%",
            containLabel: true,
          },
          toolbox: {
            feature: {
              saveAsImage: {},
            },
          },
          xAxis: {
            type: "category",
            boundaryGap: false,
            data: chartData.xAxis,
            nameTextStyle: {
              fontFamily: "PingFangSC-Regular",
              fontSize: "12px",
              color: "#000000",
              textAlign: "center",
              fontWeight: "400",
            },
          },
          yAxis: {
            type: "value",
            nameTextStyle: {
              fontFamily: "Helvetica",
              fontSize: "12px",
              color: "#000000",
              textAlign: "right",
              fontWeight: "400",
            },
          },
          series: chartData.series,
        };
        option && myChart_3.setOption(option);
        chartList.push(myChart_3);
      }
    },
  },
  components: { SvgIcon },
};
</script>

<style lang="less">
@import "../../common/common.less";
</style>
<style lang="less" scoped>
.home-page-wrap {
  padding: 22px 20px;
  :deep(.ant-progress-success-bg) {
    background-color: #9EDAAC;
  }

  .hp-content {
    &-card {
      min-height: 150px;
      background: inherit;
      background-color: rgba(255, 255, 255, 1);
      box-sizing: border-box;
      border-radius: 2px;
      &-title {
        padding-left: 32px;
        height: 54px;
        line-height: 54px;
        font-family: PingFangSC-Regular;
        font-size: 16px;
        color: #606266;
        font-weight: 800;
        border-bottom: 1px solid;
        border-color: #dee4ec;
      }
      &-main {
        padding: 24px 32px;
        &-span {
          font-family: PingFangSC-Regular;
          font-size: 12px;
          color: rgba(0, 0, 0, 0.45);
          font-weight: 400;
        }
      }
    }
  }

  .home-page-card-item {
    display: flex;
    align-items: center;
    flex-direction: column;

    &-img {
      width: 25px;
      height: 25px;
    }

    &-svg {
      height: 32px;
      width: 32px;
    }

    &-txt-t {
      font-family: HelveticaNeue;
      font-size: 24px;
      color: rgba(0, 0, 0, 0.65);
      text-align: center;
      font-weight: 400;
    }

    &-txt-b {
      font-family: PingFangSC-Regular;
      font-size: 14px;
      color: rgba(0, 0, 0, 0.45);
      text-align: center;
      line-height: 22px;
      font-weight: 400;
    }
  }

  .hp-top-card {
    display: flex;
    flex-direction: row;
    align-items: center;
    &-line {
      height: 60px;
      width: 1px;
      background: #eaf2fc;
    }
  }

  .my-chart {
    margin-bottom: 12px;
  }
}
</style>

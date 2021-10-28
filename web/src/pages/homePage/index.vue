<template>
  <div class="home-page-wrap">
    <!-- top -->

    <!-- content -->
    <div class="hp-content">
      <div class="hp-content-card">
        <!-- title -->
        <div class="hp-content-card-title"><span>重点指标</span></div>
        <!-- main -->
        <div style="padding-bottom: 12px">
          <a-row :gutter="16">
            <a-col :span="4">
              <a-card>
                <div class="home-page-card-item">
                  <div class="home-page-card-img">
                    <img src="../../images/homePage/u3989.png" alt="" />
                  </div>
                  <div class="home-page-card-item-txt-t">
                    <span>{{ taskState.FAILED }}</span>
                  </div>
                  <div class="home-page-card-item-txt-b">
                    <span>失败任务</span>
                  </div>
                </div>
              </a-card>
            </a-col>
            <a-col :span="4">
              <a-card>
                <div class="home-page-card-item">
                  <div class="home-page-card-img">
                    <img src="../../images/homePage/u6032.png" alt="" />
                  </div>
                  <div class="home-page-card-item-txt-t">
                    <span>{{ taskState.RUNNING }}</span>
                  </div>
                  <div class="home-page-card-item-txt-b">
                    <span>运行中</span>
                  </div>
                </div>
              </a-card>
            </a-col>
            <a-col :span="4">
              <a-card>
                <div class="home-page-card-item">
                  <div class="home-page-card-img">
                    <img src="../../images/homePage/u3998.png" alt="" />
                  </div>
                  <div class="home-page-card-item-txt-t">
                    <span>{{ taskState.BUSY }}</span>
                  </div>
                  <div class="home-page-card-item-txt-b">
                    <span>慢任务</span>
                  </div>
                </div>
              </a-card>
            </a-col>
            <a-col :span="4">
              <a-card>
                <div class="home-page-card-item">
                  <div class="home-page-card-img">
                    <img src="../../images/homePage/u6036.png" alt="" />
                  </div>
                  <div class="home-page-card-item-txt-t">
                    <span>{{ taskState.IDLE }}</span>
                  </div>
                  <div class="home-page-card-item-txt-b">
                    <span>等待中</span>
                  </div>
                </div>
              </a-card>
            </a-col>
            <a-col :span="4">
              <a-card>
                <div class="home-page-card-item">
                  <div class="home-page-card-img">
                    <img src="../../images/homePage/u4010.png" alt="" />
                  </div>
                  <div class="home-page-card-item-txt-t">
                    <span>{{ taskState.UNLOCK }}</span>
                  </div>
                  <div class="home-page-card-item-txt-b">
                    <span>等待重试</span>
                  </div>
                </div>
              </a-card>
            </a-col>
            <a-col :span="4">
              <a-card>
                <div class="home-page-card-item">
                  <div class="home-page-card-img">
                    <img src="../../images/homePage/u4009.png" alt="" />
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

      <div class="hp-content-card">
        <!-- title -->
        <div class="hp-content-card-title"><span>同步进度</span></div>
        <!-- main -->
        <div>
          <a-row type="flex">
            <a-col flex="100px"><span>总进度</span></a-col>
            <a-col flex="auto">
              <a-tooltip :title="totalTitle">
                <a-progress
                  :percent="taskProcess.total.percentOfComplete"
                  :success-percent="taskProcess.total.initialized"
                  :strokeWidth="18"
                /> </a-tooltip
            ></a-col>
          </a-row>
          <a-row type="flex">
            <a-col flex="100px"><span>同步到BDP</span></a-col>
            <a-col flex="auto">
              <a-tooltip :title="bdpTitle">
                <a-progress
                  :percent="taskProcess.bdp.percentOfComplete"
                  :success-percent="taskProcess.bdp.initialized"
                  :strokeWidth="18"
                /> </a-tooltip
            ></a-col>
          </a-row>
          <a-row type="flex">
            <a-col flex="100px"><span>同步到ES</span></a-col>
            <a-col flex="auto">
              <a-tooltip :title="esTitle">
                <a-progress
                  :percent="taskProcess.es.percentOfComplete"
                  :success-percent="taskProcess.es.initialized"
                  :strokeWidth="18"
                /> </a-tooltip
            ></a-col>
          </a-row>
          <a-row type="flex">
            <a-col flex="100px"><span>同步到FPS</span></a-col>
            <a-col flex="auto">
              <a-tooltip :title="fpsTitle">
                <a-progress
                  :percent="taskProcess.fps.percentOfComplete"
                  :success-percent="taskProcess.fps.initialized"
                  :strokeWidth="18"
                /> </a-tooltip
            ></a-col>
          </a-row>
        </div>
      </div>

      <div class="hp-content-card">
        <!-- title -->
        <div class="hp-content-card-title"><span>流量监控</span></div>
        <!-- main -->
        <div>
          <div
            ref="myChart_1"
            style="width: 100%; height: 520px"
            id="myChart_1"
          ></div>
        </div>
      </div>

      <div class="hp-content-card">
        <!-- title -->
        <div class="hp-content-card-title"><span>资源使用监控</span></div>
        <!-- main -->
        <div>
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
    </div>
  </div>
</template>

<script>
import { onMounted, reactive, computed, toRaw } from "@vue/runtime-core";
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

// 轉換函數 後端二維數組 to echarts 數據
const initOptions = (ori, tar) => {
  const _tar = cloneDeep(tar);
  _tar.legend = ori[0].slice(1);
  _tar.legend.forEach((item) => {
    let o = Object.create(null);
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
      ...state,
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
    };
  },
  async mounted() {
    try {
      await this.initEngineriesChart();
      await this.initCpuChart();
      await this.initMemChart();
    } catch (err) {
      console.log("init chart error", err);
    }
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
      const chartData = cloneDeep(that.engineriesChartData);
      console.log("chartData", chartData);
      if (chartDom) {
        const myChart_1 = echarts.init(chartDom);
        const option = {
          tooltip: {
            trigger: "axis",
          },
          legend: {
            data: chartData.legend,
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
          },
          yAxis: {
            type: "value",
          },
          series: chartData.series,
        };

        option && myChart_1.setOption(option);
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
      if (chartDom) {
        const myChart_2 = echarts.init(chartDom);
        const option = {
          title: {
            text: "CPU资源使用情况",
            textStyle: {
              fontSize: 15,
            },
          },
          tooltip: {
            trigger: "axis",
          },
          legend: {
            data: chartData.legend,
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
          },
          yAxis: {
            type: "value",
          },
          series: chartData.series,
        };

        option && myChart_2.setOption(option);
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
      if (chartDom) {
        const myChart_3 = echarts.init(chartDom);
        const option = {
          title: {
            text: "MEM资源使用情况",
            textStyle: {
              fontSize: 15,
            },
          },
          tooltip: {
            trigger: "axis",
          },
          legend: {
            data: chartData.legend,
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
          },
          yAxis: {
            type: "value",
          },
          series: chartData.series,
        };

        option && myChart_3.setOption(option);
      }
    },
  },
};
</script>

<style lang="less" scoped>
.home-page-wrap {
  padding: 22px 20px;

  .hp-content {
    &-card {
      margin-top: 30px;
      max-width: 1160px;
      min-height: 160px;
      background: inherit;
      background-color: rgba(255, 255, 255, 1);
      box-sizing: border-box;
      border-width: 1px;
      border-style: solid;
      border-color: rgba(228, 228, 228, 1);
      border-radius: 5px;
      box-shadow: 5px 5px 5px rgb(0 0 0 / 35%);
      padding-left: 18px;
      padding-right: 18px;
      &-title {
        margin-top: 12px;
        margin-bottom: 12px;
        border-left: 5px solid rgba(45, 140, 240, 1);
        padding-left: 15px;
        height: 22px;
        font-family: "Arial Negreta", "Arial Normal", "Arial";
        font-weight: 700;
        font-style: normal;
        font-size: 16px;
        color: #797979;
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

    &-txt-t {
      font-family: "Arial Negreta", "Arial Normal", "Arial";
      font-weight: 700;
      font-style: normal;
      font-size: 20px;
    }

    &-txt-b {
      font-family: "Arial Normal", "Arial";
      font-weight: 400;
      font-style: normal;
      font-size: 14px;
      color: #515151;
    }
  }

  .my-chart {
    margin-bottom: 12px;
  }
}
</style>

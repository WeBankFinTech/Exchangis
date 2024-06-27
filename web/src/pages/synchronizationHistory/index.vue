<template>
  <div class="sync-history-wrap">
    <div class="sh-top">
      <!-- 表单搜索 -->
      <a-form :model="formState">
        <a-row :gutter="24">
          <a-col :span="4">
            <a-form-item label="执行ID">
              <a-input v-model:value="formState.jobExecutionId" placeholder="请输入" />
            </a-form-item>
          </a-col>

          <a-col :span="4">
            <a-form-item label="作业名称">
              <a-input v-model:value="formState.jobName" placeholder="请输入" />
            </a-form-item>
          </a-col>

          <a-col :span="4">
            <a-form-item label="作业状态">
              <a-select v-model:value="formState.status" placeholder="请选择任务状态">
                <a-select-option v-for="status in statusList" :value="status">{{statusMap[status]}}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="4">
            <a-form-item label="开始时间">
              <a-range-picker v-model:value="formState.time" show-time type="date"
                style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="4">
            <a-form-item>
              <a-button @click="clearData"><span class="sh-top-txt">重置</span></a-button>
              <a-button type="primary" @click="search" style="margin-left: 8px"><span
                  class="sh-top-txt" style="color: #fff">查询</span></a-button>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <div class="sh-bottom">
      <!-- 表格 -->
      <div class="sh-b-table">
        <a-table :columns="columns" :data-source="tableData" :pagination="pagination"
          @change="onChange" :scroll="{ x: 1600, y: 650 }">
          <template #operation="{ record }">
            <a-popconfirm v-if="unfinishedStatusList.indexOf(record.status) === -1"
              title="确定要删除这条历史吗？" ok-text="确定" cancel-text="取消"
              @confirm="onConfirmDel(record.jobExecutionId)">
              <a href="#">删除</a>
            </a-popconfirm>
            <a-popconfirm v-if="unfinishedStatusList.indexOf(record.status) > -1"
              title="确定要停止当前作业吗？" ok-text="确定" cancel-text="取消"
              @confirm="killTask(record.jobExecutionId)">
              <a href="#">停止当前作业</a>
            </a-popconfirm>
            <!--<a-divider type="vertical" />-->
            <!--<a @click="dyncSpeedlimit(record.taskName, record.jobId)">动态限速</a>-->
          </template>
          <template #status="{ record }">
            <span v-if="record.status != 'Running'">{{statusMap[record.status]}}</span>
            <div class="progress-bg" v-else>
              <div class="progress-bar" :style="{'width': `${record.progress * 100}%`}"></div>
            </div>
          </template>
          <template #jobExecutionId="{ record }">
            <a-tooltip @click="showInfoLog(record.jobExecutionId)">
              <template #title>{{record.jobExecutionId}}</template>
              <span style="color: rgb(46, 146, 247);cursor: pointer;">{{record.jobExecutionId}}</span>
            </a-tooltip>
          </template>
          <template #flow="{ record }">
            <span>{{record.flow}} Records/S</span>
          </template>
        </a-table>
        <!-- 分页 -->
        <!--<div class="sh-b-pagination"></div>-->
      </div>
    </div>

    <!-- 动态限速 弹窗 -->
    <a-modal v-model:visible="visibleSpeedLimit" title="动态限速" @ok="putSpeedLimit" okText="保存">
      <a-form :label-col="labelCol">
        <!-- 动态组件 -->
        <a-form-item v-for="item in speedLimit.speedLimitData" :key="item.field" :label="item.label"
          :name="item.label" class="speed-limit-label">
          <dync-render v-bind:param="item" @updateInfo="updateSpeedLimitData" />
        </a-form-item>
      </a-form>
    </a-modal>

    <bottom-log :jobId="jobId" v-if="!!jobId" @onCloseLog="closeInfoLog"></bottom-log>

  </div>
</template>

<script>
import {
  defineComponent,
  reactive,
  toRaw,
  ref,
  onMounted,
  defineAsyncComponent,
} from 'vue';
import { useRoute } from 'vue-router';
import { SearchOutlined } from '@ant-design/icons-vue';
import { cloneDeep } from 'lodash-es';
import {
  getSyncHistoryJobList,
  delSyncHistory,
  getSpeedLimit,
  saveSpeedLimit,
  killJob,
} from '@/common/service';
import { message } from 'ant-design-vue';
import { dateFormat, dateFormatSeconds } from '@/common/utils';
import bottomLog from '../jobManagement/components/syncBottomLog';

const columns = [
  {
    title: '执行ID',
    dataIndex: 'jobExecutionId',
    slots: {
      customRender: 'jobExecutionId',
    },
    ellipsis: true,
  },
  {
    title: '执行节点',
    dataIndex: 'executeNode',
  },
  {
    title: '项目名称',
    dataIndex: 'projectName',
  },
  {
    title: '作业名称',
    dataIndex: 'name',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
  },
  {
    title: '速率',
    dataIndex: 'flow',
    slots: {
      customRender: 'flow',
    },
  },
  {
    title: '创建用户',
    dataIndex: 'createUser',
  },
  {
    title: '执行用户',
    dataIndex: 'executeUser',
  },
  {
    title: '状态',
    dataIndex: 'status',
    slots: {
      customRender: 'status',
    },
  },
  {
    title: '最后更新时间',
    dataIndex: 'lastUpdateTime',
  },
  // {
  //   title: "参数",
  //   dataIndex: "address",
  // },
  {
    title: '操作',
    dataIndex: 'options',
    slots: {
      customRender: 'operation',
    },
  },
];

const statusMap = {
  Inited: '初始化',
  Scheduled: '准备',
  Running: '运行',
  WaitForRetry: '等待重试',
  Cancelled: '取消',
  Failed: '失败',
  Partial_Success: '部分成功',
  Success: '成功',
  Undefined: '未定义',
  Timeout: '超时',
};

const statusList = [
  'Inited',
  'Scheduled',
  'Running',
  'WaitForRetry',
  'Cancelled',
  'Failed',
  'Partial_Success',
  'Success',
  'Undefined',
  'Timeout',
];

const unfinishedStatusList = ['Inited', 'Scheduled', 'Running', 'WaitForRetry'];

export default {
  components: {
    SearchOutlined,
    dyncRender: defineAsyncComponent(() =>
      import('../jobManagement/components/dyncRender.vue')
    ),
    bottomLog,
  },
  setup() {
    const route = useRoute();
    let defaultExecutionId = route.query.jobExecutionId;
    const state = reactive({
      formState: {
        jobExecutionId: defaultExecutionId || '',
        jobId: '',
        jobName: '',
        status: '',
        time: [],
      },
    });
    defaultExecutionId = '';
    const visibleSpeedLimit = ref(false);
    const jobId = ref('');
    const showLogs = ref(false);
    const logs = reactive({
      logs: ['', '', '', ''],
    });
    const speedLimit = reactive({
      speedLimitData: [],
      selectItem: {},
    });
    let tableData = ref([]);
    let pagination = ref({
      total: 0,
      current: 1,
      pageSize: 10,
      showQuickJumper: true,
      showSizeChanger: true,
      showTotal: (total) => `总计 ${total} 条`,
    });

    // 根据 current 获取当前页的数据
    const getTableFormCurrent = (type) => {
      const formData = cloneDeep(state.formState);
      formData['launchStartTime'] = Date.parse(formData.time[0]) || '';
      formData['launchEndTime'] = Date.parse(formData.time[1]) || '';
      formData['current'] = pagination.value.current;
      formData['size'] = pagination.value.pageSize;
      delete formData.time;

      getSyncHistoryJobList(formData)
        .then((res) => {
          const { jobList } = res;
          jobList.forEach((item) => {
            item['createTime'] = item['createTime']
              ? dateFormatSeconds(item['createTime'])
              : '';
            item['lastUpdateTime'] = item['lastUpdateTime']
              ? dateFormatSeconds(item['lastUpdateTime'])
              : '';
            /*switch (item["status"]) {
              case "SUCCESS":
                item["status"] = "执行成功";
                break;
              case "FAILED":
                item["status"] = "执行失败";
                break;
              case "RUNNING":
                item["status"] = "运行中";
            }*/
            item['jobExecutionId'] = item['jobExecutionId'] || item['jobId'];
            item['key'] = item['jobExecutionId'];
          });
          if (type == 'search') {
            tableData.value = [];
          }
          pagination.value.total = res['total'];
          tableData.value = jobList;
        })
        .catch((err) => {
          console.log('syncHistory error', err);
        });
    };

    const search = () => {
      jobId.value = null;
      pagination.value.current = 1;
      getTableFormCurrent('search');
    };

    const clearData = () => {
      state.formState['jobId'] = '';
      state.formState['jobName'] = '';
      state.formState['status'] = '';
      state.formState['time'] = [];
      state.formState['jobExecutionId'] = '';
      search();
    };

    const onChange = ({ current, pageSize }) => {
      pagination.value.current = current;
      pagination.value.pageSize = pageSize;
      getTableFormCurrent('onChange');
    };

    const showInfoLog = (id) => {
      jobId.value = id;
      showLogs.value = true;
    };

    const closeInfoLog = () => {
      jobId.value = null;
    };

    const onConfirmDel = (jobExecutionId) => {
      if (jobExecutionId) {
        delSyncHistory(jobExecutionId)
          .then((res) => {
            message.success('删除成功');
            search();
          })
          .catch((err) => {
            console.log('delSyncHistory error', err);
          });
      }
    };

    const dyncSpeedlimit = (taskName, jobId) => {
      visibleSpeedLimit.value = true;
      speedLimit.selectItem = { taskName, jobId };
      getSpeedLimit({ taskName, jobId })
        .then((res) => {
          res.ui && (speedLimit.speedLimitData = res.ui);
        })
        .catch((err) => {
          console.log('dyncSpeedlimit error', err);
        });
    };

    const updateSpeedLimitData = (e) => {
      const _data = cloneDeep(speedLimit.speedLimitData);
      _data.forEach((item) => {
        if (item.key == e.key) {
          item = e;
        }
      });
      speedLimit.speedLimitData = _data;
    };

    const putSpeedLimit = () => {
      const params = toRaw(speedLimit.selectItem);
      const body = toRaw(speedLimit.speedLimitData);
      saveSpeedLimit(params, body)
        .then((res) => {
          speedLimit.selectItem = {};
          speedLimit.speedLimitData = [];
          message.success('保存成功');
        })
        .catch((err) => {
          console.log('saveSpeedLimit error', err);
        });
    };

    const killTask = (jobExecutionId) => {
      killJob(jobExecutionId)
        .then((res) => {
          message.success('停止成功');
          search();
        })
        .catch((err) => {
          console.log(err);
          message.error('停止失败');
        });
    };

    onMounted(() => {
      search();
    });

    return {
      ...state,
      search,
      clearData,
      columns,
      statusMap,
      statusList,
      unfinishedStatusList,
      tableData,
      pagination,
      showInfoLog,
      closeInfoLog,
      dyncSpeedlimit,
      onChange,
      onConfirmDel,
      speedLimit,
      visibleSpeedLimit,
      showLogs,
      logs,
      jobId,
      updateSpeedLimitData,
      putSpeedLimit,
      labelCol: {
        style: {
          width: '150px',
        },
      },
      killTask
    };
  },
};
</script>

<style lang="less" scoped>
@import '../../common/content.less';
.sync-history-wrap {
  display: flex;
  flex-direction: column;
  height: 100vh;
}
.sh-top {
  flex: 0 0 64px;
  height: 64px;
  width: 100%;
  padding: 16px 24px;
  box-sizing: border-box;
  border-bottom: 1px solid;
  border-color: #dee4ec;
  background-color: #fff;
  min-width: 1000px;
  .sh-top-txt {
    font-family: PingFangSC-Regular;
    font-size: 14px;
    color: rgba(0, 0, 0, 0.65);
    text-align: left;
    line-height: 22px;
    font-weight: 400;
  }
}
.sh-bottom {
  padding: 24px;
  background-color: #fff;
  flex: 1;
  overflow: auto;
  :deep(.ant-form-item-label > label) {
    width: 80px;
    text-align: right;
    display: inline-block;
    line-height: 30px;
  }
  :deep(.ant-table-thead > tr > th) {
    height: 60px;
  }
  :deep(.ant-table-tbody > tr > td) {
    height: 60px;
  }
}

.log-btns {
  margin: -40px 0 20px;
  float: right;
  > button {
    margin-left: 10px;
  }
}
.progress-bg {
  position: relative;
  display: inline-block;
  width: 100%;
  overflow: hidden;
  vertical-align: middle;
  background-color: #c9c9c9;
  .progress-bar {
    position: relative;
    height: 12px;
    background: #52c41a;
  }
}
</style>
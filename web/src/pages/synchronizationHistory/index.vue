<template>
  <div class="sync-history-wrap">
    <div class="sh-top">
      <!-- 表单搜索 -->
      <div class="sh-top-left">
        <a-form layout="inline" :model="formState">
          <a-form-item
            label="作业ID"
            style="width: 235px; padding-bottom: 10px"
          >
            <a-input v-model:value="formState.jobId" placeholder="请输入" />
          </a-form-item>

          <a-form-item
            label="任务名称"
            style="width: 235px; padding-bottom: 10px"
          >
            <a-input v-model:value="formState.taskName" placeholder="请输入" />
          </a-form-item>

          <a-form-item
            label="任务状态"
            style="width: 235px; padding-bottom: 10px"
          >
            <a-select
              v-model:value="formState.status"
              placeholder="请选择任务状态"
            >
              <a-select-option value="SUCCESS">执行成功</a-select-option>
              <a-select-option value="FAILED">执行失败</a-select-option>
              <a-select-option value="RUNNING">运行中</a-select-option>
            </a-select>
          </a-form-item>

          <a-form-item label="触发时间" style="width: 488px">
            <a-range-picker
              v-model:value="formState.time"
              show-time
              type="date"
              style="width: 100%"
            />
          </a-form-item>
        </a-form>
      </div>

      <div class="sh-top-right">
        <div class="sh-top-search">
          <a-button type="primary" @click="search">
            <template #icon><SearchOutlined /></template>
            查询
          </a-button>
        </div>
      </div>
    </div>

    <div class="sh-bottom">
      <!-- 表格 -->
      <div class="sh-b-table">
        <a-table
          :columns="columns"
          :data-source="tableData"
          :pagination="pagination"
          @change="onChange"
        >
          <template #operation="{ record }">
            <a @click="showInfoLog(record.key)">详细日志</a>
            <a-divider type="vertical" />
            <a-popconfirm
              title="确定要删除这条历史吗？"
              ok-text="确定"
              cancel-text="取消"
              @confirm="onConfirmDel(record.id)"
            >
              <a href="#">删除</a>
            </a-popconfirm>
            <a-divider type="vertical" />
            <a @click="dyncSpeedlimit(record.taskName, record.jobId)"
              >动态限速</a
            >
          </template>
        </a-table>
      </div>

      <!-- 分页 -->
      <div class="sh-b-pagination"></div>
    </div>

    <!-- 动态限速 弹窗 -->
    <a-modal
      v-model:visible="visibleSpeedLimit"
      title="动态限速"
      @ok="putSpeedLimit"
      okText="保存"
    >
      <a-form :label-col="labelCol">
        <!-- 动态组件 -->
        <a-form-item
          v-for="item in speedLimit.speedLimitData"
          :key="item.field"
          :label="item.label"
          :name="item.label"
          class="speed-limit-label"
        >
          <dync-render v-bind:param="item" @updateInfo="updateSpeedLimitData" />
        </a-form-item>
      </a-form>
    </a-modal>
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
} from "vue";
import { SearchOutlined } from "@ant-design/icons-vue";
import { cloneDeep } from "lodash-es";
import {
  getSyncHistory,
  delSyncHistory,
  getSpeedLimit,
  saveSpeedLimit,
} from "@/common/service";
import { message } from "ant-design-vue";
import { dateFormat } from "@/common/utils";
const columns = [
  {
    title: "ID",
    dataIndex: "id",
  },
  // {
  //   title: "执行节点",
  //   dataIndex: "age",
  // },
  {
    title: "任务名称",
    dataIndex: "taskName",
  },
  // {
  //   title: "触发类型",
  //   dataIndex: "address",
  // },
  {
    title: "触发时间",
    dataIndex: "launchTime",
  },
  // {
  //   title: "速率（M/s）",
  //   dataIndex: "address",
  // },
  {
    title: "创建用户",
    dataIndex: "createUser",
  },
  {
    title: "状态",
    dataIndex: "status",
  },
  {
    title: "完成时间",
    dataIndex: "completeTime",
  },
  // {
  //   title: "参数",
  //   dataIndex: "address",
  // },
  {
    title: "操作",
    dataIndex: "options",
    slots: {
      customRender: "operation",
    },
  },
];

export default {
  components: {
    SearchOutlined,
    dyncRender: defineAsyncComponent(() =>
      import("../jobManagement/components/dyncRender.vue")
    ),
  },
  setup() {
    const state = reactive({
      formState: {
        jobId: "",
        taskName: "",
        status: "",
        time: [],
      },
    });
    const visibleSpeedLimit = ref(false);
    const speedLimit = reactive({
      speedLimitData: [],
      selectItem: {},
    });
    let pageSize = 10;
    let currentPage = 1;
    let tableData = ref([]);
    let pagination = ref({
      total: 0,
      pageSize: pageSize,
    });

    // 根据 current 获取当前页的数据
    const getTableFormCurrent = (current, type) => {
      if (
        tableData.value.length == pagination.value.total &&
        pagination.value.total > 0 &&
        type !== "search"
      )
        return;
      if (currentPage == current && type !== "search") return;

      const formData = cloneDeep(state.formState);
      formData["launchStartTime"] = Date.parse(formData.time[0]) || "";
      formData["launchEndTime"] = Date.parse(formData.time[1]) || "";
      currentPage = current;
      formData["current"] = currentPage;
      formData["size"] = pageSize;
      delete formData.time;

      getSyncHistory(formData)
        .then((res) => {
          const { result } = res;
          if (result.length > 0) {
            result.forEach((item) => {
              item["launchTime"] = dateFormat(item["launchTime"]);
              item["completeTime"] = dateFormat(item["completeTime"]);
              switch (item["status"]) {
                case "SUCCESS":
                  item["status"] = "执行成功";
                  break;
                case "FAILED":
                  item["status"] = "执行失败";
                  break;
                case "RUNNING":
                  item["status"] = "运行中";
              }
              item["key"] = item["id"];
            });
            if (type == "search") {
              tableData.value = [];
            }
            pagination.value.total = res["total"];
            tableData.value = tableData.value.concat(result);
          }
        })
        .catch((err) => {
          console.log("syncHistory error", err);
        });
    };

    const search = () => {
      getTableFormCurrent(1, "search");
    };

    const onChange = (page) => {
      const { current } = page;
      getTableFormCurrent(current, "onChange");
    };

    const showInfoLog = (key) => {};

    const onConfirmDel = (id) => {
      let tmp, idx;
      tableData.value.forEach((item, index) => {
        if (item.id == id) {
          tmp = item;
          idx = index;
        }
      });
      if (tmp) {
        delSyncHistory(tmp.id)
          .then((res) => {
            tableData.value.splice(idx, 1);
            message.success("删除成功");
          })
          .catch((err) => {
            console.log("delSyncHistory error", err);
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
          console.log("dyncSpeedlimit error", err);
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
          message.success("保存成功");
        })
        .catch((err) => {
          console.log("saveSpeedLimit error", err);
        });
    };

    onMounted(() => {
      search();
    });

    return {
      ...state,
      search,
      columns,
      tableData,
      pagination,
      showInfoLog,
      dyncSpeedlimit,
      onChange,
      onConfirmDel,
      speedLimit,
      visibleSpeedLimit,
      updateSpeedLimitData,
      putSpeedLimit,
      labelCol: {
        style: {
          width: "150px",
        },
      },
    };
  },
};
</script>

<style lang="less" scoped>
.sh-top {
  display: flex;
  .sh-top-left {
    flex: 3;
    padding: 15px 45px;
  }
  .sh-top-right {
    flex: 1;
    position: relative;
    .sh-top-search {
      position: absolute;
      right: 30px;
      bottom: 12px;
    }
  }
}
</style>

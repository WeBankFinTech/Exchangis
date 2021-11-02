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
              placeholder="请选择日期"
              style="width: 100%"
            />
          </a-form-item>

          <!-- <a-form-item label="结束时间">
            <a-date-picker
              v-model:value="formState.completeTime"
              show-time
              type="date"
              placeholder="请选择日期"
              style="width: 100%"
            />
          </a-form-item> -->
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
            <a @click="onDelete(record.key)">删除</a>
            <a-divider type="vertical" />
            <a @click="dyncSpeedlimit(record.key)">动态限速</a>
          </template>
        </a-table>
      </div>

      <!-- 分页 -->
      <div class="sh-b-pagination"></div>
    </div>
  </div>
</template>

<script>
import { defineComponent, reactive, toRaw, ref, onMounted } from "vue";
import { SearchOutlined } from "@ant-design/icons-vue";
import { getSyncHistory } from "@/common/service";
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

      const formData = Object.assign(
        JSON.parse(JSON.stringify(toRaw(state.formState)))
      );
      formData["launchStartTime"] = Date.parse(formData.time[0]) || "";
      formData["launchEndTime"] = Date.parse(formData.time[1]) || "";
      currentPage = current;
      formData["current"] = currentPage;
      formData["size"] = pageSize;
      delete formData.time;

      getSyncHistory(formData)
        .then((res) => {
          if (res.result.length > 0) {
            const result = res.result || [];
            result.forEach((item) => {
              item["launchTime"] = formatDate(item["launchTime"]);
              item["completeTime"] = formatDate(item["completeTime"]);
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

    function formatDate(d) {
      let date = new Date(d);
      let YY = date.getFullYear() + "-";
      let MM =
        (date.getMonth() + 1 < 10
          ? "0" + (date.getMonth() + 1)
          : date.getMonth() + 1) + "-";
      let DD = date.getDate() < 10 ? "0" + date.getDate() : date.getDate();
      let hh =
        (date.getHours() < 10 ? "0" + date.getHours() : date.getHours()) + ":";
      let mm =
        (date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes()) +
        ":";
      let ss =
        date.getSeconds() < 10 ? "0" + date.getSeconds() : date.getSeconds();
      return YY + MM + DD + " " + hh + mm + ss;
    }

    const onChange = (page) => {
      const { current } = page;
      getTableFormCurrent(current, "onChange");
    };

    const showInfoLog = (key) => {};

    const onDelete = (key) => {};

    const dyncSpeedlimit = (key) => {};

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
      onDelete,
      dyncSpeedlimit,
      onChange,
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

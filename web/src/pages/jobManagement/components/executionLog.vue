<template>
  <div class="exec-content">
    <a-select
      v-model:value="curLogId"
      ref="select"
      :options="checkOptions.options"
      @change="changeData(curLogId)"
      style="position: absolute;width: 90px;left: 21px;z-index: 1;top: 7px"
    >
    </a-select>
    <a-tabs type="card" default-active-key="all" size="small">
      <a-tab-pane key="all" tab="All">
        <a-textarea id="t1" :auto-size="{ minRows: 10, maxRows: 10 }" v-bind:value="curLog.all"></a-textarea>
      </a-tab-pane>
      <a-tab-pane key="error" tab="Error">
        <a-textarea id="t2" :auto-size="{ minRows: 10, maxRows: 10 }" v-bind:value="curLog.error"></a-textarea>
      </a-tab-pane>
      <a-tab-pane key="warn" tab="Warning">
        <a-textarea id="t3" :auto-size="{ minRows: 10, maxRows: 10 }" v-bind:value="curLog.warn"></a-textarea>
      </a-tab-pane>
      <a-tab-pane key="info" tab="Info">
        <a-textarea id="t4" :auto-size="{ minRows: 10, maxRows: 10 }" v-bind:value="curLog.info"></a-textarea>
      </a-tab-pane>
    </a-tabs>
    <a-input-search v-model:value="searchKeyword" placeholder="关键字" style="position: absolute;width: 120px;left:420px;z-index: 1;top: 5px" @search="onSearch"></a-input-search>
    <a-input-search v-model:value="ignoreKeyword" placeholder="忽略字" style="position: absolute;width: 120px;left:550px;z-index: 1;top: 5px" @search="onSearch2"></a-input-search>
  </div>
</template>
<script>
import { defineComponent, h, toRaw, watch, computed, reactive, ref, nextTick, onBeforeUnmount} from "vue"
import { getJobExecLog, getTaskExecLog } from "@/common/service"
import { message, notification } from "ant-design-vue";

export default defineComponent({
  props: {
    param: Object
  },
  emits: ["updateInfo"],
  setup(props, context) {
    let { id, list} = props.param
    const newProps = computed(() => JSON.parse(JSON.stringify(props.param)))
    watch(newProps, (val, oldVal) => {
      id = val.id
      list = val.list
      _updateInfo()
      if (val.id && val.id !== oldVal.id) {
        changeData(id)
      }
    })
    let curLogId,
      curLog = reactive({
        all: '',
        error: '',
        info: '',
        warn: ''
      }),
      searchKeyword,
      ignoreKeyword
    let logs = reactive({
      logs: {},
      endLine: 0,
      isEnd : false,
    })
    let showLogTimer = null

    let checkOptions = reactive({
      options: []
    })
    const _updateInfo = () => {
      checkOptions.options = [{
        value: id,
        label: 'Job'
      }]
      list.map((item) => {
        checkOptions.options.push({
          value: item.taskId,
          label: item.name
        })
      })
      curLogId = id
    }
    _updateInfo()

    const _showInfoLog = (curId) => {
      const fromLine = logs.endLine ?  logs.endLine + 1 : 0
      if (logs.isEnd) {
        clearInterval(showLogTimer)
        showLogTimer = null
        return message.warning("已经在最后一页")
      }
      const _updateLog = (res) => {
        logs.logs = res.logs
        logs.isEnd = res.isEnd
        logs.endLine = res.endLine
        curLog.all = curLog.all ? curLog.all + logs.logs?.all :  logs.logs?.all
        curLog.error = curLog.error ? curLog.error + logs.logs?.error : logs.logs?.error
        curLog.info = curLog.info ? curLog.info + logs.logs?.info : logs.logs?.info
        curLog.warn = curLog.warn ? curLog.warn + logs.logs?.warn : logs.logs?.warn
        //message.success("更新日志成功")
        nextTick(() => {
          const textareas = document.querySelectorAll('.exec-content textarea')
          textareas.forEach(textarea => {
            textarea.scrollTop = textarea.scrollHeight
          })
        })
      }
      if (curId === id) {
        getJobExecLog({
          id: curId,
          fromLine,
          onlyKeywords: searchKeyword,
          ignoreKeywords: ignoreKeyword
        })
          .then((res) => {
            _updateLog(res)
          })
          .catch((err) => {
            console.log(err)
            message.error("获取日志失败")
          })
      } else {
        getTaskExecLog({
          taskId: curId,
          id: id,
          fromLine,
          onlyKeywords: searchKeyword,
          ignoreKeywords: ignoreKeyword
        })
          .then((res) => {
            _updateLog(res)
          })
          .catch((err) => {
            console.log(err)
            message.error("获取日志失败")
          })
      }
    }

    const _resetData = () => {
      logs.logs = {}
      logs.endLine = 0
      logs.isEnd = false
      curLog.all = ''
      curLog.error = ''
      curLog.info = ''
      curLog.warn = ''
      clearInterval(showLogTimer)
      showLogTimer = null
    }

    const changeData = (curId) => {
      _resetData()
      showLogTimer = setInterval(() => {
        _showInfoLog(curId)
      }, 1000*5)
    }

    const onSearch = (keyword) => {
      searchKeyword = keyword
      _resetData()
      changeData(curLogId)
    }

    const onSearch2 = (keyword) => {
      ignoreKeyword = keyword
      _resetData()
      changeData(curLogId)
    }

    onBeforeUnmount(() => {
      clearInterval(showLogTimer)
    })

    return {
      curLogId: ref(curLogId),
      changeData,
      checkOptions,
      curLog,
      searchKeyword: ref(searchKeyword),
      ignoreKeyword: ref(ignoreKeyword),
      onSearch,
      onSearch2
    }
  }
})
</script>

<style scoped lang="less">
  .exec-content {
    position: relative;
    :deep(.ant-tabs-bar) {
      margin-left: 120px;
      margin-bottom: -4px;
    }
    textarea {
      border: none;
      box-shadow: none;
      &:deep(.ant-input:focus) {
         border: none;
      }
    }
  }
</style>

<template>
  <div class="exec-content">
    <a-select
      v-model:value="curLogId"
      ref="select"
      :options="checkOptions.options"
      @change="resetData();changeData(curLogId)"
      style="position: absolute;width: 90px;left: 21px;z-index: 1;top: 7px"
    >
    </a-select>
    <a-tabs type="card" default-active-key="all" size="small">
      <a-tab-pane key="all" tab="All" class="log-textarea" :key="maxRows">
        <a-textarea id="t1" :auto-size="{ minRows: 10, maxRows: maxRows }" v-bind:value="curLog.all" :key="maxRows"></a-textarea>
      </a-tab-pane>
      <a-tab-pane key="error" tab="Error" class="log-textarea" :key="maxRows">
        <a-textarea id="t2" :auto-size="{ minRows: 10, maxRows: maxRows }" v-bind:value="curLog.error" :key="maxRows"></a-textarea>
      </a-tab-pane>
      <a-tab-pane key="warn" tab="Warning" class="log-textarea" :key="maxRows">
        <a-textarea id="t3" :auto-size="{ minRows: 10, maxRows: maxRows }" v-bind:value="curLog.warn" :key="maxRows"></a-textarea>
      </a-tab-pane>
      <a-tab-pane key="info" tab="Info" class="log-textarea" :key="maxRows">
        <a-textarea id="t4" :auto-size="{ minRows: 10, maxRows: maxRows }" v-bind:value="curLog.info" :key="maxRows"></a-textarea>
      </a-tab-pane>
    </a-tabs>
    <a-input-search v-model:value="searchKeyword" placeholder="关键字" style="position: absolute;width: 120px;left:420px;z-index: 1;top: 5px" @search="onSearch"></a-input-search>
    <a-input-search v-model:value="ignoreKeyword" placeholder="忽略字" style="position: absolute;width: 120px;left:550px;z-index: 1;top: 5px" @search="onSearch2"></a-input-search>
    <a-input-search v-if="pauseLog.isPause" v-model:value="lastRows" placeholder="读取最后n行日志" style="position: absolute;width: 240px;left:680px;z-index: 1;top: 5px" @search="onSearch3"></a-input-search>
    <a-button
      size="small"
      @click="pauseFetchingLog(true)"
      type="link"
      v-if="!pauseLog.isPause"
      style="position: absolute;left:680px;z-index: 1;top: 8px"
    >{{logs.isEnd ? '显示读取最后n行日志' :'暂停日志拉取' }}</a-button>
    <a-button
      size="small"
      @click="pauseFetchingLog(false)"
      type="link"
      v-if="pauseLog.isPause"
      style="position: absolute;left:930px;z-index: 1;top: 8px"
    >{{pauseLog.pauseIsEnd ? '隐藏读取最后n行日志' : '恢复日志拉取' }}</a-button>
    <div style="text-align: center;position: fixed;bottom: 12px;left: 50%;z-index: 9;">
      <a-button size="small" style="margin: 0 5px;" :disabled="isLoading" @click="getLog('pre')">上一页</a-button>
      <a-button size="small" style="margin: 0 5px;" :disabled="isLoading" @click="getLog('next')">下一页</a-button>
      <a-button size="small" type="primary" style="margin: 0 5px;" :disabled="isLoading" @click="getLog('new')">查看最新日志</a-button>
    </div>
  </div>
</template>
<script>
import { defineComponent, h, toRaw, watch, computed, reactive, ref, nextTick, onBeforeUnmount} from "vue"
import { getJobExecLog, getTaskExecLog } from "@/common/service"
import { message, notification } from "ant-design-vue";

export default defineComponent({
  props: {
    param: Object,
    isShow: Boolean,
    maxRows: {
      type: [String, Number],
      default: 20
    }
  },
  emits: ["updateInfo"],
  setup(props, context) {
    let { id, list} = props.param
    const newProps = computed(() => JSON.parse(JSON.stringify(props.param)))
    watch(newProps, (val, oldVal) => {
      id = val.id
      list = val.list
      _updateInfo()
      if ((val.id && val.id !== oldVal.id) || (val.id && val.sync)) {
        resetData()
        changeData(id)
      }
    })

    const showProp = computed(() => `${props.isShow}`)
    watch(showProp, (newVal, oldVal) => {
      if(!props.isShow) {
        clearInterval(showLogTimer)
        pauseLog.isPause = false
      }
    })

    let pauseLog = reactive({
      isPause: false,
      pauseEndLine: 0,
      pauseIsEnd: false
    })
    let curLogId,
      curLog = reactive({
        all: '',
        error: '',
        info: '',
        warn: ''
      }),
      searchKeyword = ref(''),
      ignoreKeyword = ref(''),
      lastRows = ref('')
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

    // 更新日志
    const _updateLog = (res) => {
      logs.logs = res.logs
      logs.isEnd = res.isEnd
      const buildLog = (key) => {
        let arr = logs.logs[key] ? logs.logs[key].split('\n') : []
        for (let i = 0; i < arr.length; i++) {
          arr[i] = `${logs.endLine + i + 1}.   ${arr[i]}`
        }
        if (logs.logs[key]) {
          return curLog[key] ? curLog[key] + '\n' + arr.join('\n') : arr.join('\n')
        } else {
          return curLog[key] ? curLog[key] : ''
        }
      }
      curLog.all = buildLog('all')
      curLog.error = buildLog('error')
      curLog.info = buildLog('info')
      curLog.warn = buildLog('warn')
      logs.endLine = res.endLine
      nextTick(() => {
        const textareas = document.querySelectorAll('.exec-content textarea')
        textareas.forEach(textarea => {
          textarea.scrollTop = textarea.scrollHeight
        })
      })
    }

    const isLoading = ref(false);
    const recordLogs = ref({
      endLine: 0,
      isEnd: false
    })
    const getLog = (type) => {
      recordLogs.value = {endLine: logs.endLine, isEnd: logs.isEnd};
      let fromLine = 1;
      if (type === 'pre') {
        const page = Math.ceil((recordLogs.value.endLine || 0) / 50);
        if (page <= 1) {
          return message.warning("当前日志已经是第一页")
        }
        fromLine = (page-2) * 50 + 1;
      } else if (type === 'next') {
        if (recordLogs.value.endLine % 50 !== 0) {
          logs.isEnd = true;
          return message.warning("当前日志已经是最后一页");
        }
        fromLine = recordLogs.value.endLine + 1;
      }
      isLoading.value = true;
      if (curLogId === id) {
        getJobExecLog({
          id: curLogId,
          fromLine,
          onlyKeywords: searchKeyword.value,
          ignoreKeywords: ignoreKeyword.value || '[main],[SpringContextShutdownHook]',
          lastRows: lastRows.value
        })
          .then((res) => {
            if(res.logs && JSON.stringify(res.logs) !== '{}') {
              resetData();
              _updateLog(res)
            }
          })
          .catch((err) => {
            console.log(err)
            //message.error("获取日志失败")
            pauseFetchingLog(true);
          }).finally(() => {
            isLoading.value = false;
          })
      } else {
        getTaskExecLog({
          taskId: curLogId,
          id: id,
          fromLine,
          onlyKeywords: searchKeyword.value,
          ignoreKeywords: ignoreKeyword.value || '[main],[SpringContextShutdownHook]',
          lastRows: lastRows.value
        })
          .then((res) => {
            if(res.logs && JSON.stringify(res.logs) !== '{}') {
              resetData();
              _updateLog(res)
            }
          })
          .catch((err) => {
            console.log(err)
            message.error("获取日志失败")
            pauseFetchingLog(true);
          }).finally(() => {
            isLoading.value = false;
          })
      }
    }

    const _showInfoLog = (curId) => {
      if (logs.isEnd) {
        isLoading.value = false;
        clearInterval(showLogTimer)
        showLogTimer = null
        return message.warning("查询日志结束")
      }
      isLoading.value = true;
      if (curId === id) {
        getJobExecLog({
          id: curId,
          fromLine: 1,
          onlyKeywords: searchKeyword.value,
          ignoreKeywords: ignoreKeyword.value || '[main],[SpringContextShutdownHook]',
          lastRows: lastRows.value
        })
          .then((res) => {
            resetData(null, false);
            _updateLog(res)
          })
          .catch((err) => {
            isLoading.value = false;
            console.log(err)
            //message.error("获取日志失败")
            pauseFetchingLog(true);
          })
      } else {
        getTaskExecLog({
          taskId: curId,
          id: id,
          fromLine: 1,
          onlyKeywords: searchKeyword.value,
          ignoreKeywords: ignoreKeyword.value || '[main],[SpringContextShutdownHook]',
          lastRows: lastRows.value
        })
          .then((res) => {
            resetData(null, false);
            _updateLog(res)
          })
          .catch((err) => {
            isLoading.value = false;
            console.log(err)
            message.error("获取日志失败")
            pauseFetchingLog(true);
          })
      }
    }

    const resetData = (data = null, isClear = true) => {
      logs.logs = {}
      logs.endLine = 0
      pauseLog.pauseEndLine = 0
      logs.isEnd = false
      pauseLog.pauseIsEnd = false
      curLog.all = ''
      curLog.error = ''
      curLog.info = ''
      curLog.warn = ''
      if (isClear) {
        clearInterval(showLogTimer)
        showLogTimer = null
      }
      if (data) {
        logs.endLine = data.endLine
        logs.isEnd = data.isEnd
      }
    }

    const changeData = (curId) => {
      curLogId = curId
      pauseLog.isPause = false
      _showInfoLog(curId)
      showLogTimer = setInterval(() => {
        _showInfoLog(curId)
      }, 1000*2)
    }

    const onSearch = (keyword) => {
      searchKeyword.value = keyword
      resetData()
      changeData(curLogId)
    }

    const onSearch2 = (keyword) => {
      ignoreKeyword.value = keyword
      resetData()
      changeData(curLogId)
    }

    const onSearch3 = (keyword) => {
      if (keyword && !/^[1-9]\d*$/.test(keyword)) {
        return message.error('请正确输入')
      }
      lastRows.value = keyword
      resetData()
      _showInfoLog(curLogId)
    }

    const pauseFetchingLog = (isPause) => {
      pauseLog.isPause = isPause
      if (isPause) {
        pauseLog.pauseEndLine = logs.endLine
        pauseLog.pauseIsEnd = logs.isEnd
        clearInterval(showLogTimer)
      } else {
        lastRows.value = ''
        if (!pauseLog.pauseIsEnd) {
          logs.logs = {}
          logs.endLine = 0
          logs.isEnd = false
          /*resetData({
            endLine: pauseLog.pauseEndLine,
            isEnd: pauseLog.pauseIsEnd
          })*/
          changeData(curLogId)
        }
      }
    }

    onBeforeUnmount(() => {
      clearInterval(showLogTimer)
    })
 
    const maxRows = ref(10)
    watch(() => props.maxRows, (cur, pre) => {
       maxRows.value = cur
    }, {immediate: true})
    return {
      curLogId: ref(curLogId),
      changeData,
      checkOptions,
      curLog,
      pauseLog,
      searchKeyword,
      ignoreKeyword,
      lastRows,
      onSearch,
      onSearch2,
      onSearch3,
      logs,
      pauseFetchingLog,
      resetData,
      maxRows,
      isLoading,
      getLog
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

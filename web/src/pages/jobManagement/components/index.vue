<template>
    <div class="container jd-container">
        <div class="tools-bar">
            <span @click="modalCfg.visible = true"><SettingOutlined />配置</span>
            <div class="divider"></div>
            <span v-if="!spinning" @click="executeTask"><CaretRightOutlined />执行</span>
            <a-popconfirm
                v-else
                title="是否终止?"
                ok-text="确定"
                cancel-text="取消"
                @confirm="killTask"
                @cancel="cancel"
            >
                <span><StopFilled />停止</span>
            </a-popconfirm>
            <div class="divider"></div>
            <span @click="saveAll()"><SaveOutlined />保存</span>
            <div class="divider"></div>
            <span @click="executeHistory"><HistoryOutlined />执行历史</span>
        </div>
        <div class="jd-outWrapper">
            <div v-if="list.length !== 0" class="jd-content">
                <a-spin size="large" :spinning="loading" wrapperClassName="wrap-spin">
                    <div class="jd_left">
                        <div class="left-wrap">
                            <div class="sub-title">
                                <span>子任务列表</span>
                            </div>
                            <div v-for="(item, idx) in list" :key="idx" :class="getClass(idx)">
                                <div class="task-title">
                                    <div
                                        v-if="
                                            activeIndex !== idx || (activeIndex === idx && !nameEditable)
                                        "
                                        class="subjobName"
                                        :title="item.subJobName"
                                        @click="changeCurTask(idx)"
                                    >
                                        {{item.subJobName}}
                                    </div>
                                    <a-input
                                        v-if="activeIndex === idx && nameEditable"
                                        ref="currentInput"
                                        v-model:value="item.subJobName"
                                        style="width: 115px"
                                        @pressEnter="nameEditable = false"
                                        @blur="nameEditable = false"
                                    ></a-input>
                                    <a-popconfirm
                                        title="是否删除子任务?"
                                        ok-text="确定"
                                        cancel-text="取消"
                                        @confirm="deleteSub(idx)"
                                        @cancel="cancel"
                                    >
                                        <DeleteOutlined class="delete-icon" />
                                    </a-popconfirm>
                                    <a-popconfirm
                                        title="是否复制子任务?"
                                        ok-text="确定"
                                        cancel-text="取消"
                                        @confirm="copySub(item)"
                                        @cancel="cancel"
                                    >
                                        <CopyOutlined class="copy-icon" />
                                    </a-popconfirm>
                                    <EditOutlined
                                        v-if="activeIndex === idx && !nameEditable"
                                        class="rename-icon"
                                        @click="getEditableInput"
                                    />
                                </div>
                                <template
                                    v-if="
                                        item.dataSourceIds &&
                                            item.dataSourceIds.source &&
                                            item.dataSourceIds.source.db
                                    "
                                >
                                    <div
                                        class="sub-table"
                                        :title="
                                            item.dataSourceIds.source.db +
                                                '.' +
                                                item.dataSourceIds.source.table
                                        "
                                        @click="changeCurTask(idx)"
                                    >
                                        {{item.dataSourceIds.source.db +
                                            "." +
                                            item.dataSourceIds.source.table}}
                                    </div>
                                    <div class="arrow-down-icon" @click="changeCurTask(idx)"><ArrowDownOutlined /></div>
                                    <div
                                        class="sub-table"
                                        :title="
                                            item.dataSourceIds.sink.db + '.' + item.dataSourceIds.sink.table
                                        "
                                        @click="changeCurTask(idx)"
                                    >
                                        {{item.dataSourceIds.sink.db + "." + item.dataSourceIds.sink.table}}
                                    </div>
                                </template>
                            </div>
                        </div>
                        <a-button
                            size="large"
                            style="
                width: 218px;
                font-family: PingFangSC-Regular;
                font-size: 14px;
                line-height: 22px;
                font-weight: 400;
                border: 1px dashed #dee4ec;
                margin: 0 15px;
                "
                            @click="addNewTask"
                        >
                            <template #icon> <PlusOutlined /></template>添加子任务
                        </a-button>
                    </div>
                    <div class="jd_right">
                        <div>
                            <DataSource
                                v-if="curTask"
                                :key="curTask.subJobName"
                                v-bind:dsData="curTask"
                                v-bind:engineType="curTask.engineType"
                                :projectId="jobData.projectId"
                                @updateSourceInfo="updateSourceInfo"
                                @updateSinkInfo="updateSinkInfo"
                                @updateSourceParams="updateSourceParams"
                                @updateSinkParams="updateSinkParams"
                            />
                        </div>
                        <div style="position: relative;">
                            <a-select
                                v-if="curType"
                                v-model:value="curType"
                                style="width: 140px;position: absolute;left: 46px;top: 20px;"
                                class="top-line-select"
                                :allowClear="true"
                                :placeholder="$t('请选择')"
                                @select="selectTranforms"
                                >
                                <a-select-option 
                                    v-for="item of processOptions" 
                                    :value="item.value" 
                                    :key="item.value">
                                    {{ item.label }}
                                </a-select-option>
                            </a-select>
                            <template v-if="curType === 'MAPPING'"> 
                                <FieldMap
                                    v-if="curTask"
                                    v-bind:srcTableNotExist="curTask.dataSourceIds.source.tableNotExist"
                                    v-bind:sinkTableNotExist="curTask.dataSourceIds.sink.tableNotExist"
                                    v-bind:fmData="curTask.transforms"
                                    v-bind:fieldsSink="fieldsSink"
                                    v-bind:fieldsSource="fieldsSource"
                                    v-bind:deductions="deductions"
                                    v-bind:addEnabled="addEnable"
                                    v-bind:transformEnable="transformEnable"
                                    v-bind:engineType="curTask.engineType"
                                    @updateFieldMap="updateFieldMap"
                                />
                            </template>
                            <template v-else-if="curType === 'PROCESSOR'">
                                <!-- 后置控制器 -->
                                <processor 
                                    ref="processorRef"
                                    :key="activeIndex"
                                    v-bind:jobId="curTab.id"
                                    v-bind:procCodeId="curTask.transforms.code_id" 
                                    v-bind:copyCodeId="curTask.transforms.copy_code_id"
                                    @updateProMap="updateProMap"/>
                            </template>
                        </div>
                        <div>
                            <ProcessControl
                                v-if="curTask"
                                v-bind:psData="curTask.settings"
                                v-bind:engineType="curTask.engineType"
                                @updateProcessControl="updateProcessControl"
                            />
                        </div>
                    </div>
                </a-spin>
            </div>
            <div v-if="list.length === 0" class="cardWrap">
                <a-spin :spinning="loading">
                    <div class="emptyTab">
                        <div class="void-page-wrap">
                            <div class="void-page-main">
                                <div class="void-page-main-img">
                                    <img src="../../../assets/img/void_page.png" alt="空页面" />
                                </div>
                                <div class="void-page-main-title">
                                    <span>该任务下没有子任务，请先创建一个子任务</span>
                                </div>
                                <div class="void-page-main-button">
                                    <a-button
                                        type="primary"
                                        size="large"
                                        style="
                    font-family: PingFangSC-Regular;
                    font-size: 14px;
                    line-height: 22px;
                    font-weight: 400;
                    "
                                        @click="addNewTask"
                                    >
                                        <template #icon> <PlusOutlined /></template>{{t("job.action.createJob")}}
                                    </a-button>
                                </div>
                            </div>
                        </div>
                    </div>
                </a-spin>
            </div>
        </div>
        <!-- 执行历史  jd-bottom -->
        <div v-show="visibleDrawer" class="jd-bottom">
            <div class="jd-bottom-top">
                <span>执行历史</span>
                <CloseOutlined
                    style="
            color: rgba(0, 0, 0, 0.45);
            font-size: 12px;
            position: absolute;
            right: 24px;
            top: 18px;
            cursor: pointer;
          "
                    @click="onCloseDrawer"
                />
            </div>
            <div class="jd-bottom-content">
                <a-table
                    :columns="ehColumns"
                    :data-source="ehTableData"
                    :pagination="ehPagination"
                    @change="onPageChange"
                >
                </a-table>
            </div>
        </div>
        <!-- 执行日志  jd-bottom -->
        <div :class="[visibleLog ? 'display-bottom' : 'hide-botttom']" class="jd-bottom jd-bottom-log" :style="bottomStyle">
            <div class="jd-bottom-top jd-bottom-log-top">
                 <!-- 放大 -->
                <ExpandOutlined 
                v-if="activeKey === '2'"
                style="
            color: rgba(0, 0, 0, 0.45);
            font-size: 12px;
            cursor: pointer;
            margin-right: 10px;
          "
                    @click="expandLog"/>
                <!--<span>执行日志</span>-->
                <CloseOutlined
                    style="
            color: rgba(0, 0, 0, 0.45);
            font-size: 12px;
            cursor: pointer;
          "
                    @click="onCloseLog"
                />
            </div>
            <div class="jd-bottom-content log-bottom-content">
                <a-tabs v-model:activeKey="activeKey" class="exec-info-tab">
                    <a-tab-pane key="1" tab="运行情况">
                        <div v-if="jobProgress.tasks" class="job-progress-percent job-progress-wrap">
                            <span>总进度<span style="font-size: 11px;color:rgba(0,0,0,0.5)">({{statusMap[jobStatus]}})</span></span>
                            <a-tooltip :title="jobProgress.title">
                                <a-progress v-if="jobProgress.failedTasks" :percent="jobProgress.percent" status="exception" />
                                <a-progress v-else :percent="jobProgress.percent" />
                            </a-tooltip>
                        </div>
                        <div v-if="jobProgress.tasks && jobProgress.tasks.Running" class="job-progress-wrap">
                            <span class="job-progress-title">正在运行</span>
                            <div class="job-progress-body">
                                <div v-for="(progress, index) in jobProgress.tasks.Running" class="job-progress-percent">
                                    <span :title="progress.name" style="color:#2e92f7;cursor: pointer;text-decoration:underline" @click="getTaskInfo(progress)">{{progress.name}}</span>
                                    <a-progress :percent="progress.progress * 100" />
                                    <metrics v-if="openMetricsId === progress.taskId && metricsInfo[progress.taskId]" :metricsInfo="metricsInfo" :progress="progress" style="margin-left: 100px"></metrics>
                                </div>
                            </div>
                        </div>
                        <div v-if="jobProgress.tasks && jobProgress.tasks.Scheduled" class="job-progress-wrap">
                            <span class="job-progress-title">准备中</span>
                            <div class="job-progress-body">
                                <div v-for="(progress, index) in jobProgress.tasks.Scheduled" class="job-progress-percent">
                                    <span :title="progress.name">{{progress.name}}</span><a-progress :percent="0" />
                                </div>
                            </div>
                        </div>
                        <div v-if="jobProgress.tasks && jobProgress.tasks.Inited" class="job-progress-wrap">
                            <span class="job-progress-title">初始化</span>
                            <div class="job-progress-body">
                                <div v-for="(progress, index) in jobProgress.tasks.Inited" class="job-progress-percent">
                                    <span :title="progress.name">{{progress.name}}</span><a-progress :percent="0" />
                                </div>
                            </div>
                        </div>
                        <div v-if="jobProgress.tasks && jobProgress.tasks.Failed" class="job-progress-wrap">
                            <span class="job-progress-title" style="color:#ff4d4f">失败</span>
                            <div class="job-progress-body">
                                <div v-for="(progress, index) in jobProgress.tasks.Failed" class="job-progress-percent">
                                    <span :title="progress.name" style="color:#2e92f7;cursor: pointer;text-decoration:underline" @click="getTaskInfo(progress)">{{progress.name}}</span>
                                    <a-progress :percent="progress.progress * 100" />
                                    <metrics v-if="openMetricsId === progress.taskId && metricsInfo[progress.taskId]" :metricsInfo="metricsInfo" :progress="progress" style="margin-left: 100px"></metrics>
                                </div>
                            </div>
                        </div>
                        <div v-if="jobProgress.tasks && jobProgress.tasks.Cancelled" class="job-progress-wrap">
                            <span class="job-progress-title" style="color:#ff4d4f">终止</span>
                            <div class="job-progress-body">
                                <div v-for="(progress, index) in jobProgress.tasks.Cancelled" class="job-progress-percent">
                                    <span :title="progress.name" style="color:#2e92f7;cursor: pointer;text-decoration:underline" @click="getTaskInfo(progress)">{{progress.name}}</span>
                                    <a-progress :percent="progress.progress * 100" />
                                    <metrics v-if="openMetricsId === progress.taskId && metricsInfo[progress.taskId]" :metricsInfo="metricsInfo" :progress="progress" style="margin-left: 100px"></metrics>
                                </div>
                            </div>
                        </div>
                        <div v-if="jobProgress.tasks && jobProgress.tasks.Success" class="job-progress-wrap">
                            <span class="job-progress-title">成功</span>
                            <div class="job-progress-body">
                                <div v-for="(progress, index) in jobProgress.tasks.Success" class="job-progress-percent">
                                    <span :title="progress.name" style="color:#2e92f7;cursor: pointer;text-decoration:underline" @click="getTaskInfo(progress)">{{progress.name}}</span>
                                    <a-progress :percent="progress.progress * 100" />
                                    <metrics v-if="openMetricsId === progress.taskId && metricsInfo[progress.taskId]" :metricsInfo="metricsInfo" :progress="progress" style="margin-left: 100px"></metrics>
                                </div>
                            </div>
                        </div>
                    </a-tab-pane>
                    <a-tab-pane key="2" tab="实时日志" force-render>
                        <execution-log :param="logParams" :isShow="visibleLog" :maxRows="maxRows"></execution-log>
                    </a-tab-pane>
                    <a-tab-pane key="3" tab="执行历史" force-render>
                        <a-table
                            style="margin: 0 -24px 0 24px;"
                            :columns="ehColumns"
                            :data-source="ehTableData"
                            :pagination="false"
                            :scroll="{ y: 240 }"
                        >
                            <template #jobExecutionId="{ record }">
                                <a-tooltip>
                                    <template #title>{{record.jobExecutionId}}</template>
                                    <router-link :to="`/synchronizationHistory?jobExecutionId=${jobExecutionId}`">
                                       {{record.jobExecutionId}}
                                    </router-link>
                                </a-tooltip>
                            </template>
                            <template #status="{ record }">
                                <span>{{statusMap[record.status]}}</span>
                            </template>
                            <template #createTime="{ record }">
                                <span>{{dateFormat(record.createTime)}}</span>
                            </template>
                        </a-table>
                    </a-tab-pane>
                </a-tabs>
            </div>
        </div>

        <config-drawer
            :id="modalCfg.id"
            v-model:visible="modalCfg.visible"
            :formData="configModalData"
            :dialogStyle="dialogStyle"
            @finish="handleModalFinish"
        />
        <copy-modal
            v-model:visible="modalCopy.visible"
            :origin="copyObj"
            @finish="handleModalCopy"
        />
    </div>
</template>
<script>
import { toRaw, h, defineAsyncComponent } from 'vue';
import {
    SettingOutlined,
    CaretRightOutlined,
    SaveOutlined,
    HistoryOutlined,
    DatabaseFilled,
    PlusSquareOutlined,
    CopyOutlined,
    DeleteOutlined,
    ArrowDownOutlined,
    CheckCircleOutlined,
    EditOutlined,
    MinusOutlined,
    PlusOutlined,
    CloseOutlined,
    StopFilled,
    ExpandOutlined
} from '@ant-design/icons-vue';
import { message, notification } from 'ant-design-vue';
import { useI18n } from '@fesjs/fes';
import {
    getJobInfo,
    saveProject,
    getSettingsParams,
    getFields,
    executeTask,
    updateTaskConfiguration,
    getSyncHistory,
    executeJob,
    getLogs,
    getJobStatus,
    getJobTasks,
    getProgress,
    getMetrics,
    killJob
} from '@/common/service';
import { randomString, moveUpDown, dateFormat } from '../../../common/utils';
import executionLog from './executionLog';
import metrics from './metricsInfo';
import processor from './processor.vue';
import { cloneDeep } from "lodash-es";
/**

/**
 * 用于判断一个对象是否有空 value,如果有返回 true
 */
const objectValueEmpty = (obj) => {
    let isEmpty = false;
    Object.keys(obj).forEach((o) => {
        if (obj[o] === null || obj[o] === '' || obj[o] === undefined) {
            isEmpty = true;
        }
    });
    return isEmpty;
};

const formatDate = (d) => {
    const date = new Date(d);
    const YY = `${date.getFullYear()}-`;
    const MM = `${date.getMonth() + 1 < 10
        ? `0${date.getMonth() + 1}`
        : date.getMonth() + 1}-`;
    const DD = date.getDate() < 10 ? `0${date.getDate()}` : date.getDate();
    const hh = `${date.getHours() < 10 ? `0${date.getHours()}` : date.getHours()}:`;
    const mm = `${date.getMinutes() < 10 ? `0${date.getMinutes()}` : date.getMinutes()
    }:`;
    const ss = date.getSeconds() < 10 ? `0${date.getSeconds()}` : date.getSeconds();
    return `${YY + MM + DD} ${hh}${mm}${ss}`;
};

const ehColumns = [
    {
        title: '编号',
        dataIndex: 'key',
        align: 'center'
    },
    {
        title: '执行ID',
        dataIndex: 'jobExecutionId',
        slots: {
            customRender: 'jobExecutionId'
        },
        align: 'center'
    },
    {
        title: '状态',
        dataIndex: 'status',
        slots: {
            customRender: 'status'
        },
        align: 'center'
    },
    {
        title: '创建时间',
        dataIndex: 'createTime',
        slots: {
            customRender: 'createTime'
        },
        align: 'center'
    }
];

const DEF_OPTIONS = [
    { label: '字段映射', value: 'MAPPING' },
    { label: '后置控制器', value: 'PROCESSOR'}
]

const defaultMaxRows = Math.floor(260 / 22.2);

export default {
    components: {
        SettingOutlined,
        CaretRightOutlined,
        SaveOutlined,
        HistoryOutlined,
        DatabaseFilled,
        PlusSquareOutlined,
        CopyOutlined,
        DeleteOutlined,
        ArrowDownOutlined,
        CheckCircleOutlined,
        EditOutlined,
        PlusOutlined,
        ExpandOutlined,
        'config-drawer': defineAsyncComponent(() => import('./configDrawer.vue')),
        'copy-modal': defineAsyncComponent(() => import('./copyModal.vue')),
        DataSource: defineAsyncComponent(() => import('./dataSource.vue')),
        FieldMap: defineAsyncComponent(() => import('./fieldMap.vue')),
        ProcessControl: defineAsyncComponent(() => import('./processControl.vue')),
        MinusOutlined,
        CloseOutlined,
        StopFilled,
        executionLog,
        metrics,
        processor
    },
    data() {
        const { t } = useI18n({ useScope: 'global' });
        return {
            loading: false,
            name: '',
            modalCfg: {
                id: '',
                visible: false
            },
            modalCopy: {
                visible: false
            },
            jobData: {},
            copyObj: {},
            list: [],
            activeIndex: -1,
            curTask: null,
            nameEditable: false,

            fieldsSource: [],
            fieldsSink: [],
            deductions: [],
            addEnable: false,
            transformEnable: false, // 控制是否可以编辑转换函数
            configModalData: {},

            visibleDrawer: false,
            visibleLog: false,
            pageSize: 10,
            currentPage: 1,
            ehColumns,
            ehTableData: [],
            ehPagination: {
                total: 0,
                pageSize: 10
            },
            t,
            spinning: false,
            logs: {
                logs: ['', '', '', '']
            },
            executeId: '',
            jobExecutionId: '',
            jobStatus: '',
            allTaskStatus: '',
            jobStatusTimer: null,
            tasklist: [],
            progressTimer: null,
            jobProgress: {},
            metricsInfo: {},
            openMetricsId: '',
            statusMap: {
                Inited: '初始化',
                Scheduled: '准备',
                Running: '运行',
                WaitForRetry: '等待重试',
                Cancelled: '取消',
                Failed: '失败',
                Partial_Success: '部分成功',
                Success: '成功',
                Undefined: '未定义',
                Timeout: '超时'
            },
            activeKey: '1',
            dateFormat,
            curTab: {
                id: ''
            },
            dialogStyle: {
                right: 0,
            },
            bottomStyle: '', //底部样式
            maxRows: defaultMaxRows,
            processTypes: [], // MAPPING字段映射 | PROCESSOR后置控制器
            curType: ''
        };
    },
    computed: {
        logParams() {
            return {
                list: this.tasklist,
                id: this.jobExecutionId
            };
        },
        processOptions() {
            const filters = DEF_OPTIONS.filter(v => this.processTypes.includes(v.value));
            return filters;
        }
    },
    watch: {
        activeKey(val, oldVal) {
            if (val !== '2') {
                this.bottomStyle = "";
                this.maxRows = defaultMaxRows;
            }
        }
    },
    created() {
        this.curTab.id = this.$route.query.id;
        this.init();
    },
    mounted() {
        if (this.$route.query.labels) {
            localStorage.setItem('exchangis_environment', this.$route.query.labels);
        }

        this.$nextTick(() => {
            if (this.$route.path === '/childJobManagement') {
                document.querySelector('.layout-sider-fixed-stuff').style.width = '0';
                // document.querySelector('.ant-layout-header.layout-header').style.display = 'none'
            }
        });
    },
    beforeUnmount() {
        clearInterval(this.jobStatusTimer);
        clearInterval(this.progressTimer);
    },
    methods: {
        init() {
            this.getInfo();
        },
        async getInfo() {
            try {
                this.loading = true;
                const data = (await getJobInfo(this.curTab.id)).result;
                this.loading = false;
                if (!data.content || data.content === '[]') {
                    data.content = {
                        subJobs: []
                    };
                } else {
                    data.content = JSON.parse(data.content);
                }
                data.content.subJobs.forEach((item) => {
                    item.engineType = data.engineType;
                });
                this.jobData = data;
                this.jobData.projectId = String(data.projectId || '');

                const configData = Object.create(null);
                configData.executeNode = data.executeNode || '';
                configData.proxyUser = data.proxyUser || '';
                configData.syncType = data.syncType || '';
                configData.jobParams = JSON.parse(data.jobParams);
                this.configModalData = configData;

                this.list = this.jobData.content.subJobs;
                if (this.list.length) {
                    this.activeIndex = 0;
                    this.curTask = this.list[this.activeIndex];
                    this.curTask._transforms = cloneDeep(this.curTask.transforms, 1)
                    this.curTask.dataSourceIds.source.tableNotExist = !!this.curTask.transforms.srcTblNotExist;
                    this.curTask.dataSourceIds.sink.tableNotExist = !!this.curTask.transforms.sinkTblNotExist;
                    this.curType = this.curTask.transforms.type;
                    // test
                    console.log('初始获取详情', this.curTask)
                    this.addEnable = this.curTask.transforms.addEnable;
                    this.transformEnable = this.curTask.transforms.transformEnable;
                    this.updateSourceInfo(this.curTask, true);
                    // this.updateSinkInfo(this.curTask); 当sink和source都有值的时候,请求的结果是一致的,所以省去一次多余重复请求
                }
            } catch (error) {
                this.loading = false;
            }
        },
        // 更新保存任务配置
        handleModalFinish(config, type = 'config') {
            const _this = this;
            const { id } = this.curTab;
            const _config = Object.assign(
                {},
                JSON.parse(JSON.stringify(toRaw(config)))
            );
            if (_config.jobParams) {
                const jobParams = Object.create(null);
                _config.jobParams.forEach((item) => {
                    // jobParams = Object.assign(jobParams, item);
                    jobParams[item.key] = item.value;
                });
                _config.jobParams = JSON.stringify(jobParams);
            }
            _config.projectId = _this.jobData.projectId
            updateTaskConfiguration(id, _config)
                .then((res) => {
                    if(type !== 'init') message.success('更新/保存成功');
                    _this.jobData.proxyUser = _config.proxyUser;
                })
                .catch((err) => {
                    if(type !== 'init') message.error('更新/保存失败');
                    console.log('updateTaskConfiguration error', err);
                });
        },
        handleModalCopy(data) {
            if (data) {
                this.jobData.content.subJobs.push(data);
            }
        },
        copySub(item) {
            this.copyObj = cloneDeep(item, 1);
            if (this.copyObj.transforms.type === 'PROCESSOR') {
                this.copyObj.transforms.copy_code_id = this.copyObj.transforms.code_id;
                this.copyObj.transforms.code_id = ''
            }
            this.modalCopy.visible = true;
        },
        deleteSub(index) {
            this.jobData.content.subJobs.splice(index, 1);
            if (this.list.length) {
                let bool1 = (this.activeIndex === index) && index >= (this.list.length - 1)// 当前选项在末尾
                let activeIndex = (this.activeIndex > index ||bool1)  ? this.activeIndex - 1 : this.activeIndex;
                this.changeCurTask(activeIndex, true);
            } else {
                this.activeIndex = -1;
                this.curTask = null;
                this.addEnable = false;
                this.transformEnable = false;
            }
        },
        cancel() {},
        getClass(index) {
            if (this.activeIndex === index) {
                return 'sub-content active';
            }
            return 'sub-content';
        },
        async changeCurTask(index, isFresh) {
            if (this.activeIndex === index && !isFresh) return
            if (this.curType === 'PROCESSOR') {
                const valid = await this.$refs.processorRef.beforeSave();
                console.log('保存控制器', valid);
                if (!valid) return;
            }
            this.activeIndex = index;
            this.curTask = this.list[this.activeIndex];
            this.curTask._transforms = cloneDeep(this.curTask.transforms, 1)
            this.curTask.dataSourceIds.source.tableNotExist = !!this.curTask.transforms.srcTblNotExist;
            this.curTask.dataSourceIds.sink.tableNotExist = !!this.curTask.transforms.sinkTblNotExist;
            this.curType = this.curTask.transforms.type;
            this.addEnable = this.curTask.transforms.addEnable;
            this.transformEnable = this.curTask.transforms.transformEnable;
            console.log('当前任务切换后', this.curTask.transforms);
            const data = this.getFieldsParams(this.curTask);
            if (data) {
                getFields(data).then((res) => {
                    this.fieldsSource = res.sourceFields;
                    this.fieldsSink = res.sinkFields;
                    this.deductions = res.deductions;
                    this.addEnable = res.addEnable;
                    this.transformEnable = res.transformEnable;
                    this.processTypes = res.types;
                }).catch((err) => {
                    console.log(err);
                });
            }
        },
        addNewTask() {
            const subJobName = randomString(12);
            const task = {
                subJobName,
                engineType: this.jobData.engineType,
                dataSourceIds: {
                    source: {
                        type: '',
                        id: '',
                        db: '',
                        table: '',
                        name: ''
                    },
                    sink: {
                        type: '',
                        id: '',
                        db: '',
                        table: '',
                        name: ''
                    }
                },
                params: {
                    sources: [],
                    sinks: []
                },
                transforms: {
                    type: '',
                    mapping: []
                },
                settings: []
            };
            getSettingsParams(this.jobData.engineType).then((res) => {
                res.ui.forEach((ui) => {
                    if (!ui.value && ui.defaultValue) {
                        ui.value = ui.defaultValue;
                    }
                });
                task.settings = res.ui || [];
                this.jobData.content.subJobs.push(task);
                this.$nextTick(() => {
                    this.activeIndex = this.jobData.content.subJobs.length - 1;
                    this.curTask = this.list[this.activeIndex];
                    this.curTask._transforms = cloneDeep(this.curTask.transforms, 1)
                    this.curType = '';
                    this.addEnable = false;
                    this.transformEnable = false;
                    this.deductions = [];
                });
            });
        },
        // 字段映射更新
        updateFieldMap(transforms) {
            console.log('update field map', transforms);
            this.curTask.transforms = transforms;
        },
        // 更新控制器
        updateProMap(processor) {
            this.curTask.transforms = processor;
            console.log('更新控制器', this.curTask.transforms)
        },
        updateProcessControl(settings) {
            this.curTask.settings = settings;
        },
        getFieldsParams(dataSource, isUpdate) {
            const { dataSourceIds, params } = dataSource;
            this.curTask.dataSourceIds = dataSourceIds;
            this.curTask.params = params;
            if (isUpdate) {
                this.curTask.transforms.srcTblNotExist = dataSourceIds.source.tableNotExist;
                this.curTask.transforms.sinkTblNotExist = dataSourceIds.sink.tableNotExist;
            }
            const source = this.curTask.dataSourceIds.source;
            const sink = this.curTask.dataSourceIds.sink;
            if (!source.type || !source.id || !sink.type || !sink.id) return null;
            return {
                sourceTypeId: source.type,
                sourceDataSourceId: source.id,
                sourceDataBase: source.db,
                sourceTable: source.table,
                sinkTypeId: sink.type,
                sinkDataSourceId: sink.id,
                sinkDataBase: sink.db,
                sinkTable: sink.table,
                engine: this.jobData.engineType,
                srcTblNotExist: !!source.tableNotExist,
                sinkTblNotExist: !!sink.tableNotExist
            };
        },
        convertDeductions(deductions) {
            const mapping = [];
            deductions.forEach((deduction) => {
                const o = {};
                const source = deduction.source;
                const sink = deduction.sink;
                o.sink_field_name = sink.name;
                o.sink_field_type = sink.type;
                o.sink_field_index = sink.fieldIndex;
                o.sink_field_editable = sink.fieldEditable;
                o.deleteEnable = deduction.deleteEnable;
                o.source_field_name = source.name;
                o.source_field_type = source.type;
                o.source_field_index = source.fieldIndex;
                o.source_field_editable = source.fieldEditable;
                mapping.push(o);
            });
            return mapping;
        },
        updateSourceInfo(dataSource, firstInit) {
            const data = this.getFieldsParams(dataSource, true);
            if (data) {
                getFields(data).then((res) => {
                    this.fieldsSource = res.sourceFields;
                    this.fieldsSink = res.sinkFields;
                    this.deductions = res.deductions;
                    this.addEnable = res.addEnable;
                    this.transformEnable = res.transformEnable;
                    this.processTypes = res.types;
                    this.curType = res.types.includes(this.curType) ? this.curType : (res.types[0] || '');
                    // 不在使用deductions 直接将deductions作为值使用
                    if (!(firstInit && this.curTask.transforms.mapping && this.curTask.transforms.mapping.length)) {
                        this.curTask.transforms.mapping = this.convertDeductions(res.deductions);
                        this.curTask.transforms.type = this.curType;
                    }
                });
            } else {
                // 没有数据源的情况下清空字段映射
                this.fieldsSource = [];
                this.fieldsSink = [];
                this.deductions = [];
                this.addEnable = false;
                this.transformEnable = false;
                this.curTask.transforms.mapping = [];
            }
        },
        updateSinkInfo(dataSource, firstInit) {
            const data = this.getFieldsParams(dataSource, true);
            if (data) {
                getFields(data).then((res) => {
                    this.fieldsSource = res.sourceFields;
                    this.fieldsSink = res.sinkFields;
                    this.deductions = res.deductions;
                    this.addEnable = res.addEnable;
                    this.transformEnable = res.transformEnable;
                    this.processTypes = res.types;
                    this.curType = res.types.includes(this.curType) ? this.curType : (res.types[0] || '');
                    // 不在使用deductions 直接将deductions作为值使用
                    if (!(firstInit && this.curTask.transforms.mapping && this.curTask.transforms.mapping.length)) {
                        this.curTask.transforms.mapping = this.convertDeductions(res.deductions);
                        this.curTask.transforms.type = this.curType;
                    }
                });
            } else {
                // 没有数据源的情况下清空字段映射
                this.fieldsSource = [];
                this.fieldsSink = [];
                this.deductions = [];
                this.addEnable = false;
                this.transformEnable = false;
                this.curTask.transforms.mapping = [];
            }
        },
        updateSourceParams(dataSource) {
            const { params } = dataSource;
            this.curTask.params = params;
        },
        updateSinkParams(dataSource) {
            const { params } = dataSource;
            this.curTask.params = params;
        },
        // 提交前 对 必填数据进行校验
        checkPostData(data) {
            const res = [];
            const { proxyUser, content } = data;
            const jobs = content.subJobs.slice();
            if (!proxyUser) {
                res.push(<li style="list-style: none;"><span style="margin-left: -30px;">配置任务中执行用户不可为空</span></li>);
            }
            jobs.forEach((job) => {
                const { params, settings, transforms } = job;
                let isInsert = false
                res.push(<li style="list-style: none;"><span style="margin-left: -30px;">{job.subJobName}:</span></li>);
                for (const key in params) {
                    let nullFormatUnique = {refId: ''}
                    let transferModeUnique = {id: '', value: ''};
                    params[key].forEach(i => {
                        if (i.field === 'transferMode') {
                            transferModeUnique = {value: i.value, id: i.id};
                        } else if (i.field === 'nullFormat') {
                            nullFormatUnique = {refId: i.refId};
                        }
                    })
                    params[key].forEach((i) => {
                        // 判断分区alue有值的情况下可能为空的情况
                        let judePartition = i.value && 
                            i.field === "partition" &&
                            ((Array.isArray(i.value) && (i.value.length < 1 || (i.value.length === 1 && !i.value[0].key && !i.value[0].value))) || 
                            (!Object.keys(i.value).length || Object.values(i.value).filter(v => v).length < Object.keys(i.value).length));
                        if ((!i.value || judePartition) && i.required ) {
                            const isPartitionEmpty = i.field === "partition" && !i.value;
                            if (i.field === 'nullFormat') {
                                if (transferModeUnique.id === nullFormatUnique.refId) {
                                    if(transferModeUnique.value === '记录') {
                                        isInsert = true;
                                        res.push(<li>{i.label}不可为空</li>);
                                    }
                                }else {
                                    isInsert = true;
                                    res.push(<li>{i.label}不可为空</li>);
                                }
                            } else if (!isPartitionEmpty) {
                                isInsert = true;
                                res.push(<li>{i.label}不可为空</li>);
                            }
                        } else if (i.value && i.validateType === "REGEX") {
                            const value_reg = new RegExp(`${i.validateRange}`);
                            const keyMap = { sinks: 'sink', sources: 'source' };
                            if (i.field === "partition" && keyMap[key] && job.dataSourceIds[keyMap[key]].tableNotExist) {
                                const value_reg = new RegExp(`${i.validateRange}`);
                                // 非必填的情况下允许保存的分区 null, { a: b }, [{key: '', value}]
                                const isPartionValid = !Array.isArray(i.value) || 
                                    (Array.isArray(i.value) && (i.value.length < 1 || (i.value.length === 1 && !i.value[0].key && !i.value[0].value)));
                                if (isPartionValid) {
                                    return;
                                } else if (i.value.some(item => !item.key)) {
                                    isInsert = true;
                                    res.push(<li>{i.label}key值不可为空</li>);
                                } else if (i.value.some(item => !item.value)) {
                                    isInsert = true;
                                    res.push(<li>{i.label}value值不可为空</li>);
                                } else if (i.value.some(item => !/^[a-zA-Z0-9_-]+$/.test(item.key))) {
                                    isInsert = true;
                                    res.push(<li>{i.label}key值只能为字母、数字、横线和下划线</li>);
                                } else if (i.value.some(item => !value_reg.test(item.value))) {
                                    isInsert = true;
                                    res.push(<li>{i.label}value值过长</li>);
                                } else {
                                    const partitionKeys = i.value.map(item => item.key);
                                    const filterPartitionKeys = [...new Set(partitionKeys)];
                                    if (partitionKeys.length !== filterPartitionKeys.length) {
                                        isInsert = true;
                                        res.push(<li>{i.label}key值不能重复</li>);
                                    }
                                }
                            } else if (!value_reg.test(i.value)) {
                                isInsert = true;
                                res.push(<li>{i.label}格式不正确</li>);
                            }
                        }
                    });
                }

                settings.forEach((i) => {
                    if (!i.value && i.required) {
                        isInsert = true;
                        res.push(<li>{i.label}不可为空</li>);
                    } else if (i.value && i.validateType === "REGEX") {
                        const num_reg = new RegExp(`${i.validateRange}`);
                        if (!num_reg.test(i.value)) {
                            isInsert = true;
                            res.push(<li>{i.label}格式不正确</li>);
                        }
                    }
                });

                const sinkFields = (transforms.mapping || []).map(item => item.sink_field_name).filter(item => item);
                if(sinkFields.length && sinkFields.length > [...new Set(sinkFields)].length) {
                    isInsert = true;
                    res.push(<li>字段映射目的端字段存在重复</li>);
                }


                if (!isInsert) {
                    res.splice(res.length - 1, 1)
                }
            });

            return res;
        },
        async saveAll(type = 'save', cb) {
            message.destroy();
            this.loading = true;
            if (this.curType === 'PROCESSOR') {
                const valid = await this.$refs.processorRef.beforeSave();
                console.log('保存控制器', valid);
                if (!valid) {
                    message.error('后置控制器保存失败');
                    return this.loading = false;
                }
            }
            const saveContent = [];
            const data = toRaw(this.jobData);
            const tips = this.checkPostData(data);
            if (tips.length > 0) {
                this.loading = false;
                return notification.warning({
                    message: '任务信息未完整填写',
                    description: h(
                        'ul',
                        {},
                        tips.map(tip => tip)
                    ),
                    duration: 5
                });
            }
            if (!data.content || !data.content.subJobs) {
                this.loading = false;
                return message.error('缺失保存对象');
            }
            for (let i = 0; i < data.content.subJobs.length; i++) {
                const jobData = toRaw(data.content.subJobs[i]);
                const cur = {};
                cur.subJobName = jobData.subJobName;
                if (
                    !jobData.dataSourceIds
                    || objectValueEmpty(jobData.dataSourceIds.source)
                    || objectValueEmpty(jobData.dataSourceIds.sink)
                ) {
                    this.loading = false;
                    return message.error('未选择数据源库表');
                }
                cur.dataSources = {
                    source: {
                        id: jobData.dataSourceIds.source.id,
                        type: jobData.dataSourceIds.source.type, 
                        name: jobData.dataSourceIds.source.name, 
                        db: jobData.dataSourceIds.source.db, 
                        table: jobData.dataSourceIds.source.table,
                        creator: jobData.dataSourceIds.source.creator
                    },
                    sink: {
                        id: jobData.dataSourceIds.sink.id,
                        type: jobData.dataSourceIds.sink.type, 
                        name: jobData.dataSourceIds.sink.name, 
                        db: jobData.dataSourceIds.sink.db, 
                        table: jobData.dataSourceIds.sink.table,
                        creator: jobData.dataSourceIds.sink.creator
                    }
                };
                /* if (
          !jobData.params ||
          !jobData.params.sources.length ||
          !jobData.params.sinks.length
        ) {
          return message.error("缺失数据源参数");
        } */
                cur.params = {
                    sources: [],
                    sinks: []
                };
                jobData.params.sources.forEach((source) => {
                    if (!(source.field === "partition" && !source.value)) {
                        if (source.field === "partition" && jobData.dataSourceIds.source.tableNotExist) {
                            let config_obj = {};
                            if (source.value && Array.isArray(source.value)) {
                                source.value.forEach(item => {
                                    config_obj[item.key] = item.value
                                })
                            } else {
                                config_obj = source.value
                            }
                            cur.params.sources.push({
                                config_key: source.field, // UI中field
                                config_name: source.label, // UI中label
                                config_value: config_obj ? {...config_obj} : config_obj, // UI中value
                                sort: source.sort
                            });
                        } else { 
                            cur.params.sources.push({
                                config_key: source.field, // UI中field
                                config_name: source.label, // UI中label
                                config_value: source.value, // UI中value
                                sort: source.sort
                            });
                        }
                    }
                });
                jobData.params.sinks.forEach((sink) => {
                    if (!(sink.field === "partition" && !sink.value)) {
                        if (sink.field === "partition" && jobData.dataSourceIds.sink.tableNotExist) {
                            let config_obj = {};
                            if (sink.value && Array.isArray(sink.value)) {
                                sink.value.forEach(item => {
                                    config_obj[item.key] = item.value
                                })
                            } else {
                                config_obj = sink.value
                            }
                            cur.params.sinks.push({
                                config_key: sink.field, // UI中field
                                config_name: sink.label, // UI中label
                                config_value: config_obj ? {...config_obj} : config_obj, // UI中value
                                sort: sink.sort
                            });
                        } else {
                            cur.params.sinks.push({
                                config_key: sink.field, // UI中field
                                config_name: sink.label, // UI中label
                                config_value: sink.value, // UI中value
                                sort: sink.sort
                            });
                        }
                        
                    }
                });
                cur.transforms = jobData.transforms;
                cur.transforms.srcTblNotExist = jobData.dataSourceIds.source.tableNotExist;
                cur.transforms.sinkTblNotExist = jobData.dataSourceIds.sink.tableNotExist;
                if (this.curType === 'MAPPING') { // 为字段映射时才需要
                    cur.transforms.addEnable = this.addEnable;
                    cur.transforms.transformEnable = this.transformEnable;
                }
                cur.settings = [];
                if (jobData.settings && jobData.settings.length) {
                    jobData.settings.forEach((setting) => {
                        cur.settings.push({
                            config_key: setting.key, // UI中field
                            config_name: setting.label, // UI中label
                            config_value: setting.value, // UI中value
                            sort: setting.sort
                        });
                    });
                }
                saveContent.push(cur);
            }
            // test
            console.log(saveContent)
            saveProject(this.jobData.id, {
                projectId: this.jobData.projectId,
                content: JSON.stringify(saveContent)
            }, type).then((res) => {
                cb && cb();
                message.success('保存成功');
            }).finally(() => {
                this.loading = false;
            });
        },
        // 执行任务
        executeTask() {
            this.saveAll('execute', () => {
                const { id } = this.curTab;
                this.tasklist = [];
                this.spinning = true;
                executeJob(id)
                    .then((res) => {
                        this.jobExecutionId = res.jobExecutionId;
                        this.visibleLog = true;
                        // moveUpDown('.jd-bottom-log', '.jd-container', '.jd-bottom-log-top');
                        this.getStatus(res.jobExecutionId);

                        // 新增执行历史
                        this.ehTableData.push({
                            jobExecutionId: this.jobExecutionId,
                            createTime: new Date().getTime(),
                            status: '',
                            key: this.ehTableData.length + 1
                        });
                    })
                    .catch((err) => {
                        console.log(err);
                        this.spinning = false;
                        message.error('执行失败');
                    });
            });
        },
        killTask() {
            if (!this.jobStatus) {
                return message.error('正在等待');
            }
            killJob(this.jobExecutionId)
                .then((res) => {
                    this.spinning = false;
                    clearInterval(this.jobStatusTimer);
                    clearInterval(this.progressTimer);
                    // this.visibleLog = false

                    // 更新执行历史状态
                    for (let i = this.ehTableData.length; i > 0; i--) {
                        const cur = this.ehTableData[i - 1];
                        if (cur.jobExecutionId === this.jobExecutionId) {
                            cur.status = 'Cancelled';
                            break;
                        }
                    }
                })
                .catch((err) => {
                    console.log(err);
                    this.spinning = false;
                    message.error('停止失败');
                });
        },
        // 获取Job状态
        getStatus(jobExecutionId) {
            this.getStatusInvoke(jobExecutionId);
            this.jobStatusTimer = setInterval(() => {
                this.getStatusInvoke(jobExecutionId);
            }, 1000 * 5);
        },
        getStatusInvoke(jobExecutionId) {
            const unfinishedStatusList = ['Inited', 'Scheduled', 'Running', 'WaitForRetry'];
            getJobStatus(jobExecutionId)
                .then((res) => {
                    this.jobStatus = res.status;
                    this.allTaskStatus = res.allTaskStatus;

                    // 更新执行历史状态
                    for (let i = this.ehTableData.length; i > 0; i--) {
                        const cur = this.ehTableData[i - 1];
                        if (cur.jobExecutionId === jobExecutionId) {
                            cur.status = res.status;
                            break;
                        }
                    }

                    if (!this.tasklist.length) {
                        this.getTasks(jobExecutionId, true);
                    }
                    if (unfinishedStatusList.indexOf(this.jobStatus) === -1 && this.allTaskStatus) {
                        this.spinning = false;
                        clearInterval(this.jobStatusTimer);
                        setTimeout(() => {
                            clearInterval(this.progressTimer);
                        }, 1000 * 5);
                    }
                })
                .catch((err) => {
                    message.error('查询job状态失败');
                });
        },
        // 获取tasklist
        getTasks(jobExecutionId, shouldGetJobProgress) {
            getJobTasks(jobExecutionId)
                .then((res) => {
                    this.tasklist = res.tasks;
                    if (shouldGetJobProgress) this.getJobProgress(jobExecutionId);
                })
                .catch((err) => {
                    message.error('查询任务列表失败');
                });
        },
        clearProgressTimer() {
            if (this.progressTimer) {
                clearInterval(this.progressTimer);
            }
        },
        getJobProgress(jobExecutionId) {
            this.getJobProgressInvoke(jobExecutionId);
            this.clearProgressTimer();
            this.progressTimer = setInterval(() => {
                this.getJobProgressInvoke(jobExecutionId);
            }, 1000 * 5);
        },
        getJobProgressInvoke(jobExecutionId) {
            getProgress(jobExecutionId)
                .then((res) => {
                    if (res.job && res.job.tasks) {
                        res.job.successTasks = res.job.tasks.Success?.length || 0;
                        res.job.initedTasks = res.job.tasks.Inited?.length || 0;
                        res.job.runningTasks = res.job.tasks.Running?.length || 0;
                        res.job.failedTasks = res.job.tasks.Failed?.length || 0;
                        res.job.cancelledTasks = res.job.tasks.Cancelled?.length || 0;
                        res.job.scheduledTasks = res.job.tasks.Scheduled?.length || 0;

                        res.job.totalTasks = this.tasklist.length;
                        if (res.job.total && res.job.total !== this.tasklist.length) {
                            res.job.totalTasks = res.job.total;
                            this.getTasks(jobExecutionId);
                        }
                        res.job.successPercent = res.job.successTasks * 100 / res.job.totalTasks;
                        res.job.percent = res.job.progress * 100; // (res.job.successTasks + res.job.runningTasks) * 100 / res.job.totalTasks
                        res.job.title = res.job.failedTasks ? `${res.job.failedTasks}失败,${res.job.successTasks}成功,${res.job.runningTasks}正在运行,${res.job.scheduledTasks}正在准备` : `${res.job.successTasks}成功,${res.job.runningTasks}正在运行,${res.job.scheduledTasks}正在准备`;
                    }
                    this.jobProgress = res.job;
                })
                .catch((err) => {
                    message.error('查询进度失败');
                    this.clearProgressTimer();
                });
        },
        getTaskInfo(progress) {
            if (this.openMetricsId !== progress.taskId) {
                this.openMetricsId = progress.taskId;
                getMetrics(progress.taskId, this.jobExecutionId)
                    .then((res) => {
                        res.task.taskId = progress.taskId;
                        this.metricsInfo[res.task.taskId] = res.task?.metrics;
                    })
                    .catch((err) => {
                        message.error('查询任务指标失败');
                    });
            } else {
                this.openMetricsId = '';
            }
        },

        executeHistory() {
            this.visibleLog = true;
            this.activeKey = '3';
        },
        getTableFormCurrent(current, type) {
            const _this = this;
            if (
                _this.ehTableData.length == _this.ehPagination.total
                && _this.ehPagination.total > 0
                && type !== 'search'
            ) return;
            if (_this.currentPage == current && type !== 'search') return;
            const jobId = _this.curTab.id || '';
            const _pageSize = _this.pageSize;
            getSyncHistory({ jobId, current, size: _pageSize })
                .then((res) => {
                    if (res.result.length > 0) {
                        const result = res.result || [];
                        result.forEach((item) => {
                            item.launchTime = formatDate(item.launchTime);
                            item.completeTime = formatDate(item.completeTime);
                            switch (item.status) {
                                case 'SUCCESS':
                                    item.status = '执行成功';
                                    break;
                                case 'FAILED':
                                    item.status = '执行失败';
                                    break;
                                case 'RUNNING':
                                    item.status = '运行中';
                            }
                            item.key = item.id;
                        });
                        if (type == 'search') {
                            _this.TableData = [];
                        }
                        _this.ehPagination.total = res.total;
                        _this.ehTableData = _this.ehTableData.concat(result);
                    }
                })
                .catch((err) => {
                    console.log('syncHistory error', err);
                });
        },
        onPageChange(page) {
            const { current } = page;
            this.getTableFormCurrent(current, 'onChange');
        },
        onCloseDrawer() {
            this.visibleDrawer = false;
        },
        onCloseLog() {
            // clearInterval(this.jobStatusTimer)
            clearInterval(this.progressTimer);
            this.visibleLog = false;
            this.bottomStyle = "";
            this.maxRows = defaultMaxRows;
        },
        getEditableInput() {
            this.nameEditable = true;
            this.$nextTick(() => {
                if (this.$refs.currentInput && this.$refs.currentInput.focus) {
                    this.$refs.currentInput.focus();
                }
            });
        },
        expandLog() {
            if (this.bottomStyle) {
                this.bottomStyle = "";
                this.maxRows = defaultMaxRows;
            } else {
                this.bottomStyle = "position: absolute;height: 100vh !important;";
                this.maxRows = Math.floor((window.innerHeight - 140) / 22.2);
            }
        },
        // 切换控制器和映射
        selectTranforms(val) {
            let _tranforms = cloneDeep(this.curTask._transforms, 1)
            console.log('切换控制器', val, _tranforms.type!== 'MAPPING' && this.curType === 'MAPPING')
            const MAPS = {
                'MAPPING': {
                    type: 'MAPPING',
                    mapping: []
                },
                'PROCESSOR': {
                    type: 'PROCESSOR',
                   code_id: ''
                }
            }
            if (this.curType === _tranforms.type) { // 恢复默认配置
                this.curTask.transforms = _tranforms;
            } else {
                this.curTask.transforms = MAPS[this.curType];
            }

            // 如果默认值不是MAPPING, 则需要重新获取
            if (_tranforms.type!== 'MAPPING' && this.curType === 'MAPPING') {
                const data = this.getFieldsParams(this.curTask);
                if (data) {
                    getFields(data).then((res) => {
                        this.fieldsSource = res.sourceFields;
                        this.fieldsSink = res.sinkFields;
                        this.deductions = res.deductions;
                        this.addEnable = res.addEnable;
                        this.transformEnable = res.transformEnable;
                        this.processTypes = res.types;
                        if (!(this.curTask.transforms.mapping && this.curTask.transforms.mapping.length)) {
                           this.curTask.transforms.mapping = this.convertDeductions(res.deductions);
                        }
                    }).catch((err) => {
                        console.log(err);
                    });
                }
            }
        }
    }
};
</script>
<style scoped lang="less">
@import "../../../common/content.less";
.container {
  position: relative;
  display: flex;
  flex-direction: column;
  height: 100vh;
  .tools-bar {
    width: 100%;
    border-bottom: 1px solid #dee4ec;
    background: #f8f9fc;
    padding: 10px 30px;
    font-size: 16px;
    color: rgba(0, 0, 0, 0.65);
    > span {
      cursor: pointer;
    }
    .anticon {
      margin-right: 5px;
    }
    .divider {
      width: 1px;
      height: 20px;
      background: #dee4ec;
      margin-left: 20px;
      margin-right: 20px;
      display: inline-block;
      position: relative;
      top: 5px;
    }
  }
  .jd-content {
    overflow: hidden;
    width: 100%;
    .wrap-spin {
      height: calc(100vh - 130px)
    }
    .jd_left {
      float: left;
      width: 250px;
      background-color: #f8f9fc;
      border-right: 1px solid #dee4ec;
      padding-bottom: 2000px;
      margin-bottom: -2000px;
      .left-wrap {
        max-height: calc(100vh - 82px);
        overflow-y: scroll;
        padding: 0 15px;
      }
      .sub-title {
        font-size: 14px;
        margin-top: 16px;
        font-weight: 500;
        .database-icon {
          color: rgb(102, 102, 255);
          margin-right: 5px;
        }
        .ps-icon {
          float: right;
          cursor: pointer;
          line-height: 28px;
        }
      }
      .sub-content {
        width: 218px;
        margin: 10px 0;
        border: 1px solid #dee4ec;
        padding: 10px 15px 5px;
        border-radius: 5px;
        position: relative;
        &.active {
          border: 1px solid #2e92f7;
          .task-title {
            .subjobName {
              color: rgba(0, 0, 0, 0.85);
            }
          }
          .sub-table {
            color: #677c99;
          }
        }
        .task-title {
          font-size: 15px;
          .subjobName {
            color: rgba(0, 0, 0, 0.85);
            cursor: pointer;
            overflow: hidden;
            white-space: nowrap;
            text-overflow: ellipsis;
            max-width: 115px;
            display: inline-block;
          }
          .rename-icon {
            float: right;
            cursor: pointer;
            margin-right: 10px;
            line-height: 28px;
            color: rgba(0, 0, 0, 0.5);
          }
          .copy-icon {
            float: right;
            cursor: pointer;
            margin-right: 10px;
            line-height: 28px;
            color: rgba(0, 0, 0, 0.5);
          }
          .delete-icon {
            float: right;
            cursor: pointer;
            margin-right: 0;
            line-height: 28px;
            color: rgba(0, 0, 0, 0.5);
          }
        }
        .sub-table {
          color: #677c99;
          text-align: center;
          margin: 5px 0;
          width: 100%;
          overflow: hidden;
          white-space: nowrap;
          text-overflow: ellipsis;
          cursor: pointer;
        }
        .arrow-down-icon {
          text-align: center;
          font-weight: bolder;
          font-size: 16px;
          color: #677c99;
          cursor: pointer;
        }
        .mask {
          width: 100%;
          height: 100%;
          position: absolute;
          background-color: rgba(255, 255, 255, 0.6);
          top: 0;
          left: 0;
        }
      }
    }
    .jd_right {
      background: white;
      overflow-x: auto;
      float: right;
      width: calc(100% - 250px);
    }
  }

  .jd-outWrapper {
    flex: 1;
    max-height: 100vh;
    overflow: auto;
  } 

  .jd-bottom {
    overflow: auto;
    width: 100%;
    background-color: white;
    flex: 0 0 400px;
    height: 400px !important;
    position: relative;
    top: 0;
    &.hide-botttom {
        display: none;
    }
    &.display-bottom {
        display: block;
    }
    .jd-bottom-top {
      max-width: 100px;
      height: 43px;
      background-color: #f8f9fc;
      padding: 8px 24px;
      font-family: PingFangSC-Medium;
      font-size: 16px;
      color: rgba(0, 0, 0, 0.85);
      font-weight: 500;
      position: absolute;
      top: 0 !important;
      right: 0px;
      z-index: 9999;
      text-align: right;
    }


    &-content {
      padding: 18px 24px;
      .exec-info-tab {
        :deep(.ant-tabs-content) {
          padding: 5px 10px;
        }
      }
    }
    .log-bottom-content {
      padding: 0 0 0 24px;
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
  }
}

.cardWrap {
  display: flex;
  flex-wrap: wrap;
  padding-bottom: 30px;
  :deep(.ant-spin-nested-loading) {
    width: 100%;
  }
  .emptyTab {
    font-size: 16px;
    height: calc(100vh - 130px);
    width: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  .card {
    margin: 10px 20px 10px 0px;
  }
}

.void-page-wrap {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background-color: #fff;
  .void-page-main {
    &-img {
      text-align: center;
    }
    &-title {
      font-family: PingFangSC-Regular;
      font-size: 14px;
      color: rgba(0, 0, 0, 0.45);
      letter-spacing: 0;
      text-align: center;
      line-height: 28px;
      font-weight: 400;
      margin-top: 24px;
      margin-bottom: 16px;
    }
    &-button {
      min-width: 106px;
      height: 32px;
      line-height: 32px;
      text-align: center;
      &-item {
        background: #2e92f7;
        color: #fff;
      }
    }
  }
}
</style>

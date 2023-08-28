<template>
    <div class="content">
        <a-row :gutter="[16, 16]">
            <a-col :span="24">
                <TopLine
                    :loading="loading"
                    :sourceTypeList="sourceTypeList"
                    @create="handleCreate"
                    @search="handleSearch"
                    @encrypt="showEncrypt"
                />
            </a-col>
            <a-col :span="24"><div class="line"></div></a-col>
            <a-col :span="24" style="overflow-x: auto">
                <a-table
                    :columns="columns"
                    :data-source="dataSourceList"
                    :loading="loading"
                    rowKey="id"
                    :pagination="pagination"
                    class="data-source-manage-table"
                    @change="onChange"
                    :scroll="{ x: 1600, y: 650 }"
                >
                    <template #tags="{ text }">
                        <a-tag color="#2e92f7">{{text.labels}}</a-tag>
                    </template>
                    <template #status="{ text: expire }">
                        <span> {{expire ? "已过期" : "可用"}} </span>
                    </template>
                    <template #dataSourceTypeId="{ text: dataSourceTypeId }">
                        <span>
                            {{(
                                sourceTypeList.find(
                                    (item) => item.id == dataSourceTypeId
                                ) || { option: "无" }
                            ).name}}
                        </span>
                    </template>
                    <template #action="row">
                        <a-space>
                            <a-button
                                :disabled="!row.text.versionId"
                                size="small"
                                type="link"
                                @click="handleEdit(row.text)"
                            >
                                {{$t("dataSource.table.list.columns.actions.editButton")}}
                            </a-button>
                            <span style="color: #DEE4EC">|</span>
                            <a-button
                                :disabled="!(!row.text.expire && row.text.versionId)"
                                size="small"
                                type="link"
                                @click="handleExpire(row.text.id)"
                            >
                                {{$t("dataSource.table.list.columns.actions.expireButton")}}
                            </a-button>
                            <span style="color: #DEE4EC">|</span>
                            <a-button
                                :disabled="!row.text.versionId"
                                size="small"
                                type="link"
                                @click="handleTestConnect(row)"
                            >
                                {{$t("dataSource.table.list.columns.actions.testConnectButton")}}
                            </a-button>
                            <span style="color: #DEE4EC">|</span>
                            <a-popconfirm
                                title="是否删除?"
                                ok-text="确定"
                                cancel-text="取消"
                                @confirm="handleDelete(row)"
                                @cancel="cancel"
                            >
                                <a-button size="small" type="link">
                                    {{$t("dataSource.table.list.columns.actions.deleteButton")}}
                                </a-button>
                            </a-popconfirm>
                        </a-space>
                    </template>
                    <template #version="{ text }">
                        <a-tag style="cursor: pointer; color:#2e92f7" @click="handleOpenVersionModal(text)">
                            {{text.versionId}}
                        </a-tag>
                        <!--<a-button-->
                        <!--size="small"-->
                        <!--@click="handleOpenVersionModal(text)"-->
                        <!--type="link"-->
                        <!--&gt;{{ text.versionId }}</a-button>-->
                    </template>
                    <template #desc="{ text }">
                        <div class="dsm-desc" :title="text.desc">{{text.desc}}</div>
                    </template>
                    <template #name="{ text }">
                        <div class="dsm-name" :title="text.name">{{text.name}}</div>
                    </template>
                    <template #modifyTime="{ text }">
                        {{text && dateFormatSeconds(text)}}
                    </template>
                </a-table>
            </a-col>
        </a-row>
        <select-type-modal
            v-model:visible="selectTypeModalVisible"
            :sourceTypeList="sourceTypeList"
            @select="handleSelectType"
        />
        <version-modal
            :id="versionModalCfg.id"
            v-model:visible="versionModalCfg.visible"
            :rowInfo="versionModalCfg.rowInfo"
            @openModal="openModal"
        />
        <edit-modal
            :id="modalCfg.id"
            v-model:visible="modalCfg.visible"
            :type="modalCfg.type"
            :mode="modalCfg.mode"
            :modalCfg="modalCfg"
            :zIndex="1002"
            @finish="handleModalFinish"
        />
        <encrypt-modal
            v-model:visible="encryptVisible"
        />
    </div>
</template>

<script>
import { useI18n } from '@fesjs/fes';
import { computed } from 'vue';
import { message } from 'ant-design-vue';
import TopLine from './components/topLine.vue';
import SelectTypeModal from './components/selectTypeModal.vue';
import EditModal from './components/editModal.vue';
import VersionModal from './components/versionModal.vue';
import EncryptModal from './components/encryptModal.vue';
import {
    getDataSourceList,
    deleteDataSource,
    expireDataSource,
    getDataSourceTypes,
    testDataSourceConnect
} from '@/common/service';
import { dateFormat, dateFormatSeconds } from '@/common/utils';

const data = [];
export default {
    components: {
        TopLine,
        SelectTypeModal,
        EditModal,
        VersionModal,
        EncryptModal
    },
    setup() {
        const { t } = useI18n({ useScope: 'global' });
        const columns = computed(() => [
            {
                title: t('dataSource.table.list.columns.title.name'),
                align: 'center',
                slots: { customRender: 'name' },
                width: 200
            },
            {
                title: t('dataSource.table.list.columns.title.type'),
                dataIndex: 'dataSourceTypeId',
                align: 'center',
                slots: { customRender: 'dataSourceTypeId' },
                width: 100
            },
            {
                title: t('dataSource.table.list.columns.title.status'),
                dataIndex: 'expire',
                align: 'center',
                slots: { customRender: 'status' },
                width: 80
            },
            {
                title: t('dataSource.table.list.columns.title.tags'),
                align: 'center',
                slots: { customRender: 'tags' },
                width: 150
            },
            {
                title: t('dataSource.table.list.columns.title.version'),
                align: 'center',
                slots: { customRender: 'version' },
                width: 80
            },
            {
                title: t('dataSource.table.list.columns.title.describe'),
                align: 'center',
                slots: { customRender: 'desc' },
                width: 250
            },
            {
                title: t('dataSource.table.list.columns.title.updatetim'),
                align: 'center',
                dataIndex: 'modifyTime',
                slots: { customRender: 'modifyTime' },
                width: 200
            },
            {
                title: t('dataSource.table.list.columns.title.creator'),
                align: 'center',
                dataIndex: 'createUser',
                width: 120
            },
            {
                title: t('dataSource.table.list.columns.title.updater'),
                align: 'center',
                dataIndex: 'modifyUser',
                width: 120
            },
            {
                title: t('dataSource.table.list.columns.title.action'),
                align: 'center',
                slots: { customRender: 'action' }
            }
        ]);
        return {
            columns
        };
    },

    data() {
        return {
            // 时间格式化
            dateFormat,
            dateFormatSeconds,
            // 数据源列表
            dataSourceList: data,
            // 数据源类型选择弹窗
            selectTypeModalVisible: false,
            // 是否加载中
            loading: false,
            // 数据源类型列表
            sourceTypeList: [],
            // 编辑弹框
            modalCfg: {
                mode: '',
                id: '',
                type: '',
                versionId: '',
                visible: false
            },
            // 版本弹窗
            versionModalCfg: {
                id: '',
                rowInfo: {},
                visible: false
            },
            pagination: {
                total: 0,
                current: 1,
                pageSize: 10,
                showQuickJumper: true,
                showSizeChanger: true,
                showTotal: total => `总计 ${total} 条`
            },
            searchData: {},
            // 加密弹窗
            encryptVisible: false,
        };
    },
    mounted() {
        this.getDataSourceList();
        this.getTypeData();
    },
    methods: {
        // 处理过期
        async handleExpire(id) {
            await expireDataSource(id);
            message.success('操作成功');
            this.getDataSourceList();
        },
        // 处理搜索
        handleSearch(data) {
            this.searchData = {
                ...data
            };
            this.getDataSourceList();
        },

        // 打开版本弹框
        handleOpenVersionModal(text) {
            this.versionModalCfg = {
                visible: true,
                rowInfo: text,
                id: text.id
            };
        },
        // 处理选择类型
        handleSelectType(item) {
            this.selectTypeModalVisible = false;
            this.modalCfg = {
                mode: 'create',
                id: '',
                versionId: '',
                type: item.id,
                visible: true,
                createSystem: item.name
            };
        },
        // 处理删除
        async handleDelete(row) {
            await deleteDataSource(row.text.id);
            message.success('删除成功');
            this.getDataSourceList();
        },
        // 处理编辑
        handleEdit(item) {
            this.modalCfg = {
                mode: 'edit',
                id: item.id,
                type: item.dataSourceTypeId,
                visible: true,
                createSystem: item.createSystem,
                versionId: item.versionId
            };
        },
        // 打开创建弹窗
        handleCreate() {
            this.selectTypeModalVisible = true;
        },
        // 弹框操作完成
        handleModalFinish() {
            this.getDataSourceList();
        },
        // 测试链接
        async handleTestConnect(row) {
            message.destroy();
            await testDataSourceConnect(row.text.id, row.text.versionId);
            message.success('连接成功');
        },
        // 获取列表数据
        async getDataSourceList(data) {
            this.pagination = {
                current: data?.page || 1,
                pageSize: data?.pageSize || 10
            };
            const params = {
                page: this.pagination.current,
                pageSize: this.pagination.pageSize,
                ...this.searchData
            };
            this.loading = true;
            const { list, total } = await getDataSourceList(params);
            this.pagination.total = total;
            this.loading = false;
            this.dataSourceList = list;
        },
        // 获取数据源类型
        async getTypeData() {
            const { list } = await getDataSourceTypes();
            this.sourceTypeList = list;
        },
        openModal(info, rowInfo) {
            this.modalCfg = {
                mode: 'read',
                id: info.datasourceId,
                versionId: info.versionId,
                type: rowInfo.dataSourceTypeId,
                visible: true,
                createSystem: rowInfo.createSystem
            };
        },
        cancel() {},
        onChange(page) {
            const { current, pageSize } = page;
            this.getDataSourceList({
                page: current,
                pageSize
            });
        },
        // 显示加密弹窗
        showEncrypt() {
            this.encryptVisible = true
        }
    }
};
</script>
<style scoped lang="less">
@import '../../common/content.less';
.content {
  padding: 16px 24px;
  box-sizing: border-box;
  background: #fff;
  min-height: 100vh;
  :deep(.ant-table-thead > tr > th) {
    height: 60px;
  }
  :deep(.ant-table-tbody > tr > td) {
    height: 60px;
  }
}
.top-line {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.line {
  height: 1px;
  width: calc(100% + 48px);
  background-color: #dee4ec;
  margin-left: -24px;
}
.data-source-manage-table {
  min-width: 1000px;
  :deep(.ant-tag) {
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    max-width: 150px;
  }
  .dsm-desc {
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    max-width: 250px;
  }
  .dsm-name {
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    max-width: 200px;
  }
}

</style>

import { SOURCETYPE } from '../constants/common.js';
export default {
    data() {
        const validateFieldDelimiter = (rule, value, callback) => {
            // 分隔符在两种情况下抛错：1.带\的字符 2.错误的带\u的unicode
            const reg = /^\\$/
            const checkUnicode = /^\\u/
            if (reg.test(value)) {
                callback(new Error(this.$t('mixin.FGFBCC')));
            } else if (checkUnicode.test(value) && value.split('\\u')[1].length != 4) {
                callback(new Error(this.$t('mixin.QZQTX')));
            } else {
                callback();
            }
        };
        const validateBatchSize = (rule, value, callback) => {
            if (value > 100000) {
                callback(new Error(this.$t('mixin.PLDXBCG')));
            } else {
                callback();
            }
        }
        return {
            partVal: [{ name: '', value: '', partitions: {} }],
            sourceShow: true,
            addData: {
                type: '',
                dataSourceValue: '',
                dataSourceId: '',
                db: '',
                table: '',
                DBData: '',
                partitions: '',
                partValueList: '',
                transfer: 'record'
            },
            part_loading: false,
            ds_loading: false,
            db_loading: false,
            table_loading: false,
            sourceOrigin: [],
            dbDataList: [],
            tableList: [],
            partKey: '',
            partValCache: {},
            nextStepButtonDisable: false,
            ruleValidate: {
                source: [
                    {required: true, message: this.$t('mixin.SJYBNK')}
                ],
                path: [
                    {required: true, message: `path${this.$t('mixin.BBK')}`},
                    {type: 'string', pattern: /^[^ ]+$/, message: this.$t('mixin.LJBKBJKG'), trigger: 'blur'}
                ],
                encoding: [
                    {
                        type: 'string',
                        pattern: /^(ISO-8859-1|GB2312|GBK|UTF-8|UTF-16)$/,
                        message: this.$t('mixin.BMLXZC'),
                        trigger: 'change'
                    }
                ],
                fieldDelimiter: [
                    {
                        validator: validateFieldDelimiter,
                        trigger: 'change'},
                    { type: 'string', pattern: /^[^\u4e00-\u9fa5！……、，。’；（）￥]+$/, message: this.$t('mixin.CZBNRSZW'), trigger: 'change' }
                ],
                transfer: [
                    {required: true, message: `transfer${this.$t('mixin.BBK')}`}
                ],
                fielType: [
                    {required: true, message: this.$t('mixin.QXZWJLX')}
                ],
                database: [
                    {required: true, message: this.$t('mixin.SJKBNNQJ')}
                ],
                tableTow: [
                    {required: true, message: this.$t('mixin.BBNWK')}
                ],
                indexName: [
                    {required: true, message: this.$t('mixin.SYMCBNWK')},
                    {
                        type: 'string',
                        pattern: /^[a-zA-Z][a-zA-Z0-9#${}_.-]*$/,
                        message: this.$t('mixin.BXYZMKR'),
                        trigger: 'change'
                    }
                ],
                indexType: [
                    {required: true, message: this.$t('mixin.STLXBNWK')}
                ],
                writeMode: [
                    {required: true, message: this.$t('mixin.DRFSBNWK')}
                ],
                batchSize: [
                    {validator: validateBatchSize}
                ]
            }
        }
    },
    methods: {
        // 根据类型回去数据源
        getSourceDataList(value) {
            if (value != '') {
                this.ds_loading = true;
                this.FesApi.fetch('/datasource/selectAll', { sourceType: value, projectIds: this.treeId }, 'get').then(rst => {
                    this.ds_loading = false;
                    this.sourceOrigin = rst.map(item => {
                        return {
                            id: item.id,
                            sourceName: item.sourceName
                        }
                    })
                }).catch(() => this.ds_loading = false);
            }
        },
        sourceChange(value) { // 数据源选择事件
            this.addData.DBData = '';
            this.addData.table = '';
            this.dbDataList = [];
            this.tableList = [];
            this.partKey = [];
            this.partVal = [{ name: '', value: '', partitions: {} }];
            if (value == '') {
                return false;
            } else {
                this.db_loading = true;
                this.FesApi.fetch('/datasource/meta/hive/' + value + '/dbs', {}, {
                    method: 'get',
                    timeout: '60000'
                }).then(rst => {
                    this.db_loading = false;
                    this.dbDataList = JSON.stringify(rst) === '{}' ? [] : rst;
                }).catch(() => this.db_loading = false);
            }
        },
        dbSeleted(value) { // 库选择
            this.addData.table = '';
            this.tableList = [];
            this.partKey = '';
            this.partVal = [{name: '', value: '', partitions: {}}];

            if (value != '') {
                this.table_loading = true;
                this.FesApi.fetch('/datasource/meta/hive/' + this.addData.dataSourceId + '/' + value + '/tables', {}, {
                    method: 'get',
                    timeout: '60000'
                }).then(rst => {
                    this.table_loading = false;
                    this.tableList = JSON.stringify(rst) === '{}' ? [] : rst;
                }).catch(() => this.table_loading = false);
                this.addData.db = value;
            }
        },
        tableValueChangeAction(value, dataparams) { // 选择表
            this.addData.table = value;
            this.partKey = '';
            this.partVal = [{ name: '', value: '', partitions: {} }];
            if (value != '') {
                this.part_loading = true;
                this.FesApi.fetch('/datasource/meta/hive/' + this.addData.dataSourceId + '/' + this.addData.db + '/' + value + '/partitions', {}, {
                    method: 'get',
                    timeout: '60000'
                }).then(partitionInfo => {
                    this.part_loading = false;
                    let partition = partitionInfo.root;
                    if (typeof partition.name !== 'undefined') {
                        // 增加的格式
                        this.timeTemplate(partition);
                        this.partVal = [partition];
                    } else if (partitionInfo.partKeys.length > 0) {
                        partition = { name: partitionInfo.partKeys[0], value: '', partitions: {} };
                        // 增加的格式
                        this.timeTemplate(partition);
                        this.partVal = [partition];
                    }
                    // if have the edit data, just invoke the select event
                    if (this.editData !== '' &&
                    this.editData.config[dataparams].partitions &&
                    this.editData.config[dataparams].partitions !== '' && value === this.editData.config[dataparams].table) {
                        let val = this.editData.config[dataparams].partitions.split(',')[0];
                        // custom value
                        if (value.trim() !== '' && !partition.partitions[val]) {
                            partition.custom = val;
                            this.partCustom(partition);
                        }
                        partition.value = val;
                    }
                    this.partValCache = partitionInfo;
                }).catch(() => this.part_loading = false);
            }
            if (this.addData.dataSourceId !== '' && this.addData.db !== '' && value !== '') {
                this.FesApi.fetch('/datasource/meta/hive/' + this.addData.dataSourceId + '/' + this.addData.db + '/' + value + '/fields', {}, {
                    method: 'get',
                    timeout: '60000'
                }).then(rst=>{
                    this.addData.partValueList = rst;
                    this.nextStepButtonDisable = false;
                }).catch(() => {
                    this.nextStepButtonDisable = true;
                })
            }
        },
        partValueSelectAction(index, value, partitions, dataParams) { // 分区选择
            let deletedItems = this.partVal.splice(index + 1);
            if (deletedItems.length > 0) {
                for (let i in deletedItems) {
                    if (typeof deletedItems[i] === 'object') {
                        deletedItems[i].value = '';
                    }
                }
            }
            if (value !== '') {
                let partition = partitions[value];
                if (typeof (partition) !== 'undefined'
                        && typeof (partition.name) !== 'undefined') {
                    this.timeTemplate(partition);
                    this.partVal.push(partition);
                } else if (typeof (partition) !== 'undefined' && index + 1 < this.partValCache.partKeys.length) {
                    partition = { name: this.partValCache.partKeys[index + 1], value: '', partitions: {} };
                    this.timeTemplate(partition);
                    this.partVal.push(partition);
                }
                if (this.editData !== '' &&
                this.editData.config[dataParams].partitions &&
                this.editData.config[dataParams].partitions !== '') {
                    let partValArray = this.editData.config[dataParams].partitions.split(',');

                    if (index + 1 < partValArray.length) {
                        let nextValue = partValArray[index + 1];
                        // custom value
                        if (nextValue.trim() !== '' && !partition.partitions[nextValue]) {
                            partition.custom = nextValue;
                            this.partCustom(partition);
                        }
                        partition.value = nextValue;
                        // invoke select method
                        this.partValueSelect({index: index + 1, value: nextValue, partitions: partition.partitions});
                    } else {
                        this.editData.config[dataParams].partitions = '';
                    }
                }

            }
            // when select change, delete custom partitions(exclude selected one)
            for (let keyV in partitions) {
                let partition = partitions[keyV];
                if (value !== keyV && partition.tmp === true) {
                    delete partitions[keyV];
                }
            }
        },
        partCustom(item) {
            // get from the 'custom' property, then set into 'value'
            let value = item.custom;
            item.partitions[value] = { tmp: true };
            item.value = value;
            item.custom = '';
        },
        partCustomClear(item) {
            item.custom = '';
        },
        parNextStep() {
            const params = {
                show: this.syncMetaShow,
                syncMeta: this.syncMeta,
                writeMode: this.writeMode
            }
            this.$emit('syncMeta', params)
            if (this.addData.type === '') {
                this.$Toast.warn(this.$t('mixin.XZSJYLX'));
                return false;
            }
            if (this.addData.type === 'hive') {
                if (this.addData.dataSourceId === '') {
                    this.$Toast.warn(this.$t('mixin.XZSJY'));
                    return false;
                }
                if (this.addData.DBData === '') {
                    this.$Toast.warn(this.$t('mixin.XZK'));
                    return false;
                }
                if (this.addData.table === '') {
                    this.$Toast.warn(this.$t('mixin.QXZB'));
                    return false;
                }
                this.addData.partitions = '';
                if (this.partVal.length > 0) {
                    let partValArray = [];
                    for (let idx in this.partVal) {
                        let value = this.partVal[idx].value;
                        if (typeof (value) !== 'undefined' && value !== '') {
                            partValArray.push(value);
                        }
                    }
                    if (partValArray.length > 0) {
                        this.addData.partitions = partValArray.join(',');
                    }
                }
                this.$refs.DataSource.validate((valid) => {
                    if (valid) {
                        this.$emit('nextStep', this.addData);
                    }
                })
            } else {
                this.$refs.DataSource.validate((valid) => {
                    if (valid) {
                        if (this.addData.type === SOURCETYPE.MYSQL && this.tdsqlData.table <= 0) {
                            this.tdsqlData.errorShow = true;
                        } else {
                            this.$emit('nextStep', this[this.typeTransitionDataName(this.addData.type)]);
                        }
                    } else {
                        if (this.addData.type === SOURCETYPE.MYSQL && this.tdsqlData.table <= 0) {
                            this.tdsqlData.errorShow = true;
                        }
                    }
                })
            }
        },
        localFsDataAction(params) {
            this.localFsData = params;
        },
        hdfsDataAction(params) {
            this.hdfsData = params;
        },
        sftpDataAction(params) {
            this.sftpData = params;
        },
        tdsqlDataAction(params) {
            this.tdsqlData = params
        },
        timeTemplate(value) {
            value.partitions['${yyyyMMdd}'] = {}
            value.partitions['${yyyy-MM-dd}'] = {}
            value.partitions['${run_date-1}'] = {}
            value.partitions['${run_date-7}'] = {}
            value.partitions['${run_month_begin-1}'] = {}
            value.partitions['${run_month_end-1}'] = {}
            value.partitions['${run_date}'] = {}
        },
        typeTransitionDataName(type) {
            switch (type) {
                case SOURCETYPE.HIVE:
                    return 'addData';
                case SOURCETYPE.HDFS:
                    return 'hdfsData';
                case SOURCETYPE.SFTP:
                    return 'sftpData';
                case SOURCETYPE.MYSQL:
                    return 'tdsqlData';
                case SOURCETYPE.ELASTICSEARCH:
                    return 'elasticSearchData';
                case SOURCETYPE.LOCAL_FS:
                    return 'localFsData';
                case SOURCETYPE.ORACLE:
                    return 'oracleData';
                default:
                    break;
            }
        },
        changeDataSourceId(type, val, idKey) {
            if (!type) return;
            for (let index in val) {
                if (this.editData[idKey] === val[index].id) {
                    this[type].dataSourceId = this.editData[idKey];
                    return;
                }
            }
        }
    }
}

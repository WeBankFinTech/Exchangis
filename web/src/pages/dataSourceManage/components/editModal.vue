<template>
    <a-modal :title="$t(`dataSource.editModal.title.${mode}`)"
             :footer="null"
             :visible="visible"
             :confirm-loading="confirmLoading"
             :width="600"
             @cancel="$emit('update:visible', false)"
    >
        <a-spin :spinning="confirmLoading">
            <DatasourceForm v-if="visible" ref="datasourceForm" :data="modalCfg" @connect="handleConnect" @submit="handleOk" @cancel="$emit('update:visible', false)" />
            <div v-if="mode === 'read'" class="mark-layer"></div>
            <!--<a-form ref="formRef" :model="formState" :label-col="{ span: 6 }">-->
        <!--<a-form-item :label="$t(`dataSource.editModal.form.fields.dataSourceName.label`)" name="dataSourceName">-->
            <!--<a-input :maxLength="30" v-model:value="formState.dataSourceName" :placeholder="$t(`dataSource.editModal.form.fields.dataSourceName.placeholder`)" />-->
        <!--</a-form-item>-->
        <!--<a-form-item label="地址" :name="['connectParams', 'host']">-->
            <!--<a-input :maxLength="100" v-model:value="formState.connectParams.host" placeholder="数据源host" />-->
        <!--</a-form-item>-->
        <!--<a-form-item-->
            <!--:validate-status="portValidateStatus"-->
            <!--:help="portErrorMsg"-->
            <!--label="端口" :name="['connectParams', 'port']" >-->
            <!--<a-input-->
            <!--:maxLength="6" v-model:value="formState.connectParams.port" placeholder="端口" @change="validatePort" />-->
        <!--</a-form-item>-->
        <!--<a-form-item v-if="type == 1" label="用户名" :name="['connectParams', 'username']">-->
            <!--<a-input :maxLength="30" v-model:value="formState.connectParams.username" placeholder="用户名" />-->
        <!--</a-form-item>-->
        <!--<a-form-item v-if="type == 1" label="密码" :name="['connectParams', 'password']">-->
            <!--<a-input :maxLength="30" v-model:value="formState.connectParams.password" placeholder="密码" type="password"/>-->
        <!--</a-form-item>-->
        <!--<a-form-item label="标签" :name="['connectParams', 'labels']">-->
            <!--<a-input :maxLength="200" v-model:value="formState.labels" placeholder="逗号分割"/>-->
        <!--</a-form-item>-->
        <!--<a-form-item label="注释" :name="['connectParams', 'comment']">-->
            <!--<a-input :maxLength="200" v-model:value="formState.connectParams.comment" placeholder="注释" />-->
        <!--</a-form-item>-->
        <!--<a-form-item :label="$t(`dataSource.editModal.form.fields.dataSourceDesc.label`)" name="dataSourceDesc">-->
            <!--<a-textarea :maxLength="300" v-model:value="formState.dataSourceDesc" :placeholder="$t(`dataSource.editModal.form.fields.dataSourceDesc.placeholder`)" />-->
        <!--</a-form-item>-->
            <!--</a-form>-->
        </a-spin>
    </a-modal>
</template>

<script>
import { toRaw } from 'vue';
import { message } from 'ant-design-vue';
import { createDataSource, updateDataSource, testDataSourceNotSavedConnect } from '@/common/service';
import DatasourceForm from './datasourceForm/index';

export default {
    name: 'projectEditModal',
    components: {
        DatasourceForm
    },
    props: {
        // 是否可见
        visible: {
            type: Boolean,
            required: true
        },
        // 模式
        mode: {
            type: String,
            required: true
        },
        // id如果是新增模式可以不传
        id: {
            default: ''
        },
        // 创建的数据源类型
        type: {
            default: ''
        },
        modalCfg: {
            type: Object,
            required: true
        }
    },
    emits: ['finish', 'cancel', 'update:visible'],
    data() {
        return {
            // 是否加载中
            confirmLoading: false,
            dataList: [],
            // 表单数据
            formState: {
                // 数据源名称
                dataSourceName: '',
                // 数据源描述
                dataSourceDesc: '',
                // 连接参数
                connectParams: {
                    host: '',
                    port: '',
                    username: '',
                    password: ''
                },
                label: '',
                comment: ''
            },

            // port
            portValidateStatus: '',
            portErrorMsg: null
        };
    },
    watch: {
        id(val) {
            // if (val) this.getDataSourceById();
        },
        type() {
            this.init();
        }
    },
    methods: {
        init() {
            /* this.formState = {
        // 数据源名称
        dataSourceName: "",
        // 数据源描述
        dataSourceDesc: "",
        // 连接参数
        connectParams: {
        host: "",
          port: "",
          username: "",
          password: "",
        },
        labels: '',
        comment: ""
      }
      //port
      this.portValidateStatus = ''
      this.portErrorMsg = null
      this.confirmLoading = false */
        },
        /* validatePort() {
      if (!/^[0-9]*$/.test(this.formState.connectParams.port)) {
        this.portValidateStatus = 'error'
        this.portErrorMsg = '请正确输入端口号'
      } else {
        this.portValidateStatus = 'success'
        this.portErrorMsg = null
      }
    },
    async getDataSourceById() {
      this.confirmLoading = true;
      let { info } = await getDataSourceById(this.id);
      this.confirmLoading = false;
      this.formState = {
        dataSourceName: info.dataSourceName,
        dataSourceDesc: info.dataSourceDesc,
        connectParams: info.connectParams,
        labels: info.labels,
        comment: info.comment || "",
      };
    }, */
        async handleConnect(formState, originalDefine) {
            // console.log('this.modalCfg.isTested: ', this.modalCfg.isTested);
            // if (!this.modalCfg.isTested) {
            //     this.modalCfg.isTested = true;
            //     console.log('this.modalCfg.isTested2: ', this.modalCfg.isTested);
            formState = JSON.parse(formState);
            const connectParams = {};
            originalDefine.forEach((item) => {
                connectParams[item.key] = formState[item.key];
            });
            // try {
            await testDataSourceNotSavedConnect({
                dataSourceTypeId: this.type,
                createSystem: this.modalCfg.createSystem,
                createIdentify: '',
                dataSourceName: formState.dataSourceName,
                dataSourceDesc: formState.dataSourceDesc || '',
                label: formState.label || '',
                comment: formState.comment || '更新',
                connectParams: {
                    ...connectParams
                }
            });
            message.success('连接成功');
            //     this.modalCfg.isTested = false;
            // } catch (error) {
            //     console.log('error: ', error);
            //     this.modalCfg.isTested = false;
            // }
            // }
        },
        // modal完成
        async handleOk(formState, originalDefine) {
            formState = JSON.parse(formState);
            const connectParams = {};
            originalDefine.forEach((item) => {
                connectParams[item.key] = formState[item.key];
            });
            this.confirmLoading = true;
            try {
                if (this.mode === 'create') {
                    await createDataSource({
                        dataSourceTypeId: this.type,
                        createSystem: this.modalCfg.createSystem,
                        createIdentify: '',
                        dataSourceName: formState.dataSourceName,
                        dataSourceDesc: formState.dataSourceDesc || '',
                        label: formState.label || '',
                        comment: formState.comment || '更新',
                        connectParams: {
                            ...connectParams
                        }
                    });
                    message.success('创建成功');
                }
                if (this.mode === 'edit') {
                    await updateDataSource(this.id, {
                        dataSourceTypeId: this.type,
                        createSystem: this.modalCfg.createSystem,
                        createIdentify: '',
                        dataSourceName: formState.dataSourceName,
                        dataSourceDesc: formState.dataSourceDesc || '',
                        label: formState.label || '',
                        comment: formState.comment || '更新',
                        connectParams: {
                            ...connectParams
                        }
                    });
                    message.success('修改成功');
                }
            } catch (error) {}
            this.confirmLoading = false;
            this.$emit('update:visible', false);
            this.$emit('finish');
        }
    }
};
</script>

<style scoped lang="less">
  .mark-layer {
    position: absolute;
    width: 100%;
    top:0;
    height: calc(100% - 40px);
    background-color: rgba(255,255,255,0.5);
  }
</style>

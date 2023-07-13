<template>
    <div class="container">
        <a-modal
            :visible="visible"
            :title="actionMap[mode]"
            @ok="handleOk"
            @cancel="handleCancel"
        >
            <template #footer>
                <a-button key="back" @click="handleCancel">
                    {{$t('job.action.cancel')}}
                </a-button>
                <a-button
                    key="submit"
                    type="primary"
                    :loading="loading"
                    @click="handleOk"
                >
                    {{$t('job.action.save')}}
                </a-button>
            </template>
            <a-form
                ref="formRef"
                :model="formState"
                :rules="rules"
                :label-col="labelCol"
                :wrapper-col="wrapperCol"
            >
                <a-form-item
                    v-if="mode === 'copy'"
                    ref="originName"
                    :label="$t('job.jobDetail.originJob')"
                    name="originName"
                >
                    <a-input v-model:value="formState.originName" disabled />
                </a-form-item>
                <a-form-item
                    ref="jobName"
                    :label="$t('job.jobDetail.name')"
                    name="jobName"
                >
                    <a-textarea v-model:value="formState.jobName" :maxLength="100" showCount  />
                </a-form-item>
                <a-form-item :label="$t('job.jobDetail.label')" name="jobLabels">
                    <a-tag
                        v-for="tag in state.tags"
                        :key="tag"
                        closable
                        @close="handleClose(tag)"
                    >
                        {{tag}}
                    </a-tag>
                    <a-input
                        v-if="state.inputVisible"
                        ref="inputRef"
                        v-model:value="state.inputValue"
                        type="text"
                        size="small"
                        :style="{ width: '100px' }"
                        :maxLength="20"
                        @blur="handleInputConfirm"
                        @keyup.enter="handleInputConfirm"
                    />
                    <a-tag
                        v-else
                        style="background: #fff; border-style: dashed"
                        @click="showInput"
                    >
                        <plus-outlined />
                    </a-tag>
                </a-form-item>
                <a-form-item
                    v-if="!formState.originName"
                    :label="$t('job.jobDetail.type')"
                    name="jobType"
                >
                    <a-select v-model:value="formState.jobType">
                        <a-select-option value="OFFLINE">{{$t('job.type.offline')}}</a-select-option>
                        <!--<a-select-option value="STREAM">{{$t('job.type.stream')}} </a-select-option>-->
                    </a-select>
                </a-form-item>
                <a-form-item :label="$t('job.jobDetail.engine')" name="engineType">
                    <a-select
                        v-model:value="formState.engineType"
                        :disabled="!!formState.originName || (mode === 'modify')"
                    >
                        <a-select-option v-for="engine in engines" :key="engine" :value="engine">{{engine}}</a-select-option>
                    </a-select>
                </a-form-item>
                <a-form-item :label="$t('job.jobDetail.description')" name="jobDesc">
                    <a-textarea v-model:value="formState.jobDesc" :maxLength="200" />
                </a-form-item>
            </a-form>
        </a-modal>
    </div>
</template>
<script>
import {
    defineComponent,
    reactive,
    ref,
    toRaw,
    watchEffect,
    toRefs,
    nextTick,
    onMounted
} from 'vue';
import { PlusOutlined } from '@ant-design/icons-vue';
import { useI18n } from '@fesjs/fes';
import { message } from 'ant-design-vue';
import {
    createJob, copyJob, modifyJob, getEngineType
} from '@/common/service';

export default defineComponent({
    components: {
        PlusOutlined
    },
    props: {
        visible: Boolean,
        editData: Object,
        projectId: String,
        mode: String
    },
    emits: ['handleJobAction'], // 需要声明emits
    setup(props, context) {
        const actionMap = {
            create: '创建任务',
            copy: '复制任务',
            modify: '编辑任务'
        };
        const successMap = {
            create: '创建任务成功',
            copy: '复制任务成功',
            modify: '编辑任务成功'
        };
        const { t } = useI18n({ useScope: 'global' });
        const formRef = ref();
        const formState = reactive({
            id: '',
            originName: '',
            jobName: '',
            jobType: 'OFFLINE',
            engineType: '',
            jobLabels: '',
            jobDesc: ''
        });
        const inputRef = ref();
        const state = reactive({
            tags: [],
            inputVisible: false,
            inputValue: ''
        });
        const _setFormData = (editData) => {
            formState.jobName = editData.jobName || '';
            formState.jobType = editData.jobType || '';
            formState.engineType = editData.engineType || '';
            formState.jobLabels = editData.jobLabels || '';
            formState.jobDesc = editData.jobDesc || '';
            formState.id = editData.id || '';
        };
        watchEffect(() => {
            const { editData, mode } = props;
            _setFormData(editData);
            if (mode === 'copy') {
                formState.originName = editData.jobName;
                formState.jobName = '';
                state.tags = editData.jobLabels ? editData.jobLabels.split(',') : [];
            } else if (mode === 'modify') {
                state.tags = editData.jobLabels ? editData.jobLabels.split(',') : [];
            }
        });
        const engines = reactive({
            engines: []
        });
        onMounted(async () => {
            const data = await getEngineType();
            engines.engines = data && data.result || [];
            console.log(data);
        });
        const rules = {
            jobName: [
                {
                    required: true,
                    message: t('job.jobDetail.jobNameEmpty'),
                    trigger: 'blur'
                }
            ],
            jobType: [
                {
                    required: true,
                    message: t('job.jobDetail.jobTypeEmpty'),
                    trigger: 'change'
                }
            ],
            engineType: [
                {
                    required: true,
                    message: t('job.jobDetail.engineEmpty'),
                    trigger: 'change'
                }
            ]
        };

        const handleOk = () => {
            formRef.value
                .validate()
                .then(async () => {
                    const data = toRaw(formState);
                    const tags = toRaw(state.tags);
                    const { originName, ...rest } = data;
                    const editData = toRaw(props.editData);
                    let params; let
                        res;
                    if (props.mode === 'copy') {
                        params = {
                            jobName: rest.jobName,
                            jobLabels: tags.join(','),
                            jobDesc: rest.jobDesc
                        };
                        res = await copyJob(editData.id, params);
                    } else if (props.mode === 'create') {
                        params = {
                            ...rest,
                            projectId: props.projectId,
                            jobLabels: tags.join(',')
                        };
                        res = await createJob(params);
                    } else if (props.mode === 'modify') {
                        params = {
                            ...rest,
                            jobLabels: tags.join(',')
                        };
                        res = await modifyJob(editData.id, params);
                    }
                    if (res && res.result) {
                        message.success(successMap[props.mode]);
                        context.emit('handleJobAction', { ...params, ...editData });
                        formRef.value.resetFields();
                        editData.jobLabels = res.result.jobLabels ? res.result.jobLabels : '';
                        state.tags = [];
                        state.inputVisible = false;
                        state.inputValue = '';
                    }
                })
                .catch((e) => {
                    console.log(e);
                });
        };
        const handleCancel = () => {
            context.emit('handleJobAction');
            formRef.value.resetFields();
            state.tags = [];
            state.inputVisible = false;
            state.inputValue = '';
        };

        const handleClose = (removedTag) => {
            const tags = state.tags.filter(tag => tag !== removedTag);
            state.tags = [...tags];
        };

        const showInput = () => {
            state.inputVisible = true;
            nextTick(() => {
                inputRef.value.focus();
            });
        };

        const handleInputConfirm = () => {
            const inputValue = state.inputValue;
            let tags = state.tags;
            if (inputValue && !tags.includes(inputValue.trim())) {
                tags = [...tags, inputValue.trim()];
            }
            state.tags = tags;
            state.inputVisible = false;
            state.inputValue = '';
        };

        return {
            formRef,
            loading: false,
            labelCol: { span: 5 },
            wrapperCol: { span: 19 },
            formState,
            rules,
            handleOk,
            handleCancel,
            state,
            ...toRefs(engines),
            handleClose,
            showInput,
            handleInputConfirm,
            inputRef,
            actionMap
        };
    }
});
</script>

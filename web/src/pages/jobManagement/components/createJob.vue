<template>
  <div class="container">
    <a-modal
      :visible="visible"
      :title="
        !formState.originName
          ? $t('job.action.createJob')
          : $t('job.action.copyJob')
      "
      @ok="handleOk"
      @cancel="handleCancel"
    >
      <template #footer>
        <a-button key="back" @click="handleCancel">{{
          $t('job.action.cancel')
        }}</a-button>
        <a-button
          key="submit"
          type="primary"
          :loading="loading"
          @click="handleOk"
        >
          {{ $t('job.action.save') }}
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
          v-if="!!formState.originName"
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
          <a-input v-model:value="formState.jobName" />
        </a-form-item>
        <a-form-item :label="$t('job.jobDetail.label')" name="jobLabels">
          <a-tag
            v-for="tag in tags"
            :key="tag"
            closable
            @close="handleClose(tag)"
          >
            {{ tag }}
          </a-tag>
          <a-input
            v-if="inputVisible"
            ref="inputRef"
            type="text"
            size="small"
            :style="{ width: '100px' }"
            v-model:value="inputValue"
            @blur="handleInputConfirm"
            @keyup.enter="handleInputConfirm"
          />
          <a-tag
            v-else
            @click="showInput"
            style="background: #fff; border-style: dashed"
          >
            <plus-outlined />
          </a-tag>
        </a-form-item>
        <a-form-item
          :label="$t('job.jobDetail.type')"
          name="jobType"
          v-if="!formState.originName"
        >
          <a-select v-model:value="formState.jobType">
            <a-select-option value="OFFLINE">离线任务</a-select-option>
            <a-select-option value="STREAM">流式任务 </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item :label="$t('job.jobDetail.engine')" name="engineType">
          <a-select
            v-model:value="formState.engineType"
            :disabled="!!formState.originName"
          >
            <a-select-option value="DataX">DataX</a-select-option>
            <a-select-option value="Sqoop">Sqoop</a-select-option>
            <a-select-option value="Flink">Flink</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item :label="$t('job.jobDetail.description')" name="jobDesc">
          <a-textarea v-model:value="formState.jobDesc" />
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
} from 'vue';
import { PlusOutlined } from '@ant-design/icons-vue';
import { useI18n } from '@fesjs/fes';
import { createJob, copyJob, getEngineType } from '@/common/service';
export default defineComponent({
  components: {
    PlusOutlined,
  },
  props: {
    visible: Boolean,
    editData: Object,
    projectId: Number,
  },
  emits: ['handleJobAction'], //需要声明emits
  setup(props, context) {
    const { t } = useI18n({ useScope: 'global' });
    const formRef = ref();
    const formState = reactive({
      originName: '',
      jobName: '',
      jobType: 'OFFLINE',
      engineType: 'DataX',
      jobLabels: '',
      jobDesc: '',
    });
    const inputRef = ref();
    const state = reactive({
      tags: [],
      inputVisible: false,
      inputValue: '',
    });
    watchEffect(() => {
      const editData = toRaw(props.editData);
      if (editData.id) {
        formState.originName = editData.jobName;
        formState.engineType = editData.engineType;
        state.tags = editData.jobLabels ? editData.jobLabels.split(',') : [];
      } else {
        formState.originName = '';
        formState.engineType = 'DataX';
      }
    });
    const rules = {
      jobName: [
        {
          required: true,
          message: '任务名不能为空',
          trigger: 'blur',
        },
      ],
      jobType: [
        {
          required: true,
          message: '任务类型不能为空',
          trigger: 'change',
        },
      ],
      engineType: [
        {
          required: true,
          message: '执行引擎不能为空',
          trigger: 'change',
        },
      ],
    };

    const handleOk = () => {
      formRef.value
        .validate()
        .then(async () => {
          const data = toRaw(formState);
          const tags = toRaw(state.tags);
          const { originName, ...rest } = data;
          const editData = toRaw(props.editData);
          const isCopy = !!editData.id;
          const params = isCopy
            ? {
                jobName: rest.jobName,
                jobLabels: tags.join(','),
                jobDesc: rest.jobDesc,
              }
            : {
                ...rest,
                projectId: props.projectId,
                jobLabels: tags.join(','),
              };
          const res = isCopy
            ? await copyJob(editData.id, params)
            : await createJob(params);
          if (res && res.result) {
            context.emit('handleJobAction', { ...params, ...editData });
            formRef.value.resetFields();
            Object.assign(state, {
              tags: [],
              inputVisible: false,
              inputValue: '',
            });
          }
        })
        .catch((e) => {
          console.log(e);
        });
    };
    const handleCancel = () => {
      context.emit('handleJobAction');
      formRef.value.resetFields();
      Object.assign(state, {
        tags: [],
        inputVisible: false,
        inputValue: '',
      });
    };

    const handleClose = (removedTag) => {
      const tags = state.tags.filter((tag) => tag !== removedTag);
      console.log(tags);
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
      console.log(tags);
      Object.assign(state, {
        tags,
        inputVisible: false,
        inputValue: '',
      });
    };

    return {
      formRef,
      loading: false,
      labelCol: { span: 6 },
      wrapperCol: { span: 18 },
      formState,
      rules,
      handleOk,
      handleCancel,
      ...toRefs(state),
      handleClose,
      showInput,
      handleInputConfirm,
      inputRef,
    };
  },
});
</script>

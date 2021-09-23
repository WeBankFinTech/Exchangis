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
        <a-button key="back" @click="handleCancel">取消</a-button>
        <a-button
          key="submit"
          type="primary"
          :loading="loading"
          @click="handleOk"
          >保存</a-button
        >
      </template>
      <a-form
        ref="formRef"
        :model="formState"
        :rules="rules"
        :label-col="labelCol"
        :wrapper-col="wrapperCol"
      >
        <a-form-item
          ref="originName"
          label="元任务"
          name="originName"
          v-if="!!formState.originName"
        >
          <a-input v-model:value="formState.originName" disabled />
        </a-form-item>
        <a-form-item ref="jobName" label="任务名" name="jobName">
          <a-input v-model:value="formState.jobName" />
        </a-form-item>
        <a-form-item label="业务标签" name="jobLabels">
          <a-input v-model:value="formState.jobLabels" />
        </a-form-item>
        <a-form-item label="任务类型" name="jobType" v-if="!formState.originName">
          <a-select v-model:value="formState.jobType">
            <a-select-option value="OFFLINE">离线任务</a-select-option>
            <a-select-option value="STREAM">流式任务 </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="执行引擎" name="engineType">
          <a-select
            v-model:value="formState.engineType"
            :disabled="!!formState.originName"
          >
            <a-select-option v-for="item in engineList" :value="item">{{item}}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="任务描述" name="jobDesc">
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
} from "vue";

import { createJob, getEngineType } from "@/common/service"
import { useRoute } from 'vue-router'

export default defineComponent({
  props: {
    visible: Boolean,
    editData: Object,
  },
  data() {
    return {
      engineList: []
    }
  },
  async mounted(){
    this.engineList = (await getEngineType()).result
  },
  emits: ["handleJobAction"], //需要声明emits
  setup(props, context) {
    const route = useRoute()
    const formRef = ref();
    const formState = reactive({
      originName: "",
      jobName: "",
      jobType: "OFFLINE",
      engineType: "DataX",
      jobLabels: "",
      jobDesc: "",
    });
    watchEffect(() => {
      const editData = toRaw(props.editData);
      console.log(editData);
      if (editData.id) {
        formState.originName = editData.jobName;
        formState.engineType = editData.engineType;
      } else {
        formState.originName = "";
        formState.engineType = "DataX";
      }
    });
    const rules = {
      jobName: [
        {
          required: true,
          message: "任务名不能为空",
          trigger: "blur",
        },
      ],
      jobType: [
        {
          required: true,
          message: "任务类型不能为空",
          trigger: "change",
        },
      ],
      engineType: [
        {
          required: true,
          message: "执行引擎不能为空",
          trigger: "change",
        },
      ],
    };

    const handleOk = () => {
      formRef.value
        .validate()
        .then(() => {
          createJob({ ...toRaw(formState), projectId: route.query.projectId })
          context.emit("handleJobAction", "success");
          formRef.value.resetFields();
        })
        .catch((e) => {
          console.log(e);
        });
    };
    const handleCancel = () => {
      context.emit("handleJobAction", "cancel");
      formRef.value.resetFields();
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
    };
  },
});
</script>

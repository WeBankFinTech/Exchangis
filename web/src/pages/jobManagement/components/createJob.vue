<template>
  <div class="container">
    <a-modal
      :visible="visible"
      title="新建任务"
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
        <a-form-item ref="jobName" label="任务名" name="jobName">
          <a-input v-model:value="formState.jobName" />
        </a-form-item>
        <a-form-item label="业务标签" name="jobLabels">
          <a-input v-model:value="formState.jobLabels" />
        </a-form-item>
        <a-form-item label="任务类型" name="jobType">
          <a-select v-model:value="formState.jobType">
            <a-select-option value="OFFLINE">离线任务</a-select-option>
            <a-select-option value="STREAM">流式任务 </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="执行引擎" name="engineType">
          <a-select v-model:value="formState.engineType">
            <a-select-option value="DataX">DataX</a-select-option>
            <a-select-option value="Sqoop">Sqoop</a-select-option>
            <a-select-option value="Flink">Flink</a-select-option>
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
import { defineComponent, reactive, ref, toRaw } from "vue";
import { useI18n } from "@fesjs/fes";
export default defineComponent({
  props: {
    visible: Boolean,
  },
  emits: ["handleCreateJob"], //需要声明emits
  setup(props, context) {
    const formRef = ref();
    const { t } = useI18n();
    const formState = reactive({
      jobName: "",
      jobType: "OFFLINE",
      engineType: "DataX",
      delivery: false,
      jobLabels: "",
      jobDesc: "",
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
          console.log(toRaw(formState));
          context.emit("handleCreateJob", "success");
        })
        .catch((e) => {
          console.log(e);
        });
    };
    const handleCancel = () => {
      context.emit("handleCreateJob", "cancel");
      formRef.value.resetFields();
    };

    return {
      formRef,
      t,
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

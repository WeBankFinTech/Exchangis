<template>
  <a-modal :title="$t(`projectManage.editModal.title.${mode}`)" :visible="visible"
    :confirm-loading="confirmLoading" @ok="handleOk" @cancel="$emit('update:visible', false)">
    <a-spin :spinning="confirmLoading">
      <a-form ref="formRef" :model="formState" :label-col="{ span: 4 }">
        <a-form-item :label="$t(`projectManage.editModal.form.fields.projectName.label`)"
          name="projectName" :rules="[
            { required: true, message: '项目名必须填写!' },
            { pattern: /^[a-zA-Z]/, message: '项目名请以字母开头进行填写!', trigger: 'change' }
          ]">
          <a-input v-model:value="formState.projectName" :maxLength="100" :placeholder="
              $t(`projectManage.editModal.form.fields.projectName.placeholder`)
            " :disabled="mode === 'edit'"/>
        </a-form-item>
        <!--<a-form-item :label="$t(`projectManage.editModal.form.fields.tags.label`)" name="tags">-->
        <!--<a-select mode="multiple" v-model:value="formState.tags" :placeholder="$t(`projectManage.editModal.form.fields.tags.placeholder`)">-->
        <!--<a-select-option value="标签1">标签1</a-select-option>-->
        <!--<a-select-option value="标签2">标签2</a-select-option>-->
        <!--</a-select>-->
        <!--</a-form-item>-->
        <a-form-item :label="$t(`projectManage.editModal.form.fields.description.label`)"
          name="description">
          <a-textarea v-model:value="formState.description" :placeholder="
              $t(`projectManage.editModal.form.fields.description.placeholder`)
            " />
        </a-form-item>

        <a-form-item :label="$t(`projectManage.editModal.form.fields.editUsers.label`)"
          name="editUsers">
          <a-select mode="multiple" v-model:value="formState.editUsers"
            :placeholder="$t(`projectManage.editModal.form.fields.editUsers.placeholder`)" disabled>
            <a-select-option v-for="item of allUsers" :value="item.username" :key="item.id">
              {{ item.username}}
            </a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item :label="$t(`projectManage.editModal.form.fields.viewUsers.label`)"
          name="viewUsers">
          <a-select mode="multiple" v-model:value="formState.viewUsers"
            :placeholder="$t(`projectManage.editModal.form.fields.viewUsers.placeholder`)" disabled>
            <a-select-option v-for="item of allUsers" :value="item.username" :key="item.id">
              {{ item.username}}
            </a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item :label="$t(`projectManage.editModal.form.fields.execUsers.label`)"
          name="execUsers">
          <a-select mode="multiple" v-model:value="formState.execUsers"
            :placeholder="$t(`projectManage.editModal.form.fields.execUsers.placeholder`)" disabled>
            <a-select-option v-for="item of allUsers" :value="item.username" :key="item.id">
              {{ item.username}}
            </a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
import { toRaw } from 'vue';
import { message } from 'ant-design-vue';
import {
  createProject,
  getProjectById,
  updateProject,
} from '@/common/service';
export default {
  name: 'ProjectEditModal',
  props: {
    // 是否可见
    visible: {
      type: Boolean,
      required: true,
    },
    // 模式
    mode: {
      type: String,
      required: true,
    },
    id: {
      type: String,
      default: '',
    },
  },
  emits: ['finish', 'cancel', 'update:visible'],
  data() {
    return {
      // 是否加载中
      confirmLoading: false,
      // 表单数据
      formState: {
        projectName: '',
        tags: [],
        description: '',
        viewUsers: [],
        execUsers: [],
        editUsers: [],
      },
      allUsers: [],
    };
  },
  computed: {
    // 确保弹窗显示且id存在
    bothParams() {
      return this.id && this.visible;
    },
  },
  watch: {
    async bothParams(newVlaue) {
      if (newVlaue && this.mode === 'edit') {
        this.confirmLoading = true;
        const { item } = await getProjectById(this.id);
        this.confirmLoading = false;
        this.formState = {
          projectName: item.name,
          tags: item.tags.split(','),
          description: item.description,
          viewUsers: item.viewUsers.split(',').filter((v) => v),
          execUsers: item.execUsers.split(',').filter((v) => v),
          editUsers: item.editUsers.split(',').filter((v) => v),
        };
      }
    },
    visible(cur, pre) {
      if (!cur) {
        this.$refs.formRef.resetFields();
      }
    },
  },
  methods: {
    async handleOk() {
      await this.$refs.formRef.validate();
      const formatData = {
        id: this.id ? this.id : undefined,
        projectName: this.formState.projectName,
        description: this.formState.description,
        tags: this.formState.tags.join(),
        viewUsers: this.formState.viewUsers.join(),
        execUsers: this.formState.execUsers.join(),
        editUsers: this.formState.editUsers.join(),
      };
      this.confirmLoading = true;
      try {
        if (this.mode === 'create') {
          await createProject(formatData);
          message.success('创建成功');
        }
        if (this.mode === 'edit') {
          await updateProject(formatData);
          message.success('修改成功');
        }
      } catch (error) {}
      this.confirmLoading = false;
      this.$emit('update:visible', false);
      this.$emit('finish');
    }
  },
};
</script>

<style scoped lang="less"></style>

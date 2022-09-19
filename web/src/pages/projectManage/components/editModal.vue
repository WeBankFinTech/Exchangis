<template>
  <a-modal :title="$t(`projectManage.editModal.title.${mode}`)" :visible="visible"
    :confirm-loading="confirmLoading" @ok="handleOk" @cancel="$emit('update:visible', false)">
    <a-spin :spinning="confirmLoading">
      <a-form ref="formRef" :model="formState" :label-col="{ span: 4 }">
        <a-form-item :label="$t(`projectManage.editModal.form.fields.projectName.label`)"
          name="projectName" required :help="helpMsg" :validate-status="helpStatus">
          <a-input v-model:value="formState.projectName" :maxLength="100" :placeholder="
              $t(`projectManage.editModal.form.fields.projectName.placeholder`)
            " />
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
          <a-select 
            mode="multiple" 
            v-model:value="formState.editUsers"
            :placeholder="$t(`projectManage.editModal.form.fields.editUsers.placeholder`)" :disabled="isDss">
            <a-select-option 
              v-for="item of allUsers" 
              :value="item.username" 
              :key="item.id">{{ item.username}}
            </a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item :label="$t(`projectManage.editModal.form.fields.viewUsers.label`)"
          name="viewUsers">
          <a-select 
            mode="multiple" 
            v-model:value="formState.viewUsers"
            :placeholder="$t(`projectManage.editModal.form.fields.viewUsers.placeholder`)" :disabled="isDss">
            <a-select-option 
              v-for="item of allUsers" 
              :value="item.username" 
              :key="item.id">{{ item.username}}
            </a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item :label="$t(`projectManage.editModal.form.fields.execUsers.label`)"
          name="execUsers">
          <a-select 
            mode="multiple" 
            v-model:value="formState.execUsers"
            :placeholder="$t(`projectManage.editModal.form.fields.execUsers.placeholder`)" :disabled="isDss">
            <a-select-option 
              v-for="item of allUsers" 
              :value="item.username" 
              :key="item.id">{{ item.username}}
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
  getAllUsers,
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
    isDss: {
      type: Boolean,
      default: false
    }
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
      helpMsg: '',
      helpStatus: 'success',
      allUsers: [],
    };
  },
  watch: {
    async id(newVlaue) {
      if (newVlaue && this.mode === 'edit') {
        this.confirmLoading = true;
        const { item } = await getProjectById(newVlaue);
        this.confirmLoading = false;
        this.formState = {
          projectName: item.name,
          tags: item.tags.split(','),
          description: item.description,
          viewUsers: item.viewUsers.split(','),
          execUsers: item.execUsers.split(','),
          editUsers: item.editUsers.split(','),
        };
      }
    },
    'formState.projectName'(newval, oldVal) {
      if (!/^[a-zA-Z]/.test(newval)) {
        this.helpMsg = '项目名请以字母开头进行填写';
        this.helpStatus = 'error';
      } else {
        this.helpMsg = '';
        this.helpStatus = 'success';
      }
    },
  },
  created() {
    // 获取dss用户
    this.getAllUsers();
  },
  methods: {
    async handleOk() {
      await this.$refs.formRef.validate();
      if (!/^[a-zA-Z]/.test(this.formState.projectName)) {
        return message.error('项目名请以字母开头进行填写');
      }
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
    },

    // 获取所有用户
    async getAllUsers() {
      try {
        const { users } = (await getAllUsers()) || {};
        this.allUsers = users;
      } catch (err) {
        console.log(err);
      }
    },
  },
};
</script>

<style scoped lang="less"></style>

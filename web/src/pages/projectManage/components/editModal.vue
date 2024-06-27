<template>
  <a-modal :title="$t(`projectManage.editModal.title.${mode}`)" :visible="visible" :confirm-loading="confirmLoading"
    @ok="handleOk" @cancel="$emit('update:visible', false)">
    <a-spin :spinning="confirmLoading">
      <a-form ref="formRef" :model="formState" :label-col="{ span: 4 }">
        <a-form-item :label="$t(`projectManage.editModal.form.fields.projectName.label`)" name="projectName" :rules="[
          { required: true, message: '项目名必须填写!' },
          { pattern: /^[a-zA-Z]/, message: '项目名请以字母开头进行填写!', trigger: 'change' }
        ]">
          <a-input v-model:value="formState.projectName" :maxLength="100" :placeholder="$t(`projectManage.editModal.form.fields.projectName.placeholder`)
            " :disabled="mode === 'edit'" />
        </a-form-item>
        <!--<a-form-item :label="$t(`projectManage.editModal.form.fields.tags.label`)" name="tags">-->
        <!--<a-select mode="multiple" v-model:value="formState.tags" :placeholder="$t(`projectManage.editModal.form.fields.tags.placeholder`)">-->
        <!--<a-select-option value="标签1">标签1</a-select-option>-->
        <!--<a-select-option value="标签2">标签2</a-select-option>-->
        <!--</a-select>-->
        <!--</a-form-item>-->
        <a-form-item label="数据源" name="dataSources" v-if="['DSS'].includes(domain)">
          <a-select
            v-model:value="formState.dataSources"
            mode="multiple"
            placeholder="请选择数据源"
            :options="dataSourceOptions"
            :disabled="['DSS'].includes(domain)" 
            :filter-option="filterOption"
          ></a-select>
        </a-form-item>

        <a-form-item :label="$t(`projectManage.editModal.form.fields.description.label`)" name="description">
          <a-textarea v-model:value="formState.description" :placeholder="$t(`projectManage.editModal.form.fields.description.placeholder`)
            " />
        </a-form-item>

        <a-form-item :label="$t(`projectManage.editModal.form.fields.editUsers.label`)" name="editUsers">
          <a-select mode="multiple" v-model:value="formState.editUsers"
            :placeholder="$t(`projectManage.editModal.form.fields.editUsers.placeholder`)" disabled>
            <a-select-option v-for="item of allUsers" :value="item.username" :key="item.id">
              {{ item.username }}
            </a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item :label="$t(`projectManage.editModal.form.fields.viewUsers.label`)" name="viewUsers">
          <a-select mode="multiple" v-model:value="formState.viewUsers"
            :placeholder="$t(`projectManage.editModal.form.fields.viewUsers.placeholder`)" disabled>
            <a-select-option v-for="item of allUsers" :value="item.username" :key="item.id">
              {{ item.username }}
            </a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item :label="$t(`projectManage.editModal.form.fields.execUsers.label`)" name="execUsers">
          <a-select mode="multiple" v-model:value="formState.execUsers"
            :placeholder="$t(`projectManage.editModal.form.fields.execUsers.placeholder`)" disabled>
            <a-select-option v-for="item of allUsers" :value="item.username" :key="item.id">
              {{ item.username }}
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
    dataSources: {
      type: Array,
      default: () => ([])
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
        dataSources: [],
        viewUsers: [],
        execUsers: [],
        editUsers: [],
      },
      allUsers: [],
      domain: '',
      editDataSources: [],
      dataSourceOptions: [],
    };
  },
  computed: {
    // 确保弹窗显示且id存在
    bothParams() {
      return this.id && this.visible;
    },
    dataSourceList() {
      const list = [...this.dataSources, ...this.editDataSources];
      const result = [];
      list.forEach(item => {
        if(!result.some(n => n.dataSourceId === item.dataSourceId)) {
          result.push(item)
        }
      })
      return result;
    },
    dataSourceOptions() {
      const map = {};
      this.dataSourceList.forEach(item => {
        if (map[item.type]) {
          map[item.type].push(item);
        } else {
          map[item.type] = [item];
        }
      })
      const arr = [];
      Object.keys(map).forEach(label => {
        const options = map[label].map(item => ({value: item.dataSourceId, label: item.dataSourceName}));
        arr.push({ label, options })
      })
      return arr;
    },
  },
  watch: {
    async bothParams(newVlaue) {
      if (newVlaue && this.mode === 'edit') {
        this.confirmLoading = true;
        const { item } = await getProjectById(this.id);
        this.confirmLoading = false;
        this.domain = (item.domain || '').toUpperCase();
        this.formState = {
          projectName: item.name,
          tags: item.tags.split(','),
          description: item.description,
          viewUsers: item.viewUsers.split(',').filter((v) => v),
          execUsers: item.execUsers.split(',').filter((v) => v),
          editUsers: item.editUsers.split(',').filter((v) => v),
          dataSources: (item.dataSources || []).map(item => item.dataSourceId)
        };
        this.editDataSources = item.dataSources || [];
      }
    },
    visible(cur, pre) {
      if (!cur) {
        this.$refs.formRef.resetFields();
        this.domain = '';
        this.editDataSources = []
      }
    },
  },
  methods: {
    filterOption(input, option) {
      return option.label.toLowerCase().includes(input.toLowerCase());;
    },
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
      formatData.dataSources = this.dataSourceList
        .filter(item => this.formState.dataSources
          .includes(item.dataSourceId))
          .map(item => ({
            dataSourceType: item.dataSourceType,
            dataSourceName: item.dataSourceName,
            dataSourceDesc: item.dataSourceDesc || '',
            dataSourceId: item.dataSourceId,
            modifyTime: item.modifyTime,
            createTime: item.createTime,
            createUser: item.createUser
          }))
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
      } catch (error) { }
      this.confirmLoading = false;
      this.$emit('update:visible', false);
      this.$emit('finish');
    }
  },
};
</script>

<style scoped lang="less"></style>

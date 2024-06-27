<template>
  <div class="table-warp" style="padding-bottom: 32px;">
    <form-create v-model:api="fApi" v-model="formData" :rule="rule" :option="options" />
    <div v-if="isShowWarnTips" class="table-bottom-warn-tips">账户私钥不保存，测试或保存前请重新输入私钥！</div>
    <a-button v-if="data.mode !== 'read'" type="primary" @click="handleTestConnect(row)">
      {{$t("dataSource.table.list.columns.actions.testConnectButton")}}
    </a-button>
    <a-button v-if="data.mode !== 'read'" type="primary" style="float: right;margin: 0 0 0 10px;"
      @click="submit">确定</a-button>
    <a-button style="float: right" @click="$emit('cancel')">取消</a-button>
  </div>
</template>
<script>
import _, { merge, mergeWith } from 'lodash-es';
import { request } from '@fesjs/fes';
import { message } from 'ant-design-vue';
import { toRaw } from 'vue';
import { getEnvironment } from '@/common/utils';
import { getKeyDefine, getDataSourceById } from '@/common/service';

const type = {
  TEXT: { type: 'input' },
  NUMBER: { type: 'input', props: { type: 'number' } },
  PASSWORD: { type: 'input', props: { type: 'password' } },
  EMAIL: { type: 'input', props: { type: 'email' } },
  DATE: { type: 'input', props: { type: 'date' } },
  TEL: { type: 'input', props: { type: 'tel' } },
  TEXTAREA: { type: 'input', props: { type: 'textarea' } },
  URL: { type: 'input', props: { type: 'url' } },
  FILE: (self, source) => ({
    type: 'upload',
    props: {
      uploadType: 'file',
      action: '',
      allowRemove: true,
      maxLength: 1,
      beforeUpload: (file) => {
        self.file = file;
        source.props.value = file.name;
        return false;
      },
    },
  }),
  SELECT: { type: 'select', props: { placement: 'bottom' } },
};
const typesMap = {
  valueType: (data, source, self) => {
    if (type[data.valueType]) {
      if (typeof type[data.valueType] === 'function') {
        return type[data.valueType](self, source);
      }
      return type[data.valueType];
    }
    return { type: data.valueType };
  },
  name: 'title',

  defaultValue: 'value',
  // dataSource: 'options',
  dataSource: (data, source, self) => {
    try {
      return { options: JSON.parse(data.dataSource) };
    } catch (error) {
      console.log('继续判断是否需要加载接口')
    }
    const fApi = self.fApi;
    if (typeof data.dataSource === 'string') {
      if (!/^https?:/.test(data.dataSource)) {
        data.dataSource =
          window.location.origin + '/api/rest_j/v1' + data.dataSource;
        // data.dataSource = window.location.origin + data.dataSource;
      }
      request(
        data.dataSource,
        {
          labels: getEnvironment(),
        },
        {
          method: 'GET',
        }
      ).then((result) => {
        delete source.options;
        source.options = result.envList.map((item) => ({
          label: item.envName,
          value: `${item.id}`,
        }));
        fApi.refreshOptions();
      });
      return { options: [] };
    } else {
      return { options: [] };
    }
  },
  key: 'field',
  description: (data) => ({ props: { placeholder: data.description } }),
  require: (data) => {
    if (data.require) {
      // && !data.valueRegex
      return {
        validate: [
          { required: true, message: `请输入${data.name}`, trigger: 'blur' },
        ],
      };
    }
    return null;
  },
  valueRegex: (data) => {
    if (data.valueRegex) {
      return {
        validate: [
          {
            pattern: new RegExp(data.valueRegex),
            message: '不符合规则',
            trigger: 'blur',
          },
        ],
      };
    }
    return null;
  },
  // valueRegex: (data)=>{
  //   if(data.valueRegex)
  //     return {validate: { pattern: new RegExp(data.valueRegex), message: '不符合规则', trigger: 'blur' }}
  //   else return null
  // },

  refId: 'refId',
  refValue: 'refValue',
  id: 'id',
};
export default {
  props: {
    data: Object,
  },
  data() {
    return {
      sourceConnectData: {},
      fApi: {},
      loading: false,
      formData: { file: 'adn' },
      options: {
        submitBtn: false,
      },
      originalDefine: [],
      rule: [],
      defaultRule: [
        {
          type: 'input',
          title: this.$t('message.linkis.datasource.sourceName'),
          field: 'dataSourceName',
          value: '',
          props: {
            placeholder: this.$t('message.linkis.datasource.sourceName'),
          },
          validate: [
            {
              required: true,
              message: `${this.$t(
                'message.linkis.datasource.pleaseInput'
              )}${this.$t('message.linkis.datasource.sourceName')}`,
              trigger: 'blur',
            },
            {
              pattern: /^(.){0,200}$/,
              message: '最多200字',
              trigger: 'change',
            },
          ],
        },
        {
          type: 'input',
          title: this.$t('message.linkis.datasource.sourceDec'),
          field: 'dataSourceDesc',
          value: '',
          props: {
            placeholder: this.$t('message.linkis.datasource.sourceDec'),
          },
          validate: [
            {
              pattern: /^(.){0,200}$/,
              message: '最多200字',
              trigger: 'change',
            },
          ],
        },
        {
          type: 'input',
          title: this.$t('message.linkis.datasource.label'),
          field: 'label',
          value: '',
          props: {
            placeholder: this.$t('message.linkis.datasource.label'),
          },
          validate: [
            {
              pattern: /^(.){0,200}$/,
              message: '最多200字',
              trigger: 'change',
            },
          ],
        },
      ],
      onlyReadDefaultRule: [
        {
          type: 'input',
          title: this.$t('message.linkis.datasource.sourceName'),
          field: 'dataSourceName',
          value: '',
          props: {
            placeholder: this.$t('message.linkis.datasource.sourceName'),
          },
          validate: [
            {
              required: true,
              message: `${this.$t(
                'message.linkis.datasource.pleaseInput'
              )}${this.$t('message.linkis.datasource.sourceName')}`,
              trigger: 'blur',
            },
            {
              pattern: /^(.){0,200}$/,
              message: '最多200字',
              trigger: 'change',
            },
          ],
        },
      ],
    };
  },
  watch: {
    data: {
      handler(newV) {
        getKeyDefine(this.data.type).then((data) => {
          this.originalDefine = data.list;
          this.transformData(data.list);
          this.getDataSource(newV);
        });
      },
      deep: true,
    },
  },
  computed: {
    isShowWarnTips() {
      return this.data.id && this.formData.createSystem === 'TDSQL' && this.formData.authType === 'dpm';
    }
  },
  created() {
    this.loading = true;

    this.getDataSource(this.data);

    getKeyDefine(this.data.type).then((data) => {
      this.loading = false;
      this.originalDefine = data.list;
      this.transformData(data.list);
    });
  },
  methods: {
    getDataSource(newV) {
      if (this.data.id) {
        getDataSourceById(newV.id, newV.versionId).then((result) => {
          const mConnect = result.info.connectParams;
          this.sourceConnectData = mConnect;
          delete result.info.connectParams;
          // this.dataSrc = { ...result.info,  ...mConnect};
          this.formData = { ...result.info, ...mConnect };
          if (this.formData.elasticUrls) {
            let str = this.formData.elasticUrls.match(/(?<=\[).*?(?=\])/)[0];
            let arr = (str || '')
              .split(',')
              .map((v) => v.replaceAll(/[\"|\"]/g, ''));
            this.formData.elasticUrls = arr;
          }
        });
      } else {
        const connectParams = newV.connectParams;
        delete newV.connectParams;
        this.formData = {
          ...newV,
          dataSourceDesc: '',
          dataSourceName: '',
          label: '',
          ...connectParams,
        };
        // this.dataSrc = {...newV, ...connectParams};
      }
    },
    transformData(keyDefinitions) {
      const tempData = [];

      keyDefinitions.forEach((obj) => {
        let item = {};
        Object.keys(obj).forEach((keyName) => {
          switch (typeof typesMap[keyName]) {
            case 'object':
              item = merge({}, item, typesMap[keyName]);
              break;

            case 'function':
              item = mergeWith(
                item,
                typesMap[keyName](obj, item, this),
                (objValue, srcValue) => {
                  if (_.isArray(objValue)) {
                    return objValue.concat(srcValue);
                  }
                }
              );
              break;

            case 'string':
              item[typesMap[keyName]] = obj[keyName];
              break;
          }
        });
        tempData.push(item);
      });

      const insertParent = (id, child) => {
        const parent = tempData.find((item) => id == item.id);
        if (parent && child) {
          if (!parent.control || parent.control.length === 0) {
            parent.control = [
              // 不存在新建
              {
                value: child.refValue,
                rule: [{ ...child }],
              },
            ];
          } else {
            const index = parent.control.findIndex(
              (item) => `${item.value}` === `${child.refValue}`
            );
            if (index > -1) {
              parent.control[index].rule.push({ ...child });
            } else {
              parent.control.push({
                value: child.refValue,
                rule: [{ ...child }],
              });
            }
          }
        }
      };
      for (let i = 0; i < tempData.length; i++) {
        const item = tempData[i];
        if (item.refId && item.refId > 0) {
          const children = tempData.splice(i, 1);
          i--;
          insertParent(item.refId, children[0]);
        }
      }
      if (this.data.mode === 'read') {
        this.rule = this.onlyReadDefaultRule.concat(tempData);
      } else {
        this.rule = this.defaultRule.concat(tempData);
      }
      let curItem = tempData.find((v) => v.field === 'elasticUrls');
      if (curItem) {
        Object.assign(curItem, {
          type: 'group',
          props: {
            rule: [{ type: 'input', field: 'field1', class: 'inputStyle' }],
            field: 'field1',
            fontSize: 22,
          },
          validate: [
            {
              required: true,
              type: 'array',
              min: 1,
              message: '最少添加1个Elastic Url',
            },
          ],
        });
      }
      return tempData;
    },
    beforeHandle(_formData) {
      let elasticUrls = _formData?.elasticUrls;
      if (elasticUrls && Array.isArray(elasticUrls)) {
        // es数据源时转换为字符串
        let midStr = elasticUrls
          .filter((v) => !!v)
          .map((n) => `"${n}"`)
          .join(',');
        elasticUrls = `[${midStr}]`;
      }
      return { ..._formData, elasticUrls: elasticUrls };
    },
    submit() {
      this.fApi.submit((formData, fApi) => {
        const _formData = this.beforeHandle(formData);
        this.$emit('submit', JSON.stringify(_formData), this.originalDefine);
      });
    },
    // 测试链接
    handleTestConnect() {
      const _formData = this.beforeHandle(this.formData);
      this.$emit(
        'connect',
        JSON.stringify(toRaw(_formData)),
        this.originalDefine
      );
    },
  },
};
</script>

<style lang="less" scoped>
.search-bar {
  .search-item {
    display: flex;
    justify-content: flex-start;
    align-items: center;
    font-size: 14px;
    .lable {
      min-width: 90px;
      // flex-basis: 130px;
      text-align: right;
    }
  }

  .ivu-col {
    display: flex;
    justify-content: center;
  }
}

.table-content {
  margin-top: 25px;
}

.datasource-type-wrap {
  .project-header {
    height: 32px;

    .header-title {
      font-size: 16px;
      font-weight: bold;
      padding-left: 12px;
      border-left: 3px solid #2d8cf0;
      color: rgba(0, 0, 0, 0.85);
    }

    .header-tool {
      float: right;
      margin-right: 6%;
      color: #2d8cf0;
      cursor: pointer;

      .sort-icon {
        margin-right: 20px;

        .icon {
          margin-left: 5px;
          color: #2d8cf0;
        }
      }

      .search-input {
        width: 200px;
        margin-right: 30px;
      }
    }
  }
}
:deep(.ant-form-item-label) {
  flex: 0 0 20%;
  max-width: 20%;
}

.table-warp {
  :deep(.ant-form) {
    max-height: 660px;
    overflow-y: auto;
    overflow-X: hidden;
    .ant-col-2 {
      display: block;
      flex: 0 0 60px;
      max-width: 60px;
      margin-bottom: 24px;
      span {
        margin: 0 3px;
      }
    }
    .ant-form-item {
      .inputStyle {
        width: 340px;
      }
    }
  }
  .table-bottom-warn-tips {
    color: #faad14;
    padding-left: 110px;
    margin-top: -16px;
    margin-bottom: 10px;
  }
}
</style>

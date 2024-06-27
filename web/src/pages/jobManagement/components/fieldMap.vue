<template>
  <div class="field-map-wrap">
    <!-- left -->
    <!-- <div class="fm-l">
      <div class="main-header">
        <img src="../../../images/jobDetail/u2664.png" />
        <img
          src="../../../images/jobDetail/u2666.png"
          style="
            width: 25px;
            height: 25px;
            position: absolute;
            left: 16px;
            top: 3px;
          "
        />
        <span class="main-header-label" @click="showInfo">字段映射</span>
      </div>
    </div> -->
    <!-- right -->
    <div class="fm-r">
      <!-- <div class="main-header">
        <div>
          <span class="main-header-label">来源字段</span>
        </div>
        <div>
          <span class="main-header-label">目的字段</span>
        </div>
      </div> -->

      <div class="main-header" @click="showInfo">
        <span style="margin-right: 8px; color: rgba(0, 0, 0, 0.45)">
          <RightOutlined v-if="!isFold" />
          <DownOutlined v-else />
        </span>
        <span>
          <span style="display: inline-block;width: 140px;"></span>
          <a-tooltip v-if="engineType === 'SQOOP'" placement="rightTop">
            <template #title>使用sqoop引擎从hive导出到mysql时，均以hcatlog的方式按照字段名称去匹配导出，所以即使改变字段顺序也不会生效。
使用sqoop引擎从mysql导入到hive的时候，导入text格式的hive表，设置overwrite写入方式可以改变字段映射并生效，为append时候会强制转换为hcatlog，hcatlog按照字段名去匹配，改变字段映射无效。</template>
            <QuestionCircleOutlined style="color: #e6a23c;margin-left: 10px;"/> 操作须知
          </a-tooltip>
        </span>
      </div>

      <div class="main-content" v-show="isFold"
        :class="{ 'text-danger': !fieldsSource.length && !fieldsSink.length }">
        <div style="margin-bottom: 15px" v-if="((fieldsSource.length && fieldsSink.length) || selectType) && addEnabled">
          <a-button type="dashed" @click="addTableRow">新增</a-button>
        </div>
        <!-- left -->
        <div style="display: flex">
          <div class="filed-map-wrap-l">
            <div class="filed-map-wrap-l-content">
              <a-table :dataSource="fieldMap.sourceDS" :columns="columns1" size="large"
                :pagination="pagination" v-if="type === 'MAPPING' && fieldMap.sourceDS.length > 0"
                bordered>
                <template #fieldName="{ record }">
                  <a-select :mode="selectType" ref="select" :disabled="!record.fieldEditable" :value="record.fieldName" style="width: 150px"
                    :options="record.fieldOptions" @change="updateSourceFieldName(record, $event)">
                  </a-select>
                </template>
                <template #fieldType="{ record }">
                  <a-input :value="record.fieldType" style="width: 150px" disabled>
                  </a-input>
                </template>
              </a-table>
            </div>
          </div>

          <!-- mid -->
          <div class="field-map-wrap-mid">
            <a-table :dataSource="fieldMap.transformerList" :columns="columns3" size="large"
              :pagination="pagination" v-if="fieldMap.transformerList.length > 0" bordered>
              <template #fieldName="{ record }">
                <div style="
                    position: relative;
                    height: 57px;
                    float: left;
                    width: 200px;
                    margin-top: -15px;
                  ">
                  <Transformer 
                    v-if="engineType === 'DATAX'" 
                    v-bind:tfData="record"
                    :checkOptions="checkOptions"
                    :transformFuncOptions="transformFuncOptions"
                    :transformEnable="transformEnable"
                    @updateTransformer="updateTransformer" />
                  <DeleteOutlined v-if="record.deleteEnable" @click="deleteField(record.key)"
                    style="position: absolute; right: 0; top: 30px; color:#1890ff;" />
                </div>
              </template>
            </a-table>
          </div>

          <!-- right -->
          <div class="field-map-wrap-r">
            <div class="field-map-wrap-r-content">
              <a-table :dataSource="fieldMap.sinkDS" :columns="columns2" size="large"
                :pagination="pagination" v-if="type === 'MAPPING' && fieldMap.sinkDS.length > 0"
                bordered>
                <template #fieldName="{ record }">
                  <a-select :mode="selectType" :disabled="!record.fieldEditable" ref="select" :value="record.fieldName"
                    style="width: 150px" :options="record.fieldOptions"
                    @change="updateSinkFieldName(record, $event)">
                  </a-select>
                </template>
                <template #fieldType="{ record }">
                  <a-input :value="record.fieldType" style="width: 150px" disabled>
                  </a-input>
                </template>
              </a-table>
            </div>
          </div>
        </div>

        <a-pagination
          style="margin-top: 15px;"
          v-if="type === 'MAPPING' && fieldMap.sourceDS.length > 0"
          v-model:current="pagination.current" 
          v-model:page-size="pagination.pageSize"
          :page-size-options="pagination.pageSizeOptions" 
          :total="fieldMap.sourceDS.length"
          :show-total="total => `总共 ${fieldMap.sourceDS.length}条`" 
          show-size-changer
          @change="changeEvents" />
      </div>
    </div>
  </div>
</template>

<script>
import {
  defineComponent,
  ref,
  reactive,
  toRaw,
  watch,
  computed,
  defineAsyncComponent,
  onMounted
} from 'vue';
import { cloneDeep } from 'lodash-es';
import {
  DeleteOutlined,
  RightOutlined,
  DownOutlined,
  QuestionCircleOutlined
} from '@ant-design/icons-vue';
import { getFieldFunc } from '@/common/service';

export default defineComponent({
  props: {
    srcTableNotExist: Boolean,
    sinkTableNotExist: Boolean,
    fmData: Object,
    fieldsSource: Array,
    fieldsSink: Array,
    deductions: Array,
    addEnabled: Boolean,
    transformEnable: Boolean,
    engineType: String,
  },
  emits: ['updateFieldMap'],
  components: {
    Transformer: defineAsyncComponent(() => import('./transformer.vue')),
    DeleteOutlined,
    RightOutlined,
    DownOutlined,
    QuestionCircleOutlined
  },
  setup(props, context) {
    const { type } = props.fmData;

    // 分页操作
    const pagination = reactive({
      current: 1,
      pageSize: 10,
      pageSizeOptions: ['10', '20', '30', '40'],
    });

    const changeEvents = (page, pageSize) => {
      pagination.current = page;
      pagination.pageSize = pageSize;
    };

    let fieldMap = reactive({
      sourceDS: [],
      sinkDS: [],
      transformerList: [],
    });

    const srcTableNotExist = computed(() => props.srcTableNotExist);
    const sinkTableNotExist = computed(() => props.sinkTableNotExist);
    const selectType = computed(() => srcTableNotExist.value || sinkTableNotExist.value ? 'SECRET_COMBOBOX_MODE_DO_NOT_USE' : '');
    const newProps = computed(() => JSON.parse(JSON.stringify(props.fmData)));
    watch(newProps, (val, oldVal) => {
      const newVal = typeof val === 'string' ? JSON.parse(val) : val;
      isFold.value = !!toRaw(newVal).mapping.length;
      createDataSource(toRaw(newVal).mapping || []);
    });

    const deductionsArray = computed(() =>
      JSON.parse(JSON.stringify(props.deductions))
    );
    watch(deductionsArray, (val, oldVal) => {
      if (val) isFold.value = !!toRaw(props.fmData.mapping).length;
      createDataSource(toRaw(props.fmData.mapping) || []);
    });

    const createFieldOptions = (fieldInfo) => {
      const fieldOptions = [];
      if (fieldInfo) {
        const fieldList = toRaw(fieldInfo);
        fieldList.forEach((info) => {
          const o = {};
          o.value = info.name;
          o.label = info.name;
          o.type = info.type;
          o.fieldIndex = info.fieldIndex;
          fieldOptions.push(o);
        });
      }
      return fieldOptions;
    };

    const createTransforms = (sourceDS, sinkDS, transformerList) => {
      const mapping = [];
      sourceDS.forEach((source) => {
        let o = {};
        const { key } = source;
        sinkDS.forEach((sink) => {
          if (sink.key == key) {
            o.sink_field_name = sink.fieldName;
            o.sink_field_type = sink.fieldType;
            o.sink_field_index = sink.fieldIndex;
            o.sink_field_editable = sink.fieldEditable;
          }
        });
        transformerList.forEach((tf) => {
          if (tf.key == key) {
            o.validator = tf.validator;
            o.transformer = tf.transformer;
            o.deleteEnable = tf.deleteEnable;
          }
        });
        o.source_field_name = source.fieldName;
        o.source_field_type = source.fieldType;
        o.source_field_index = source.fieldIndex;
        o.source_field_editable = source.fieldEditable;
        mapping.push(o);
      });
      const { type, sql } = props.fmData;
      return {
        type,
        mapping,
        sql,
      };
    };

    // crate dataSource
    const createDataSource = (map) => {
      fieldMap.sourceDS = [];
      fieldMap.sinkDS = [];
      fieldMap.transformerList = [];
      if (typeof map !== 'object') {
        return;
      }

      /*if (!map.length) {
        toRaw(props.deductions).forEach((item, idx) => {
          let sourceItem = {};
          let sinkItem = {};
          let transformerItem = {};

          sourceItem.key = idx + "";
          sourceItem.fieldName = item.source.name;
          sourceItem.fieldOptions = createFieldOptions(props.fieldsSource);
          sourceItem.fieldType = item.source.type;

          sinkItem.key = idx + "";
          sinkItem.fieldName = item.sink.name;
          sinkItem.fieldOptions = createFieldOptions(props.fieldsSink);
          sinkItem.fieldType = item.sink.type;

          transformerItem.key = idx + "";
          transformerItem.validator = [];
          transformerItem.transformer = {};
          transformerItem.deleteEnable = item.deleteEnable;

          fieldMap.transformerList.push(transformerItem);
          fieldMap.sourceDS.push(sourceItem);
          fieldMap.sinkDS.push(sinkItem);
        });
      } else { */
      map.forEach((item, idx) => {
        let sourceItem = {};
        let sinkItem = {};
        let transformerItem = {};

        if ((item.source_field_name && item.source_field_type) || (item.sink_field_name && item.sink_field_type)) {
          sourceItem.key = idx + '';
          sourceItem.fieldName =
            item.source_field_name && item.source_field_name;
          sourceItem.fieldOptions = createFieldOptions(props.fieldsSource);
          sourceItem.fieldType =
            item.source_field_type && item.source_field_type;
          sourceItem.fieldIndex = item.source_field_index;
          sourceItem.fieldEditable = item.source_field_editable;

          sinkItem.key = idx + '';
          sinkItem.fieldName = item.sink_field_name && item.sink_field_name;
          sinkItem.fieldOptions = createFieldOptions(props.fieldsSink);
          sinkItem.fieldType = item.sink_field_type && item.sink_field_type;
          sinkItem.fieldIndex = item.sink_field_index;
          sinkItem.fieldEditable = item.sink_field_editable;

          transformerItem.key = idx + '';
          transformerItem.validator = item.validator && item.validator;
          transformerItem.transformer = item.transformer && item.transformer;
          transformerItem.deleteEnable = item.deleteEnable;
 
          fieldMap.transformerList.push(transformerItem);
          fieldMap.sourceDS.push(sourceItem);
          fieldMap.sinkDS.push(sinkItem);
        } else if (item.source && item.source.name && item.source.type) {
          let sourceItem = {};
          let sinkItem = {};
          let transformerItem = {};

          sourceItem.key = idx + '';
          sourceItem.fieldName = item.source.name;
          sourceItem.fieldOptions = createFieldOptions(props.fieldsSource);
          sourceItem.fieldType = item.source.type;
          sourceItem.fieldIndex = item.source.fieldIndex;
          sourceItem.fieldEditable = item.source.fieldEditable;

          sinkItem.key = idx + '';
          sinkItem.fieldName = item.sink.name;
          sinkItem.fieldOptions = createFieldOptions(props.fieldsSink);
          sinkItem.fieldType = item.sink.type;
          sinkItem.fieldIndex = item.sink.fieldIndex;
          sinkItem.fieldEditable = item.sink.fieldEditable;

          transformerItem.key = idx + '';
          transformerItem.validator = [];
          transformerItem.transformer = {};
          transformerItem.deleteEnable = item.deleteEnable;

          fieldMap.transformerList.push(transformerItem);
          fieldMap.sourceDS.push(sourceItem);
          fieldMap.sinkDS.push(sinkItem);
        }
      });
      //}
    };
    createDataSource(toRaw(props.fmData).mapping || []);

    console.log('sourceDS', fieldMap);

    const updateTransformer = (res) => {
      const _transformerList = fieldMap.transformerList.filter(
        (item) => item.key != res.key
      );
      _transformerList.push(res);
      fieldMap.transformerList = _transformerList;

      const transforms = createTransforms(
        fieldMap.sourceDS,
        fieldMap.sinkDS,
        fieldMap.transformerList
      );
      context.emit('updateFieldMap', transforms);
    };

    const defaultFieldValue = computed(() => srcTableNotExist.value || sinkTableNotExist.value ? '[Auto]' : '');

    const updateSourceFieldName = (info, e) => {
      const { key, fieldOptions } = info;
      fieldMap.sourceDS.forEach((item) => {
        if (item.key == key) {
          item.fieldName = e;
          item.fieldType = defaultFieldValue.value;
          delete item.fieldIndex;
          fieldOptions.forEach((field) => {
            if (field.value === item.fieldName) {
              item.fieldType = field.type;
              item.fieldIndex = field.fieldIndex;
            }
          });
          return;
        }
      });

      const transforms = createTransforms(
        fieldMap.sourceDS,
        fieldMap.sinkDS,
        fieldMap.transformerList
      );
      context.emit('updateFieldMap', transforms);
    };

    const updateSinkFieldName = (info, e) => {
      const { key, fieldOptions } = info;
      fieldMap.sinkDS.forEach((item) => {
        if (item.key == key) {
          item.fieldName = e;
          item.fieldType = defaultFieldValue.value;
          delete item.fieldIndex;
          fieldOptions.forEach((field) => {
            if (field.value === item.fieldName) {
              item.fieldType = field.type;
              item.fieldIndex = field.fieldIndex;
            }
          });
          return;
        }
      });

      const transforms = createTransforms(
        fieldMap.sourceDS,
        fieldMap.sinkDS,
        fieldMap.transformerList
      );
      context.emit('updateFieldMap', transforms);
    };

    const addTableRow = () => {
      let sourceLen = fieldMap.sourceDS.length;
      let sinkLen = fieldMap.sinkDS.length;
      let tfLen = fieldMap.transformerList.length;

      let sourceItem = {};
      let sinkItem = {};
      let transformerItem = {};

      sourceItem.key = sourceLen + '';
      sourceItem.fieldName = '';
      sourceItem.fieldOptions = createFieldOptions(props.fieldsSource);
      sourceItem.fieldType = defaultFieldValue.value;
      sourceItem.fieldEditable = true;

      sinkItem.key = sinkLen + '';
      sinkItem.fieldName = '';
      sinkItem.fieldOptions = createFieldOptions(props.fieldsSink);
      sinkItem.fieldType = defaultFieldValue.value;
      sinkItem.fieldEditable = true;

      transformerItem.key = tfLen + '';
      transformerItem.validator = [];
      transformerItem.transformer = {};
      transformerItem.deleteEnable = true;

      fieldMap.transformerList.push(transformerItem);
      fieldMap.sourceDS.push(sourceItem);
      fieldMap.sinkDS.push(sinkItem);
    };
    const deleteField = (index) => {
      fieldMap.transformerList.splice(+index, 1);
      fieldMap.sourceDS.splice(+index, 1);
      fieldMap.sinkDS.splice(+index, 1);

      const transforms = createTransforms(
        fieldMap.sourceDS,
        fieldMap.sinkDS,
        fieldMap.transformerList
      );
      context.emit('updateFieldMap', transforms);
    };
    let isFold = ref(false);
    const showInfo = () => {
      isFold.value = !isFold.value;
    };

    // 检验函数和转换函数的下拉选项
    const checkOptions = ref([]);
    const transformFuncOptions = ref([]);

    onMounted(() => {
      isFold.value = !!(toRaw(props.fmData.mapping) || []).length;
      // 获取检验列表
      getFieldFunc('verify').then((res) => {
        checkOptions.value = (res?.data || []).map((v) => ({
          label: v.funcName,
          value: v.funcName,
          ...v,
        }));
      });

      // 获取转换函数
      getFieldFunc('transform').then((res) => {
        transformFuncOptions.value = (res?.data || []).map((v) => ({
          label: v.funcName,
          value: v.funcName,
          ...v,
        }));
        transformFuncOptions.value.unshift({
          value: "",
          label: "--",
          paramNames: []
        })
      });
    });

    return {
      type,
      fieldMap,
      columns1: [
        {
          title: '来源字段',
          children: [
            {
              title: '字段名',
              dataIndex: 'fieldName',
              key: 'fieldName',
              slots: {
                customRender: 'fieldName',
              },
            },
            {
              title: '类型',
              dataIndex: 'fieldType',
              key: 'fieldType',
              slots: {
                customRender: 'fieldType',
              },
            },
          ],
        },
      ],
      columns2: [
        {
          title: '目的字段',
          children: [
            {
              title: '字段名',
              dataIndex: 'fieldName',
              key: 'fieldName',
              slots: {
                customRender: 'fieldName',
              },
            },
            {
              title: '类型',
              dataIndex: 'fieldType',
              key: 'fieldType',
              slots: {
                customRender: 'fieldType',
              },
            },
          ],
        },
      ],
      columns3: [
        {
          title: '',
          children: [
            {
              title: '',
              dataIndex: 'fieldName',
              key: 'fieldName',
              slots: {
                customRender: 'fieldName',
              },
            },
          ],
        },
      ],
      updateTransformer,
      updateSourceFieldName,
      updateSinkFieldName,
      addTableRow,
      isFold,
      showInfo,
      deleteField,
      pagination,
      changeEvents,
      checkOptions,
      transformFuncOptions,
      selectType
    };
  },
});
</script>

<style lang="less" scoped>
@import '../../../common/content.less';
.field-map-wrap {
  display: flex;
  border-bottom: 1px solid #dee4ec;
  border-top: 1px solid #dee4ec;
  min-height: 68px;
  padding: 24px;
  box-sizing: border-box;
}
.fm-l {
  width: 122px;
  .main-header {
    height: 20px;
    font-family: PingFangSC-Medium;
    font-size: 14px;
    color: rgba(0, 0, 0, 0.85);
    font-weight: 500;
  }
}
.fm-r {
  flex: 1;
  display: flex;
  background: inherit;
  background-color: rgba(255, 255, 255, 1);
  box-sizing: border-box;
  border-top: none;
  flex-direction: column;
  .main-header {
    height: 32px;
    font-family: PingFangSC-Medium;
    font-size: 14px;
    color: rgba(0, 0, 0, 0.85);
    font-weight: 500;
    margin-bottom: 10px;
  }
  .main-content {
    padding: 24px;
    min-width: 950px;
    max-width: 950px;
    display: flex;
    flex-direction: column;
  }
  .text-danger {
    padding: 0px;
    border: none;
  }
}
.field-map-wrap-l {
  flex: 1;
  min-width: 332px;
  .fm-wrap-l-c-item {
    > div {
      display: inline;
    }
  }
}
.field-map-wrap-r {
  flex: 1;
}
.field-map-label {
  font-size: 14px;
  text-align: left;
}

.filed-map-wrap-l,
.field-map-wrap-r {
  :deep(.ant-table-pagination) {
    display: none;
  }
  :deep(.ant-table-thead) {
    tr {
      th {
        text-align: center;
        background-color: #f8fafd;
      }
    }
  }
  :deep(.ant-table-tbody) {
    tr {
      td {
        padding: 21px 10px;
      }
    }
  }
}
.field-map-wrap-mid {
  width: 248px;
  display: flex;
  justify-content: center;
  position: relative;
  margin-top: 86px;
  :deep(.ant-table-thead) {
    display: none;
  }
  :deep(.ant-table-tbody) {
    tr {
      td {
        padding: 16px 10px;
      }
    }
  }
  :deep(.ant-table-pagination) {
    display: none;
  }
}
</style>

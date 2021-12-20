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
          <RightOutlined v-if="!isFold"/>
          <DownOutlined v-else/>
        </span>
        <span>字段映射</span>
      </div>

      <div
        class="main-content"
        v-show="isFold"
        :class="{ 'text-danger': !fieldsSource.length && !fieldsSink.length }"
      >
        <div
          style="margin-bottom: 15px"
          v-if="fieldsSource.length && fieldsSink.length && addEnabled"
        >
          <a-button type="dashed" @click="addTableRow">新增</a-button>
        </div>
        <!-- left -->
        <div style="display: flex">
          <div class="filed-map-wrap-l">
            <div class="filed-map-wrap-l-content">
              <a-table
                :dataSource="fieldMap.sourceDS"
                :columns="columns1"
                size="large"
                :pagination="false"
                v-if="type === 'MAPPING' && fieldMap.sourceDS.length > 0"
                bordered
              >
                <template #fieldName="{ record }">
                  <a-select
                    ref="select"
                    :value="record.fieldName"
                    style="width: 150px"
                    :options="record.fieldOptions"
                    @change="updateSourceFieldName(record, $event)"
                  >
                  </a-select>
                </template>
                <template #fieldType="{ record }">
                  <a-input
                    :value="record.fieldType"
                    style="width: 150px"
                    disabled
                  >
                  </a-input>
                </template>
              </a-table>
            </div>
          </div>

          <!-- mid -->
          <div class="field-map-wrap-mid">
            <div>
              <template
                v-for="(item, index) in fieldMap.transformerList"
                :key="item.key"
              >
                <div
                  style="
                    position: relative;
                    height: 66px;
                    float: left;
                    width: 200px;
                  "
                >
                  <Transformer
                    v-if="engineType === 'DATAX'"
                    v-bind:tfData="item"
                    @updateTransformer="updateTransformer"
                  />
                  <DeleteOutlined
                    v-if="item.deleteEnable"
                    @click="deleteField(index)"
                    style="position: absolute; right: 0; top: 23px; color:#1890ff;"
                  />
                </div>
              </template>
            </div>
          </div>

          <!-- right -->
          <div class="field-map-wrap-r">
            <div class="field-map-wrap-r-content">
              <a-table
                :dataSource="fieldMap.sinkDS"
                :columns="columns2"
                size="large"
                :pagination="false"
                v-if="type === 'MAPPING' && fieldMap.sinkDS.length > 0"
                bordered
              >
                <template #fieldName="{ record }">
                  <a-select
                    ref="select"
                    :value="record.fieldName"
                    style="width: 150px"
                    :options="record.fieldOptions"
                    @change="updateSinkFieldName(record, $event)"
                  >
                  </a-select>
                </template>
                <template #fieldType="{ record }">
                  <a-input
                    :value="record.fieldType"
                    style="width: 150px"
                    disabled
                  >
                  </a-input>
                </template>
              </a-table>
            </div>
          </div>
        </div>
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
} from "vue";
import { cloneDeep } from "lodash-es";
import { DeleteOutlined, RightOutlined,DownOutlined } from "@ant-design/icons-vue";

export default defineComponent({
  props: {
    fmData: Object,
    fieldsSource: Array,
    fieldsSink: Array,
    deductions: Array,
    addEnabled: Boolean,
    engineType: String,
  },
  emits: ["updateFieldMap"],
  components: {
    Transformer: defineAsyncComponent(() => import("./transformer.vue")),
    DeleteOutlined,
    RightOutlined,
    DownOutlined
  },
  setup(props, context) {
    const { type } = props.fmData;

    let fieldMap = reactive({
      sourceDS: [],
      sinkDS: [],
      transformerList: [],
    });

    const newProps = computed(() => JSON.parse(JSON.stringify(props.fmData)));
    watch(newProps, (val, oldVal) => {
      const newVal = typeof val === "string" ? JSON.parse(val) : val;
      createDataSource(toRaw(newVal).mapping || []);
    });

    const deductionsArray = computed(() =>
      JSON.parse(JSON.stringify(props.deductions))
    );
    watch(deductionsArray, (val, oldVal) => {
      if (val && val.length)
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
      if (typeof map !== "object") {
        return;
      }

      if (!map.length) {
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
        // const transforms = createTransforms(
        //   fieldMap.sourceDS,
        //   fieldMap.sinkDS,
        //   fieldMap.transformerList
        // );
        // context.emit("updateFieldMap", transforms);
      } else {
        map.forEach((item, idx) => {
          let sourceItem = {};
          let sinkItem = {};
          let transformerItem = {};

          sourceItem.key = idx + "";
          sourceItem.fieldName =
            item.source_field_name && item.source_field_name;
          sourceItem.fieldOptions = createFieldOptions(props.fieldsSource);
          sourceItem.fieldType =
            item.source_field_type && item.source_field_type;

          sinkItem.key = idx + "";
          sinkItem.fieldName = item.sink_field_name && item.sink_field_name;
          sinkItem.fieldOptions = createFieldOptions(props.fieldsSink);
          sinkItem.fieldType = item.sink_field_type && item.sink_field_type;

          transformerItem.key = idx + "";
          transformerItem.validator = item.validator && item.validator;
          transformerItem.transformer = item.transformer && item.transformer;
          transformerItem.deleteEnable = item.deleteEnable;

          fieldMap.transformerList.push(transformerItem);
          fieldMap.sourceDS.push(sourceItem);
          fieldMap.sinkDS.push(sinkItem);
        });
      }
    };
    createDataSource(toRaw(props.fmData).mapping || []);

    console.log("sourceDS", fieldMap);

    const updateTransformer = (res) => {
      console.log("field map update", res);
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
      context.emit("updateFieldMap", transforms);
    };

    const updateSourceFieldName = (info, e) => {
      const { key, fieldOptions } = info;
      fieldMap.sourceDS.forEach((item) => {
        if (item.key == key) {
          item.fieldName = e;
          fieldOptions.forEach((field) => {
            if (field.value === item.fieldName) {
              item.fieldType = field.type;
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
      context.emit("updateFieldMap", transforms);
    };

    const updateSinkFieldName = (info, e) => {
      const { key, fieldOptions } = info;
      fieldMap.sinkDS.forEach((item) => {
        if (item.key == key) {
          item.fieldName = e;
          fieldOptions.forEach((field) => {
            if (field.value === item.fieldName) {
              item.fieldType = field.type;
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
      context.emit("updateFieldMap", transforms);
    };

    const addTableRow = () => {
      let sourceLen = fieldMap.sourceDS.length;
      let sinkLen = fieldMap.sinkDS.length;
      let tfLen = fieldMap.transformerList.length;

      let sourceItem = {};
      let sinkItem = {};
      let transformerItem = {};

      sourceItem.key = sourceLen + "";
      sourceItem.fieldName = "";
      sourceItem.fieldOptions = createFieldOptions(props.fieldsSource);
      sourceItem.fieldType = "";

      sinkItem.key = sinkLen + "";
      sinkItem.fieldName = "";
      sinkItem.fieldOptions = createFieldOptions(props.fieldsSink);
      sinkItem.fieldType = "";

      transformerItem.key = tfLen + "";
      transformerItem.validator = [];
      transformerItem.transformer = {};
      transformerItem.deleteEnable = true;

      fieldMap.transformerList.push(transformerItem);
      fieldMap.sourceDS.push(sourceItem);
      fieldMap.sinkDS.push(sinkItem);
    };
    const deleteField = (index) => {
      fieldMap.transformerList.splice(index, 1);
      fieldMap.sourceDS.splice(index, 1);
      fieldMap.sinkDS.splice(index, 1);

      const transforms = createTransforms(
        fieldMap.sourceDS,
        fieldMap.sinkDS,
        fieldMap.transformerList
      );
      context.emit("updateFieldMap", transforms);
    };
    let isFold = ref(true);
    const showInfo = () => {
      isFold.value = !isFold.value;
    };

    return {
      type,
      fieldMap,
      columns1: [{
        title: "来源字段",
        children: [
          {
            title: "字段名",
            dataIndex: "fieldName",
            key: "fieldName",
            slots: {
              customRender: "fieldName",
            },
          },
          {
            title: "类型",
            dataIndex: "fieldType",
            key: "fieldType",
            slots: {
              customRender: "fieldType",
            },
          },
        ]
      }],
      columns2: [{
        title: "目的字段",
        children: [
          {
            title: "字段名",
            dataIndex: "fieldName",
            key: "fieldName",
            slots: {
              customRender: "fieldName",
            },
          },
          {
            title: "类型",
            dataIndex: "fieldType",
            key: "fieldType",
            slots: {
              customRender: "fieldType",
            },
          },
        ]
      }],
      updateTransformer,
      updateSourceFieldName,
      updateSinkFieldName,
      addTableRow,
      isFold,
      showInfo,
      deleteField,
    };
  },
});
</script>

<style lang="less" scoped>
@import "../../../common/content.less";
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
    height: 20px;
    font-family: PingFangSC-Medium;
    font-size: 14px;
    color: rgba(0, 0, 0, 0.85);
    font-weight: 500;
  }
  .main-content {
    padding: 24px;
    min-width: 950px;
    max-width: 950px;
    display: flex;
    flex-direction: column;
    :deep(.ant-table-thead) {
      tr {
        th {
          text-align: center;
          background-color: #F8FAFD;
        }
      }
    }
    :deep(.ant-table-tbody) {
      tr {
        td {
          padding: 16px 10px;
        }
      }
    }
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
.field-map-wrap-mid {
  width: 248px;
  display: flex;
  justify-content: center;
  position: relative;
  margin-top: 86px;
}
.field-map-label {
  font-size: 14px;
  text-align: left;
}
</style>

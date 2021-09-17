<template>
  <div class="field-map-wrap">
    <!-- left -->
    <div class="fm-l">
      <div class="main-header">
        <span class="main-header-label">字段映射</span>
      </div>
    </div>
    <!-- right -->
    <div class="fm-r">
      <div class="main-header">
        <div>
          <span class="main-header-label">来源字段</span>
        </div>
        <div>
          <span class="main-header-label">目的字段</span>
        </div>
      </div>

      <div class="main-content">
        <div style="margin-bottom: 15px">
          <a-button type="dashed" @click="addTableRow">新增</a-button>
        </div>
        <!-- left -->
        <div style="display: flex">
          <div class="filed-map-wrap-l">
            <div class="filed-map-wrap-l-content">
              <a-table
                :dataSource="sourceDS"
                :columns="columns"
                size="small"
                :pagination="false"
                v-if="type === 'MAPPING'"
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
                  <a-select
                    ref="select"
                    :value="record.fieldType"
                    style="width: 150px"
                    :options="record.typeOptions"
                    @change="updateSourceTypeName(record, $event)"
                  >
                  </a-select>
                </template>
              </a-table>
            </div>
          </div>

          <!-- mid -->
          <div class="field-map-wrap-mid">
            <template v-for="item in transformerList" :key="item.key">
              <Transformer
                v-bind:tfData="item"
                v-bind:id="item.key"
                @updateTransformer="updateTransformer"
              />
            </template>
          </div>

          <!-- right -->
          <div class="field-map-wrap-r">
            <div class="field-map-wrap-r-content">
              <a-table
                :dataSource="sinkDS"
                :columns="columns"
                size="small"
                :pagination="false"
                v-if="type === 'MAPPING'"
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
                  <a-select
                    ref="select"
                    :value="record.fieldType"
                    style="width: 150px"
                    :options="record.typeOptions"
                    @change="updateSinkTypeName(record, $event)"
                  >
                  </a-select>
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
import { defineComponent, ref, reactive, toRaw } from "vue";
import { cloneDeep } from "lodash-es";
import Transformer from "./transformer.vue";
import { fieldInfo } from "../mock";
export default defineComponent({
  props: {
    fmData: Object,
  },
  emits: ["updateFieldMap"],
  components: {
    Transformer,
  },
  setup(props, context) {
    const { type } = props.fmData;
    const typeOptions = [
      {
        value: "varchar",
        label: "varchar",
      },
      {
        value: "text",
        label: "text",
      },
      {
        value: "int",
        label: "int",
      },
      {
        value: "char",
        label: "char",
      },
      {
        value: "float",
        label: "float",
      },
      {
        value: "double",
        label: "double",
      },
    ];
    const createFieldOptions = (fieldInfo) => {
      const fieldOptions = [];
      fieldInfo.forEach((info) => {
        const o = Object.create(null);
        o.value = info.name;
        o.label = info.name;
        fieldOptions.push(o);
      });
      return fieldOptions;
    };
    const fieldOptions = createFieldOptions(fieldInfo);

    // crate dataSource
    const createDataSource = (map, fieldOptions, typeOptions) => {
      if (typeof map !== "object") return {};
      const sourceDS = [],
        sinkDS = [],
        transformerList = [];
      map.forEach((item, idx) => {
        let sourceItem = Object.create(null);
        let sinkItem = Object.create(null);
        let transformerItem = Object.create(null);

        sourceItem.key = idx + "";
        sourceItem.fieldName = item.source_field_name && item.source_field_name;
        sourceItem.fieldOptions = fieldOptions;
        sourceItem.fieldType = item.source_field_type && item.source_field_type;
        sourceItem.typeOptions = typeOptions;

        sinkItem.key = idx + "";
        sinkItem.fieldName = item.sink_field_name && item.sink_field_name;
        sinkItem.fieldOptions = fieldOptions;
        sinkItem.fieldType = item.sink_field_type && item.sink_field_type;
        sinkItem.typeOptions = typeOptions;

        transformerItem.key = idx + "";
        transformerItem.validator = item.validator && item.validator;
        transformerItem.transformer = item.transformer && item.transformer;

        transformerList.push(transformerItem);
        sourceDS.push(sourceItem);
        sinkDS.push(sinkItem);
      });
      return {
        sourceDS,
        sinkDS,
        transformerList,
      };
    };

    let { sourceDS, sinkDS, transformerList } = createDataSource(
      toRaw(props.fmData).mapping,
      fieldOptions,
      typeOptions
    );

    sourceDS = ref(sourceDS);
    sinkDS = ref(sinkDS);
    transformerList = ref(transformerList);

    console.log("sourceDS", sourceDS);
    console.log("sinkDS", sinkDS);
    console.log("transformerList", transformerList);

    const createTransforms = (sourceDS, sinkDS, transformerList) => {
      const mapping = [];
      sourceDS.value.forEach((source) => {
        let o = Object.create(null);
        const { key } = source;
        sinkDS.value.forEach((sink) => {
          if (sink.key == key) {
            o.sink_field_name = sink.fieldName;
            o.sink_field_type = sink.fieldType;
          }
        });
        transformerList.value.forEach((tf) => {
          if (tf.key == key) {
            o.validator = tf.validator;
            o.transformer = tf.transformer;
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

    const updateTransformer = (res) => {
      const _transformerList = transformerList.value.filter(
        (item) => item.key != res.key
      );
      _transformerList.push(res);
      transformerList = _transformerList;

      const transforms = createTransforms(sourceDS, sinkDS, transformerList);
      context.emit("updateFieldMap", transforms);
    };

    const updateSourceFieldName = (info, e) => {
      const { key } = info;
      sourceDS.value.forEach((item) => {
        if (item.key == key) {
          return (item.fieldName = e);
        }
      });

      const transforms = createTransforms(sourceDS, sinkDS, transformerList);
      context.emit("updateFieldMap", transforms);
    };

    const updateSourceTypeName = (info, e) => {
      const { key } = info;
      sourceDS.value.forEach((item) => {
        if (item.key == key) {
          return (item.fieldType = e);
        }
      });

      const transforms = createTransforms(sourceDS, sinkDS, transformerList);
      context.emit("updateFieldMap", transforms);
    };

    const updateSinkFieldName = (info, e) => {
      const { key } = info;
      sinkDS.value.forEach((item) => {
        if (item.key == key) {
          return (item.fieldName = e);
        }
      });

      const transforms = createTransforms(sourceDS, sinkDS, transformerList);
      context.emit("updateFieldMap", transforms);
    };

    const updateSinkTypeName = (info, e) => {
      const { key } = info;
      sinkDS.value.forEach((item) => {
        if (item.key == key) {
          return (item.fieldType = e);
        }
      });

      const transforms = createTransforms(sourceDS, sinkDS, transformerList);
      context.emit("updateFieldMap", transforms);
    };

    const addTableRow = () => {
      let sourceLen = sourceDS.length;
      let sinkLen = sinkDS.length;
      let tfLen = transformerList.length;

      let sourceItem = Object.create(null);
      let sinkItem = Object.create(null);
      let transformerItem = Object.create(null);

      sourceItem.key = sourceLen + "";
      sourceItem.fieldName = "";
      sourceItem.fieldOptions = fieldOptions;
      sourceItem.fieldType = "";
      sourceItem.typeOptions = typeOptions;

      sinkItem.key = sinkLen + "";
      sinkItem.fieldName = "";
      sinkItem.fieldOptions = fieldOptions;
      sinkItem.fieldType = "";
      sinkItem.typeOptions = typeOptions;

      transformerItem.key = tfLen + "";
      transformerItem.validator = [];
      transformerItem.transformer = {};

      transformerList.value.push(transformerItem);
      sourceDS.value.push(sourceItem);
      sinkDS.value.push(sinkItem);
    };

    return {
      type,
      sourceDS,
      sinkDS,
      transformerList,
      columns: [
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
      ],
      updateTransformer,
      updateSourceFieldName,
      updateSourceTypeName,
      updateSinkFieldName,
      updateSinkTypeName,
      addTableRow,
    };
  },
  watch: {
    fmData: {
      handler: function (newVal) {
        console.log("watch props");
        this.props = newVal;
      },
      deep: true,
    },
  },
});
</script>

<style lang="less" scoped>
.field-map-wrap {
  margin-top: 30px;
  width: 1100px;
  display: flex;
}
.fm-l {
  width: 122px;
  .main-header {
    height: 33px;
    background: inherit;
    background-color: rgba(102, 102, 255, 1);
    border: none;
    display: flex;
    border-top-left-radius: 16px;
    border-bottom-left-radius: 16px;
    :nth-of-type(1) {
      width: 100%;
      text-align: center;
      line-height: 33px;
      font-size: 16px;
    }
    .main-header-label {
      font-family: "Arial Negreta", "Arial Normal", "Arial";
      font-weight: 700;
      font-style: normal;
      color: #ffffff;
    }
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
    height: 33px;
    background: inherit;
    background-color: rgba(107, 107, 107, 1);
    border: none;
    display: flex;
    > div {
      flex: 1;
      text-align: center;
      line-height: 33px;
    }
    .main-header-label {
      font-family: "Arial Negreta", "Arial Normal", "Arial";
      font-weight: 700;
      font-style: normal;
      color: #ffffff;
    }
  }
  .main-content {
    border: 1px solid rgba(102, 102, 255, 1);
    border-top: none;
    padding: 15px 30px;
    display: flex;
    flex-direction: column;
  }
}
.field-map-wrap-l {
  flex: 1;
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
  flex-direction: column;
  align-items: center;
}
.feld-map-label {
  font-size: 14px;
  text-align: left;
}
</style>

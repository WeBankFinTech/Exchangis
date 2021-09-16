<template>
  <div class="field-map-wrap">
    <!-- left -->
    <div class="fm-l">
      <span>字段映射</span>
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
        <!-- left -->
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
                >
                </a-select>
              </template>
              <template #fieldType="{ record }">
                <a-select
                  ref="select"
                  :value="record.fieldType"
                  style="width: 150px"
                  :options="record.typeOptions"
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
                >
                </a-select>
              </template>
              <template #fieldType="{ record }">
                <a-select
                  ref="select"
                  :value="record.fieldType"
                  style="width: 150px"
                  :options="record.typeOptions"
                >
                </a-select>
              </template>
            </a-table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, reactive, toRaw } from "vue";
import Transformer from "./transformer.vue";
export default defineComponent({
  props: {
    fmData: Object,
  },
  components: {
    Transformer,
  },
  setup(props) {
    const { type } = props.fmData;
    console.log("fmData", toRaw(props.fmData));
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
    const fieldOptions = [
      {
        value: "field_1",
        label: "field_1",
      },
      {
        value: "field_2",
        label: "field_2",
      },
      {
        value: "field_3",
        label: "field_3",
      },
    ];

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
        updateTransformer,
      };
    };

    const { sourceDS, sinkDS, transformerList } = createDataSource(
      toRaw(props.fmData).mapping,
      fieldOptions,
      typeOptions
    );

    console.log("sourceDS", sourceDS);
    console.log("sinkDS", sinkDS);
    console.log("transformerList", transformerList);

    const updateTransformer = (res) => {
      console.log("res", res);
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
    };
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
    padding: 25px 30px;
    display: flex;
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

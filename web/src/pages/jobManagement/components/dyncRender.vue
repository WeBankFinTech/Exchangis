<template>
  <div>
    <!-- 自定义库表存在分区时 -->
    <template v-if="tableNotExist && itemKey === 'partition'">
      <NoTablePartition  
        v-model:modelValue="value"
        @updateValue="emitData(value)"
      />
    </template>
    <template v-else>
      <a-select
        v-if="type === 'OPTION'"
        v-model:value="value"
        ref="select"
        :options="checkOptions"
        @change="emitData(value)"
        :style="style"
        :placeholder="description"
      >
      </a-select>
      <template v-if="type === 'MAP'">
        <!-- 源数据类型为HIVE，且数据分区返回为空 -->
        <div style="margin-bottom: 5px" v-if="(sourceType ==='HIVE') && !partitionArr.length">
          <a-input style="width: 400px;" disabled />
        </div>
        <div style="margin-bottom: 5px" v-for="item in partitionArr">
          <a-input
            style="width: 30%"
            disabled
            v-model:value = item.label
          />
          <a-input
            v-if="item.type === 'INPUT'"
            v-model:value="item.value"
            @change="handleChange(item.value, item)"
            style="margin-left: 5px;width: 60%"
          />
          <a-select
            v-if="item.type === 'OPTION'"
            mode="tags"
            v-model:value="item.value"
            :maxTagCount="1"
            @change="handleChange(item.value, item)"
            :options="item.options"
            style="margin-left: 5px;width: 60%"
          />
        </div>
      </template>
      <template v-if="type === 'INPUT'">
        <a-input
          v-model:value="value"
          @change="emitData(value)"
          :style="style"
          :placeholder="description"
        />
        <span style="margin-left: 5px">{{unit}}</span>
      </template>
    </template>
  </div>
</template>
<script>
import { defineComponent, defineAsyncComponent, h, toRaw, watch, computed, reactive, ref, nextTick } from "vue";
import { getPartitionInfo } from "@/common/service";
import { debounce } from 'lodash-es'
import { message } from "ant-design-vue";

export default defineComponent({
  props: {
    param: Object,
    style: {
      type: Object,
      default: () => {
        return { width: '200px' }
      }
    },
    data: Object, // 数据源参数包含数据类型， 数据源等参数{ db，ds，id，table，type }
    tableNotExist: {
      type: Boolean,
      default: false
    }
  },
  components: {
    NoTablePartition: defineAsyncComponent(() => import("./noTablePartition.vue")), // 没有库表的分区
  },
  emits: ["updateInfo"],
  setup(props, context) {
    let { type, field, value, unit, source, description, key } = props.param;
    // console.log(props.param, 'asdadsad')
    if (key === 'partition' && props.tableNotExist && value !== null && !Array.isArray(value)) {
      const temp = [];
      Object.entries(value || {}).forEach(([index, val]) => {
        temp.push({ key: index, value: val });
      })
      value = ref(temp)
    } else {
      value = ref(value)
    }
    const itemKey = ref(key);
    //let tmlName = field.split(".").pop();
    const newProps = computed(() => JSON.parse(JSON.stringify(props.param)))
    const newData = computed(() => JSON.parse(JSON.stringify(props.data || {})))
    const tableNotExist = computed(() => props.tableNotExist);
    watch(() => [newProps.value, newData.value], async ([val, data1], [oldVal, oldData1]) => {
      if (key === 'partition' && props.tableNotExist && value !== null && !Array.isArray(val.value)) {
        const temp = [];
        Object.entries(val.value || {}).forEach(([index, val]) => {
          temp.push({ key: index, value: val });
        })
        value.value = temp
      } else {
        value.value = val.value
      }
      if (type === 'OPTION'){
        checkOptions.value = []
        val.values.map((item) => {
          checkOptions.value.push({
            value: item,
            label: item
          })
        })
      }
      if (type === 'MAP') {
        if (data1.id != oldData1.id || data1.db !== oldData1.db || data1.table !== oldData1.table) {
          await nextTick();
          _buildMap()
        } else {
          value.value = value.value || {}
          if (val.source === oldVal.source) {
            partitionArr.value.forEach(partition => {
              if (partition.type === 'OPTION') {
                partition.value = value.value[partition.label] ? [value.value[partition.label]] : []
              } else {
                partition.value = value.value[partition.label] ? value.value[partition.label] : partition.defaultValue || ''
              }
            })
          }
        }
      }
    })
    // watch(newData, (val, oldVal) => {
    //   if (type === 'MAP') {
    //     if (val.id != oldVal.id || val.db !== oldVal.db || val.table !== oldVal.table) {
    //       _buildMap()
    //     }
    //   }
    // })
    let checkOptions = ref([])
    if (type === 'OPTION'){
      checkOptions.value = []
      props.param.values.map((item) => {
        checkOptions.value.push({
          value: item,
          label: item
        })
      })
    }
    // 数据源类型
    const sourceType = computed(() => props.data.type)
    let partitionArr = ref([])
    const _buildMap = function () {
      partitionArr.value = []
      if (props.data?.type !== 'HIVE') return; // 只有hive会执行分区
      let url
      const source_reg = new RegExp('^http');
      if (source_reg.test(source.value)) {
        url = source.split('?')[0]
      } else {
        url = window.location.origin + source.split('?')[0]
      }
      // 获取分区信息
      getPartitionInfo({
        source: url,
        dataSourceId: props.data.id,
        database: props.data.db,
        table: props.data.table,
        tableNotExist: tableNotExist.value
      })
        .then(res => {
          for (let i in res.render) {
            if (typeof(res.render[i]) === 'string') {
              partitionArr.value.push({
                type: 'INPUT',
                label: i,
                value: value.value && value.value[i] ? value.value[i] : res.render[i],
                defaultValue: res.render[i]
              })
            } else {
              let checkOptions = []
              res.render[i].map((item) => {
                checkOptions.push({
                  value: item,
                  label: item
                })
              })
              partitionArr.value.push({
                type: 'OPTION',
                label: i,
                options: checkOptions,
                value: value.value && value.value[i] ? [value.value[i]] : []
              })
            }
          }
          if (partitionArr.value.length > 0) {
            emitData({}, true)
          }
        })
        .catch(err => {
          message.error("获取分区信息失败");
        })
    }
    if (type === 'MAP') {
      _buildMap()
    }


    const emitData = (value, isCan) => {
      let res = toRaw(props.param)
      if (isCan) {
        res.value = res.value || value
      } else {
        res.value = value
      }
      context.emit("updateInfo", res)
    }

    // 处理map变化
    const handleChange = (value, item) => {
      if (item.type === 'OPTION') {
        if (value && value.length) {
          item.value = [value.pop()]
        }
      }
      let res = toRaw(props.param)
      if (!res.value) {
        res.value = {}
      }
      partitionArr.value.map(part => {
        if (part.type === 'OPTION') {
          if (part.value && part.value.length) {
            res.value[part.label] = part.value.join()
          } else {
            res.value[part.label] = ''
          }
        } else {
          if (part.value) {
            res.value[part.label] = part.value
          } else {
            res.value[part.label] = ''
          }
        }
      })
      context.emit("updateInfo", res)
    }

    return {
      tableNotExist,
      checkOptions,
      type,
      value,
      itemKey,
      emitData,
      unit,
      source,
      partitionArr,
      description,
      handleChange,
      sourceType
    }
  }
});
</script>

<style scoped>
.custom_input {
  width: 226px;
  height: 25px;
}

.custom_select {
  width: 226px;
  height: 25px;
}
.custom_span_unit {
  width: 30px;
  margin-left: 8px;
  white-space: nowrap;
  display: inline-block;
  text-align: start;
}
</style>

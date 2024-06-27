<template>
    <div class="partition-wrapper">
        <a-row v-for="(item, index) in values" :key="'partition' + index" class="partition-item">
            <a-col :span="7">
                <!-- 暂时只支持输入 -->
                <a-input v-model:value="values[index]['key']" :placeholder="placeholder[0]"
                    @change="updateValues(index, 'key', $event)" />
                <!-- <a-select
                    ref="select"
                    mode="SECRET_COMBOBOX_MODE_DO_NOT_USE"
                    :placeholder="placeholder[0]"
                    v-model="values[index]['key']"
                    @change="updateSelectValues(index, 'key', $event)"
                    >
                        <a-select-option value="jack">Jack</a-select-option>
                        <a-select-option value="lucy">Lucy</a-select-option>
                        <a-select-option value="disabled">Disabled</a-select-option>
                        <a-select-option value="Yiminghe">yiminghe</a-select-option>
                </a-select> -->
            </a-col>
            <a-col :span="11" :offset="1">
                <a-input v-model:value="values[index]['value']" :placeholder="placeholder[1]"
                    @change="updateValues(index, 'value', $event)"/>
            </a-col>
            <a-col :span="2" :offset="1" class="partition-operate__icon">
                <PlusCircleOutlined :style="{ fontSize: 20 }" @click="updateItem('add')" />
            </a-col>
            <a-col v-if="values.length > 1" span="2" class="partition-operate__icon">
                <MinusCircleOutlined :style="{ fontSize: 20 }" @click="updateItem('del', index)" />
            </a-col>
        </a-row>
    </div>
</template>

<script>
import { defineComponent, h, toRaw, watch, ref } from "vue";
import { PlusCircleOutlined, MinusCircleOutlined } from '@ant-design/icons-vue';
import { cloneDeep } from 'lodash-es';

export default defineComponent({
    components: {
        PlusCircleOutlined,
        MinusCircleOutlined,
    },
    props: {
        modelValue: {
            type: Array,
            default: () => ([{ key: '', value: '' }])
        },
        placeholder: {
            type: [Array, String],
            default: ['请输入', '请输入']
        },
    },
    emits: ["update:modelValue", "updateValue"],
    setup(props, context) {
        const init = () => ([{ key: '', value: '' }]);
        const values = ref(init());

        function isSame(arr, target) {
            let result = false;
            if (arr && target && arr.length === target.length) {
                result = arr.every((item, index) => item.key === target[index].key && item.value === target[index].value);
            }
            return result;
        }

        watch(() => props.modelValue, (cur) => {
            if(!cur || !cur.length) {
                values.value = init();
            } else if (!isSame(cur, values.value)) {
                values.value = [...cloneDeep(cur)];
            }
        }, { immediate: true, deep: true })

        watch(() => values.value, (cur) => {
            if(!isSame(cur, props.modelValue)) {
                context.emit('update:modelValue', cloneDeep(cur));
                context.emit('updateValue', cloneDeep(cur))
            }
        }, { deep: true })

        function updateItem(type, index) {
            const len = values.value.length;
            if (type === 'del' && len <= 1) {
                return;
            }
            const item = { key: '', value: '' };
            const param = type === 'add' ? [len, 0, item] : [index, 1];
            values.value.splice(...param);
        }

        function updateSelectValues(index, key, val) {
            values.value[index][key] = val
        }

        function updateValues(index, key, e) {
            values.value[index][key] = e.target.value;
        }

        return {
            values,
            updateItem,
            updateValues,
            updateSelectValues
        }

    }
});
</script>
<style scoped>
.partition-item:not(:last-of-type) {
    margin-bottom: 10px;
}
.partition-operate__icon {
    display: flex;
    align-items: center;
}
</style>
<template>
  <div class="data-source-warp">
    <!-- left -->
    <div class="ds-l">
      <span>数据源</span>
    </div>
    <!-- right -->
    <div class="ds-r">
      <div class="main-header">
        <div>
          <span class="main-header-label">数据来源</span>
        </div>
        <div>
          <span class="main-header-label">数据目的</span>
        </div>
      </div>

      <div class="main-content">
        <!-- left -->
        <div class="data-source-warp-l">
          <div class="data-source-warp-l-content">
            <a-form ref="formRef" :model="formState" :rules="rules">
              <a-form-item ref="dsInfo" label="数据源信息" name="dsInfo">
                <SelectDataSource @updateDsInfo="updateDsInfo" />
              </a-form-item>
              <a-form-item label="传输方式" name="transMode">
                <a-select
                  v-model:value="formState.transMode"
                  placeholder="please select your transmission mode"
                >
                  <a-select-option value="Record">Record</a-select-option>
                  <a-select-option value="Binary">二进制</a-select-option>
                </a-select>
              </a-form-item>
              <a-form-item ref="partInfo" label="分区信息" name="partInfo">
                <span>ds1=</span>
                <a-input
                  v-model:value="formState.partInfo"
                  style="width: 200px"
                />
              </a-form-item>
              <a-form-item
                ref="nullCharacter"
                label="空值字符"
                name="nullCharacter"
              >
                <a-input v-model:value="formState.nullCharacter" />
              </a-form-item>
            </a-form>
          </div>
        </div>

        <!-- mid -->
        <div class="data-source-warp-mid"></div>

        <!-- right -->
        <div class="data-source-warp-r">
          <div class="data-source-warp-r-content">
            <a-form ref="formRef" :model="formState2" :rules="rules2">
              <a-form-item ref="dsInfo2" label="数据源信息" name="dsInfo2">
                <SelectDataSource @updateDsInfo="updateDsInfo2" />
              </a-form-item>
              <a-form-item label="写入方式" name="writeMode">
                <a-select
                  v-model:value="formState2.writeMode"
                  placeholder="please select your write mode"
                >
                  <a-select-option value="Insert">Insert</a-select-option>
                  <a-select-option value="Replace">Replace</a-select-option>
                  <a-select-option value="Update">Update</a-select-option>
                </a-select>
              </a-form-item>
              <a-form-item ref="batchSize" label="批量大小" name="batchSize">
                <a-input v-model:value="formState2.batchSize" />
              </a-form-item>
            </a-form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, reactive, toRaw } from "vue";
import SelectDataSource from "./selectDataSource";
export default defineComponent({
  components: {
    SelectDataSource,
  },
  setup() {
    const formRef = ref();
    const formState = reactive({
      dsInfo: "",
      transMode: undefined,
      partInfo: "",
      nullCharacter: "",
    });
    const formState2 = reactive({
      dsInfo2: "",
      writeMode: undefined,
      batchSize: "",
    });
    const rules = {
      transMode: [
        {
          required: true,
          message: "Please select transmission mode",
          trigger: "change",
        },
      ],
    };
    const rules2 = {
      writeMode: [
        {
          required: true,
          message: "Please select write mode",
          trigger: "change",
        },
      ],
    };
    const updateDsInfo = (dsInfo) => {
      formState.dsInfo = dsInfo;
      console.log(formState);
    };
    const updateDsInfo2 = (dsInfo) => {
      formState2.dsInfo = dsInfo;
      console.log(formState2);
    };
    return {
      formRef,
      formState,
      rules,
      formState2,
      rules2,
      updateDsInfo,
      updateDsInfo2,
    };
  },
});
</script>

<style lang="less" scoped>
.data-source-warp {
  width: 1100px;
  display: flex;
}
.ds-l {
  width: 122px;
}
.ds-r {
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
.data-source-warp-l {
  flex: 1;
  .ds-warp-l-c-item {
    > div {
      display: inline;
    }
  }
}
.data-source-warp-r {
  flex: 1;
}
.data-source-warp-mid {
  width: 172px;
}
.data-source-label {
  font-size: 14px;
  text-align: left;
}
</style>

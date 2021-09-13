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
            <div class="ds-warp-l-c-item">
              <div class="data-source-label">
                <span>数据源信息：</span>
              </div>
              <!-- 三级联 选择 -->
              <div class="data-source-select-modal">
                <a-button type="primary" @click="showModal"
                  >Open Modal</a-button
                >
                <a-modal
                  v-model:visible="visible_1"
                  title="选择数据源"
                  :footer="null"
                >
                  <SelectDataSource />
                </a-modal>
              </div>
            </div>
          </div>
        </div>

        <!-- mid -->
        <div class="data-source-warp-mid"></div>

        <!-- right -->
        <div class="data-source-warp-r"></div>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref } from "vue";
import SelectDataSource from "./selectDataSource";
export default defineComponent({
  components: {
    SelectDataSource,
  },
  setup() {
    const visible_1 = ref(false);

    const showModal = () => {
      visible_1.value = true;
    };

    return {
      visible_1,
      showModal,
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

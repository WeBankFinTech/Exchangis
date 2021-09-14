<template>
  <div class="field-map-warp">
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
        <div class="filed-map-warp-l">
          <div class="filed-map-warp-l-content"></div>
        </div>

        <!-- mid -->
        <div class="filed-map-warp-mid"></div>

        <!-- right -->
        <div class="field-map-warp-r">
          <div class="field-map-warp-r-content"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, reactive, toRaw } from "vue";
export default defineComponent({
  components: {},
  setup() {},
});
</script>

<style lang="less" scoped>
.field-map-warp {
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
.field-map-warp-l {
  flex: 1;
  .fm-warp-l-c-item {
    > div {
      display: inline;
    }
  }
}
.field-map-warp-r {
  flex: 1;
}
.field-map-warp-mid {
  width: 172px;
}
.field-map-label {
  font-size: 14px;
  text-align: left;
}
</style>

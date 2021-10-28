<template>
  <div class="home-page-wrap">
    <!-- top -->

    <!-- content -->
    <div class="hp-content">
      <div class="hp-content-card">
        <!-- title -->
        <div class="hp-content-card-title"><span>重点指标</span></div>
        <!-- main -->
        <div style="padding-bottom: 12px">
          <a-row :gutter="16">
            <a-col :span="4">
              <a-card>
                <div class="home-page-card-item">
                  <div class="home-page-card-img">
                    <img src="../../images/homePage/u3989.png" alt="" />
                  </div>
                  <div class="home-page-card-item-txt-t">
                    <span>{{ 0 }}</span>
                  </div>
                  <div class="home-page-card-item-txt-b">
                    <span>失败任务</span>
                  </div>
                </div>
              </a-card>
            </a-col>
            <a-col :span="4">
              <a-card>
                <div class="home-page-card-item">
                  <div class="home-page-card-img">
                    <img src="../../images/homePage/u6032.png" alt="" />
                  </div>
                  <div class="home-page-card-item-txt-t">
                    <span>{{ 0 }}</span>
                  </div>
                  <div class="home-page-card-item-txt-b">
                    <span>运行中</span>
                  </div>
                </div>
              </a-card>
            </a-col>
            <a-col :span="4">
              <a-card>
                <div class="home-page-card-item">
                  <div class="home-page-card-img">
                    <img src="../../images/homePage/u3998.png" alt="" />
                  </div>
                  <div class="home-page-card-item-txt-t">
                    <span>{{ 0 }}</span>
                  </div>
                  <div class="home-page-card-item-txt-b">
                    <span>慢任务</span>
                  </div>
                </div>
              </a-card>
            </a-col>
            <a-col :span="4">
              <a-card>
                <div class="home-page-card-item">
                  <div class="home-page-card-img">
                    <img src="../../images/homePage/u4010.png" alt="" />
                  </div>
                  <div class="home-page-card-item-txt-t">
                    <span>{{ 0 }}</span>
                  </div>
                  <div class="home-page-card-item-txt-b">
                    <span>等待中</span>
                  </div>
                </div>
              </a-card>
            </a-col>
            <a-col :span="4">
              <a-card>
                <div class="home-page-card-item">
                  <div class="home-page-card-img">
                    <img src="../../images/homePage/u4009.png" alt="" />
                  </div>
                  <div class="home-page-card-item-txt-t">
                    <span>{{ 0 }}</span>
                  </div>
                  <div class="home-page-card-item-txt-b">
                    <span>等待重试</span>
                  </div>
                </div>
              </a-card>
            </a-col>
            <a-col :span="4">
              <a-card>
                <div class="home-page-card-item">
                  <div class="home-page-card-img">
                    <img src="../../images/homePage/u3989.png" alt="" />
                  </div>
                  <div class="home-page-card-item-txt-t">
                    <span>{{ 0 }}</span>
                  </div>
                  <div class="home-page-card-item-txt-b">
                    <span>已完成</span>
                  </div>
                </div>
              </a-card>
            </a-col>
          </a-row>
        </div>
      </div>

      <div class="hp-content-card">
        <!-- title -->
        <div class="hp-content-card-title"><span>同步进度</span></div>
        <!-- main -->
        <div>
          <a-row type="flex">
            <a-col flex="100px"><span>总进度</span></a-col>
            <a-col flex="auto"
              ><a-progress :percent="50" status="active" strokeWidth="18"
            /></a-col>
          </a-row>
          <a-row type="flex">
            <a-col flex="100px"><span>同步到BDP</span></a-col>
            <a-col flex="auto"
              ><a-progress :percent="50" status="active" strokeWidth="18"
            /></a-col>
          </a-row>
          <a-row type="flex">
            <a-col flex="100px"><span>同步到ES</span></a-col>
            <a-col flex="auto"
              ><a-progress :percent="50" status="active" strokeWidth="18"
            /></a-col>
          </a-row>
          <a-row type="flex">
            <a-col flex="100px"><span>同步到FPS</span></a-col>
            <a-col flex="auto"
              ><a-progress :percent="50" status="active" strokeWidth="18"
            /></a-col>
          </a-row>
        </div>
      </div>

      <div class="hp-content-card">
        <!-- title -->
        <div class="hp-content-card-title"><span>流量监控</span></div>
        <!-- main -->
        <div></div>
      </div>

      <div class="hp-content-card">
        <!-- title -->
        <div class="hp-content-card-title"><span>资源使用监控</span></div>
        <!-- main -->
        <div></div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  setup() {},
};
</script>

<style lang="less" scoped>
.home-page-wrap {
  padding: 22px 20px;

  .hp-content {
    &-card {
      margin-top: 30px;
      max-width: 1160px;
      min-height: 160px;
      background: inherit;
      background-color: rgba(255, 255, 255, 1);
      box-sizing: border-box;
      border-width: 1px;
      border-style: solid;
      border-color: rgba(228, 228, 228, 1);
      border-radius: 5px;
      box-shadow: 5px 5px 5px rgb(0 0 0 / 35%);
      padding-left: 18px;
      padding-right: 18px;
      &-title {
        margin-top: 12px;
        margin-bottom: 12px;
        border-left: 5px solid rgba(45, 140, 240, 1);
        padding-left: 15px;
        height: 22px;
        font-family: "Arial Negreta", "Arial Normal", "Arial";
        font-weight: 700;
        font-style: normal;
        font-size: 16px;
        color: #797979;
      }
    }
  }

  .home-page-card-item {
    display: flex;
    align-items: center;
    flex-direction: column;

    &-img {
      width: 25px;
      height: 25px;
    }

    &-txt-t {
      font-family: "Arial Negreta", "Arial Normal", "Arial";
      font-weight: 700;
      font-style: normal;
      font-size: 20px;
    }

    &-txt-b {
      font-family: "Arial Normal", "Arial";
      font-weight: 400;
      font-style: normal;
      font-size: 14px;
      color: #515151;
    }
  }
}
</style>

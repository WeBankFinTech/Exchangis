<template>
  <div class="content">
    <a-tabs v-model:activeKey="activeKey">
      <a-tab-pane key="1">
        <template #tab>
          <div>
            <svg
              style="
                width: 16px;
                height: 16px;
                margin-right: 8px;
                fill: currentColor;
                color: #2e92f7;
              "
            >
              <use xlink:href="#icon-gongzuoliu"></use>
            </svg>
            <span
              style="
                font-family: PingFangSC-Medium;
                font-size: 14px;
                color: #2e92f7;
                text-align: left;
                font-weight: 500;
              "
              >项目列表</span
            >
          </div>
        </template>
        <div style="padding: 0 24px">
          <a-spin :spinning="loading">
            <a-row :gutter="[24, 24]">
              <a-col :span="24">
                <div class="title-line">
                  <a-input
                    :loading="loading"
                    :placeholder="$t('projectManage.topLine.searchBar.searchInputPlaceholder')"
                    :allowClear="true"
                    @pressEnter="handleOnSearch"
                    v-model:value="projectName"
                    style="width: 336px"
                  >
                    <template #prefix>
                      <icon-searchOutlined  style="color: #DEE4EC"/>
                    </template>
                  </a-input>

                  <!-- <a-input-search
                    :loading="loading"
                    :placeholder="
                      $t(
                        'projectManage.topLine.searchBar.searchInputPlaceholder'
                      )
                    "
                    :allowClear="true"
                    style="width: 300px"
                    @search="handleOnSearch"
                  >
                    <template #enterButton>
                      <a-button type="primary">
                        <template #icon> <icon-searchOutlined /></template>
                        {{
                          $t("projectManage.topLine.searchBar.searchButtonText")
                        }}
                      </a-button>
                    </template>
                  </a-input-search> -->

                  <a-button type="primary" @click="handleCreateCardAction"
                    ><PlusOutlined /><span>创建项目</span></a-button
                  >
                </div>
              </a-col>
              <!-- 创建卡片 -->
              <!-- <a-col :span="6">
              <project-create-card @action="handleCreateCardAction" />
            </a-col> -->
              <!-- 视图卡片 -->
              <template v-if="projectList.length">
                <a-col v-for="item in projectList" :key="item.id">
                  <project-view-card
                    @delete="handleOnDelteProject"
                    @edit="handleOnEditProject"
                    :name="item.name"
                    :describe="item.describe"
                    :id="item.id"
                    :tags="item.tags"
                    :domain="item.domain"
                  />
                </a-col>
                <!-- 分页行 -->
                <a-col :span="24" style="position: fixed;right: 10px;bottom: 10px">
                  <div class="pagination-line">
                    <a-pagination
                      v-model:current="pageCfg.current"
                      v-model:pageSize="pageCfg.size"
                      @change="handleChangePage"
                      :total="total"
                      show-less-items
                    />
                  </div>
                </a-col>
              </template>
              <template v-else>
                <a-empty style="width: 100%"/>
              </template>
            </a-row>
          </a-spin>
        </div>
      </a-tab-pane>
      <!-- 创建表单 -->
    </a-tabs>
    <edit-modal
      v-model:visible="modalCfg.visible"
      :mode="modalCfg.mode"
      :id="modalCfg.id"
      :dataSources="dataSourceList"
      @finish="handleModalFinish"
    />
  </div>
</template>

<script>
import { useI18n } from "@fesjs/fes";
import { PlusOutlined, SearchOutlined } from "@ant-design/icons-vue";
import ProjectCreateCard from "./components/projectCreateCard.vue";
import ProjectViewCard from "./components/projectViewCard.vue";
import EditModal from "./components/editModal.vue";
import { getProjectList, deleteProject, getDataSourceList } from "@/common/service";
import { message } from "ant-design-vue";
import { defineComponent, ref } from "vue";

export default {
  components: {
    ProjectViewCard,
    ProjectCreateCard,
    EditModal,
    iconSearchOutlined: SearchOutlined,
    PlusOutlined,
  },
  setup() {
    return {
      activeKey: ref("1"),
    };
  },
  data() {
    return {
      // 弹窗参数
      modalCfg: {
        mode: "",
        id: "",
        visible: false,
      },
      // 项目列表
      projectList: [],
      // 是否加载中
      loading: false,
      projectName: '',
      // 分页配置
      pageCfg: {
        current: 1,
        size: 10,
      },
      total : 0,
      name: this.$route.query.name
    };
  },
  computed: {
  },
  methods: {
    async getDataList(name='', current=1, size=10) {
      this.loading = true;
      let { list, total } = await getProjectList(name, current, size);
      this.loading = false;
      this.total = total
      this.projectList = list
        .map((item) => ({
          id: item.id,
          name: item.name,
          describe: item.description,
          tags: item.tags.split(","),
          domain: item.domain
        }))
    },
    async getDataSourceList(page = 1) {
        const params = { page, pageSize: 100 };
        const { list, total } = await getDataSourceList(params);
        (list || []).forEach(item => {
          item.dataSourceType = item.type;
          item.dataSourceName = item.name;
          item.dataSourceDesc = item.desc;
          item.dataSourceId = item.id;
          item.modifyTime = item.modifyTime;
          item.createUser = item.createUser;
        })
        this.dataSourceList = [...this.dataSourceList, ...list];
        if(this.dataSourceList.length < total) {
          this.getDataSourceList(page + 1);
        }
    },
    // 模态框操作完成
    handleModalFinish() {
      this.pageCfg.current = 1;
      this.getDataList();
    },
    // 新建卡片点击
    handleCreateCardAction() {
      this.modalCfg = {
        visible: true,
        mode: "create",
      };
    },
    // 处理搜索
    handleOnSearch() {
      this.pageCfg.current = 1;
      if (/\%/.test(this.projectName)) {
        return message.error("项目名搜索不支持通配符%");
      }
      this.getDataList(this.projectName);
    },
    // 删除项目
    async handleOnDelteProject(id) {
      await deleteProject(id);
      message.success("删除成功");
      this.pageCfg.current = 1;
      this.getDataList(this.projectName);
    },
    // 编辑项目
    handleOnEditProject(id) {
      this.modalCfg = {
        visible: true,
        mode: "edit",
        id: id,
      };
    },
    handleChangePage(current) {
      this.pageCfg.current = current
      this.getDataList(this.projectName, current)
    }
  },
  async mounted() {
    if (this.$route.query.labels) {
      localStorage.setItem('exchangis_environment', this.$route.query.labels)
    }
    this.dataSourceList = [];
    this.getDataList();
    this.getDataSourceList();
  },
};
</script>
<style scoped lang="less">
.content {
  box-sizing: border-box;
  background: #fff;
  min-height: 100%;
  &-header {
    height: 35px;
    background-color: #fff;
  }
}
.title-line {
  display: flex;
  justify-content: space-between;
  align-items: center;
  .title {
    border-left: 6px solid #1890ff;
    padding-left: 6px;
  }
}
.pagination-line {
  display: flex;
  justify-content: flex-end;
}
.ant-tabs-tab-active .ant-tabs-tab /deep/ {
  background-color: #ebf5ff;
}
</style>

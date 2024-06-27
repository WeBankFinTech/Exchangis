<template>
  <div class="content">
    <div class="content-main" @click="gotoDetail">
      <div><img class="img" :src="imageSrc" v-if="imageSrc" /></div>
      <div class="content-main-c">
        <div class="content-main-c-header" :title="jobData.jobName">
          {{ jobData.jobName }}
        </div>
        <div class="content-main-c-desc">
          <p
            :class="[
              jobData.jobLabels && jobData.jobLabels.length > 0
                ? 'content-main-c-desc-oneline'
                : 'content-main-c-desc-2line',
            ]"
            :title="jobData.jobDesc"
          >
            {{ jobData.jobDesc }}
          </p>
        </div>
        <div
          class="content-main-c-tags"
          v-if="jobData.jobLabels && jobData.jobLabels.length > 0"
        >
          <template v-for="(tag, index) in jobData.jobLabels.split(',').slice(0,5)" :key="index">
            <a-tooltip v-if="tag.length > 5" :title="tag">
              <a-tag>
                {{ `${tag.slice(0, 5)}...` }}
              </a-tag>
            </a-tooltip>
            <a-tag v-else>
              {{ tag }}
            </a-tag>
          </template>
        </div>
      </div>
    </div>
    <div class="content-sidebar">
      <div @click="handleJobModify">
        <span class="iconfont icon-need-fault-tolerance job_card_icon"></span>
      </div>
      <a-divider type="horizontal" style="width: 16px" />
      <div>
        <a-popconfirm
          :title="$t('job.action.confirmDelete')"
          :ok-text="$t('job.action.yes')"
          :cancel-text="$t('job.action.no')"
          @confirm="confirmDelete"
        >
          <span class="iconfont icon-delete job_card_icon"></span>
        </a-popconfirm>
      </div>
    </div>
  </div>
</template>
<script>
import { defineComponent, reactive, ref, computed } from "vue";
import {
  CopyOutlined,
  DeleteOutlined,
  ExportOutlined,
} from "@ant-design/icons-vue";
import { useI18n } from "@fesjs/fes";
import { message } from "ant-design-vue";
import { deleteJob } from "@/common/service";
import { cloneDeep } from 'lodash-es'

export default defineComponent({
  components: {
    CopyOutlined,
    DeleteOutlined,
    ExportOutlined,
  },
  props: {
    jobData: Object,
    type: String,
  },
  emits: ["showJobDetail", "handleJobCopy", "refreshList", "handleJobModify"],
  setup(props, context) {
    const { t } = useI18n({ useScope: "global" });
    const jobData = computed(() => cloneDeep(props.jobData));
    const { engineType, id, projectId } = jobData.value;
    const imageText = engineType.toUpperCase();
    const imageName =
      imageText === "DATAX"
        ? "datax.png"
        : imageText === "SQOOP"
        ? "sqoop.svg"
        : imageText === "FLINK"
        ? "flink.svg"
        : "";
    const managementVisible = ref(false);
    const changeManagement = () => {
      console.log(managementVisible.value);
      managementVisible.value = !managementVisible.value;
    };
    const confirmDelete = async () => {
      const result = await deleteJob(id);
      console.log(result);
      if (result) {
        message.success(t("job.action.deleteJobSuccess"));
        context.emit("refreshList", props.type);
        context.emit("handleDel", id);
        changeManagement();
      }
    };

    const gotoDetail = () => {
      context.emit("showJobDetail", jobData.value);
    };

    const handleJobCopy = () => {
      context.emit("handleJobCopy", jobData.value);
    };

    const handleJobModify = () => {
      context.emit("handleJobModify", jobData.value);
    }

    return {
      jobData,
      imageSrc: imageName ? require(`../../../images/${imageName}`) : "",
      imageText,
      changeManagement,
      managementVisible,
      confirmDelete,
      gotoDetail,
      handleJobCopy,
      handleJobModify
    };
  },
});
</script>
<style scoped lang="less">
.job_card_icon {
  width: 16px;
  height: 16px;
  fill: currentColor;
  color: rgba(0, 0, 0, 0.45);
  cursor: pointer;
}
.icon-delete:hover {
  color: rgba(0, 0, 0, 0.65);
}
.icon-copy:hover {
  color: rgba(0, 0, 0, 0.65);
}
.content {
  box-sizing: border-box;
  border: 1px solid #dee4ec;
  background: #fff;
  width: 381px;
  height: 130px;
  display: flex;
  &-main {
    flex: 1;
    padding: 16px;
    display: flex;
    cursor: pointer;
    &-c {
      flex: 1;
      margin-left: 16px;
      &-header {
        font-family: PingFangSC-Medium;
        font-size: 16px;
        color: rgba(0, 0, 0, 0.85);
        text-align: left;
        line-height: 22px;
        font-weight: 500;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        width: 224px;
      }
      &-desc {
        font-family: PingFangSC-Regular;
        font-size: 14px;
        color: rgba(0, 0, 0, 0.45);
        text-align: left;
        line-height: 22px;
        font-weight: 400;
        &-2line {
          width: 224px;
          display: -webkit-box;
          -webkit-box-orient: vertical;
          -webkit-line-clamp: 2;
          overflow: hidden;
        }
        &-oneline {
          width: 224px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
      }
      &-tags {
        font-family: PingFangSC-Regular;
        font-size: 12px;
        color: rgba(0, 0, 0, 0.65);
        line-height: 20px;
        font-weight: 400;
      }
    }
  }
  &-sidebar {
    background: #f7f9fa;
    height: 129px;
    width: 37px;
    padding: 22px 10px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: space-around;
    border: 1px solid #dee4ec;
    border-top: none;
    :deep(.ant-divider-horizontal) {
      margin: 8px 0;
    }
  }
}
.img {
  margin-top: 10px;
  width: 71px;
  height: 71px;
}
.imageText {
  margin-top: 10px;
  height: 40px;
  border: 1px solid rgba(0, 0, 0, 0.2);
  font-size: 14px;
  font-weight: 700;
  line-height: 40px;
  padding: 0 4px;
  border-radius: 2px;
  box-sizing: border-box;
}
.infoWrap {
  flex: 1;
  height: 200px;
  padding: 10px 0px 10px 15px;
  position: relative;
  .jobNameWrap {
    display: flex;
    justify-content: space-between;
    align-items: center;
    .jobName {
      font-size: 13px;
      font-weight: 700;
      color: #000;
      cursor: pointer;
      width: 140px;
      text-overflow: ellipsis;
      white-space: nowrap;
      overflow: hidden;
    }
    .available {
      background: rgba(0, 128, 0, 1);
      font-size: 13px;
      font-weight: 700;
      font-family: "Comic Sans MS Negreta", "Comic Sans MS Normal",
        "Comic Sans MS";
      color: #fff;
      padding: 0px 4px;
    }
    .disable {
      background: rgba(255, 204, 153, 1);
    }
  }
  .jobDesc {
    margin-top: 5px;
    font-size: 12px;
    color: #797979;
  }
  .jobLabels {
    margin-top: 5px;
  }
  .btnWrap {
    position: absolute;
    right: 0px;
    bottom: 10px;
  }
}
</style>

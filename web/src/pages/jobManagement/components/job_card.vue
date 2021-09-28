<template>
  <div class="content">
    <div class="main">
      <img class="img" :src="imageSrc" v-if="imageSrc" />
      <div class="imageText" v-if="!imageSrc">{{ imageText }}</div>
      <div class="infoWrap">
        <div class="jobNameWrap">
          <a-tooltip :title="jobData.jobName">
            <div class="jobName" @click="gotoDetail">
              {{ jobData.jobName }}
            </div>
          </a-tooltip>
          <div
            :class="{
              available: true,
              disable: jobData.jobStatus === 'valid',
            }"
          >
            {{ jobData.jobStatus !== 'valid' ? 'Available' : 'Disable' }}
          </div>
        </div>
        <div class="jobDesc">{{ jobData.jobDesc }}</div>
        <div class="jobLabels">
          <a-tag
            v-for="(tag, index) in jobData.jobLabels.split(',')"
            :key="index"
            >{{ tag }}</a-tag
          >
        </div>
        <div class="btnWrap">
          <a-button
            v-if="!managementVisible"
            type="primary"
            @click="changeManagement"
          >
            {{ $t('job.action.manage') }}
          </a-button>
          <div v-if="managementVisible">
            <CopyOutlined class="icon" @click="handleJobCopy" />
            <a-popconfirm
              :title="$t('job.action.confirmDelete')"
              :ok-text="$t('job.action.yes')"
              :cancel-text="$t('job.action.no')"
              @confirm="confirmDelete"
            >
              <DeleteOutlined class="icon" />
            </a-popconfirm>
            <ExportOutlined class="icon" @click="changeManagement" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { defineComponent, reactive, ref, toRaw } from 'vue';
import {
  CopyOutlined,
  DeleteOutlined,
  ExportOutlined,
} from '@ant-design/icons-vue';
import { useI18n } from '@fesjs/fes';
import { message } from 'ant-design-vue';
import { deleteJob } from '@/common/service';

export default {
  components: {
    CopyOutlined,
    DeleteOutlined,
    ExportOutlined,
  },
  props: {
    jobData: Object,
    type: String,
  },
  emits: ['showJobDetail', 'handleJobCopy', 'refreshList'],
  setup(props, context) {
    const { t } = useI18n({ useScope: 'global' });
    const jobData = toRaw(props.jobData);
    const { engineType, id, projectId } = jobData;
    const imageText = engineType.toUpperCase();
    const imageName =
      imageText === 'DATAX'
        ? 'datax.png'
        : imageText === 'SQOOP'
        ? 'sqoop.svg'
        : imageText === 'FLINK'
        ? 'flink.svg'
        : '';
    const managementVisible = ref(false);
    const changeManagement = () => {
      console.log(managementVisible.value);
      managementVisible.value = !managementVisible.value;
    };
    const confirmDelete = async () => {
      const result = await deleteJob(id);
      console.log(result);
      if (result) {
        message.success(t('job.action.deleteJobSuccess'));
        context.emit('refreshList', props.type, projectId);
        changeManagement();
      }
    };

    const gotoDetail = () => {
      context.emit('showJobDetail', jobData);
    };

    const handleJobCopy = () => {
      context.emit('handleJobCopy', jobData);
    };

    return {
      imageSrc: imageName ? require(`../../../images/${imageName}`) : '',
      imageText,
      changeManagement,
      managementVisible,
      confirmDelete,
      gotoDetail,
      handleJobCopy,
    };
  },
};
</script>
<style scoped lang="less">
.content {
  padding: 0px 10px;
  box-sizing: border-box;
  border: 1px solid rgba(0, 0, 0, 0.2);
  background: #fff;
  width: 320px;
  height: 200px;
  border-radius: 5px;
  box-shadow: 5px 5px 6px rgba(0, 0, 0, 0.2);
  .main {
    display: flex;
  }
}
.img {
  margin-top: 10px;
  width: 40px;
  height: 40px;
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
      font-family: 'Comic Sans MS Negreta', 'Comic Sans MS Normal',
        'Comic Sans MS';
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
    .icon {
      font-weight: 600;
      font-size: 24px;
      cursor: pointer;
      color: #1890ff;
      margin-left: 10px;
    }
  }
}
</style>

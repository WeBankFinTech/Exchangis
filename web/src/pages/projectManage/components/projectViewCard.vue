<template>
  <!-- <a-card class="view-card" size="small" bordered>
    <template v-slot:title>
      <router-link :to="`/jobManagement?id=${id}&name=${name}`">
        <div class="title">
          <icon-apartment-outlined style="color: #1890ff" /> {{ name }}
        </div>
      </router-link>
    </template>
    <template v-slot:extra>
      <a-dropdown placement="bottomCenter" trigger="click">
        <a class="ant-dropdown-link">
          {{ $t("projectManage.viewCard.actionTitle") }}
        </a>
        <template v-slot:overlay>
          <a-menu>
            <a-menu-item @click="$emit('edit', id)">
              {{ $t("projectManage.viewCard.action.edit") }}
            </a-menu-item>
            <a-menu-item @click="$emit('delete', id)">
              {{ $t("projectManage.viewCard.action.delete") }}
            </a-menu-item>
          </a-menu>
        </template>
      </a-dropdown>
    </template>
    <div class="content-box">
      <p class="describe">{{ describe }}</p>
    </div>
    <div>
      <a-tag v-for="(tag, index) in tags" :key="index"> {{ tag }} </a-tag>
    </div>
  </a-card> -->

  <div class="card">
    <div class="card-main">
      <router-link :to="`/jobManagement?id=${id}&name=${name}`">
        <div class="card-main-title">
          <div class="title" :title="name">{{ name }}</div>
        </div>
        <div class="card-main-description">
          <p
            :class="[
              card_tages && card_tages.length > 0
                ? 'card-main-desc-oneline'
                : 'card-main-desc-2line',
            ]"
            :title="describe"
          >
            {{ describe }}
          </p>
          <div>
            <div v-if="card_tages && card_tages.length > 0">
              <a-tag v-for="(tag, index) in card_tages" :key="index">
                {{ tag ? tag : null }}
              </a-tag>
            </div>
            <div v-else></div>
          </div>
        </div>
      </router-link>
    </div>
    <div class="card-buttton-group">
      <div @click="$emit('edit', id)">
        <span class="iconfont icon-need-fault-tolerance project_view_card_icon"></span>
      </div>
      <a-divider type="horizontal" style="width: 16px" />
      <a-popconfirm
        title="是否删除项目?"
        ok-text="确定"
        cancel-text="取消"
        @confirm="$emit('delete', id)"
      >
        <DeleteOutlined class="delete-icon" />
      </a-popconfirm>
      <!--<div @click="$emit('delete', id)">-->
        <!--<span class="iconfont icon-delete project_view_card_icon"></span>-->
      <!--</div>-->
    </div>
  </div>
</template>

<script>
import { ApartmentOutlined, DeleteOutlined } from "@ant-design/icons-vue";
import { useRouter } from "@fesjs/fes";
import { toRefs } from "vue";
const router = useRouter();
export default {
  name: "ProjectViewCard",
  components: {
    iconApartmentOutlined: ApartmentOutlined,
    DeleteOutlined
  },
  props: {
    id: {
      type: String,
      required: true,
    },
    // 项目名称
    name: {
      type: String,
      required: true,
      default: "",
    },
    // 项目描述
    describe: {
      type: String,
      default: "",
    },
    // tag
    tags: {
      type: Array,
      default: [],
    },
  },
  setup(props) {
    const { tags } = toRefs(props);
    const card_tages = tags.value.filter((i) => !!i);
    return {
      card_tages,
    };
  },
  emits: ["delete", "edit"],
};
</script>

<style scoped lang="less">
.project_view_card_icon {
  width: 16px;
  height: 16px;
  fill: currentColor;
  color: rgba(0, 0, 0, 0.45);
  cursor: pointer;
}
.icon-delete:hover {
  color: rgba(0, 0, 0, 0.65);
}
.icon-need-fault-tolerance:hover {
  color: rgba(0, 0, 0, 0.65);
}

.title {
  cursor: pointer;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  &:hover {
    color: #1890ff;
  }
}
.view-card {
  height: 180px;
}
.content-box {
  height: 100%;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  .describe {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}

.card {
  min-height: 115px;
  border: 1px solid #dee4ec;
  background-color: #fff;
  display: flex;
  width: 381px;
  &-main {
    display: flex;
    flex-direction: column;
    justify-content: center;
    padding-left: 24px;
    height: 115px;
    flex: 1;
    width: calc(100% - 37px);
    &-title {
      font-family: PingFangSC-Medium;
      font-size: 16px;
      color: #2e92f7;
      text-align: left;
      line-height: 22px;
      font-weight: 500;
    }
    &-description {
      font-family: PingFangSC-Regular;
      font-size: 14px;
      color: rgba(0, 0, 0, 0.45);
      text-align: left;
      font-weight: 400;
      height: 40px;
      width: 100%;
      margin-top: 5px;
    }
    &-desc-2line {
      width: 296px;
      display: -webkit-box;
      -webkit-box-orient: vertical;
      -webkit-line-clamp: 2;
      overflow: hidden;
    }
    &-desc-oneline {
      width: 296px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }

  &-buttton-group {
    background: #f7f9fa;
    height: 115px;
    width: 37px;
    padding: 22px 10px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: space-around;
  }
}
</style>

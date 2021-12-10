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
    <a-row>
      <a-col :span="22">
        <div class="card-main">
          <div class="card-main-title">
            <router-link :to="`/jobManagement?id=${id}&name=${name}`">
              <div class="title">
                {{ name }}
              </div>
            </router-link>
          </div>
          <div class="card-main-description">
            <p>{{ describe }}</p>
            <div>
              <div v-if="tags && tags.length > 0">
                <a-tag v-for="(tag, index) in tags" :key="index">
                  {{ tag }}
                </a-tag>
              </div>
              <div v-else></div>
            </div>
          </div>
        </div>
      </a-col>
      <a-col :span="2">
        <div class="card-buttton-group">
          <div @click="$emit('edit', id)">
            <svg
              style="
                width: 24px;
                height: 24px;
                fill: currentColor;
                color: #dee4ec;
              "
            >
              <use xlink:href="#icon-need-fault-tolerance"></use>
            </svg>
          </div>
          <div @click="$emit('delete', id)">
            <svg
              style="
                width: 24px;
                height: 24px;
                fill: currentColor;
                color: #dee4ec;
              "
            >
              <use xlink:href="#icon-delete"></use>
            </svg>
          </div>
        </div>
      </a-col>
    </a-row>
  </div>
</template>

<script>
import { ApartmentOutlined } from "@ant-design/icons-vue";
import { useRouter } from "@fesjs/fes";
const router = useRouter();
export default {
  name: "ProjectViewCard",
  components: {
    iconApartmentOutlined: ApartmentOutlined,
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
  emits: ["delete", "edit"],
};
</script>

<style scoped lang="less">
.title {
  cursor: pointer;
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
  // min-width: 381px;
  min-height: 115px;
  border: 1px solid #dee4ec;
  background-color: #fff;
  &-main {
    display: flex;
    flex-direction: column;
    justify-content: center;
    padding-left: 24px;
    height: 115px;
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
      line-height: 22px;
      font-weight: 400;
    }
  }

  &-buttton-group {
    background: #f7f9fa;
    height: 115px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: space-around;
  }
}
</style>

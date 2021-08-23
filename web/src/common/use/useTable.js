import { ref, reactive } from "vue";
import { request } from "@fesjs/fes";

export const useTableBase = (api) => {
  // 加载中状态
  const queryDataSourceLoading = ref(true);
  const dataSource = ref([]);
  // 缓存参数
  let cacheParams = {};
  // 返回查询方法
  const queryDataSource = async (params) => {
    queryDataSourceLoading.value = true;
    try {
      cacheParams = params || cacheParams;
      const result = await api.bind(null, params)();
      dataSource.value = result || [];
      return result;
    } finally {
      queryDataSourceLoading.value = false;
    }
  };

  return {
    dataSource,
    queryDataSource,
    queryDataSourceLoading,
  };
};

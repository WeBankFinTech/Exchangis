import { ref, reactive } from 'vue';
import { request } from '@fesjs/fes';

export const useTableBase = (api) => {
    const queryDataSourceLoading = ref(true);
    const dataSource = ref([]);

    let cacheParams = {};
    const queryDataSource = async (params) => {
        queryDataSourceLoading.value = true;
        try {
            cacheParams = params || cacheParams;
            const result = await request(api, {
                ...cacheParams
            });
            dataSource.value = result.cycle || [];
            return result;
        } finally {
            queryDataSourceLoading.value = false;
        }
    };

    return {
        dataSource,
        queryDataSource,
        queryDataSourceLoading
    };
};


export const useTableWithPagination = (api) => {
    const {
        dataSource, queryDataSourceLoading, queryDataSource
    } = useTableBase(api);

    const pagination = reactive({
        current: 1,
        pageSize: 20,
        showSizeChanger: true,
        total: 0,
        pageSizeOptions: ['10', '20', '50', '100'],
        showTotal: total => `共${total}条，共${Math.ceil(total / pagination.pageSize)}页`
    });

    let cacheParams = {};
    const queryDataSourceWithPage = async (params) => {
        cacheParams = params || cacheParams;
        const result = await queryDataSource({
            ...cacheParams,
            page: {
                currentPage: pagination.current,
                pageSize: pagination.pageSize
            }
        });
        if (result.page) {
            pagination.total = result.page.totalCount;
        }
    };

    const changePage = (currentPagination) => {
        pagination.current = currentPagination.current;
        pagination.pageSize = currentPagination.pageSize;
        queryDataSourceWithPage();
    };

    return {
        dataSource,
        queryDataSourceLoading,
        queryDataSource: queryDataSourceWithPage,
        pagination,
        resetPagination: () => {
            pagination.current = 1;
        },
        changePage
    };
};

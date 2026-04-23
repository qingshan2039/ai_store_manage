/* ========================================
   通用表格 Hook — 管理分页、加载、筛选
   ======================================== */
import { useState, useCallback, useEffect } from 'react';
import { message } from 'antd';
import { DEFAULT_PAGE_SIZE } from '@/constants/config';

interface UseTableOptions<T, P extends Record<string, unknown>> {
  /** 请求函数，接收查询参数，返回分页数据 */
  fetchFn: (params: P & { page: number; pageSize: number }) => Promise<{
    data: { items: T[]; total: number; page: number; pageSize: number; totalPages: number };
  }>;
  /** 默认查询参数 */
  defaultParams?: Partial<P>;
  /** 是否立即加载 */
  immediate?: boolean;
}

interface UseTableReturn<T, P extends Record<string, unknown>> {
  dataSource: T[];
  loading: boolean;
  total: number;
  page: number;
  pageSize: number;
  filters: Partial<P>;
  /** 刷新当前页 */
  refresh: () => void;
  /** 分页变化 */
  onPageChange: (page: number, pageSize: number) => void;
  /** 搜索（重置到第 1 页） */
  onSearch: (filters: Partial<P>) => void;
  /** 重置筛选条件 */
  onReset: () => void;
}

export function useTable<T, P extends Record<string, unknown> = Record<string, unknown>>(
  options: UseTableOptions<T, P>,
): UseTableReturn<T, P> {
  const { fetchFn, defaultParams, immediate = true } = options;

  const [dataSource, setDataSource] = useState<T[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(DEFAULT_PAGE_SIZE);
  const [filters, setFilters] = useState<Partial<P>>(defaultParams ?? {});

  const fetchData = useCallback(
    async (currentPage: number, currentPageSize: number, currentFilters: Partial<P>) => {
      setLoading(true);
      try {
        const params = {
          ...currentFilters,
          page: currentPage,
          pageSize: currentPageSize,
        } as P & { page: number; pageSize: number };
        const response = await fetchFn(params);
        setDataSource(response.data.items);
        setTotal(response.data.total);
      } catch (error) {
        console.error('表格数据加载失败:', error);
        message.error('数据加载失败');
      } finally {
        setLoading(false);
      }
    },
    [fetchFn],
  );

  useEffect(() => {
    if (immediate) {
      fetchData(page, pageSize, filters);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const refresh = useCallback(() => {
    fetchData(page, pageSize, filters);
  }, [fetchData, page, pageSize, filters]);

  const onPageChange = useCallback(
    (newPage: number, newPageSize: number) => {
      setPage(newPage);
      setPageSize(newPageSize);
      fetchData(newPage, newPageSize, filters);
    },
    [fetchData, filters],
  );

  const onSearch = useCallback(
    (newFilters: Partial<P>) => {
      setFilters(newFilters);
      setPage(1);
      fetchData(1, pageSize, newFilters);
    },
    [fetchData, pageSize],
  );

  const onReset = useCallback(() => {
    const resetFilters = defaultParams ?? {};
    setFilters(resetFilters);
    setPage(1);
    fetchData(1, pageSize, resetFilters);
  }, [fetchData, pageSize, defaultParams]);

  return {
    dataSource,
    loading,
    total,
    page,
    pageSize,
    filters,
    refresh,
    onPageChange,
    onSearch,
    onReset,
  };
}

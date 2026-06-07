/* ========================================
   SKU 列表页面（最小库存单元）
   ======================================== */
import React, { useEffect, useState } from 'react';
import { Card, Table, Button, Space, Form, Switch, Tag, message } from 'antd';
import type { TableProps } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import PageContainer from '@/components/PageContainer';
import ConfirmAction from '@/components/ConfirmAction';
import { skuApi } from '@/api/sku';
import { spuApi } from '@/api/spu';
import { useTable } from '@/hooks/useTable';
import { useModal } from '@/hooks/useModal';
import { STATUS, ITEM_TYPE_MAP } from '@/constants/enums';
import type { SkuSummary, SkuQueryParams, Sku } from '@/types/sku';
import SkuSearchForm from './components/SkuSearchForm';
import SkuFormModal from './components/SkuFormModal';
import { formatDateTime } from '@/utils/format';

export interface SpuOption {
  label: string;
  value: number;
}

const ITEM_TYPE_COLOR: Record<string, string> = { RAW: 'blue', SEMI: 'orange', FINISHED: 'green' };

const SkuListPage: React.FC = () => {
  const [searchForm] = Form.useForm<SkuQueryParams>();
  const [spuOptions, setSpuOptions] = useState<SpuOption[]>([]);

  const { dataSource, loading, total, page, pageSize, onPageChange, onSearch, onReset, refresh } =
    useTable<SkuSummary, SkuQueryParams>({
      fetchFn: skuApi.list,
      defaultParams: { page: 1, pageSize: 20 },
    });

  const formModal = useModal<Sku>();

  useEffect(() => {
    spuApi
      .list({ page: 1, pageSize: 100, status: 1 })
      .then((res) => setSpuOptions(res.data.items.map((s) => ({ label: `${s.spuName}（${s.spuCode}）`, value: s.id }))))
      .catch(() => {});
  }, []);

  const handleCreate = () => formModal.open('create');

  const handleEdit = async (id: number) => {
    try {
      const res = await skuApi.getById(id);
      formModal.open('edit', res.data);
    } catch (e) {}
  };

  const handleDelete = async (id: number) => {
    try {
      await skuApi.delete(id);
      message.success('删除成功');
      refresh();
    } catch (e) {}
  };

  const handleStatusChange = async (id: number, checked: boolean) => {
    try {
      await skuApi.updateStatus(id, { status: checked ? STATUS.ENABLED : STATUS.DISABLED });
      message.success('状态变更成功');
      refresh();
    } catch (e) {}
  };

  const columns: TableProps<SkuSummary>['columns'] = [
    { title: 'SKU 编码', dataIndex: 'skuCode', key: 'skuCode', width: 150 },
    { title: 'SKU 名称', dataIndex: 'skuName', key: 'skuName', width: 220, ellipsis: true },
    { title: '所属 SPU', dataIndex: 'spuName', key: 'spuName', width: 160, render: (v) => v || '-' },
    {
      title: '阶段',
      dataIndex: 'itemType',
      key: 'itemType',
      width: 100,
      render: (v: string) => <Tag color={ITEM_TYPE_COLOR[v]}>{ITEM_TYPE_MAP[v] ?? v}</Tag>,
    },
    {
      title: '尺寸(长×宽 mm)',
      key: 'size',
      width: 150,
      render: (_, r) => (r.lengthMm != null || r.widthMm != null ? `${r.lengthMm ?? '-'} × ${r.widthMm ?? '-'}` : '-'),
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (val, record) => (
        <Switch
          checked={val === STATUS.ENABLED}
          onChange={(checked) => handleStatusChange(record.id, checked)}
          checkedChildren="启用"
          unCheckedChildren="禁用"
        />
      ),
    },
    { title: '创建时间', dataIndex: 'createdAt', key: 'createdAt', width: 160, render: (val) => formatDateTime(val) },
    {
      title: '操作',
      key: 'action',
      width: 140,
      fixed: 'right',
      render: (_, record) => (
        <Space size="middle">
          <a onClick={() => handleEdit(record.id)}>编辑</a>
          <ConfirmAction
            title={`确定要删除 SKU [${record.skuName}] 吗？`}
            onConfirm={() => handleDelete(record.id)}
            type="link"
            danger
            style={{ padding: 0, height: 'auto' }}
          >
            删除
          </ConfirmAction>
        </Space>
      ),
    },
  ];

  return (
    <PageContainer title="物料 SKU">
      <SkuSearchForm form={searchForm} onSearch={onSearch} onReset={onReset} spuOptions={spuOptions} />

      <Card
        extra={
          <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
            新建 SKU
          </Button>
        }
      >
        <Table
          columns={columns}
          dataSource={dataSource}
          rowKey="id"
          loading={loading}
          scroll={{ x: 1180 }}
          pagination={{
            current: page,
            pageSize: pageSize,
            total: total,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (t) => `共 ${t} 条记录`,
            onChange: onPageChange,
          }}
        />
      </Card>

      <SkuFormModal
        visible={formModal.visible}
        mode={formModal.mode}
        data={formModal.data}
        spuOptions={spuOptions}
        onClose={formModal.close}
        onSuccess={refresh}
      />
    </PageContainer>
  );
};

export default SkuListPage;

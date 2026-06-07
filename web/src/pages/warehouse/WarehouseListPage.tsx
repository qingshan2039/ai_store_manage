/* ========================================
   仓库列表页面
   ======================================== */
import React from 'react';
import { Card, Table, Button, Space, Form, Switch, Tag, message } from 'antd';
import type { TableProps } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import PageContainer from '@/components/PageContainer';
import ConfirmAction from '@/components/ConfirmAction';
import { warehouseApi } from '@/api/warehouse';
import { useTable } from '@/hooks/useTable';
import { useModal } from '@/hooks/useModal';
import { STATUS, WAREHOUSE_TYPE_MAP } from '@/constants/enums';
import type { WarehouseSummary, WarehouseQueryParams, Warehouse } from '@/types/warehouse';
import WarehouseSearchForm from './components/WarehouseSearchForm';
import WarehouseFormModal from './components/WarehouseFormModal';
import { formatDateTime } from '@/utils/format';

const TYPE_COLOR: Record<string, string> = { RAW: 'blue', SEMI: 'orange', FINISHED: 'green' };

const WarehouseListPage: React.FC = () => {
  const [searchForm] = Form.useForm<WarehouseQueryParams>();

  const { dataSource, loading, total, page, pageSize, onPageChange, onSearch, onReset, refresh } =
    useTable<WarehouseSummary, WarehouseQueryParams>({
      fetchFn: warehouseApi.list,
      defaultParams: { page: 1, pageSize: 20 },
    });

  const formModal = useModal<Warehouse>();

  const handleCreate = () => formModal.open('create');

  const handleEdit = async (id: number) => {
    try {
      const res = await warehouseApi.getById(id);
      formModal.open('edit', res.data);
    } catch (e) {}
  };

  const handleDelete = async (id: number) => {
    try {
      await warehouseApi.delete(id);
      message.success('删除成功');
      refresh();
    } catch (e) {}
  };

  const handleStatusChange = async (id: number, checked: boolean) => {
    try {
      await warehouseApi.updateStatus(id, { status: checked ? STATUS.ENABLED : STATUS.DISABLED });
      message.success('状态变更成功');
      refresh();
    } catch (e) {}
  };

  const columns: TableProps<WarehouseSummary>['columns'] = [
    { title: '仓库编码', dataIndex: 'code', key: 'code', width: 120 },
    { title: '仓库名称', dataIndex: 'name', key: 'name', width: 200, ellipsis: true },
    {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      width: 120,
      render: (v: string) => <Tag color={TYPE_COLOR[v]}>{WAREHOUSE_TYPE_MAP[v] ?? v}</Tag>,
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
            title={`确定要删除仓库 [${record.name}] 吗？`}
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
    <PageContainer title="仓库管理">
      <WarehouseSearchForm form={searchForm} onSearch={onSearch} onReset={onReset} />

      <Card
        extra={
          <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
            新建仓库
          </Button>
        }
      >
        <Table
          columns={columns}
          dataSource={dataSource}
          rowKey="id"
          loading={loading}
          scroll={{ x: 1000 }}
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

      <WarehouseFormModal
        visible={formModal.visible}
        mode={formModal.mode}
        data={formModal.data}
        onClose={formModal.close}
        onSuccess={refresh}
      />
    </PageContainer>
  );
};

export default WarehouseListPage;

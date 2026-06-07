/* ========================================
   供应商列表页面
   ======================================== */
import React from 'react';
import { Card, Table, Button, Space, Form, Switch, message } from 'antd';
import type { TableProps } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import PageContainer from '@/components/PageContainer';
import ConfirmAction from '@/components/ConfirmAction';
import { supplierApi } from '@/api/supplier';
import { useTable } from '@/hooks/useTable';
import { useModal } from '@/hooks/useModal';
import { STATUS } from '@/constants/enums';
import type { SupplierSummary, SupplierQueryParams, Supplier } from '@/types/supplier';
import SupplierSearchForm from './components/SupplierSearchForm';
import SupplierFormModal from './components/SupplierFormModal';
import { formatDateTime } from '@/utils/format';

const SupplierListPage: React.FC = () => {
  const [searchForm] = Form.useForm<SupplierQueryParams>();

  const { dataSource, loading, total, page, pageSize, onPageChange, onSearch, onReset, refresh } =
    useTable<SupplierSummary, SupplierQueryParams>({
      fetchFn: supplierApi.list,
      defaultParams: { page: 1, pageSize: 20 },
    });

  const formModal = useModal<Supplier>();

  const handleCreate = () => formModal.open('create');

  const handleEdit = async (id: number) => {
    try {
      const res = await supplierApi.getById(id);
      formModal.open('edit', res.data);
    } catch (e) {}
  };

  const handleDelete = async (id: number) => {
    try {
      await supplierApi.delete(id);
      message.success('删除成功');
      refresh();
    } catch (e) {}
  };

  const handleStatusChange = async (id: number, checked: boolean) => {
    try {
      await supplierApi.updateStatus(id, { status: checked ? STATUS.ENABLED : STATUS.DISABLED });
      message.success('状态变更成功');
      refresh();
    } catch (e) {}
  };

  const columns: TableProps<SupplierSummary>['columns'] = [
    { title: '供应商编码', dataIndex: 'code', key: 'code', width: 120 },
    { title: '供应商名称', dataIndex: 'name', key: 'name', width: 200, ellipsis: true },
    { title: '地址', dataIndex: 'address', key: 'address', width: 240, ellipsis: true, render: (v) => v || '-' },
    { title: '联系人', dataIndex: 'contact', key: 'contact', width: 100, render: (v) => v || '-' },
    { title: '电话', dataIndex: 'phone', key: 'phone', width: 140, render: (v) => v || '-' },
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
            title={`确定要删除供应商 [${record.name}] 吗？`}
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
    <PageContainer title="供应商管理">
      <SupplierSearchForm form={searchForm} onSearch={onSearch} onReset={onReset} />

      <Card
        extra={
          <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
            新建供应商
          </Button>
        }
      >
        <Table
          columns={columns}
          dataSource={dataSource}
          rowKey="id"
          loading={loading}
          scroll={{ x: 1200 }}
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

      <SupplierFormModal
        visible={formModal.visible}
        mode={formModal.mode}
        data={formModal.data}
        onClose={formModal.close}
        onSuccess={refresh}
      />
    </PageContainer>
  );
};

export default SupplierListPage;

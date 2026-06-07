/* ========================================
   托盘类型列表页面（ISO 规格）
   ======================================== */
import React from 'react';
import { Card, Table, Button, Space, Form, Switch, message } from 'antd';
import type { TableProps } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import PageContainer from '@/components/PageContainer';
import ConfirmAction from '@/components/ConfirmAction';
import { palletTypeApi } from '@/api/pallet';
import { useTable } from '@/hooks/useTable';
import { useModal } from '@/hooks/useModal';
import { STATUS } from '@/constants/enums';
import type { PalletTypeSummary, PalletTypeQueryParams, PalletType } from '@/types/pallet';
import PalletTypeSearchForm from './components/PalletTypeSearchForm';
import PalletTypeFormModal from './components/PalletTypeFormModal';
import { formatDateTime } from '@/utils/format';

const PalletTypeListPage: React.FC = () => {
  const [searchForm] = Form.useForm<PalletTypeQueryParams>();

  const { dataSource, loading, total, page, pageSize, onPageChange, onSearch, onReset, refresh } =
    useTable<PalletTypeSummary, PalletTypeQueryParams>({
      fetchFn: palletTypeApi.list,
      defaultParams: { page: 1, pageSize: 20 },
    });

  const formModal = useModal<PalletType>();

  const handleCreate = () => formModal.open('create');

  const handleEdit = async (id: number) => {
    try {
      const res = await palletTypeApi.getById(id);
      formModal.open('edit', res.data);
    } catch (e) {}
  };

  const handleDelete = async (id: number) => {
    try {
      await palletTypeApi.delete(id);
      message.success('删除成功');
      refresh();
    } catch (e) {}
  };

  const handleStatusChange = async (id: number, checked: boolean) => {
    try {
      await palletTypeApi.updateStatus(id, { status: checked ? STATUS.ENABLED : STATUS.DISABLED });
      message.success('状态变更成功');
      refresh();
    } catch (e) {}
  };

  const columns: TableProps<PalletTypeSummary>['columns'] = [
    { title: '托盘编码', dataIndex: 'code', key: 'code', width: 120 },
    { title: '托盘名称', dataIndex: 'name', key: 'name', width: 200, ellipsis: true },
    {
      title: '规格(长×宽 mm)',
      key: 'size',
      width: 160,
      render: (_, r) => `${r.length} × ${r.width}`,
    },
    { title: '最大载重(kg)', dataIndex: 'maxLoad', key: 'maxLoad', width: 120, render: (v) => v ?? '-' },
    { title: '最大堆叠层', dataIndex: 'maxStack', key: 'maxStack', width: 110, render: (v) => v ?? '-' },
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
            title={`确定要删除托盘类型 [${record.name}] 吗？`}
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
    <PageContainer title="托盘类型管理">
      <PalletTypeSearchForm form={searchForm} onSearch={onSearch} onReset={onReset} />

      <Card
        extra={
          <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
            新建托盘类型
          </Button>
        }
      >
        <Table
          columns={columns}
          dataSource={dataSource}
          rowKey="id"
          loading={loading}
          scroll={{ x: 1130 }}
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

      <PalletTypeFormModal
        visible={formModal.visible}
        mode={formModal.mode}
        data={formModal.data}
        onClose={formModal.close}
        onSuccess={refresh}
      />
    </PageContainer>
  );
};

export default PalletTypeListPage;

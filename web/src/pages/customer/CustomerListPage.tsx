/* ========================================
   顾客列表页面
   ======================================== */
import React from 'react';
import { Card, Table, Button, Space, Form, Switch, message } from 'antd';
import type { TableProps } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import PageContainer from '@/components/PageContainer';
import ConfirmAction from '@/components/ConfirmAction';
import { customerApi } from '@/api/customer';
import { useTable } from '@/hooks/useTable';
import { useModal } from '@/hooks/useModal';
import { CUSTOMER_STATUS } from '@/constants/enums';
import type { CustomerSummary, CustomerQueryParams, Customer, ShipAddress } from '@/types/customer';
import CustomerSearchForm from './components/CustomerSearchForm';
import CustomerFormModal from './components/CustomerFormModal';
import { formatDateTime } from '@/utils/format';

const CustomerListPage: React.FC = () => {
  const [searchForm] = Form.useForm<CustomerQueryParams>();

  const {
    dataSource,
    loading,
    total,
    page,
    pageSize,
    onPageChange,
    onSearch,
    onReset,
    refresh,
  } = useTable<CustomerSummary, CustomerQueryParams>({
    fetchFn: customerApi.list,
    defaultParams: { page: 1, pageSize: 20 },
  });

  const formModal = useModal<Customer>();

  const handleCreate = () => formModal.open('create');

  const handleEdit = async (id: number) => {
    try {
      const res = await customerApi.getById(id);
      formModal.open('edit', res.data);
    } catch (e) {
      // 错误已在拦截器处理
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await customerApi.delete(id);
      message.success('删除成功');
      refresh();
    } catch (e) {}
  };

  const handleStatusChange = async (id: number, checked: boolean) => {
    try {
      const targetStatus = checked ? CUSTOMER_STATUS.ENABLED : CUSTOMER_STATUS.DISABLED;
      await customerApi.updateStatus(id, { status: targetStatus });
      message.success('状态变更成功');
      refresh();
    } catch (e) {}
  };

  const columns: TableProps<CustomerSummary>['columns'] = [
    { title: '客户编码', dataIndex: 'code', key: 'code', width: 120 },
    { title: '公司名称', dataIndex: 'name', key: 'name', width: 200, ellipsis: true },
    { title: '公司地址', dataIndex: 'address', key: 'address', width: 220, ellipsis: true, render: (v) => v || '-' },
    {
      title: '收/发货地址',
      dataIndex: 'shipAddresses',
      key: 'shipAddresses',
      width: 280,
      render: (addrs: ShipAddress[]) => {
        if (!addrs || addrs.length === 0) return '-';
        return (
          <Space direction="vertical" size={0} style={{ width: '100%' }}>
            {addrs.map((a, i) => (
              <span
                key={a.id ?? i}
                style={{
                  maxWidth: 260,
                  display: 'inline-block',
                  whiteSpace: 'nowrap',
                  overflow: 'hidden',
                  textOverflow: 'ellipsis',
                }}
              >
                {a.address}
                {a.remark ? <span style={{ color: 'rgba(0,0,0,0.45)' }}>（{a.remark}）</span> : null}
              </span>
            ))}
          </Space>
        );
      },
    },
    { title: '联系人', dataIndex: 'contact', key: 'contact', width: 100, render: (v) => v || '-' },
    { title: '电话', dataIndex: 'phone', key: 'phone', width: 140, render: (v) => v || '-' },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (val, record) => (
        <Switch
          checked={val === CUSTOMER_STATUS.ENABLED}
          onChange={(checked) => handleStatusChange(record.id, checked)}
          checkedChildren="启用"
          unCheckedChildren="禁用"
        />
      ),
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 160,
      render: (val) => formatDateTime(val),
    },
    {
      title: '操作',
      key: 'action',
      width: 140,
      fixed: 'right',
      render: (_, record) => (
        <Space size="middle">
          <a onClick={() => handleEdit(record.id)}>编辑</a>
          <ConfirmAction
            title={`确定要删除顾客 [${record.name}] 吗？`}
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
    <PageContainer title="顾客管理">
      <CustomerSearchForm form={searchForm} onSearch={onSearch} onReset={onReset} />

      <Card
        extra={
          <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
            新建顾客
          </Button>
        }
      >
        <Table
          columns={columns}
          dataSource={dataSource}
          rowKey="id"
          loading={loading}
          scroll={{ x: 1300 }}
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

      <CustomerFormModal
        visible={formModal.visible}
        mode={formModal.mode}
        data={formModal.data}
        onClose={formModal.close}
        onSuccess={refresh}
      />
    </PageContainer>
  );
};

export default CustomerListPage;

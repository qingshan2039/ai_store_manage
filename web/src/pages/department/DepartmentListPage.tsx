/* ========================================
   部门列表页面
   ======================================== */
import React from 'react';
import { Card, Table, Button, Space, Form, Switch, message } from 'antd';
import type { TableProps } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import PageContainer from '@/components/PageContainer';
import ConfirmAction from '@/components/ConfirmAction';
import { departmentApi } from '@/api/department';
import { useTable } from '@/hooks/useTable';
import { useModal } from '@/hooks/useModal';
import { DEPARTMENT_STATUS, DEPARTMENT_TYPE_MAP } from '@/constants/enums';
import type { DepartmentSummary, DepartmentQueryParams, Department } from '@/types/department';
import DepartmentSearchForm from './components/DepartmentSearchForm';
import DepartmentFormModal from './components/DepartmentFormModal';
import { formatDateTime } from '@/utils/format';

const DepartmentListPage: React.FC = () => {
  const [searchForm] = Form.useForm<DepartmentQueryParams>();

  // Table Hook
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
  } = useTable<DepartmentSummary, DepartmentQueryParams>({
    fetchFn: departmentApi.list,
    defaultParams: {
      page: 1,
      pageSize: 20,
    },
  });

  // Modal Hook
  const formModal = useModal<Department>();

  // Handlers
  const handleCreate = () => formModal.open('create');

  const handleEdit = async (id: number) => {
    try {
      // 列表只有 Summary，编辑前先获取完整详情
      const res = await departmentApi.getById(id);
      formModal.open('edit', res.data);
    } catch (e) {
      // 错误已在拦截器处理
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await departmentApi.delete(id);
      message.success('删除成功');
      refresh();
    } catch (e) {}
  };

  const handleStatusChange = async (id: number, checked: boolean) => {
    try {
      const targetStatus = checked ? DEPARTMENT_STATUS.ENABLED : DEPARTMENT_STATUS.DISABLED;
      await departmentApi.updateStatus(id, { status: targetStatus });
      message.success('状态变更成功');
      refresh();
    } catch (e) {}
  };

  // Table Columns
  const columns: TableProps<DepartmentSummary>['columns'] = [
    {
      title: '排序',
      dataIndex: 'sort',
      key: 'sort',
      width: 80,
    },
    {
      title: '部门名称',
      dataIndex: 'name',
      key: 'name',
      width: 160,
    },
    {
      title: '部门编码',
      dataIndex: 'code',
      key: 'code',
      width: 120,
    },
    {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      width: 140,
      render: (val) => DEPARTMENT_TYPE_MAP[val as string] || val,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (val, record) => (
        <Switch
          checked={val === DEPARTMENT_STATUS.ENABLED}
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
            title={`确定要删除部门 [${record.name}] 吗？`}
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
    <PageContainer title="部门管理">
      <DepartmentSearchForm form={searchForm} onSearch={onSearch} onReset={onReset} />

      <Card
        extra={
          <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
            新建部门
          </Button>
        }
      >
        <Table
          columns={columns}
          dataSource={dataSource}
          rowKey="id"
          loading={loading}
          scroll={{ x: 900 }}
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

      <DepartmentFormModal
        visible={formModal.visible}
        mode={formModal.mode}
        data={formModal.data}
        onClose={formModal.close}
        onSuccess={refresh}
      />
    </PageContainer>
  );
};

export default DepartmentListPage;

/* ========================================
   用户列表页面
   ======================================== */
import React, { useState } from 'react';
import { Card, Table, Button, Space, Form, Switch, message, Dropdown } from 'antd';
import type { TableProps, MenuProps } from 'antd';
import { PlusOutlined, MoreOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import PageContainer from '@/components/PageContainer';
import ConfirmAction from '@/components/ConfirmAction';
import { userApi } from '@/api/user';
import { useTable } from '@/hooks/useTable';
import { useModal } from '@/hooks/useModal';
import { USER_STATUS, GENDER_MAP } from '@/constants/enums';
import type { UserSummary, UserQueryParams, User } from '@/types/user';
import UserSearchForm from './components/UserSearchForm';
import UserFormModal from './components/UserFormModal';
import ResetPasswordModal from './components/ResetPasswordModal';
import { formatDateTime, maskPhone } from '@/utils/format';

const UserListPage: React.FC = () => {
  const navigate = useNavigate();
  const [searchForm] = Form.useForm<UserQueryParams>();
  
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
  } = useTable<UserSummary, UserQueryParams>({
    fetchFn: userApi.list,
    defaultParams: {
      page: 1,
      pageSize: 20,
    },
  });

  // Modal Hooks
  const formModal = useModal<User>();
  const [resetPwdUserId, setResetPwdUserId] = useState<number | null>(null);
  const [resetPwdVisible, setResetPwdVisible] = useState(false);

  // Handlers
  const handleCreate = () => formModal.open('create');
  
  const handleEdit = async (id: number) => {
    try {
      // 必须先获取详情再编辑，因为列表只有 Summary
      const res = await userApi.getById(id);
      formModal.open('edit', res.data);
    } catch (e) {
      // 错误已处理
    }
  };

  const handleView = (id: number) => {
    navigate(`/system/users/${id}`);
  };

  const handleDelete = async (id: number) => {
    try {
      await userApi.delete(id);
      message.success('删除成功');
      refresh();
    } catch (e) {}
  };

  const handleStatusChange = async (id: number, checked: boolean) => {
    try {
      const targetStatus = checked ? USER_STATUS.ENABLED : USER_STATUS.DISABLED;
      await userApi.updateStatus(id, { status: targetStatus });
      message.success('状态变更成功');
      refresh();
    } catch (e) {}
  };

  const openResetPwd = (id: number) => {
    setResetPwdUserId(id);
    setResetPwdVisible(true);
  };

  // Table Columns
  const columns: TableProps<UserSummary>['columns'] = [
    {
      title: '工号',
      dataIndex: 'employeeNo',
      key: 'employeeNo',
      width: 120,
    },
    {
      title: '登录账号',
      dataIndex: 'username',
      key: 'username',
      width: 120,
    },
    {
      title: '姓名',
      dataIndex: 'name',
      key: 'name',
      width: 100,
    },
    {
      title: '性别',
      dataIndex: 'gender',
      key: 'gender',
      width: 80,
      render: (val) => GENDER_MAP[val as number] || '未知',
    },
    {
      title: '手机号',
      dataIndex: 'phoneNumber',
      key: 'phoneNumber',
      width: 120,
      render: (val) => maskPhone(val),
    },
    {
      title: '部门',
      dataIndex: 'departmentName',
      key: 'departmentName',
      width: 120,
      render: (val) => val || '-',
    },
    {
      title: '职位',
      dataIndex: 'jobTitle',
      key: 'jobTitle',
      width: 120,
      render: (val) => val || '-',
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (val, record) => (
        <Switch 
          checked={val === USER_STATUS.ENABLED} 
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
      width: 180,
      fixed: 'right',
      render: (_, record) => {
        const moreItems: MenuProps['items'] = [
          {
            key: 'resetPwd',
            label: '重置密码',
            onClick: () => openResetPwd(record.id),
          },
          {
            key: 'delete',
            label: (
              <ConfirmAction
                title={`确定要删除用户 [${record.name}] 吗？`}
                onConfirm={() => handleDelete(record.id)}
                type="text"
                danger
                style={{ padding: 0, height: 'auto', background: 'transparent' }}
              >
                删除
              </ConfirmAction>
            ),
          },
        ];

        return (
          <Space size="middle">
            <a onClick={() => handleView(record.id)}>查看</a>
            <a onClick={() => handleEdit(record.id)}>编辑</a>
            <Dropdown menu={{ items: moreItems }} trigger={['click']}>
              <a onClick={(e) => e.preventDefault()}>
                <MoreOutlined />
              </a>
            </Dropdown>
          </Space>
        );
      },
    },
  ];

  return (
    <PageContainer title="用户管理">
      <UserSearchForm 
        form={searchForm} 
        onSearch={onSearch} 
        onReset={onReset} 
      />
      
      <Card
        extra={
          <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
            新建用户
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
            showTotal: (total) => `共 ${total} 条记录`,
            onChange: onPageChange,
          }}
        />
      </Card>

      <UserFormModal
        visible={formModal.visible}
        mode={formModal.mode}
        data={formModal.data}
        onClose={formModal.close}
        onSuccess={refresh}
      />

      <ResetPasswordModal
        visible={resetPwdVisible}
        userId={resetPwdUserId}
        onClose={() => {
          setResetPwdVisible(false);
          setResetPwdUserId(null);
        }}
      />
    </PageContainer>
  );
};

export default UserListPage;

/* ========================================
   用户详情页面
   ======================================== */
import React, { useEffect, useState } from 'react';
import { Card, Descriptions, Button, Space, Spin, Tag } from 'antd';
import { ArrowLeftOutlined, EditOutlined } from '@ant-design/icons';
import { useParams, useNavigate } from 'react-router-dom';
import PageContainer from '@/components/PageContainer';
import StatusTag from '@/components/StatusTag';
import { userApi } from '@/api/user';
import { USER_STATUS_MAP, GENDER_MAP } from '@/constants/enums';
import type { User } from '@/types/user';
import { formatDateTime, maskPhone } from '@/utils/format';
import UserFormModal from './components/UserFormModal';
import { useModal } from '@/hooks/useModal';

const UserDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [user, setUser] = useState<User | null>(null);

  const formModal = useModal<User>();

  const fetchUserDetail = async () => {
    if (!id) return;
    setLoading(true);
    try {
      const res = await userApi.getById(Number(id));
      setUser(res.data);
    } catch (e) {
      // 错误已统一处理，可以选择返回上一页
      setTimeout(() => navigate(-1), 1500);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUserDetail();
  }, [id]);

  const handleEdit = () => {
    if (user) {
      formModal.open('edit', user);
    }
  };

  const handleEditSuccess = () => {
    fetchUserDetail();
  };

  if (loading && !user) {
    return (
      <PageContainer title="用户详情">
        <Card style={{ textAlign: 'center', padding: '50px 0' }}>
          <Spin size="large" />
        </Card>
      </PageContainer>
    );
  }

  if (!user) {
    return null;
  }

  return (
    <PageContainer 
      title="用户详情"
      extra={
        <Space>
          <Button icon={<ArrowLeftOutlined />} onClick={() => navigate(-1)}>
            返回
          </Button>
          <Button type="primary" icon={<EditOutlined />} onClick={handleEdit}>
            编辑
          </Button>
        </Space>
      }
    >
      <Card title="基本信息" bordered={false} style={{ marginBottom: 16 }}>
        <Descriptions column={{ xxl: 4, xl: 3, lg: 3, md: 2, sm: 1, xs: 1 }}>
          <Descriptions.Item label="工号">{user.employeeNo}</Descriptions.Item>
          <Descriptions.Item label="登录账号">{user.username}</Descriptions.Item>
          <Descriptions.Item label="真实姓名">{user.name}</Descriptions.Item>
          <Descriptions.Item label="系统昵称">{user.nickname || '-'}</Descriptions.Item>
          <Descriptions.Item label="性别">{GENDER_MAP[user.gender] || '未知'}</Descriptions.Item>
          <Descriptions.Item label="手机号">{user.hidePhoneNumber ? maskPhone(user.phoneNumber) : user.phoneNumber}</Descriptions.Item>
          <Descriptions.Item label="企业邮箱">{user.email || '-'}</Descriptions.Item>
          <Descriptions.Item label="所属部门">{user.departmentName || '-'}</Descriptions.Item>
          <Descriptions.Item label="职位">{user.jobTitle || '-'}</Descriptions.Item>
          <Descriptions.Item label="账号状态">
            <StatusTag value={user.status} statusMap={USER_STATUS_MAP} />
          </Descriptions.Item>
          <Descriptions.Item label="隐私设置">
            <Space>
              {user.hideName && <Tag color="warning">隐藏姓名</Tag>}
              {user.hidePhoneNumber && <Tag color="warning">隐藏手机号</Tag>}
              {!user.hideName && !user.hidePhoneNumber && <span>-</span>}
            </Space>
          </Descriptions.Item>
        </Descriptions>
      </Card>

      <Card title="其他信息" bordered={false}>
        <Descriptions column={{ xxl: 4, xl: 3, lg: 3, md: 2, sm: 1, xs: 1 }}>
          <Descriptions.Item label="创建时间">{formatDateTime(user.createdAt)}</Descriptions.Item>
          <Descriptions.Item label="最后更新">{formatDateTime(user.updatedAt)}</Descriptions.Item>
          <Descriptions.Item label="备注" span={3}>{user.remark || '-'}</Descriptions.Item>
        </Descriptions>
      </Card>

      <UserFormModal
        visible={formModal.visible}
        mode={formModal.mode}
        data={formModal.data}
        onClose={formModal.close}
        onSuccess={handleEditSuccess}
      />
    </PageContainer>
  );
};

export default UserDetailPage;

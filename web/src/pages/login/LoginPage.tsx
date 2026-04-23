/* ========================================
   登录页
   ======================================== */
import React from 'react';
import { Form, Input, Button, Card, App } from 'antd';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '@/stores/useAuthStore';
import { APP_TITLE } from '@/constants/config';

interface LoginFormValues {
  username: string;
  password: string;
}

const LoginPage: React.FC = () => {
  const navigate = useNavigate();
  const login = useAuthStore((s) => s.login);
  const [loading, setLoading] = React.useState(false);
  const { message } = App.useApp();

  const handleLogin = async (values: LoginFormValues) => {
    setLoading(true);
    try {
      /**
       * MVP 阶段：模拟登录，直接存储 Token 和用户信息。
       * 后端认证接口完成后替换为真实 API 调用。
       */
      await new Promise((resolve) => setTimeout(resolve, 500));
      login('mock-jwt-token', {
        id: 1,
        username: values.username,
        name: '管理员',
        permissions: [],
      });
      message.success('登录成功');
      navigate('/', { replace: true });
    } catch {
      message.error('登录失败');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      style={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
      }}
    >
      <Card
        style={{
          width: 400,
          borderRadius: 12,
          boxShadow: '0 20px 60px rgba(0,0,0,0.3)',
        }}
        styles={{ body: { padding: 40 } }}
      >
        <div style={{ textAlign: 'center', marginBottom: 32 }}>
          <h1 style={{ fontSize: 24, fontWeight: 700, color: '#1a1a2e', margin: 0 }}>
            {APP_TITLE}
          </h1>
          <p style={{ color: '#888', marginTop: 8, fontSize: 14 }}>企业仓储管理平台</p>
        </div>

        <Form<LoginFormValues>
          name="login"
          onFinish={handleLogin}
          size="large"
          initialValues={{ username: 'admin', password: 'Pass1234' }}
        >
          <Form.Item
            name="username"
            rules={[{ required: true, message: '请输入用户名' }]}
          >
            <Input prefix={<UserOutlined />} placeholder="用户名" />
          </Form.Item>

          <Form.Item
            name="password"
            rules={[{ required: true, message: '请输入密码' }]}
          >
            <Input.Password prefix={<LockOutlined />} placeholder="密码" />
          </Form.Item>

          <Form.Item style={{ marginBottom: 0 }}>
            <Button type="primary" htmlType="submit" loading={loading} block>
              登 录
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
};

export default LoginPage;

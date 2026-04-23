/* ========================================
   重置密码弹窗
   ======================================== */
import React, { useEffect } from 'react';
import { Modal, Form, Input, message } from 'antd';
import { userApi } from '@/api/user';

interface ResetPasswordModalProps {
  visible: boolean;
  userId: number | null;
  onClose: () => void;
}

const ResetPasswordModal: React.FC<ResetPasswordModalProps> = ({
  visible,
  userId,
  onClose,
}) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = React.useState(false);

  useEffect(() => {
    if (visible) {
      form.resetFields();
    }
  }, [visible, form]);

  const handleSubmit = async () => {
    if (!userId) return;
    
    try {
      const values = await form.validateFields();
      setLoading(true);
      await userApi.resetPassword(userId, { newPassword: values.newPassword });
      message.success('密码重置成功');
      onClose();
    } catch (error) {
      // 错误已经在 axios 拦截器中处理过了
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      title="重置密码"
      open={visible}
      onCancel={onClose}
      onOk={handleSubmit}
      confirmLoading={loading}
      destroyOnClose
    >
      <Form form={form} layout="vertical">
        <Form.Item
          name="newPassword"
          label="新密码"
          rules={[
            { required: true, message: '请输入新密码' },
            { min: 8, max: 32, message: '密码长度在 8-32 个字符' },
            { 
              pattern: /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d@$!%*#?&]{8,32}$/,
              message: '密码必须包含字母和数字' 
            }
          ]}
        >
          <Input.Password placeholder="请输入新密码" />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default ResetPasswordModal;

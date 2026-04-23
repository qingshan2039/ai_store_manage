/* ========================================
   用户新增/编辑弹窗
   ======================================== */
import React, { useEffect } from 'react';
import { Modal, Form, Input, Select, Switch, Row, Col, message } from 'antd';
import { userApi } from '@/api/user';
import { USER_STATUS_OPTIONS, GENDER_OPTIONS } from '@/constants/enums';
import type { User, CreateUserRequest, UpdateUserRequest } from '@/types/user';
import type { ModalMode } from '@/types/common';

const { TextArea } = Input;

interface UserFormModalProps {
  visible: boolean;
  mode: ModalMode;
  data: User | null;
  onClose: () => void;
  onSuccess: () => void;
}

const UserFormModal: React.FC<UserFormModalProps> = ({
  visible,
  mode,
  data,
  onClose,
  onSuccess,
}) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = React.useState(false);
  const isEdit = mode === 'edit';

  useEffect(() => {
    if (visible) {
      if (isEdit && data) {
        form.setFieldsValue({
          ...data,
          // departmentId may need special handling if it's an object from select, but here it's just a number
        });
      } else {
        form.resetFields();
        form.setFieldsValue({
          status: 1,
          gender: 0,
          hideName: false,
          hidePhoneNumber: false,
        });
      }
    }
  }, [visible, mode, data, form, isEdit]);

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      setLoading(true);

      if (isEdit && data) {
        // 更新逻辑
        const updateData: UpdateUserRequest = {
          name: values.name,
          nickname: values.nickname,
          gender: values.gender,
          phoneNumber: values.phoneNumber,
          email: values.email,
          jobTitle: values.jobTitle,
          departmentId: values.departmentId,
          hidePhoneNumber: values.hidePhoneNumber,
          hideName: values.hideName,
          remark: values.remark,
        };
        await userApi.update(data.id, updateData);
        message.success('更新用户成功');
      } else {
        // 创建逻辑
        const createData: CreateUserRequest = {
          employeeNo: values.employeeNo,
          username: values.username,
          password: values.password,
          name: values.name,
          phoneNumber: values.phoneNumber,
          nickname: values.nickname,
          gender: values.gender,
          email: values.email,
          jobTitle: values.jobTitle,
          departmentId: values.departmentId,
          hidePhoneNumber: values.hidePhoneNumber,
          hideName: values.hideName,
          remark: values.remark,
          status: values.status,
        };
        await userApi.create(createData);
        message.success('创建用户成功');
      }
      
      onSuccess();
      onClose();
    } catch (error) {
      // 错误已经在 axios 拦截器中处理过了
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      title={isEdit ? '编辑用户' : '新增用户'}
      open={visible}
      onCancel={onClose}
      onOk={handleSubmit}
      confirmLoading={loading}
      width={680}
      destroyOnClose
      maskClosable={false}
    >
      <Form form={form} layout="vertical">
        <Row gutter={16}>
          <Col span={12}>
            <Form.Item
              name="employeeNo"
              label="工号"
              rules={[{ required: !isEdit, message: '请输入工号' }]}
            >
              <Input placeholder="HR 分配的工号" disabled={isEdit} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item
              name="username"
              label="登录账号"
              rules={[
                { required: !isEdit, message: '请输入登录账号' },
                { min: 4, max: 64, message: '长度在 4-64 个字符之间' }
              ]}
            >
              <Input placeholder="登录系统的账号" disabled={isEdit} />
            </Form.Item>
          </Col>
        </Row>

        {!isEdit && (
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="password"
                label="初始密码"
                rules={[
                  { required: true, message: '请输入初始密码' },
                  { min: 8, max: 32, message: '密码长度在 8-32 个字符' },
                  { 
                    pattern: /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d@$!%*#?&]{8,32}$/,
                    message: '密码必须包含字母和数字' 
                  }
                ]}
              >
                <Input.Password placeholder="密码（至少包含字母和数字）" />
              </Form.Item>
            </Col>
          </Row>
        )}

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item
              name="name"
              label="真实姓名"
              rules={[{ required: true, message: '请输入真实姓名' }]}
            >
              <Input placeholder="员工真实姓名" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="nickname" label="系统昵称">
              <Input placeholder="系统内显示名称（选填）" />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item
              name="phoneNumber"
              label="手机号"
              rules={[
                { required: true, message: '请输入手机号' },
                { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号' }
              ]}
            >
              <Input placeholder="11 位手机号码" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item
              name="email"
              label="企业邮箱"
              rules={[{ type: 'email', message: '请输入有效的邮箱地址' }]}
            >
              <Input placeholder="企业邮箱（选填）" />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="jobTitle" label="职位">
              <Input placeholder="职位名称" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="departmentId" label="所属部门">
              <Select placeholder="请选择部门（当前阶段选填）">
                {/* 后续补充部门列表接口 */}
                <Select.Option value={1}>一号仓库</Select.Option>
                <Select.Option value={2}>二号仓库</Select.Option>
              </Select>
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="gender" label="性别" initialValue={0}>
              <Select options={GENDER_OPTIONS} />
            </Form.Item>
          </Col>
          {!isEdit && (
            <Col span={12}>
              <Form.Item name="status" label="状态" initialValue={1}>
                <Select options={USER_STATUS_OPTIONS} />
              </Form.Item>
            </Col>
          )}
        </Row>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="hideName" label="隐藏姓名" valuePropName="checked">
              <Switch checkedChildren="隐藏" unCheckedChildren="公开" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="hidePhoneNumber" label="隐藏手机号" valuePropName="checked">
              <Switch checkedChildren="隐藏" unCheckedChildren="公开" />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={24}>
            <Form.Item name="remark" label="备注">
              <TextArea rows={3} placeholder="管理员备注信息" maxLength={500} showCount />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  );
};

export default UserFormModal;

/* ========================================
   顾客新增/编辑弹窗
   ======================================== */
import React, { useEffect } from 'react';
import { Modal, Form, Input, Select, Row, Col, message } from 'antd';
import { customerApi } from '@/api/customer';
import { CUSTOMER_STATUS_OPTIONS } from '@/constants/enums';
import type { Customer, CreateCustomerRequest, UpdateCustomerRequest } from '@/types/customer';
import type { ModalMode } from '@/types/common';

const { TextArea } = Input;

interface CustomerFormModalProps {
  visible: boolean;
  mode: ModalMode;
  data: Customer | null;
  onClose: () => void;
  onSuccess: () => void;
}

const CustomerFormModal: React.FC<CustomerFormModalProps> = ({
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
        form.setFieldsValue({ ...data });
      } else {
        form.resetFields();
        form.setFieldsValue({ status: 1 });
      }
    }
  }, [visible, mode, data, form, isEdit]);

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      setLoading(true);

      if (isEdit && data) {
        // 更新逻辑（code 不可改，状态走独立接口）
        const updateData: UpdateCustomerRequest = {
          name: values.name,
          address: values.address,
          shipAddress: values.shipAddress,
          contact: values.contact,
          phone: values.phone,
          email: values.email,
          remark: values.remark,
        };
        await customerApi.update(data.id, updateData);
        message.success('更新顾客成功');
      } else {
        const createData: CreateCustomerRequest = {
          code: values.code,
          name: values.name,
          address: values.address,
          shipAddress: values.shipAddress,
          contact: values.contact,
          phone: values.phone,
          email: values.email,
          remark: values.remark,
          status: values.status,
        };
        await customerApi.create(createData);
        message.success('创建顾客成功');
      }

      onSuccess();
      onClose();
    } catch (error) {
      // 校验失败或接口错误已处理
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      title={isEdit ? '编辑顾客' : '新增顾客'}
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
              name="code"
              label="客户编码"
              rules={[
                { required: !isEdit, message: '请输入客户编码' },
                { pattern: /^[A-Za-z0-9-]+$/, message: '只能包含字母、数字和连字符' },
              ]}
            >
              <Input placeholder="客户编码（创建后不可修改）" disabled={isEdit} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item
              name="name"
              label="公司名称"
              rules={[
                { required: true, message: '请输入公司名称' },
                { min: 2, max: 128, message: '长度在 2-128 个字符之间' },
              ]}
            >
              <Input placeholder="客户公司名称" />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={24}>
            <Form.Item
              name="address"
              label="公司地址"
              rules={[{ required: true, message: '请输入公司地址' }]}
            >
              <Input placeholder="客户公司地址（注册/账单地址）" />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={24}>
            <Form.Item
              name="shipAddress"
              label="收/发货地址"
              rules={[{ required: true, message: '请输入收/发货地址' }]}
            >
              <Input placeholder="ship-to 收/发货地址" />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="contact" label="联系人">
              <Input placeholder="联系人姓名" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="phone" label="联系电话">
              <Input placeholder="联系电话" />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item
              name="email"
              label="邮箱"
              rules={[{ type: 'email', message: '请输入有效的邮箱地址' }]}
            >
              <Input placeholder="邮箱（选填）" />
            </Form.Item>
          </Col>
          {!isEdit && (
            <Col span={12}>
              <Form.Item name="status" label="状态" initialValue={1}>
                <Select options={CUSTOMER_STATUS_OPTIONS} />
              </Form.Item>
            </Col>
          )}
        </Row>

        <Row gutter={16}>
          <Col span={24}>
            <Form.Item name="remark" label="备注">
              <TextArea rows={3} placeholder="备注信息（选填）" maxLength={500} showCount />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  );
};

export default CustomerFormModal;

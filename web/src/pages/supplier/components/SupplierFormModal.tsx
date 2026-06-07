/* ========================================
   供应商新增/编辑弹窗
   ======================================== */
import React, { useEffect } from 'react';
import { Modal, Form, Input, Select, Row, Col, message } from 'antd';
import { supplierApi } from '@/api/supplier';
import { STATUS_OPTIONS } from '@/constants/enums';
import type {
  Supplier,
  CreateSupplierRequest,
  UpdateSupplierRequest,
} from '@/types/supplier';
import type { ModalMode } from '@/types/common';

const { TextArea } = Input;

interface SupplierFormModalProps {
  visible: boolean;
  mode: ModalMode;
  data: Supplier | null;
  onClose: () => void;
  onSuccess: () => void;
}

const SupplierFormModal: React.FC<SupplierFormModalProps> = ({ visible, mode, data, onClose, onSuccess }) => {
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
        const updateData: UpdateSupplierRequest = {
          name: values.name,
          address: values.address,
          contact: values.contact,
          phone: values.phone,
          email: values.email,
          remark: values.remark,
        };
        await supplierApi.update(data.id, updateData);
        message.success('更新供应商成功');
      } else {
        const createData: CreateSupplierRequest = {
          code: values.code,
          name: values.name,
          address: values.address,
          contact: values.contact,
          phone: values.phone,
          email: values.email,
          remark: values.remark,
          status: values.status,
        };
        await supplierApi.create(createData);
        message.success('创建供应商成功');
      }

      onSuccess();
      onClose();
    } catch (error) {
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      title={isEdit ? '编辑供应商' : '新增供应商'}
      open={visible}
      onCancel={onClose}
      onOk={handleSubmit}
      confirmLoading={loading}
      width={720}
      destroyOnClose
      maskClosable={false}
    >
      <Form form={form} layout="vertical">
        <Row gutter={16}>
          <Col span={12}>
            <Form.Item
              name="code"
              label="供应商编码"
              rules={[
                { required: !isEdit, message: '请输入供应商编码' },
                { pattern: /^[A-Za-z0-9-]+$/, message: '只能包含字母、数字和连字符' },
              ]}
            >
              <Input placeholder="供应商编码（创建后不可修改）" disabled={isEdit} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item
              name="name"
              label="供应商名称"
              rules={[
                { required: true, message: '请输入供应商名称' },
                { min: 2, max: 128, message: '长度在 2-128 个字符之间' },
              ]}
            >
              <Input placeholder="供应商名称" />
            </Form.Item>
          </Col>
        </Row>

        <Form.Item name="address" label="地址" rules={[{ required: true, message: '请输入地址' }]}>
          <Input placeholder="供应商地址" />
        </Form.Item>

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
            <Form.Item name="email" label="邮箱" rules={[{ type: 'email', message: '请输入有效的邮箱地址' }]}>
              <Input placeholder="邮箱（选填）" />
            </Form.Item>
          </Col>
          {!isEdit && (
            <Col span={12}>
              <Form.Item name="status" label="状态" initialValue={1}>
                <Select options={STATUS_OPTIONS} />
              </Form.Item>
            </Col>
          )}
        </Row>

        <Form.Item name="remark" label="备注">
          <TextArea rows={2} placeholder="备注信息（选填）" maxLength={500} showCount />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default SupplierFormModal;

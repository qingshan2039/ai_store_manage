/* ========================================
   物料品类新增/编辑弹窗
   ======================================== */
import React, { useEffect } from 'react';
import { Modal, Form, Input, InputNumber, Select, Row, Col, message } from 'antd';
import { materialCategoryApi } from '@/api/materialCategory';
import { STATUS_OPTIONS } from '@/constants/enums';
import type {
  MaterialCategory,
  CreateMaterialCategoryRequest,
  UpdateMaterialCategoryRequest,
} from '@/types/materialCategory';
import type { ModalMode } from '@/types/common';

interface Props {
  visible: boolean;
  mode: ModalMode;
  data: MaterialCategory | null;
  onClose: () => void;
  onSuccess: () => void;
}

const MaterialCategoryFormModal: React.FC<Props> = ({ visible, mode, data, onClose, onSuccess }) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = React.useState(false);
  const isEdit = mode === 'edit';

  useEffect(() => {
    if (visible) {
      if (isEdit && data) {
        form.setFieldsValue({ ...data });
      } else {
        form.resetFields();
        form.setFieldsValue({ status: 1, sortOrder: 0 });
      }
    }
  }, [visible, mode, data, form, isEdit]);

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      setLoading(true);

      if (isEdit && data) {
        const updateData: UpdateMaterialCategoryRequest = {
          name: values.name,
          sortOrder: values.sortOrder,
        };
        await materialCategoryApi.update(data.id, updateData);
        message.success('更新品类成功');
      } else {
        const createData: CreateMaterialCategoryRequest = {
          code: values.code,
          name: values.name,
          sortOrder: values.sortOrder,
          status: values.status,
        };
        await materialCategoryApi.create(createData);
        message.success('创建品类成功');
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
      title={isEdit ? '编辑品类' : '新增品类'}
      open={visible}
      onCancel={onClose}
      onOk={handleSubmit}
      confirmLoading={loading}
      width={560}
      destroyOnClose
      maskClosable={false}
    >
      <Form form={form} layout="vertical">
        <Row gutter={16}>
          <Col span={12}>
            <Form.Item
              name="code"
              label="品类编码"
              rules={[
                { required: !isEdit, message: '请输入品类编码' },
                { pattern: /^[A-Za-z0-9-]+$/, message: '只能包含字母、数字和连字符' },
              ]}
            >
              <Input placeholder="如 CORE（创建后不可改）" disabled={isEdit} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item
              name="name"
              label="品类名称"
              rules={[
                { required: true, message: '请输入品类名称' },
                { min: 1, max: 64, message: '长度在 1-64 个字符之间' },
              ]}
            >
              <Input placeholder="如 纸管" />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="sortOrder" label="排序（小在前）">
              <InputNumber min={0} precision={0} style={{ width: '100%' }} placeholder="0" />
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
      </Form>
    </Modal>
  );
};

export default MaterialCategoryFormModal;

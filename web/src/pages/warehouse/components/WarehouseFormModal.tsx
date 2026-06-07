/* ========================================
   仓库新增/编辑弹窗
   ======================================== */
import React, { useEffect } from 'react';
import { Modal, Form, Input, Select, Row, Col, message } from 'antd';
import { warehouseApi } from '@/api/warehouse';
import { STATUS_OPTIONS, WAREHOUSE_TYPE_OPTIONS } from '@/constants/enums';
import type {
  Warehouse,
  CreateWarehouseRequest,
  UpdateWarehouseRequest,
} from '@/types/warehouse';
import type { ModalMode } from '@/types/common';

const { TextArea } = Input;

interface WarehouseFormModalProps {
  visible: boolean;
  mode: ModalMode;
  data: Warehouse | null;
  onClose: () => void;
  onSuccess: () => void;
}

const WarehouseFormModal: React.FC<WarehouseFormModalProps> = ({ visible, mode, data, onClose, onSuccess }) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = React.useState(false);
  const isEdit = mode === 'edit';

  useEffect(() => {
    if (visible) {
      if (isEdit && data) {
        form.setFieldsValue({ ...data });
      } else {
        form.resetFields();
        form.setFieldsValue({ status: 1, type: 'RAW' });
      }
    }
  }, [visible, mode, data, form, isEdit]);

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      setLoading(true);

      if (isEdit && data) {
        const updateData: UpdateWarehouseRequest = {
          name: values.name,
          type: values.type,
          remark: values.remark,
        };
        await warehouseApi.update(data.id, updateData);
        message.success('更新仓库成功');
      } else {
        const createData: CreateWarehouseRequest = {
          code: values.code,
          name: values.name,
          type: values.type,
          remark: values.remark,
          status: values.status,
        };
        await warehouseApi.create(createData);
        message.success('创建仓库成功');
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
      title={isEdit ? '编辑仓库' : '新增仓库'}
      open={visible}
      onCancel={onClose}
      onOk={handleSubmit}
      confirmLoading={loading}
      width={640}
      destroyOnClose
      maskClosable={false}
    >
      <Form form={form} layout="vertical">
        <Row gutter={16}>
          <Col span={12}>
            <Form.Item
              name="code"
              label="仓库编码"
              rules={[
                { required: !isEdit, message: '请输入仓库编码' },
                { pattern: /^[A-Za-z0-9-]+$/, message: '只能包含字母、数字和连字符' },
              ]}
            >
              <Input placeholder="仓库编码（创建后不可修改）" disabled={isEdit} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item
              name="name"
              label="仓库名称"
              rules={[
                { required: true, message: '请输入仓库名称' },
                { min: 2, max: 128, message: '长度在 2-128 个字符之间' },
              ]}
            >
              <Input placeholder="仓库名称" />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="type" label="仓库类型" rules={[{ required: true, message: '请选择仓库类型' }]}>
              <Select options={WAREHOUSE_TYPE_OPTIONS} placeholder="请选择仓库类型" />
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

export default WarehouseFormModal;

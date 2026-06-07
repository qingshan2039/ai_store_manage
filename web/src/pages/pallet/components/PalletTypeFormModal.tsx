/* ========================================
   托盘类型新增/编辑弹窗（ISO 规格）
   ======================================== */
import React, { useEffect } from 'react';
import { Modal, Form, Input, InputNumber, Select, Row, Col, message } from 'antd';
import { palletTypeApi } from '@/api/pallet';
import { STATUS_OPTIONS } from '@/constants/enums';
import type {
  PalletType,
  CreatePalletTypeRequest,
  UpdatePalletTypeRequest,
} from '@/types/pallet';
import type { ModalMode } from '@/types/common';

const { TextArea } = Input;

interface PalletTypeFormModalProps {
  visible: boolean;
  mode: ModalMode;
  data: PalletType | null;
  onClose: () => void;
  onSuccess: () => void;
}

const PalletTypeFormModal: React.FC<PalletTypeFormModalProps> = ({ visible, mode, data, onClose, onSuccess }) => {
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
        const updateData: UpdatePalletTypeRequest = {
          name: values.name,
          length: values.length,
          width: values.width,
          tareWeight: values.tareWeight,
          maxLoad: values.maxLoad,
          maxStack: values.maxStack,
          remark: values.remark,
        };
        await palletTypeApi.update(data.id, updateData);
        message.success('更新托盘类型成功');
      } else {
        const createData: CreatePalletTypeRequest = {
          code: values.code,
          name: values.name,
          length: values.length,
          width: values.width,
          tareWeight: values.tareWeight,
          maxLoad: values.maxLoad,
          maxStack: values.maxStack,
          remark: values.remark,
          status: values.status,
        };
        await palletTypeApi.create(createData);
        message.success('创建托盘类型成功');
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
      title={isEdit ? '编辑托盘类型' : '新增托盘类型'}
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
              label="托盘编码"
              rules={[
                { required: !isEdit, message: '请输入托盘编码' },
                { pattern: /^[A-Za-z0-9-]+$/, message: '只能包含字母、数字和连字符' },
              ]}
            >
              <Input placeholder="托盘编码（创建后不可修改）" disabled={isEdit} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item
              name="name"
              label="托盘名称"
              rules={[
                { required: true, message: '请输入托盘名称' },
                { min: 1, max: 64, message: '长度在 1-64 个字符之间' },
              ]}
            >
              <Input placeholder="如 大托盘 1200×1000" />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="length" label="长 (mm)" rules={[{ required: true, message: '请输入长度' }]}>
              <InputNumber min={0} style={{ width: '100%' }} placeholder="如 1200" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="width" label="宽 (mm)" rules={[{ required: true, message: '请输入宽度' }]}>
              <InputNumber min={0} style={{ width: '100%' }} placeholder="如 1000" />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={8}>
            <Form.Item name="tareWeight" label="皮重 (kg)">
              <InputNumber min={0} style={{ width: '100%' }} placeholder="选填" />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="maxLoad" label="最大载重 (kg)">
              <InputNumber min={0} style={{ width: '100%' }} placeholder="选填" />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="maxStack" label="最大堆叠层">
              <InputNumber min={1} precision={0} style={{ width: '100%' }} placeholder="选填" />
            </Form.Item>
          </Col>
        </Row>

        {!isEdit && (
          <Form.Item name="status" label="状态" initialValue={1} style={{ maxWidth: 240 }}>
            <Select options={STATUS_OPTIONS} />
          </Form.Item>
        )}

        <Form.Item name="remark" label="备注">
          <TextArea rows={2} placeholder="备注信息（选填）" maxLength={500} showCount />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default PalletTypeFormModal;

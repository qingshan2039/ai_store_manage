/* 计量换算新增/编辑弹窗 */
import React, { useEffect } from 'react';
import { Modal, Form, Input, InputNumber, Select, Row, Col, message } from 'antd';
import { unitConversionApi } from '@/api/unitConversion';
import { STATUS_OPTIONS } from '@/constants/enums';
import type { UnitConversion, CreateUnitConversionRequest, UpdateUnitConversionRequest } from '@/types/unitConversion';
import type { ModalMode } from '@/types/common';
import type { SkuOption } from '../UnitConversionListPage';

interface Props {
  visible: boolean;
  mode: ModalMode;
  data: UnitConversion | null;
  skuOptions: SkuOption[];
  onClose: () => void;
  onSuccess: () => void;
}

const UnitConversionFormModal: React.FC<Props> = ({ visible, mode, data, skuOptions, onClose, onSuccess }) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = React.useState(false);
  const isEdit = mode === 'edit';

  useEffect(() => {
    if (visible) {
      if (isEdit && data) form.setFieldsValue({ ...data });
      else {
        form.resetFields();
        form.setFieldsValue({ status: 1 });
      }
    }
  }, [visible, mode, data, form, isEdit]);

  const handleSubmit = async () => {
    try {
      const v = await form.validateFields();
      setLoading(true);
      if (isEdit && data) {
        const d: UpdateUnitConversionRequest = { factor: v.factor };
        await unitConversionApi.update(data.id, d);
        message.success('更新成功');
      } else {
        const d: CreateUnitConversionRequest = { skuId: v.skuId, fromUnit: v.fromUnit, toUnit: v.toUnit, factor: v.factor, status: v.status };
        await unitConversionApi.create(d);
        message.success('创建成功');
      }
      onSuccess();
      onClose();
    } catch (e) {} finally {
      setLoading(false);
    }
  };

  return (
    <Modal title={isEdit ? '编辑计量换算' : '新增计量换算'} open={visible} onCancel={onClose} onOk={handleSubmit} confirmLoading={loading} width={600} destroyOnClose maskClosable={false}>
      <Form form={form} layout="vertical">
        <Form.Item name="skuId" label="所属 SKU" rules={[{ required: true, message: '请选择 SKU' }]}>
          <Select options={skuOptions} placeholder="选择 SKU（创建后不可改）" disabled={isEdit} showSearch optionFilterProp="label" />
        </Form.Item>
        <Row gutter={16}>
          <Col span={8}>
            <Form.Item name="fromUnit" label="源单位" rules={[{ required: !isEdit, message: '请输入' }]}>
              <Input placeholder="如 ROLL" disabled={isEdit} />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="toUnit" label="目标单位" rules={[{ required: !isEdit, message: '请输入' }]}>
              <Input placeholder="如 M2" disabled={isEdit} />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="factor" label="换算系数" rules={[{ required: true, message: '请输入' }]}>
              <InputNumber min={0} style={{ width: '100%' }} placeholder="如 300" />
            </Form.Item>
          </Col>
        </Row>
        {!isEdit && (
          <Form.Item name="status" label="状态" initialValue={1} style={{ maxWidth: 220 }}>
            <Select options={STATUS_OPTIONS} />
          </Form.Item>
        )}
      </Form>
    </Modal>
  );
};

export default UnitConversionFormModal;

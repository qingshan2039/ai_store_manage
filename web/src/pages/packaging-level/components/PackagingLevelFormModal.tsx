/* 包装层级新增/编辑弹窗 */
import React, { useEffect } from 'react';
import { Modal, Form, Input, InputNumber, Select, Row, Col, message } from 'antd';
import { packagingLevelApi } from '@/api/packagingLevel';
import { STATUS_OPTIONS, YES_NO_OPTIONS } from '@/constants/enums';
import type { PackagingLevel, CreatePackagingLevelRequest, UpdatePackagingLevelRequest } from '@/types/packagingLevel';
import type { ModalMode } from '@/types/common';
import type { SkuOption } from '../PackagingLevelListPage';

interface Props {
  visible: boolean;
  mode: ModalMode;
  data: PackagingLevel | null;
  skuOptions: SkuOption[];
  onClose: () => void;
  onSuccess: () => void;
}

const PackagingLevelFormModal: React.FC<Props> = ({ visible, mode, data, skuOptions, onClose, onSuccess }) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = React.useState(false);
  const isEdit = mode === 'edit';

  useEffect(() => {
    if (visible) {
      if (isEdit && data) form.setFieldsValue({ ...data });
      else {
        form.resetFields();
        form.setFieldsValue({ status: 1, isBaseUnit: 0, isSellable: 0 });
      }
    }
  }, [visible, mode, data, form, isEdit]);

  const handleSubmit = async () => {
    try {
      const v = await form.validateFields();
      setLoading(true);
      if (isEdit && data) {
        const d: UpdatePackagingLevelRequest = {
          levelName: v.levelName, unitCode: v.unitCode, length: v.length, width: v.width, height: v.height,
          netWeight: v.netWeight, grossWeight: v.grossWeight, isBaseUnit: v.isBaseUnit, isSellable: v.isSellable,
        };
        await packagingLevelApi.update(data.id, d);
        message.success('更新成功');
      } else {
        const d: CreatePackagingLevelRequest = {
          skuId: v.skuId, levelName: v.levelName, levelSeq: v.levelSeq, unitCode: v.unitCode,
          length: v.length, width: v.width, height: v.height, netWeight: v.netWeight, grossWeight: v.grossWeight,
          isBaseUnit: v.isBaseUnit, isSellable: v.isSellable, status: v.status,
        };
        await packagingLevelApi.create(d);
        message.success('创建成功');
      }
      onSuccess();
      onClose();
    } catch (e) {} finally {
      setLoading(false);
    }
  };

  return (
    <Modal title={isEdit ? '编辑包装层级' : '新增包装层级'} open={visible} onCancel={onClose} onOk={handleSubmit} confirmLoading={loading} width={680} destroyOnClose maskClosable={false}>
      <Form form={form} layout="vertical">
        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="skuId" label="所属 SKU" rules={[{ required: true, message: '请选择 SKU' }]}>
              <Select options={skuOptions} placeholder="请选择 SKU（创建后不可改）" disabled={isEdit} showSearch optionFilterProp="label" />
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item name="levelName" label="层级名称" rules={[{ required: true, message: '请输入' }]}>
              <Input placeholder="卷/箱/托" />
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item name="levelSeq" label="层级序号" rules={[{ required: true, message: '请输入' }]}>
              <InputNumber min={1} precision={0} style={{ width: '100%' }} placeholder="1=基本单位" disabled={isEdit} />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={16}>
          <Col span={6}>
            <Form.Item name="unitCode" label="单位" rules={[{ required: true, message: '请输入' }]}>
              <Input placeholder="ROLL/CTN/PLT" />
            </Form.Item>
          </Col>
          <Col span={6}><Form.Item name="length" label="长(mm)"><InputNumber min={0} style={{ width: '100%' }} /></Form.Item></Col>
          <Col span={6}><Form.Item name="width" label="宽(mm)"><InputNumber min={0} style={{ width: '100%' }} /></Form.Item></Col>
          <Col span={6}><Form.Item name="height" label="高(mm)"><InputNumber min={0} style={{ width: '100%' }} /></Form.Item></Col>
        </Row>
        <Row gutter={16}>
          <Col span={6}><Form.Item name="netWeight" label="净重(kg)"><InputNumber min={0} style={{ width: '100%' }} /></Form.Item></Col>
          <Col span={6}><Form.Item name="grossWeight" label="毛重(kg)"><InputNumber min={0} style={{ width: '100%' }} /></Form.Item></Col>
          <Col span={6}><Form.Item name="isBaseUnit" label="基本单位" initialValue={0}><Select options={YES_NO_OPTIONS} /></Form.Item></Col>
          <Col span={6}><Form.Item name="isSellable" label="可售" initialValue={0}><Select options={YES_NO_OPTIONS} /></Form.Item></Col>
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

export default PackagingLevelFormModal;

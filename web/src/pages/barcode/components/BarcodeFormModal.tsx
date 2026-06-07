/* 条码新增/编辑弹窗 */
import React, { useEffect } from 'react';
import { Modal, Form, Input, Select, Row, Col, message } from 'antd';
import { barcodeApi } from '@/api/barcode';
import { STATUS_OPTIONS, BARCODE_TYPE_OPTIONS, YES_NO_OPTIONS } from '@/constants/enums';
import type { Barcode, CreateBarcodeRequest, UpdateBarcodeRequest } from '@/types/barcode';
import type { ModalMode } from '@/types/common';
import type { LevelOption } from '../BarcodeListPage';

interface Props {
  visible: boolean;
  mode: ModalMode;
  data: Barcode | null;
  levelOptions: LevelOption[];
  onClose: () => void;
  onSuccess: () => void;
}

const BarcodeFormModal: React.FC<Props> = ({ visible, mode, data, levelOptions, onClose, onSuccess }) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = React.useState(false);
  const isEdit = mode === 'edit';

  useEffect(() => {
    if (visible) {
      if (isEdit && data) form.setFieldsValue({ ...data });
      else {
        form.resetFields();
        form.setFieldsValue({ status: 1, isPrimary: 0, barcodeType: 'EAN13' });
      }
    }
  }, [visible, mode, data, form, isEdit]);

  const handleSubmit = async () => {
    try {
      const v = await form.validateFields();
      setLoading(true);
      if (isEdit && data) {
        const d: UpdateBarcodeRequest = { barcodeType: v.barcodeType, isPrimary: v.isPrimary, validFrom: v.validFrom || undefined, validTo: v.validTo || undefined };
        await barcodeApi.update(data.id, d);
        message.success('更新成功');
      } else {
        const d: CreateBarcodeRequest = {
          levelId: v.levelId, barcode: v.barcode, barcodeType: v.barcodeType, isPrimary: v.isPrimary,
          validFrom: v.validFrom || undefined, validTo: v.validTo || undefined, status: v.status,
        };
        await barcodeApi.create(d);
        message.success('创建成功');
      }
      onSuccess();
      onClose();
    } catch (e) {} finally {
      setLoading(false);
    }
  };

  return (
    <Modal title={isEdit ? '编辑条码' : '新增条码'} open={visible} onCancel={onClose} onOk={handleSubmit} confirmLoading={loading} width={640} destroyOnClose maskClosable={false}>
      <Form form={form} layout="vertical">
        <Form.Item name="levelId" label="所属包装层" rules={[{ required: true, message: '请选择包装层' }]}>
          <Select options={levelOptions} placeholder="选择包装层（创建后不可改）" disabled={isEdit} showSearch optionFilterProp="label" />
        </Form.Item>
        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="barcode" label="条码" rules={[{ required: !isEdit, message: '请输入条码' }]}>
              <Input placeholder="条码（创建后不可改）" disabled={isEdit} />
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item name="barcodeType" label="类型" rules={[{ required: true, message: '请选择' }]}>
              <Select options={BARCODE_TYPE_OPTIONS} />
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item name="isPrimary" label="主条码" initialValue={0}>
              <Select options={YES_NO_OPTIONS} />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={16}>
          <Col span={8}><Form.Item name="validFrom" label="启用日"><Input placeholder="YYYY-MM-DD" /></Form.Item></Col>
          <Col span={8}><Form.Item name="validTo" label="停用日"><Input placeholder="YYYY-MM-DD" /></Form.Item></Col>
          {!isEdit && <Col span={8}><Form.Item name="status" label="状态" initialValue={1}><Select options={STATUS_OPTIONS} /></Form.Item></Col>}
        </Row>
      </Form>
    </Modal>
  );
};

export default BarcodeFormModal;

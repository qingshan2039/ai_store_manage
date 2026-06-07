/* 托盘实例新增/编辑弹窗 */
import React, { useEffect } from 'react';
import { Modal, Form, Input, InputNumber, Select, Row, Col, message } from 'antd';
import { lpnApi } from '@/api/lpn';
import { LPN_STATUS_OPTIONS } from '@/constants/enums';
import type { Lpn, CreateLpnRequest, UpdateLpnRequest } from '@/types/lpn';
import type { ModalMode } from '@/types/common';
import type { Option } from '../LpnListPage';

interface Props {
  visible: boolean;
  mode: ModalMode;
  data: Lpn | null;
  palletTypeOptions: Option[];
  warehouseOptions: Option[];
  locationOptions: Option[];
  onClose: () => void;
  onSuccess: () => void;
}

const LpnFormModal: React.FC<Props> = ({ visible, mode, data, palletTypeOptions, warehouseOptions, locationOptions, onClose, onSuccess }) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = React.useState(false);
  const isEdit = mode === 'edit';

  useEffect(() => {
    if (visible) {
      if (isEdit && data) form.setFieldsValue({ ...data });
      else { form.resetFields(); form.setFieldsValue({ status: 'IN_STOCK' }); }
    }
  }, [visible, mode, data, form, isEdit]);

  const handleSubmit = async () => {
    try {
      const v = await form.validateFields();
      setLoading(true);
      if (isEdit && data) {
        const d: UpdateLpnRequest = { locationId: v.locationId, grossWeight: v.grossWeight };
        await lpnApi.update(data.id, d);
        message.success('更新成功');
      } else {
        const d: CreateLpnRequest = { lpnCode: v.lpnCode, palletTypeId: v.palletTypeId, warehouseId: v.warehouseId, locationId: v.locationId, status: v.status, grossWeight: v.grossWeight };
        await lpnApi.create(d);
        message.success('创建成功');
      }
      onSuccess(); onClose();
    } catch (e) {} finally { setLoading(false); }
  };

  return (
    <Modal title={isEdit ? '编辑托盘' : '新增托盘'} open={visible} onCancel={onClose} onOk={handleSubmit} confirmLoading={loading} width={680} destroyOnClose maskClosable={false}>
      <Form form={form} layout="vertical">
        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="lpnCode" label="托盘号 SSCC" rules={[{ required: !isEdit, message: '请输入托盘号' }]}>
              <Input placeholder="SSCC 托盘号（创建后不可改）" disabled={isEdit} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="palletTypeId" label="托盘类型" rules={[{ required: !isEdit, message: '请选择托盘类型' }]}>
              <Select options={palletTypeOptions} placeholder="选择托盘类型（创建后不可改）" disabled={isEdit} showSearch optionFilterProp="label" />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="warehouseId" label="所属仓库" rules={[{ required: !isEdit, message: '请选择仓库' }]}>
              <Select options={warehouseOptions} placeholder="选择仓库（创建后不可改）" disabled={isEdit} showSearch optionFilterProp="label" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="locationId" label="库位（可空）">
              <Select options={locationOptions} placeholder="选择库位" allowClear showSearch optionFilterProp="label" />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={16}>
          {!isEdit && (
            <Col span={12}>
              <Form.Item name="status" label="状态" initialValue="IN_STOCK"><Select options={LPN_STATUS_OPTIONS} /></Form.Item>
            </Col>
          )}
          <Col span={12}>
            <Form.Item name="grossWeight" label="总毛重(kg)"><InputNumber min={0} style={{ width: '100%' }} placeholder="选填" /></Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  );
};

export default LpnFormModal;

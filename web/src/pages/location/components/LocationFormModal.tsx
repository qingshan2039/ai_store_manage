/* 库位新增/编辑弹窗 */
import React, { useEffect } from 'react';
import { Modal, Form, Input, Select, Row, Col, message } from 'antd';
import { locationApi } from '@/api/location';
import { STATUS_OPTIONS } from '@/constants/enums';
import type { Location, CreateLocationRequest, UpdateLocationRequest } from '@/types/location';
import type { ModalMode } from '@/types/common';
import type { Option } from '../LocationListPage';

interface Props {
  visible: boolean;
  mode: ModalMode;
  data: Location | null;
  warehouseOptions: Option[];
  zoneOptions: Option[];
  onClose: () => void;
  onSuccess: () => void;
}

const LocationFormModal: React.FC<Props> = ({ visible, mode, data, warehouseOptions, zoneOptions, onClose, onSuccess }) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = React.useState(false);
  const isEdit = mode === 'edit';

  useEffect(() => {
    if (visible) {
      if (isEdit && data) form.setFieldsValue({ ...data });
      else { form.resetFields(); form.setFieldsValue({ status: 1 }); }
    }
  }, [visible, mode, data, form, isEdit]);

  const handleSubmit = async () => {
    try {
      const v = await form.validateFields();
      setLoading(true);
      if (isEdit && data) {
        const d: UpdateLocationRequest = { zoneId: v.zoneId, locType: v.locType };
        await locationApi.update(data.id, d);
        message.success('更新成功');
      } else {
        const d: CreateLocationRequest = { warehouseId: v.warehouseId, zoneId: v.zoneId, code: v.code, locType: v.locType, status: v.status };
        await locationApi.create(d);
        message.success('创建成功');
      }
      onSuccess(); onClose();
    } catch (e) {} finally { setLoading(false); }
  };

  return (
    <Modal title={isEdit ? '编辑库位' : '新增库位'} open={visible} onCancel={onClose} onOk={handleSubmit} confirmLoading={loading} width={640} destroyOnClose maskClosable={false}>
      <Form form={form} layout="vertical">
        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="warehouseId" label="所属仓库" rules={[{ required: true, message: '请选择仓库' }]}>
              <Select options={warehouseOptions} placeholder="选择仓库（创建后不可改）" disabled={isEdit} showSearch optionFilterProp="label" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="zoneId" label="所属库区（可空）">
              <Select options={zoneOptions} placeholder="选择库区" allowClear showSearch optionFilterProp="label" />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="code" label="库位编码" rules={[{ required: !isEdit, message: '请输入库位编码' }, { pattern: /^[A-Za-z0-9-]+$/, message: '只能包含字母、数字和连字符' }]}>
              <Input placeholder="如 A-01-01（创建后不可改）" disabled={isEdit} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="locType" label="库位类型"><Input placeholder="货架/地堆" /></Form.Item>
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

export default LocationFormModal;

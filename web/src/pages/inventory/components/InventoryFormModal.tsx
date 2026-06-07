/* 库存新增/编辑弹窗。新增时「库区」「库位」均必填，库区联动过滤库位（库存按 location_id 记录）。 */
import React, { useEffect, useState } from 'react';
import { Modal, Form, Input, InputNumber, Select, Row, Col, message } from 'antd';
import { inventoryApi } from '@/api/inventory';
import { locationApi } from '@/api/location';
import type { Inventory, CreateInventoryRequest, UpdateInventoryRequest } from '@/types/inventory';
import type { ModalMode } from '@/types/common';
import type { Option } from '../InventoryListPage';

interface Props {
  visible: boolean;
  mode: ModalMode;
  data: Inventory | null;
  skuOptions: Option[];
  lpnOptions: Option[];
  locationOptions: Option[];
  zoneOptions: Option[];
  onClose: () => void;
  onSuccess: () => void;
}

const InventoryFormModal: React.FC<Props> = ({ visible, mode, data, skuOptions, lpnOptions, locationOptions, zoneOptions, onClose, onSuccess }) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [zoneLocOptions, setZoneLocOptions] = useState<Option[]>([]);
  const isEdit = mode === 'edit';

  useEffect(() => {
    if (visible) {
      setZoneLocOptions([]);
      if (isEdit && data) form.setFieldsValue({ ...data });
      else { form.resetFields(); form.setFieldsValue({ qtyReserved: 0 }); }
    }
  }, [visible, mode, data, form, isEdit]);

  /** 选库区后联动加载该库区的库位（仅新增用） */
  const onZoneChange = (zoneId?: number) => {
    form.setFieldsValue({ locationId: undefined });
    if (zoneId) {
      locationApi
        .list({ zoneId, pageSize: 100, status: 1 })
        .then((res) => setZoneLocOptions(res.data.items.map((l) => ({ label: `${l.code}（${l.warehouseName ?? ''}）`, value: l.id }))))
        .catch(() => setZoneLocOptions([]));
    } else {
      setZoneLocOptions([]);
    }
  };

  const handleSubmit = async () => {
    try {
      const v = await form.validateFields();
      setLoading(true);
      if (isEdit && data) {
        const d: UpdateInventoryRequest = {
          lpnId: v.lpnId, locationId: v.locationId, lotNo: v.lotNo,
          mfgDate: v.mfgDate || undefined, expDate: v.expDate || undefined, qtyOnHand: v.qtyOnHand, qtyReserved: v.qtyReserved,
        };
        await inventoryApi.update(data.id, d);
        message.success('更新成功');
      } else {
        const d: CreateInventoryRequest = {
          skuId: v.skuId, lpnId: v.lpnId, locationId: v.locationId, lotNo: v.lotNo,
          mfgDate: v.mfgDate || undefined, expDate: v.expDate || undefined, qtyOnHand: v.qtyOnHand, qtyReserved: v.qtyReserved,
        };
        await inventoryApi.create(d);
        message.success('创建成功');
      }
      onSuccess(); onClose();
    } catch (e) {} finally { setLoading(false); }
  };

  return (
    <Modal title={isEdit ? '编辑库存' : '新增库存'} open={visible} onCancel={onClose} onOk={handleSubmit} confirmLoading={loading} width={700} destroyOnClose maskClosable={false}>
      <Form form={form} layout="vertical">
        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="skuId" label="所属 SKU" rules={[{ required: true, message: '请选择 SKU' }]}>
              <Select options={skuOptions} placeholder="选择 SKU（创建后不可改）" disabled={isEdit} showSearch optionFilterProp="label" />
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item name="qtyOnHand" label="在库数量" rules={[{ required: true, message: '请输入' }]}>
              <InputNumber min={0} style={{ width: '100%' }} placeholder="基本单位" />
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item name="qtyReserved" label="锁定数量" initialValue={0}>
              <InputNumber min={0} style={{ width: '100%' }} />
            </Form.Item>
          </Col>
        </Row>

        {!isEdit ? (
          /* 新增：库区 + 库位 均必填，库区联动过滤库位 */
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item name="zoneId" label="库区" rules={[{ required: true, message: '请选择库区' }]}>
                <Select options={zoneOptions} placeholder="请选择库区" onChange={onZoneChange} showSearch optionFilterProp="label" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="locationId" label="库位" rules={[{ required: true, message: '请选择库位' }]}>
                <Select options={zoneLocOptions} placeholder="请先选择库区" showSearch optionFilterProp="label" notFoundContent="该库区下暂无库位" />
              </Form.Item>
            </Col>
          </Row>
        ) : (
          /* 编辑：库位可改（不强制走库区） */
          <Form.Item name="locationId" label="库位">
            <Select options={locationOptions} placeholder="选择库位" allowClear showSearch optionFilterProp="label" />
          </Form.Item>
        )}

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="lpnId" label="所在托盘（可空）">
              <Select options={lpnOptions} placeholder="选择托盘" allowClear showSearch optionFilterProp="label" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="lotNo" label="批次号"><Input placeholder="选填" /></Form.Item>
          </Col>
        </Row>
        <Row gutter={16}>
          <Col span={12}><Form.Item name="mfgDate" label="生产日期"><Input placeholder="YYYY-MM-DD" /></Form.Item></Col>
          <Col span={12}><Form.Item name="expDate" label="有效期"><Input placeholder="YYYY-MM-DD" /></Form.Item></Col>
        </Row>
      </Form>
    </Modal>
  );
};

export default InventoryFormModal;

/* 物料图片新增/编辑弹窗（仅存 URL） */
import React, { useEffect } from 'react';
import { Modal, Form, Input, InputNumber, Select, Row, Col, message } from 'antd';
import { itemImageApi } from '@/api/itemImage';
import { STATUS_OPTIONS, YES_NO_OPTIONS } from '@/constants/enums';
import type { ItemImage, CreateItemImageRequest, UpdateItemImageRequest } from '@/types/itemImage';
import type { ModalMode } from '@/types/common';
import type { SkuOption } from '../ItemImageListPage';

interface Props {
  visible: boolean;
  mode: ModalMode;
  data: ItemImage | null;
  skuOptions: SkuOption[];
  onClose: () => void;
  onSuccess: () => void;
}

const ItemImageFormModal: React.FC<Props> = ({ visible, mode, data, skuOptions, onClose, onSuccess }) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = React.useState(false);
  const isEdit = mode === 'edit';

  useEffect(() => {
    if (visible) {
      if (isEdit && data) form.setFieldsValue({ ...data });
      else {
        form.resetFields();
        form.setFieldsValue({ status: 1, isPrimary: 0, sortOrder: 0 });
      }
    }
  }, [visible, mode, data, form, isEdit]);

  const handleSubmit = async () => {
    try {
      const v = await form.validateFields();
      setLoading(true);
      if (isEdit && data) {
        const d: UpdateItemImageRequest = { imageUrl: v.imageUrl, imageType: v.imageType, sortOrder: v.sortOrder, isPrimary: v.isPrimary };
        await itemImageApi.update(data.id, d);
        message.success('更新成功');
      } else {
        const d: CreateItemImageRequest = { skuId: v.skuId, imageUrl: v.imageUrl, imageType: v.imageType, sortOrder: v.sortOrder, isPrimary: v.isPrimary, status: v.status };
        await itemImageApi.create(d);
        message.success('创建成功');
      }
      onSuccess();
      onClose();
    } catch (e) {} finally {
      setLoading(false);
    }
  };

  return (
    <Modal title={isEdit ? '编辑物料图片' : '新增物料图片'} open={visible} onCancel={onClose} onOk={handleSubmit} confirmLoading={loading} width={640} destroyOnClose maskClosable={false}>
      <Form form={form} layout="vertical">
        {!isEdit && (
          <Form.Item name="skuId" label="所属 SKU">
            <Select options={skuOptions} placeholder="选择 SKU（可空）" allowClear showSearch optionFilterProp="label" />
          </Form.Item>
        )}
        <Form.Item name="imageUrl" label="图片地址" rules={[{ required: true, message: '请输入图片 URL' }]}>
          <Input placeholder="https://..." />
        </Form.Item>
        <Row gutter={16}>
          <Col span={8}><Form.Item name="imageType" label="图片类型"><Input placeholder="实体/内/外包装" /></Form.Item></Col>
          <Col span={8}><Form.Item name="sortOrder" label="排序" initialValue={0}><InputNumber min={0} precision={0} style={{ width: '100%' }} /></Form.Item></Col>
          <Col span={8}><Form.Item name="isPrimary" label="主图" initialValue={0}><Select options={YES_NO_OPTIONS} /></Form.Item></Col>
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

export default ItemImageFormModal;

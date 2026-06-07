/* 包装关系新增/编辑弹窗 */
import React, { useEffect } from 'react';
import { Modal, Form, InputNumber, Select, Row, Col, message } from 'antd';
import { packagingRelationApi } from '@/api/packagingRelation';
import { STATUS_OPTIONS, YES_NO_OPTIONS } from '@/constants/enums';
import type { PackagingRelation, CreatePackagingRelationRequest, UpdatePackagingRelationRequest } from '@/types/packagingRelation';
import type { ModalMode } from '@/types/common';
import type { LevelOption } from '../PackagingRelationListPage';

interface Props {
  visible: boolean;
  mode: ModalMode;
  data: PackagingRelation | null;
  levelOptions: LevelOption[];
  onClose: () => void;
  onSuccess: () => void;
}

const PackagingRelationFormModal: React.FC<Props> = ({ visible, mode, data, levelOptions, onClose, onSuccess }) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = React.useState(false);
  const isEdit = mode === 'edit';

  useEffect(() => {
    if (visible) {
      if (isEdit && data) form.setFieldsValue({ ...data });
      else {
        form.resetFields();
        form.setFieldsValue({ status: 1, isFixedQty: 1 });
      }
    }
  }, [visible, mode, data, form, isEdit]);

  const handleSubmit = async () => {
    try {
      const v = await form.validateFields();
      setLoading(true);
      if (isEdit && data) {
        const d: UpdatePackagingRelationRequest = { childQty: v.childQty, isFixedQty: v.isFixedQty, tareWeight: v.tareWeight };
        await packagingRelationApi.update(data.id, d);
        message.success('更新成功');
      } else {
        const d: CreatePackagingRelationRequest = {
          parentLevelId: v.parentLevelId, childLevelId: v.childLevelId, childQty: v.childQty, isFixedQty: v.isFixedQty, tareWeight: v.tareWeight, status: v.status,
        };
        await packagingRelationApi.create(d);
        message.success('创建成功');
      }
      onSuccess();
      onClose();
    } catch (e) {} finally {
      setLoading(false);
    }
  };

  return (
    <Modal title={isEdit ? '编辑包装关系' : '新增包装关系'} open={visible} onCancel={onClose} onOk={handleSubmit} confirmLoading={loading} width={640} destroyOnClose maskClosable={false}>
      <Form form={form} layout="vertical">
        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="parentLevelId" label="父层" rules={[{ required: true, message: '请选择父层' }]}>
              <Select options={levelOptions} placeholder="如 托（创建后不可改）" disabled={isEdit} showSearch optionFilterProp="label" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="childLevelId" label="子层" rules={[{ required: true, message: '请选择子层' }]}>
              <Select options={levelOptions} placeholder="如 箱（创建后不可改）" disabled={isEdit} showSearch optionFilterProp="label" />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={16}>
          <Col span={8}>
            <Form.Item name="childQty" label="含子层数量" rules={[{ required: true, message: '请输入' }]}>
              <InputNumber min={0} style={{ width: '100%' }} placeholder="如 500" />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="isFixedQty" label="是否定量整托" initialValue={1}>
              <Select options={YES_NO_OPTIONS} />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="tareWeight" label="包装皮重(kg)">
              <InputNumber min={0} style={{ width: '100%' }} placeholder="选填" />
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

export default PackagingRelationFormModal;

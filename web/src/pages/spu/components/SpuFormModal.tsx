/* ========================================
   SPU 新增/编辑弹窗
   ======================================== */
import React, { useEffect } from 'react';
import { Modal, Form, Input, Select, Row, Col, message } from 'antd';
import { spuApi } from '@/api/spu';
import { STATUS_OPTIONS } from '@/constants/enums';
import type { Spu, CreateSpuRequest, UpdateSpuRequest } from '@/types/spu';
import type { ModalMode } from '@/types/common';
import type { CategoryOption } from '../SpuListPage';

interface Props {
  visible: boolean;
  mode: ModalMode;
  data: Spu | null;
  categoryOptions: CategoryOption[];
  onClose: () => void;
  onSuccess: () => void;
}

const SpuFormModal: React.FC<Props> = ({ visible, mode, data, categoryOptions, onClose, onSuccess }) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = React.useState(false);
  const isEdit = mode === 'edit';

  useEffect(() => {
    if (visible) {
      if (isEdit && data) {
        form.setFieldsValue({ ...data });
      } else {
        form.resetFields();
        form.setFieldsValue({ status: 1, baseUnit: 'PCS' });
      }
    }
  }, [visible, mode, data, form, isEdit]);

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      setLoading(true);

      if (isEdit && data) {
        const updateData: UpdateSpuRequest = {
          spuName: values.spuName,
          categoryCode: values.categoryCode,
          brand: values.brand,
          baseUnit: values.baseUnit,
        };
        await spuApi.update(data.id, updateData);
        message.success('更新 SPU 成功');
      } else {
        const createData: CreateSpuRequest = {
          spuCode: values.spuCode,
          spuName: values.spuName,
          categoryCode: values.categoryCode,
          brand: values.brand,
          baseUnit: values.baseUnit,
          status: values.status,
        };
        await spuApi.create(createData);
        message.success('创建 SPU 成功');
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
      title={isEdit ? '编辑 SPU' : '新增 SPU'}
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
              name="spuCode"
              label="SPU 编码"
              rules={[
                { required: !isEdit, message: '请输入 SPU 编码' },
                { pattern: /^[A-Za-z0-9-]+$/, message: '只能包含字母、数字和连字符' },
              ]}
            >
              <Input placeholder="SPU 编码（创建后不可改）" disabled={isEdit} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item
              name="spuName"
              label="SPU 名称"
              rules={[
                { required: true, message: '请输入 SPU 名称' },
                { min: 1, max: 128, message: '长度在 1-128 个字符之间' },
              ]}
            >
              <Input placeholder="如 3寸纸管" />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="categoryCode" label="所属品类" rules={[{ required: true, message: '请选择品类' }]}>
              <Select options={categoryOptions} placeholder="请选择品类" showSearch optionFilterProp="label" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item
              name="baseUnit"
              label="基本单位"
              rules={[{ required: true, message: '请输入基本单位' }]}
            >
              <Input placeholder="如 PCS / ROLL / 张" />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="brand" label="品牌">
              <Input placeholder="品牌（选填）" />
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
      </Form>
    </Modal>
  );
};

export default SpuFormModal;

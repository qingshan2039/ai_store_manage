/* ========================================
   库区新增/编辑弹窗（隶属仓库）
   ======================================== */
import React, { useEffect } from 'react';
import { Modal, Form, Input, Select, Row, Col, message } from 'antd';
import { zoneApi } from '@/api/zone';
import { STATUS_OPTIONS } from '@/constants/enums';
import type { Zone, CreateZoneRequest, UpdateZoneRequest } from '@/types/zone';
import type { ModalMode } from '@/types/common';
import type { WarehouseOption } from '../ZoneListPage';

const { TextArea } = Input;

interface ZoneFormModalProps {
  visible: boolean;
  mode: ModalMode;
  data: Zone | null;
  warehouseOptions: WarehouseOption[];
  onClose: () => void;
  onSuccess: () => void;
}

const ZoneFormModal: React.FC<ZoneFormModalProps> = ({
  visible,
  mode,
  data,
  warehouseOptions,
  onClose,
  onSuccess,
}) => {
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
        const updateData: UpdateZoneRequest = {
          code: values.code,
          name: values.name,
          type: values.type,
          remark: values.remark,
        };
        await zoneApi.update(data.id, updateData);
        message.success('更新库区成功');
      } else {
        const createData: CreateZoneRequest = {
          warehouseId: values.warehouseId,
          code: values.code,
          name: values.name,
          type: values.type,
          remark: values.remark,
          status: values.status,
        };
        await zoneApi.create(createData);
        message.success('创建库区成功');
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
      title={isEdit ? '编辑库区' : '新增库区'}
      open={visible}
      onCancel={onClose}
      onOk={handleSubmit}
      confirmLoading={loading}
      width={640}
      destroyOnClose
      maskClosable={false}
    >
      <Form form={form} layout="vertical">
        <Form.Item name="warehouseId" label="所属仓库" rules={[{ required: true, message: '请选择所属仓库' }]}>
          <Select
            options={warehouseOptions}
            placeholder="请选择所属仓库（创建后不可修改）"
            disabled={isEdit}
            showSearch
            optionFilterProp="label"
          />
        </Form.Item>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item
              name="code"
              label="库区编码"
              rules={[
                { required: true, message: '请输入库区编码' },
                { pattern: /^[A-Za-z0-9-]+$/, message: '只能包含字母、数字和连字符' },
              ]}
            >
              <Input placeholder="库区编码（仓库内唯一，可修改）" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item
              name="name"
              label="库区名称"
              rules={[
                { required: true, message: '请输入库区名称' },
                { min: 1, max: 64, message: '长度在 1-64 个字符之间' },
              ]}
            >
              <Input placeholder="库区名称" />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="type" label="库区类型" rules={[{ max: 32, message: '类型不超过 32 字' }]}>
              <Input placeholder="如 存储/拣货/暂存（选填）" />
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

        <Form.Item name="remark" label="备注">
          <TextArea rows={2} placeholder="备注信息（选填）" maxLength={500} showCount />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default ZoneFormModal;

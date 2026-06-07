/* ========================================
   SKU 新增/编辑弹窗（结构化尺寸 + spec 动态键值）
   ======================================== */
import React, { useEffect } from 'react';
import { Modal, Form, Input, InputNumber, Select, Row, Col, Space, Button, Divider, message } from 'antd';
import { MinusCircleOutlined, PlusOutlined } from '@ant-design/icons';
import { skuApi } from '@/api/sku';
import { STATUS_OPTIONS, ITEM_TYPE_OPTIONS } from '@/constants/enums';
import type { Sku, CreateSkuRequest, UpdateSkuRequest } from '@/types/sku';
import type { ModalMode } from '@/types/common';
import type { SpuOption } from '../SkuListPage';

interface Props {
  visible: boolean;
  mode: ModalMode;
  data: Sku | null;
  spuOptions: SpuOption[];
  onClose: () => void;
  onSuccess: () => void;
}

interface SpecPair {
  key?: string;
  value?: string;
}

/** spec 对象 → 键值对数组（编辑回显） */
const specToPairs = (spec?: Record<string, unknown> | null): SpecPair[] =>
  spec ? Object.entries(spec).map(([key, value]) => ({ key, value: value == null ? '' : String(value) })) : [];

/** 键值对数组 → spec 对象（提交），忽略空键；无项则返回 undefined */
const pairsToSpec = (pairs?: SpecPair[]): Record<string, unknown> | undefined => {
  const obj: Record<string, unknown> = {};
  (pairs || []).forEach((p) => {
    if (p && p.key && p.key.trim()) obj[p.key.trim()] = p.value ?? '';
  });
  return Object.keys(obj).length > 0 ? obj : undefined;
};

const SkuFormModal: React.FC<Props> = ({ visible, mode, data, spuOptions, onClose, onSuccess }) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = React.useState(false);
  const isEdit = mode === 'edit';

  useEffect(() => {
    if (visible) {
      if (isEdit && data) {
        form.setFieldsValue({ ...data, specPairs: specToPairs(data.spec) });
      } else {
        form.resetFields();
        form.setFieldsValue({ status: 1, itemType: 'RAW', specPairs: [] });
      }
    }
  }, [visible, mode, data, form, isEdit]);

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      setLoading(true);
      const spec = pairsToSpec(values.specPairs);

      if (isEdit && data) {
        const updateData: UpdateSkuRequest = {
          skuName: values.skuName,
          itemType: values.itemType,
          lengthMm: values.lengthMm,
          widthMm: values.widthMm,
          thicknessMm: values.thicknessMm,
          rollLengthM: values.rollLengthM,
          color: values.color,
          gsm: values.gsm,
          spec,
        };
        await skuApi.update(data.id, updateData);
        message.success('更新 SKU 成功');
      } else {
        const createData: CreateSkuRequest = {
          spuId: values.spuId,
          skuCode: values.skuCode,
          skuName: values.skuName,
          itemType: values.itemType,
          lengthMm: values.lengthMm,
          widthMm: values.widthMm,
          thicknessMm: values.thicknessMm,
          rollLengthM: values.rollLengthM,
          color: values.color,
          gsm: values.gsm,
          spec,
          status: values.status,
        };
        await skuApi.create(createData);
        message.success('创建 SKU 成功');
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
      title={isEdit ? '编辑 SKU' : '新增 SKU'}
      open={visible}
      onCancel={onClose}
      onOk={handleSubmit}
      confirmLoading={loading}
      width={760}
      destroyOnClose
      maskClosable={false}
    >
      <Form form={form} layout="vertical">
        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="spuId" label="所属 SPU" rules={[{ required: true, message: '请选择所属 SPU' }]}>
              <Select
                options={spuOptions}
                placeholder="请选择 SPU（创建后不可改）"
                disabled={isEdit}
                showSearch
                optionFilterProp="label"
              />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="itemType" label="阶段类型" rules={[{ required: true, message: '请选择阶段类型' }]}>
              <Select options={ITEM_TYPE_OPTIONS} placeholder="原料/半成品/成品" />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item
              name="skuCode"
              label="SKU 编码"
              rules={[
                { required: !isEdit, message: '请输入 SKU 编码' },
                { pattern: /^[A-Za-z0-9-]+$/, message: '只能包含字母、数字和连字符' },
              ]}
            >
              <Input placeholder="如 PC-340480-A（创建后不可改）" disabled={isEdit} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item
              name="skuName"
              label="SKU 名称"
              rules={[
                { required: true, message: '请输入 SKU 名称' },
                { min: 1, max: 128, message: '长度在 1-128 个字符之间' },
              ]}
            >
              <Input placeholder="如 纸管 340x480x5mm 规格A" />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={6}>
            <Form.Item name="lengthMm" label="长 (mm)">
              <InputNumber min={0} style={{ width: '100%' }} placeholder="选填" />
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item name="widthMm" label="宽 (mm)">
              <InputNumber min={0} style={{ width: '100%' }} placeholder="选填" />
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item name="thicknessMm" label="厚 (mm)">
              <InputNumber min={0} style={{ width: '100%' }} placeholder="选填" />
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item name="rollLengthM" label="卷长 (m)">
              <InputNumber min={0} style={{ width: '100%' }} placeholder="膜类" />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={8}>
            <Form.Item name="color" label="颜色">
              <Input placeholder="选填" />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="gsm" label="克重 (g/m²)">
              <InputNumber min={0} style={{ width: '100%' }} placeholder="选填" />
            </Form.Item>
          </Col>
          {!isEdit && (
            <Col span={8}>
              <Form.Item name="status" label="状态" initialValue={1}>
                <Select options={STATUS_OPTIONS} />
              </Form.Item>
            </Col>
          )}
        </Row>

        <Divider style={{ margin: '4px 0 12px' }}>细分规格 spec（同尺寸下的材质/牌号/工艺等）</Divider>
        <Form.List name="specPairs">
          {(fields, { add, remove }) => (
            <>
              {fields.map((field) => (
                <Space key={field.key} align="baseline" style={{ display: 'flex', marginBottom: 8 }}>
                  <Form.Item name={[field.name, 'key']} style={{ marginBottom: 0 }}>
                    <Input placeholder="键（如 material）" style={{ width: 260 }} />
                  </Form.Item>
                  <Form.Item name={[field.name, 'value']} style={{ marginBottom: 0 }}>
                    <Input placeholder="值（如 再生纸）" style={{ width: 320 }} />
                  </Form.Item>
                  <MinusCircleOutlined onClick={() => remove(field.name)} />
                </Space>
              ))}
              <Button type="dashed" onClick={() => add({ key: '', value: '' })} block icon={<PlusOutlined />}>
                添加规格项
              </Button>
            </>
          )}
        </Form.List>
      </Form>
    </Modal>
  );
};

export default SkuFormModal;

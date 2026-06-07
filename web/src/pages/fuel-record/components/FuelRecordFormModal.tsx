/* ========================================
   打油记录新增/编辑弹窗（含多图上传）
   ======================================== */
import React, { useEffect } from 'react';
import { Modal, Form, Select, DatePicker, InputNumber, Input, Row, Col, message } from 'antd';
import dayjs from 'dayjs';
import { fuelRecordApi } from '@/api/fuelRecord';
import ImageUpload from '@/components/ImageUpload';
import type { StaffOption } from '@/hooks/useCrewOptions';
import type { FuelRecord, CreateFuelRecordRequest, UpdateFuelRecordRequest } from '@/types/fuelRecord';
import type { ModalMode } from '@/types/common';

const { TextArea } = Input;

interface FuelRecordFormModalProps {
  visible: boolean;
  mode: ModalMode;
  data: FuelRecord | null;
  vehicleOptions: StaffOption[];
  driverOptions: StaffOption[];
  onClose: () => void;
  onSuccess: () => void;
}

const FuelRecordFormModal: React.FC<FuelRecordFormModalProps> = ({
  visible,
  mode,
  data,
  vehicleOptions,
  driverOptions,
  onClose,
  onSuccess,
}) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = React.useState(false);
  const isEdit = mode === 'edit';

  useEffect(() => {
    if (visible) {
      if (isEdit && data) {
        form.setFieldsValue({
          vehicleId: data.vehicleId,
          driverUserId: data.driverUserId,
          fuelDate: data.fuelDate ? dayjs(data.fuelDate) : undefined,
          liters: data.liters,
          amount: data.amount,
          unitPrice: data.unitPrice,
          odometer: data.odometer,
          images: data.images ?? [],
          remark: data.remark,
        });
      } else {
        form.resetFields();
        form.setFieldsValue({ fuelDate: dayjs(), images: [] });
      }
    }
  }, [visible, mode, data, form, isEdit]);

  const handleSubmit = async () => {
    try {
      const v = await form.validateFields();
      const base = {
        vehicleId: v.vehicleId,
        driverUserId: v.driverUserId ?? null,
        fuelDate: v.fuelDate ? v.fuelDate.format('YYYY-MM-DD') : undefined,
        liters: v.liters ?? null,
        amount: v.amount ?? null,
        unitPrice: v.unitPrice ?? null,
        odometer: v.odometer ?? null,
        images: v.images ?? [],
        remark: v.remark,
      };
      setLoading(true);
      if (isEdit && data) {
        await fuelRecordApi.update(data.id, base as UpdateFuelRecordRequest);
        message.success('更新打油记录成功');
      } else {
        await fuelRecordApi.create(base as CreateFuelRecordRequest);
        message.success('创建打油记录成功');
      }
      onSuccess();
      onClose();
    } catch (error) {
      /* 校验失败或请求错误：已提示 */
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      title={isEdit ? '编辑打油记录' : '新增打油记录'}
      open={visible}
      onCancel={onClose}
      onOk={handleSubmit}
      confirmLoading={loading}
      width={720}
      destroyOnClose
      maskClosable={false}
    >
      <Form form={form} layout="vertical">
        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="vehicleId" label="车辆" rules={[{ required: true, message: '请选择车辆' }]}>
              <Select placeholder="请选择车辆" options={vehicleOptions} showSearch optionFilterProp="label" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="driverUserId" label="打油司机">
              <Select placeholder="请选择司机（选填）" options={driverOptions} allowClear showSearch optionFilterProp="label" />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={8}>
            <Form.Item name="fuelDate" label="打油日期" rules={[{ required: true, message: '请选择日期' }]}>
              <DatePicker style={{ width: '100%' }} />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="liters" label="升数 (L)">
              <InputNumber style={{ width: '100%' }} min={0} precision={2} placeholder="升数" />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="amount" label="金额 (元)">
              <InputNumber style={{ width: '100%' }} min={0} precision={2} placeholder="金额" />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={8}>
            <Form.Item name="unitPrice" label="单价 (元/L)">
              <InputNumber style={{ width: '100%' }} min={0} precision={2} placeholder="单价" />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="odometer" label="里程表 (km)">
              <InputNumber style={{ width: '100%' }} min={0} precision={1} placeholder="里程读数" />
            </Form.Item>
          </Col>
        </Row>

        <Form.Item name="images" label="小票/凭证照片">
          <ImageUpload max={8} />
        </Form.Item>

        <Form.Item name="remark" label="备注">
          <TextArea rows={2} placeholder="备注信息（选填）" maxLength={500} showCount />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default FuelRecordFormModal;

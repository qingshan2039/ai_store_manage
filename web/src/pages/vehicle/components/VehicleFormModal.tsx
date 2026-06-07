/* ========================================
   车辆新增/编辑弹窗（含常态化司机 / 跟车员，支持 OTHER 替补）
   ======================================== */
import React, { useEffect } from 'react';
import { Modal, Form, Input, Select, Row, Col, message } from 'antd';
import { vehicleApi } from '@/api/vehicle';
import { STATUS_OPTIONS } from '@/constants/enums';
import StaffPicker from '@/components/StaffPicker';
import type { StaffValue } from '@/components/StaffPicker';
import type { StaffOption } from '@/hooks/useCrewOptions';
import type { Vehicle, CreateVehicleRequest, UpdateVehicleRequest } from '@/types/vehicle';
import type { ModalMode } from '@/types/common';

const { TextArea } = Input;

interface VehicleFormModalProps {
  visible: boolean;
  mode: ModalMode;
  data: Vehicle | null;
  driverOptions: StaffOption[];
  escortOptions: StaffOption[];
  onClose: () => void;
  onSuccess: () => void;
}

const VehicleFormModal: React.FC<VehicleFormModalProps> = ({
  visible,
  mode,
  data,
  driverOptions,
  escortOptions,
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
          plateNo: data.plateNo,
          defaultDriver: { userId: data.defaultDriverUserId, other: data.defaultDriverOther },
          defaultEscort: { userId: data.defaultEscortUserId, other: data.defaultEscortOther },
          remark: data.remark,
        });
      } else {
        form.resetFields();
        form.setFieldsValue({ status: 1 });
      }
    }
  }, [visible, mode, data, form, isEdit]);

  const handleSubmit = async () => {
    try {
      const v = await form.validateFields();
      const driver: StaffValue = v.defaultDriver ?? {};
      const escort: StaffValue = v.defaultEscort ?? {};
      setLoading(true);

      if (isEdit && data) {
        const payload: UpdateVehicleRequest = {
          plateNo: v.plateNo,
          defaultDriverUserId: driver.userId ?? null,
          defaultDriverOther: driver.other ?? null,
          defaultEscortUserId: escort.userId ?? null,
          defaultEscortOther: escort.other ?? null,
          remark: v.remark,
        };
        await vehicleApi.update(data.id, payload);
        message.success('更新车辆成功');
      } else {
        const payload: CreateVehicleRequest = {
          plateNo: v.plateNo,
          defaultDriverUserId: driver.userId ?? null,
          defaultDriverOther: driver.other ?? null,
          defaultEscortUserId: escort.userId ?? null,
          defaultEscortOther: escort.other ?? null,
          remark: v.remark,
          status: v.status,
        };
        await vehicleApi.create(payload);
        message.success('创建车辆成功');
      }
      onSuccess();
      onClose();
    } catch (error) {
      /* 校验失败或请求错误：Antd Form 与全局拦截器已提示 */
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      title={isEdit ? '编辑车辆' : '新增车辆'}
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
              name="plateNo"
              label="车牌号"
              rules={[
                { required: true, message: '请输入车牌号' },
                { max: 32, message: '车牌号不超过 32 字' },
              ]}
            >
              <Input placeholder="如 9924" />
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

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="defaultDriver" label="常态化司机">
              <StaffPicker options={driverOptions} placeholder="选择运输部司机或填替补" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="defaultEscort" label="常态化跟车员">
              <StaffPicker options={escortOptions} placeholder="选择仓库/生产人员或填替补" />
            </Form.Item>
          </Col>
        </Row>

        <Form.Item name="remark" label="备注">
          <TextArea rows={2} placeholder="备注信息（选填）" maxLength={500} showCount />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default VehicleFormModal;

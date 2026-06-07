/* ========================================
   司机打卡新增/编辑弹窗（司机/跟车员支持 OTHER 替补）
   ======================================== */
import React, { useEffect } from 'react';
import { Modal, Form, Select, DatePicker, Input, Row, Col, message } from 'antd';
import dayjs from 'dayjs';
import { driverCheckinApi } from '@/api/driverCheckin';
import StaffPicker from '@/components/StaffPicker';
import type { StaffValue } from '@/components/StaffPicker';
import { CHECKIN_STATUS_OPTIONS } from '@/constants/enums';
import type { StaffOption } from '@/hooks/useCrewOptions';
import type {
  DriverCheckin,
  CreateDriverCheckinRequest,
  UpdateDriverCheckinRequest,
} from '@/types/driverCheckin';
import type { ModalMode } from '@/types/common';

const { TextArea } = Input;

interface DriverCheckinFormModalProps {
  visible: boolean;
  mode: ModalMode;
  data: DriverCheckin | null;
  driverOptions: StaffOption[];
  escortOptions: StaffOption[];
  vehicleOptions: StaffOption[];
  onClose: () => void;
  onSuccess: () => void;
}

const DriverCheckinFormModal: React.FC<DriverCheckinFormModalProps> = ({
  visible,
  mode,
  data,
  driverOptions,
  escortOptions,
  vehicleOptions,
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
          driver: { userId: data.driverUserId, other: data.driverOther },
          escort: { userId: data.escortUserId, other: data.escortOther },
          vehicleId: data.vehicleId,
          checkinDate: data.checkinDate ? dayjs(data.checkinDate) : undefined,
          clockInAt: data.clockInAt ? dayjs(data.clockInAt) : undefined,
          clockOutAt: data.clockOutAt ? dayjs(data.clockOutAt) : undefined,
          checkinStatus: data.checkinStatus,
          remark: data.remark,
        });
      } else {
        form.resetFields();
        form.setFieldsValue({ checkinDate: dayjs(), checkinStatus: 'NORMAL' });
      }
    }
  }, [visible, mode, data, form, isEdit]);

  const handleSubmit = async () => {
    try {
      const v = await form.validateFields();
      const driver: StaffValue = v.driver ?? {};
      const escort: StaffValue = v.escort ?? {};
      const base = {
        driverUserId: driver.userId ?? null,
        driverOther: driver.other ?? null,
        vehicleId: v.vehicleId ?? null,
        escortUserId: escort.userId ?? null,
        escortOther: escort.other ?? null,
        checkinDate: v.checkinDate ? v.checkinDate.format('YYYY-MM-DD') : undefined,
        clockInAt: v.clockInAt ? v.clockInAt.format('YYYY-MM-DDTHH:mm:ss') : null,
        clockOutAt: v.clockOutAt ? v.clockOutAt.format('YYYY-MM-DDTHH:mm:ss') : null,
        checkinStatus: v.checkinStatus,
        remark: v.remark,
      };
      setLoading(true);
      if (isEdit && data) {
        await driverCheckinApi.update(data.id, base as UpdateDriverCheckinRequest);
        message.success('更新打卡记录成功');
      } else {
        await driverCheckinApi.create(base as CreateDriverCheckinRequest);
        message.success('创建打卡记录成功');
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
      title={isEdit ? '编辑打卡记录' : '新增打卡记录'}
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
            <Form.Item name="driver" label="司机">
              <StaffPicker options={driverOptions} placeholder="选择运输部司机或填替补" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="escort" label="跟车员">
              <StaffPicker options={escortOptions} placeholder="选择仓库/生产人员或填替补" />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="vehicleId" label="车辆">
              <Select placeholder="请选择车辆（选填）" options={vehicleOptions} allowClear showSearch optionFilterProp="label" />
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item name="checkinDate" label="打卡日期" rules={[{ required: true, message: '请选择日期' }]}>
              <DatePicker style={{ width: '100%' }} />
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item name="checkinStatus" label="出勤状态" initialValue="NORMAL">
              <Select options={CHECKIN_STATUS_OPTIONS} />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="clockInAt" label="上班打卡">
              <DatePicker style={{ width: '100%' }} showTime format="YYYY-MM-DD HH:mm" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="clockOutAt" label="下班打卡">
              <DatePicker style={{ width: '100%' }} showTime format="YYYY-MM-DD HH:mm" />
            </Form.Item>
          </Col>
        </Row>

        <Form.Item name="remark" label="备注">
          <TextArea rows={2} placeholder="如缺席原因 / 替补说明（选填）" maxLength={500} showCount />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default DriverCheckinFormModal;

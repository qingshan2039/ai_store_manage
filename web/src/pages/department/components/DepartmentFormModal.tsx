/* ========================================
   部门新增/编辑弹窗
   ======================================== */
import React, { useEffect } from 'react';
import { Modal, Form, Input, Select, InputNumber, Row, Col, message } from 'antd';
import { departmentApi } from '@/api/department';
import { DEPARTMENT_STATUS_OPTIONS, DEPARTMENT_TYPE_OPTIONS } from '@/constants/enums';
import type {
  Department,
  CreateDepartmentRequest,
  UpdateDepartmentRequest,
} from '@/types/department';
import type { ModalMode } from '@/types/common';

const { TextArea } = Input;

interface DepartmentFormModalProps {
  visible: boolean;
  mode: ModalMode;
  data: Department | null;
  onClose: () => void;
  onSuccess: () => void;
}

const DepartmentFormModal: React.FC<DepartmentFormModalProps> = ({
  visible,
  mode,
  data,
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
        form.setFieldsValue({ status: 1, sort: 0 });
      }
    }
  }, [visible, mode, data, form, isEdit]);

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      setLoading(true);

      if (isEdit && data) {
        // 更新逻辑（code 不可改，状态走独立接口）
        const updateData: UpdateDepartmentRequest = {
          name: values.name,
          type: values.type,
          sort: values.sort,
          remark: values.remark,
        };
        await departmentApi.update(data.id, updateData);
        message.success('更新部门成功');
      } else {
        // 创建逻辑
        const createData: CreateDepartmentRequest = {
          name: values.name,
          code: values.code,
          type: values.type,
          sort: values.sort,
          remark: values.remark,
          status: values.status,
        };
        await departmentApi.create(createData);
        message.success('创建部门成功');
      }

      onSuccess();
      onClose();
    } catch (error) {
      // 校验失败或接口错误已处理
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      title={isEdit ? '编辑部门' : '新增部门'}
      open={visible}
      onCancel={onClose}
      onOk={handleSubmit}
      confirmLoading={loading}
      width={560}
      destroyOnClose
      maskClosable={false}
    >
      <Form form={form} layout="vertical">
        <Row gutter={16}>
          <Col span={12}>
            <Form.Item
              name="name"
              label="部门名称"
              rules={[
                { required: true, message: '请输入部门名称' },
                { min: 2, max: 64, message: '长度在 2-64 个字符之间' },
              ]}
            >
              <Input placeholder="部门名称" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item
              name="code"
              label="部门编码"
              rules={[
                { required: !isEdit, message: '请输入部门编码' },
                { pattern: /^[A-Za-z0-9-]+$/, message: '只能包含字母、数字和连字符' },
              ]}
            >
              <Input placeholder="部门编码（创建后不可修改）" disabled={isEdit} />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item
              name="type"
              label="部门类型"
              rules={[{ required: true, message: '请选择部门类型' }]}
            >
              <Select placeholder="请选择部门类型" options={DEPARTMENT_TYPE_OPTIONS} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="sort" label="显示排序">
              <InputNumber style={{ width: '100%' }} min={0} placeholder="数字越小越靠前" />
            </Form.Item>
          </Col>
        </Row>

        {!isEdit && (
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item name="status" label="状态" initialValue={1}>
                <Select options={DEPARTMENT_STATUS_OPTIONS} />
              </Form.Item>
            </Col>
          </Row>
        )}

        <Row gutter={16}>
          <Col span={24}>
            <Form.Item name="remark" label="备注">
              <TextArea rows={3} placeholder="备注信息（选填）" maxLength={500} showCount />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  );
};

export default DepartmentFormModal;

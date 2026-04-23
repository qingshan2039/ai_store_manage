/* ========================================
   带确认提示的操作按钮
   ======================================== */
import React from 'react';
import { Popconfirm, Button } from 'antd';
import type { ButtonProps } from 'antd';

interface ConfirmActionProps extends Omit<ButtonProps, 'onClick'> {
  title: string;
  onConfirm: () => Promise<void> | void;
  children: React.ReactNode;
}

const ConfirmAction: React.FC<ConfirmActionProps> = ({
  title,
  onConfirm,
  children,
  ...buttonProps
}) => {
  return (
    <Popconfirm
      title={title}
      onConfirm={onConfirm}
      okText="确定"
      cancelText="取消"
    >
      <Button {...buttonProps}>
        {children}
      </Button>
    </Popconfirm>
  );
};

export default ConfirmAction;

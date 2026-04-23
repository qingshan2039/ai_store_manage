/* ========================================
   状态标签组件
   ======================================== */
import React from 'react';
import { Tag } from 'antd';

interface StatusTagProps {
  /** 状态值 */
  value: number;
  /** 状态映射：value → { label, color } */
  statusMap: Record<number, { label: string; color: string }>;
}

const StatusTag: React.FC<StatusTagProps> = ({ value, statusMap }) => {
  const config = statusMap[value];
  if (!config) return <Tag>{value}</Tag>;
  return <Tag color={config.color}>{config.label}</Tag>;
};

export default StatusTag;

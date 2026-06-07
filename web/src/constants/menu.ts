/* ========================================
   菜单配置
   ======================================== */
import type { ReactNode } from 'react';
import {
  DashboardOutlined,
  SettingOutlined,
  TeamOutlined,
  ApartmentOutlined,
  DatabaseOutlined,
  InboxOutlined,
} from '@ant-design/icons';
import React from 'react';

export interface MenuItemConfig {
  key: string;
  label: string;
  icon?: ReactNode;
  children?: MenuItemConfig[];
  permission?: string;
}

export const menuConfig: MenuItemConfig[] = [
  {
    key: '/dashboard',
    label: '工作台',
    icon: React.createElement(DashboardOutlined),
  },
  {
    key: '/system',
    label: '系统管理',
    icon: React.createElement(SettingOutlined),
    children: [
      { key: '/system/users', label: '用户管理', icon: React.createElement(TeamOutlined) },
      { key: '/system/departments', label: '部门管理', icon: React.createElement(ApartmentOutlined) },
    ],
  },
  {
    key: '/base',
    label: '基础数据',
    icon: React.createElement(DatabaseOutlined),
    children: [
      { key: '/base/customers', label: '顾客管理' },
      { key: '/base/products', label: '商品管理' },
      { key: '/base/warehouses', label: '仓库管理' },
      { key: '/base/suppliers', label: '供应商管理' },
    ],
  },
  {
    key: '/inventory',
    label: '库存管理',
    icon: React.createElement(InboxOutlined),
    children: [
      { key: '/inventory/stock', label: '库存查询' },
      { key: '/inventory/inbound', label: '入库管理' },
      { key: '/inventory/outbound', label: '出库管理' },
      { key: '/inventory/transfer', label: '调拨管理' },
      { key: '/inventory/check', label: '盘点管理' },
    ],
  },
];

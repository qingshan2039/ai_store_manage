/* ========================================
   路由表定义
   ======================================== */
import React from 'react';
import { Navigate } from 'react-router-dom';
import type { RouteObject } from 'react-router-dom';

import BlankLayout from '@/layouts/BlankLayout';
import AdminLayout from '@/layouts/AdminLayout';
import AuthGuard from './AuthGuard';

import LoginPage from '@/pages/login/LoginPage';
import DashboardPage from '@/pages/dashboard/DashboardPage';
import NotFound from '@/pages/exception/404';
import Forbidden from '@/pages/exception/403';
import ServerError from '@/pages/exception/500';

import UserListPage from '@/pages/user/UserListPage';
import UserDetailPage from '@/pages/user/UserDetailPage';
import DepartmentListPage from '@/pages/department/DepartmentListPage';
import CustomerListPage from '@/pages/customer/CustomerListPage';
import SupplierListPage from '@/pages/supplier/SupplierListPage';
import WarehouseListPage from '@/pages/warehouse/WarehouseListPage';
import ZoneListPage from '@/pages/zone/ZoneListPage';
import PalletTypeListPage from '@/pages/pallet/PalletTypeListPage';
import MaterialCategoryListPage from '@/pages/material-category/MaterialCategoryListPage';
import SpuListPage from '@/pages/spu/SpuListPage';
import SkuListPage from '@/pages/sku/SkuListPage';
import VehicleListPage from '@/pages/vehicle/VehicleListPage';
import FuelRecordListPage from '@/pages/fuel-record/FuelRecordListPage';
import DriverCheckinListPage from '@/pages/driver-checkin/DriverCheckinListPage';

/** 待开发占位页 */
const ComingSoon: React.FC = () => (
  <div style={{ textAlign: 'center', padding: '80px 0', color: 'rgba(0,0,0,0.25)', fontSize: 16 }}>
    🚧 功能开发中，敬请期待...
  </div>
);

export const routes: RouteObject[] = [
  {
    path: '/login',
    element: <BlankLayout />,
    children: [{ index: true, element: <LoginPage /> }],
  },
  {
    path: '/',
    element: (
      <AuthGuard>
        <AdminLayout />
      </AuthGuard>
    ),
    children: [
      { index: true, element: <Navigate to="/dashboard" replace /> },
      { path: 'dashboard', element: <DashboardPage /> },

      /* ── 系统管理 ── */
      { path: 'system/users', element: <UserListPage /> },
      { path: 'system/users/:id', element: <UserDetailPage /> },
      { path: 'system/departments', element: <DepartmentListPage /> },

      /* ── 基础数据 ── */
      { path: 'base/customers', element: <CustomerListPage /> },
      { path: 'base/products', element: <ComingSoon /> },
      { path: 'base/suppliers', element: <SupplierListPage /> },
      { path: 'base/warehouses', element: <WarehouseListPage /> },
      { path: 'base/zones', element: <ZoneListPage /> },
      { path: 'base/pallet-types', element: <PalletTypeListPage /> },

      /* ── 物料管理（目录：品类/SPU/SKU） ── */
      { path: 'material/categories', element: <MaterialCategoryListPage /> },
      { path: 'material/spus', element: <SpuListPage /> },
      { path: 'material/skus', element: <SkuListPage /> },

      /* ── 运输管理（车辆/打油/打卡） ── */
      { path: 'transport/vehicles', element: <VehicleListPage /> },
      { path: 'transport/fuel-records', element: <FuelRecordListPage /> },
      { path: 'transport/checkins', element: <DriverCheckinListPage /> },

      /* ── 库存管理（预留） ── */
      { path: 'inventory/stock', element: <ComingSoon /> },
      { path: 'inventory/inbound', element: <ComingSoon /> },
      { path: 'inventory/outbound', element: <ComingSoon /> },
      { path: 'inventory/transfer', element: <ComingSoon /> },
      { path: 'inventory/check', element: <ComingSoon /> },

      /* ── 异常页 ── */
      { path: '403', element: <Forbidden /> },
      { path: '404', element: <NotFound /> },
      { path: '500', element: <ServerError /> },
      { path: '*', element: <Navigate to="/404" replace /> },
    ],
  },
];

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
      { path: 'system/users', element: <ComingSoon /> },

      /* ── 基础数据（预留） ── */
      { path: 'base/products', element: <ComingSoon /> },
      { path: 'base/warehouses', element: <ComingSoon /> },
      { path: 'base/suppliers', element: <ComingSoon /> },

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

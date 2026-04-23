/* ========================================
   空白布局 — 登录页等无导航页面使用
   ======================================== */
import React from 'react';
import { Outlet } from 'react-router-dom';

const BlankLayout: React.FC = () => {
  return (
    <div style={{ minHeight: '100vh' }}>
      <Outlet />
    </div>
  );
};

export default BlankLayout;

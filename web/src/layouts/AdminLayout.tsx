/* ========================================
   后台主布局 — 侧边栏 + 顶栏 + 内容区
   ======================================== */
import React from 'react';
import { Layout } from 'antd';
import { Outlet } from 'react-router-dom';
import Sidebar from './components/Sidebar';
import HeaderBar from './components/Header';
import { useAppStore } from '@/stores/useAppStore';

const { Content } = Layout;

const AdminLayout: React.FC = () => {
  const collapsed = useAppStore((s) => s.sidebarCollapsed);

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sidebar />
      <Layout
        style={{
          marginLeft: collapsed ? 64 : 240,
          transition: 'margin-left 0.2s',
        }}
      >
        <HeaderBar />
        <Content
          style={{
            margin: 16,
            padding: 24,
            background: '#fff',
            borderRadius: 8,
            minHeight: 'auto',
          }}
        >
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
};

export default AdminLayout;

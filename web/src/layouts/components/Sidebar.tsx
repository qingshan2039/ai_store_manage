/* ========================================
   侧边栏组件
   ======================================== */
import React, { useMemo } from 'react';
import { Layout, Menu } from 'antd';
import type { MenuProps } from 'antd';
import { useNavigate, useLocation } from 'react-router-dom';
import { menuConfig } from '@/constants/menu';
import type { MenuItemConfig } from '@/constants/menu';
import { useAppStore } from '@/stores/useAppStore';
import { APP_TITLE } from '@/constants/config';

const { Sider } = Layout;

/** 将菜单配置转换为 Ant Design Menu items */
function convertMenuItems(items: MenuItemConfig[]): MenuProps['items'] {
  return items.map((item) => ({
    key: item.key,
    icon: item.icon,
    label: item.label,
    children: item.children ? convertMenuItems(item.children) : undefined,
  }));
}

/** 根据当前路径获取展开的菜单 key */
function getOpenKeys(pathname: string): string[] {
  const parts = pathname.split('/').filter(Boolean);
  if (parts.length > 1) {
    return [`/${parts[0]}`];
  }
  return [];
}

const Sidebar: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const collapsed = useAppStore((s) => s.sidebarCollapsed);
  const setSidebarCollapsed = useAppStore((s) => s.setSidebarCollapsed);

  const menuItems = useMemo(() => convertMenuItems(menuConfig), []);
  const openKeys = useMemo(() => getOpenKeys(location.pathname), [location.pathname]);

  const handleMenuClick: MenuProps['onClick'] = ({ key }) => {
    navigate(key);
  };

  return (
    <Sider
      trigger={null}
      collapsible
      collapsed={collapsed}
      onCollapse={setSidebarCollapsed}
      width={240}
      style={{
        overflow: 'auto',
        height: '100vh',
        position: 'fixed',
        left: 0,
        top: 0,
        bottom: 0,
        zIndex: 100,
      }}
      theme="dark"
    >
      <div
        style={{
          height: 56,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          color: '#fff',
          fontSize: collapsed ? 16 : 18,
          fontWeight: 700,
          letterSpacing: 1,
          borderBottom: '1px solid rgba(255,255,255,0.1)',
          whiteSpace: 'nowrap',
          overflow: 'hidden',
        }}
      >
        {collapsed ? 'WMS' : APP_TITLE}
      </div>
      <Menu
        theme="dark"
        mode="inline"
        selectedKeys={[location.pathname]}
        defaultOpenKeys={openKeys}
        items={menuItems}
        onClick={handleMenuClick}
      />
    </Sider>
  );
};

export default Sidebar;

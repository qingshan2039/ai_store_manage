/* ========================================
   页面容器组件 — 统一页面标题与内容布局
   ======================================== */
import type { ReactNode } from 'react';
import React from 'react';
import { Breadcrumb } from 'antd';
import { useLocation, Link } from 'react-router-dom';

interface PageContainerProps {
  title: string;
  subtitle?: string;
  extra?: ReactNode;
  children: ReactNode;
}

/** 路由路径到面包屑标签的映射 */
const breadcrumbNameMap: Record<string, string> = {
  '/dashboard': '工作台',
  '/system': '系统管理',
  '/system/users': '用户管理',
  '/system/departments': '部门管理',
  '/base': '基础数据',
  '/base/customers': '顾客管理',
  '/base/products': '商品管理',
  '/base/suppliers': '供应商管理',
  '/base/warehouses': '仓库管理',
  '/base/zones': '库区管理',
  '/base/pallet-types': '托盘类型',
  '/material': '物料管理',
  '/material/categories': '物料品类',
  '/material/spus': '物料 SPU',
  '/material/skus': '物料 SKU',
  '/packaging': '包装与条码',
  '/packaging/levels': '包装层级',
  '/packaging/relations': '包装关系',
  '/packaging/barcodes': '条码管理',
  '/packaging/unit-conversions': '计量换算',
  '/packaging/images': '物料图片',
  '/inventory': '库存管理',
  '/inventory/locations': '库位管理',
  '/inventory/lpns': '托盘实例',
  '/inventory/records': '库存查询',
  '/inventory/summary': '库存统计',
  '/inventory/inbound': '入库管理',
  '/inventory/outbound': '出库管理',
  '/inventory/transfer': '调拨管理',
  '/inventory/check': '盘点管理',
};

const PageContainer: React.FC<PageContainerProps> = ({ title, subtitle, extra, children }) => {
  const location = useLocation();
  const pathSnippets = location.pathname.split('/').filter((i) => i);

  const breadcrumbItems = [
    { title: <Link to="/">首页</Link> },
    ...pathSnippets.map((_, index) => {
      const url = `/${pathSnippets.slice(0, index + 1).join('/')}`;
      const name = breadcrumbNameMap[url] || pathSnippets[index];
      const isLast = index === pathSnippets.length - 1;
      return {
        title: isLast ? name : <Link to={url}>{name}</Link>,
      };
    }),
  ];

  return (
    <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      <div style={{ marginBottom: 16 }}>
        <Breadcrumb items={breadcrumbItems} />
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: 8 }}>
          <div>
            <h1 style={{ fontSize: 20, fontWeight: 600, margin: 0, color: 'rgba(0,0,0,0.88)' }}>{title}</h1>
            {subtitle && (
              <p style={{ fontSize: 14, color: 'rgba(0,0,0,0.45)', margin: '4px 0 0 0' }}>{subtitle}</p>
            )}
          </div>
          {extra && <div>{extra}</div>}
        </div>
      </div>
      <div style={{ flex: 1 }}>{children}</div>
    </div>
  );
};

export default PageContainer;

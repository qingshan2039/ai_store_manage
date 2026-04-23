/* ========================================
   工作台（仪表盘）占位页
   ======================================== */
import React from 'react';
import { Card, Row, Col, Statistic } from 'antd';
import {
  InboxOutlined,
  ImportOutlined,
  ExportOutlined,
  TeamOutlined,
} from '@ant-design/icons';
import PageContainer from '@/components/PageContainer';

const DashboardPage: React.FC = () => {
  return (
    <PageContainer title="工作台" subtitle="欢迎使用 WMS 仓库管理系统">
      <Row gutter={[16, 16]}>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="库存总量"
              value={'-'}
              prefix={<InboxOutlined />}
              styles={{ content: { color: '#1677ff' } }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="今日入库"
              value={'-'}
              prefix={<ImportOutlined />}
              styles={{ content: { color: '#52c41a' } }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="今日出库"
              value={'-'}
              prefix={<ExportOutlined />}
              styles={{ content: { color: '#faad14' } }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="在职员工"
              value={'-'}
              prefix={<TeamOutlined />}
              styles={{ content: { color: '#722ed1' } }}
            />
          </Card>
        </Col>
      </Row>
      <Card style={{ marginTop: 16 }}>
        <div style={{ textAlign: 'center', padding: '60px 0', color: 'rgba(0,0,0,0.25)' }}>
          更多数据看板功能即将上线
        </div>
      </Card>
    </PageContainer>
  );
};

export default DashboardPage;

/* ========================================
   搜索表单容器组件
   ======================================== */
import React, { useState } from 'react';
import { Card, Form, Row, Col, Space, Button } from 'antd';
import { DownOutlined, UpOutlined, SearchOutlined, ReloadOutlined } from '@ant-design/icons';
import type { FormInstance } from 'antd';

interface SearchFormProps<T = any> {
  form: FormInstance<T>;
  onSearch: (values: T) => void;
  onReset: () => void;
  children: React.ReactNode;
  /** 默认显示的表单项数量（不含操作按钮） */
  defaultVisibleCount?: number;
}

const SearchForm = <T extends Record<string, any>>({
  form,
  onSearch,
  onReset,
  children,
  defaultVisibleCount = 3,
}: SearchFormProps<T>) => {
  const [expanded, setExpanded] = useState(false);

  // 获取所有的表单项
  const childrenArray = React.Children.toArray(children);
  const showExpand = childrenArray.length > defaultVisibleCount;
  
  const visibleChildren = expanded
    ? childrenArray
    : childrenArray.slice(0, defaultVisibleCount);

  return (
    <Card style={{ marginBottom: 16 }} styles={{ body: { paddingBottom: 0 } }}>
      <Form
        form={form}
        onFinish={onSearch}
        layout="horizontal"
        labelCol={{ span: 6 }}
        wrapperCol={{ span: 18 }}
      >
        <Row gutter={24}>
          {visibleChildren.map((child, index) => (
            <Col span={8} key={index}>
              {child}
            </Col>
          ))}
          
          <Col
            span={8}
            style={{
              textAlign: 'right',
              marginBottom: 24,
              flex: 1,
              marginLeft: 'auto' // 使按钮组靠右
            }}
          >
            <Space>
              <Button onClick={onReset} icon={<ReloadOutlined />}>
                重置
              </Button>
              <Button type="primary" htmlType="submit" icon={<SearchOutlined />}>
                查询
              </Button>
              {showExpand && (
                <a
                  style={{ fontSize: 14, marginLeft: 8 }}
                  onClick={() => setExpanded(!expanded)}
                >
                  {expanded ? '收起' : '展开'}
                  {expanded ? <UpOutlined /> : <DownOutlined />}
                </a>
              )}
            </Space>
          </Col>
        </Row>
      </Form>
    </Card>
  );
};

export default SearchForm;

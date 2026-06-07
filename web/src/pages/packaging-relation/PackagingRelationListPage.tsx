/* 包装关系列表页面 */
import React, { useEffect, useState } from 'react';
import { Card, Table, Button, Space, Form, Switch, Tag, message } from 'antd';
import type { TableProps } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import PageContainer from '@/components/PageContainer';
import ConfirmAction from '@/components/ConfirmAction';
import { packagingRelationApi } from '@/api/packagingRelation';
import { packagingLevelApi } from '@/api/packagingLevel';
import { useTable } from '@/hooks/useTable';
import { useModal } from '@/hooks/useModal';
import { STATUS } from '@/constants/enums';
import type { PackagingRelationSummary, PackagingRelationQueryParams, PackagingRelation } from '@/types/packagingRelation';
import PackagingRelationSearchForm from './components/PackagingRelationSearchForm';
import PackagingRelationFormModal from './components/PackagingRelationFormModal';
import { formatDateTime } from '@/utils/format';

export interface LevelOption {
  label: string;
  value: number;
}

const PackagingRelationListPage: React.FC = () => {
  const [searchForm] = Form.useForm<PackagingRelationQueryParams>();
  const [levelOptions, setLevelOptions] = useState<LevelOption[]>([]);

  const { dataSource, loading, total, page, pageSize, onPageChange, onSearch, onReset, refresh } =
    useTable<PackagingRelationSummary, PackagingRelationQueryParams>({
      fetchFn: packagingRelationApi.list,
      defaultParams: { page: 1, pageSize: 20 },
    });

  const formModal = useModal<PackagingRelation>();

  useEffect(() => {
    packagingLevelApi
      .list({ page: 1, pageSize: 100, status: 1 })
      .then((res) => setLevelOptions(res.data.items.map((l) => ({ label: `${l.levelName}（${l.skuName ?? ''}）`, value: l.id }))))
      .catch(() => {});
  }, []);

  const handleEdit = async (id: number) => {
    try {
      const res = await packagingRelationApi.getById(id);
      formModal.open('edit', res.data);
    } catch (e) {}
  };
  const handleDelete = async (id: number) => {
    try {
      await packagingRelationApi.delete(id);
      message.success('删除成功');
      refresh();
    } catch (e) {}
  };
  const handleStatusChange = async (id: number, checked: boolean) => {
    try {
      await packagingRelationApi.updateStatus(id, { status: checked ? STATUS.ENABLED : STATUS.DISABLED });
      message.success('状态变更成功');
      refresh();
    } catch (e) {}
  };

  const columns: TableProps<PackagingRelationSummary>['columns'] = [
    {
      title: '包装关系',
      key: 'rel',
      width: 280,
      render: (_, r) => `${r.parentLevelName ?? r.parentLevelId} → ${r.childLevelName ?? r.childLevelId}`,
    },
    { title: '含子层数量', dataIndex: 'childQty', key: 'childQty', width: 130 },
    { title: '整托', dataIndex: 'isFixedQty', key: 'isFixedQty', width: 100, render: (v) => (v ? <Tag color="green">定量整托</Tag> : <Tag color="orange">不满托</Tag>) },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (val, record) => (
        <Switch checked={val === STATUS.ENABLED} onChange={(c) => handleStatusChange(record.id, c)} checkedChildren="启用" unCheckedChildren="禁用" />
      ),
    },
    { title: '创建时间', dataIndex: 'createdAt', key: 'createdAt', width: 160, render: (v) => formatDateTime(v) },
    {
      title: '操作',
      key: 'action',
      width: 120,
      fixed: 'right',
      render: (_, record) => (
        <Space size="middle">
          <a onClick={() => handleEdit(record.id)}>编辑</a>
          <ConfirmAction title="确定删除该包装关系吗？" onConfirm={() => handleDelete(record.id)} type="link" danger style={{ padding: 0, height: 'auto' }}>删除</ConfirmAction>
        </Space>
      ),
    },
  ];

  return (
    <PageContainer title="包装关系">
      <PackagingRelationSearchForm form={searchForm} onSearch={onSearch} onReset={onReset} levelOptions={levelOptions} />
      <Card extra={<Button type="primary" icon={<PlusOutlined />} onClick={() => formModal.open('create')}>新建关系</Button>}>
        <Table columns={columns} dataSource={dataSource} rowKey="id" loading={loading} scroll={{ x: 980 }}
          pagination={{ current: page, pageSize, total, showSizeChanger: true, showQuickJumper: true, showTotal: (t) => `共 ${t} 条记录`, onChange: onPageChange }} />
      </Card>
      <PackagingRelationFormModal visible={formModal.visible} mode={formModal.mode} data={formModal.data} levelOptions={levelOptions} onClose={formModal.close} onSuccess={refresh} />
    </PageContainer>
  );
};

export default PackagingRelationListPage;

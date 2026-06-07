/* ========================================
   SPU 列表页面（标准产品单元，品类层）
   ======================================== */
import React, { useEffect, useState } from 'react';
import { Card, Table, Button, Space, Form, Switch, Tag, message } from 'antd';
import type { TableProps } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import PageContainer from '@/components/PageContainer';
import ConfirmAction from '@/components/ConfirmAction';
import { spuApi } from '@/api/spu';
import { materialCategoryApi } from '@/api/materialCategory';
import { useTable } from '@/hooks/useTable';
import { useModal } from '@/hooks/useModal';
import { STATUS } from '@/constants/enums';
import type { SpuSummary, SpuQueryParams, Spu } from '@/types/spu';
import SpuSearchForm from './components/SpuSearchForm';
import SpuFormModal from './components/SpuFormModal';
import { formatDateTime } from '@/utils/format';

export interface CategoryOption {
  label: string;
  value: string;
}

const SpuListPage: React.FC = () => {
  const [searchForm] = Form.useForm<SpuQueryParams>();
  const [categoryOptions, setCategoryOptions] = useState<CategoryOption[]>([]);

  const { dataSource, loading, total, page, pageSize, onPageChange, onSearch, onReset, refresh } =
    useTable<SpuSummary, SpuQueryParams>({
      fetchFn: spuApi.list,
      defaultParams: { page: 1, pageSize: 20 },
    });

  const formModal = useModal<Spu>();

  useEffect(() => {
    materialCategoryApi
      .list({ page: 1, pageSize: 100, status: 1 })
      .then((res) => setCategoryOptions(res.data.items.map((c) => ({ label: c.name, value: c.code }))))
      .catch(() => {});
  }, []);

  const handleCreate = () => formModal.open('create');

  const handleEdit = async (id: number) => {
    try {
      const res = await spuApi.getById(id);
      formModal.open('edit', res.data);
    } catch (e) {}
  };

  const handleDelete = async (id: number) => {
    try {
      await spuApi.delete(id);
      message.success('删除成功');
      refresh();
    } catch (e) {}
  };

  const handleStatusChange = async (id: number, checked: boolean) => {
    try {
      await spuApi.updateStatus(id, { status: checked ? STATUS.ENABLED : STATUS.DISABLED });
      message.success('状态变更成功');
      refresh();
    } catch (e) {}
  };

  const columns: TableProps<SpuSummary>['columns'] = [
    { title: 'SPU 编码', dataIndex: 'spuCode', key: 'spuCode', width: 140 },
    { title: 'SPU 名称', dataIndex: 'spuName', key: 'spuName', width: 200, ellipsis: true },
    { title: '品类', dataIndex: 'categoryName', key: 'categoryName', width: 120, render: (v) => (v ? <Tag color="blue">{v}</Tag> : '-') },
    { title: '基本单位', dataIndex: 'baseUnit', key: 'baseUnit', width: 100 },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (val, record) => (
        <Switch
          checked={val === STATUS.ENABLED}
          onChange={(checked) => handleStatusChange(record.id, checked)}
          checkedChildren="启用"
          unCheckedChildren="禁用"
        />
      ),
    },
    { title: '创建时间', dataIndex: 'createdAt', key: 'createdAt', width: 160, render: (val) => formatDateTime(val) },
    {
      title: '操作',
      key: 'action',
      width: 140,
      fixed: 'right',
      render: (_, record) => (
        <Space size="middle">
          <a onClick={() => handleEdit(record.id)}>编辑</a>
          <ConfirmAction
            title={`确定要删除 SPU [${record.spuName}] 吗？`}
            onConfirm={() => handleDelete(record.id)}
            type="link"
            danger
            style={{ padding: 0, height: 'auto' }}
          >
            删除
          </ConfirmAction>
        </Space>
      ),
    },
  ];

  return (
    <PageContainer title="物料 SPU">
      <SpuSearchForm form={searchForm} onSearch={onSearch} onReset={onReset} categoryOptions={categoryOptions} />

      <Card
        extra={
          <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
            新建 SPU
          </Button>
        }
      >
        <Table
          columns={columns}
          dataSource={dataSource}
          rowKey="id"
          loading={loading}
          scroll={{ x: 1000 }}
          pagination={{
            current: page,
            pageSize: pageSize,
            total: total,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (t) => `共 ${t} 条记录`,
            onChange: onPageChange,
          }}
        />
      </Card>

      <SpuFormModal
        visible={formModal.visible}
        mode={formModal.mode}
        data={formModal.data}
        categoryOptions={categoryOptions}
        onClose={formModal.close}
        onSuccess={refresh}
      />
    </PageContainer>
  );
};

export default SpuListPage;

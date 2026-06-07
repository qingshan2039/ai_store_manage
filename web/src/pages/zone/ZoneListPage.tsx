/* ========================================
   库区列表页面（隶属仓库）
   ======================================== */
import React, { useEffect, useState } from 'react';
import { Card, Table, Button, Space, Form, Switch, Tag, message } from 'antd';
import type { TableProps } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import PageContainer from '@/components/PageContainer';
import ConfirmAction from '@/components/ConfirmAction';
import { zoneApi } from '@/api/zone';
import { warehouseApi } from '@/api/warehouse';
import { useTable } from '@/hooks/useTable';
import { useModal } from '@/hooks/useModal';
import { STATUS } from '@/constants/enums';
import type { ZoneSummary, ZoneQueryParams, Zone } from '@/types/zone';
import ZoneSearchForm from './components/ZoneSearchForm';
import ZoneFormModal from './components/ZoneFormModal';
import { formatDateTime } from '@/utils/format';

export interface WarehouseOption {
  label: string;
  value: number;
}

const ZoneListPage: React.FC = () => {
  const [searchForm] = Form.useForm<ZoneQueryParams>();
  const [warehouseOptions, setWarehouseOptions] = useState<WarehouseOption[]>([]);

  const { dataSource, loading, total, page, pageSize, onPageChange, onSearch, onReset, refresh } =
    useTable<ZoneSummary, ZoneQueryParams>({
      fetchFn: zoneApi.list,
      defaultParams: { page: 1, pageSize: 20 },
    });

  const formModal = useModal<Zone>();

  useEffect(() => {
    warehouseApi
      .list({ page: 1, pageSize: 100 })
      .then((res) => setWarehouseOptions(res.data.items.map((w) => ({ label: w.name, value: w.id }))))
      .catch(() => {});
  }, []);

  const handleCreate = () => formModal.open('create');

  const handleEdit = async (id: number) => {
    try {
      const res = await zoneApi.getById(id);
      formModal.open('edit', res.data);
    } catch (e) {}
  };

  const handleDelete = async (id: number) => {
    try {
      await zoneApi.delete(id);
      message.success('删除成功');
      refresh();
    } catch (e) {}
  };

  const handleStatusChange = async (id: number, checked: boolean) => {
    try {
      await zoneApi.updateStatus(id, { status: checked ? STATUS.ENABLED : STATUS.DISABLED });
      message.success('状态变更成功');
      refresh();
    } catch (e) {}
  };

  const columns: TableProps<ZoneSummary>['columns'] = [
    { title: '所属仓库', dataIndex: 'warehouseName', key: 'warehouseName', width: 160, render: (v) => v || '-' },
    { title: '库区编码', dataIndex: 'code', key: 'code', width: 120 },
    { title: '库区名称', dataIndex: 'name', key: 'name', width: 180, ellipsis: true },
    { title: '类型', dataIndex: 'type', key: 'type', width: 120, render: (v) => (v ? <Tag>{v}</Tag> : '-') },
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
            title={`确定要删除库区 [${record.name}] 吗？`}
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
    <PageContainer title="库区管理">
      <ZoneSearchForm form={searchForm} onSearch={onSearch} onReset={onReset} warehouseOptions={warehouseOptions} />

      <Card
        extra={
          <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
            新建库区
          </Button>
        }
      >
        <Table
          columns={columns}
          dataSource={dataSource}
          rowKey="id"
          loading={loading}
          scroll={{ x: 1100 }}
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

      <ZoneFormModal
        visible={formModal.visible}
        mode={formModal.mode}
        data={formModal.data}
        warehouseOptions={warehouseOptions}
        onClose={formModal.close}
        onSuccess={refresh}
      />
    </PageContainer>
  );
};

export default ZoneListPage;

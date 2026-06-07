/* 库存统计页面（需求②：库存数量 + 托盘数量 + 整托/尾托） */
import React, { useEffect, useState } from 'react';
import { Card, Select, Row, Col, Statistic, Table, Tag, Empty, Spin } from 'antd';
import type { TableProps } from 'antd';
import PageContainer from '@/components/PageContainer';
import { inventoryApi } from '@/api/inventory';
import { skuApi } from '@/api/sku';
import type { InventorySummary, InventoryPallet } from '@/types/inventory';

interface Option {
  label: string;
  value: number;
}

const InventorySummaryPage: React.FC = () => {
  const [skuOptions, setSkuOptions] = useState<Option[]>([]);
  const [skuId, setSkuId] = useState<number | undefined>(undefined);
  const [summary, setSummary] = useState<InventorySummary | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    skuApi.list({ page: 1, pageSize: 100, status: 1 }).then((res) => setSkuOptions(res.data.items.map((s) => ({ label: `${s.skuName}（${s.skuCode}）`, value: s.id })))).catch(() => {});
  }, []);

  const loadSummary = (id: number) => {
    setSkuId(id);
    setLoading(true);
    inventoryApi
      .summary({ skuId: id })
      .then((res) => setSummary(res.data))
      .catch(() => setSummary(null))
      .finally(() => setLoading(false));
  };

  const palletColumns: TableProps<InventoryPallet>['columns'] = [
    { title: '托盘号', dataIndex: 'lpnCode', key: 'lpnCode', render: (v) => v || '-' },
    { title: '该托数量', dataIndex: 'qty', key: 'qty' },
    {
      title: '整托/尾托',
      dataIndex: 'fullPallet',
      key: 'fullPallet',
      render: (v: boolean | null) => (v == null ? <Tag>未知</Tag> : v ? <Tag color="green">整托</Tag> : <Tag color="orange">尾托</Tag>),
    },
  ];

  return (
    <PageContainer title="库存统计" subtitle="选择 SKU 查看库存数量、托盘数量与整托/尾托明细">
      <Card style={{ marginBottom: 16 }}>
        <Select
          style={{ width: 360 }}
          placeholder="请选择 SKU 查看统计"
          options={skuOptions}
          value={skuId}
          onChange={loadSummary}
          showSearch
          optionFilterProp="label"
        />
      </Card>

      <Spin spinning={loading}>
        {summary ? (
          <>
            <Card style={{ marginBottom: 16 }}>
              <Row gutter={16}>
                <Col span={6}><Statistic title="总库存（基本单位）" value={summary.totalQty} /></Col>
                <Col span={6}><Statistic title="托盘数量" value={summary.palletCount} /></Col>
                <Col span={6}><Statistic title="标准每托数" value={summary.standardPalletQty ?? '—'} /></Col>
                <Col span={6}><Statistic title="可用 / 锁定" value={`${summary.totalAvailable} / ${summary.totalReserved}`} /></Col>
              </Row>
            </Card>
            <Card title={`每托明细（${summary.skuName ?? ''}）`}>
              <Table
                columns={palletColumns}
                dataSource={summary.pallets}
                rowKey="lpnId"
                pagination={false}
                locale={{ emptyText: '该 SKU 暂无上托库存' }}
              />
            </Card>
          </>
        ) : (
          !loading && <Empty description="请选择 SKU" />
        )}
      </Spin>
    </PageContainer>
  );
};

export default InventorySummaryPage;

import { describe, it, expect, vi, beforeEach } from 'vitest';

vi.mock('./request', () => ({
  default: {
    get: vi.fn(() => Promise.resolve({ data: {} })),
    post: vi.fn(() => Promise.resolve({ data: {} })),
    put: vi.fn(() => Promise.resolve({ data: {} })),
    patch: vi.fn(() => Promise.resolve({ data: {} })),
    delete: vi.fn(() => Promise.resolve({ data: {} })),
  },
}));

import request from './request';
import { inventoryApi } from './inventory';

const r = request as unknown as Record<'get' | 'post' | 'put' | 'patch' | 'delete', ReturnType<typeof vi.fn>>;

describe('inventoryApi（CRUD 5 端点 + 统计）', () => {
  beforeEach(() => Object.values(r).forEach((fn) => fn.mockClear()));

  it('create → POST /api/inventory', () => {
    const data = { skuId: 1, lpnId: 1, qtyOnHand: 500 };
    inventoryApi.create(data);
    expect(r.post).toHaveBeenCalledWith('/api/inventory', data);
  });
  it('list → GET /api/inventory，按 SKU 过滤', () => {
    inventoryApi.list({ page: 1, pageSize: 20, skuId: 1 });
    expect(r.get).toHaveBeenCalledWith('/api/inventory', { params: { page: 1, pageSize: 20, skuId: 1 } });
  });
  it('summary → GET /api/inventory/summary（需求②）', () => {
    inventoryApi.summary({ skuId: 1 });
    expect(r.get).toHaveBeenCalledWith('/api/inventory/summary', { params: { skuId: 1 } });
  });
  it('getById → GET /api/inventory/:id', () => {
    inventoryApi.getById(9);
    expect(r.get).toHaveBeenCalledWith('/api/inventory/9');
  });
  it('update → PUT /api/inventory/:id', () => {
    inventoryApi.update(9, { qtyOnHand: 480 });
    expect(r.put).toHaveBeenCalledWith('/api/inventory/9', { qtyOnHand: 480 });
  });
  it('delete → DELETE /api/inventory/:id', () => {
    inventoryApi.delete(9);
    expect(r.delete).toHaveBeenCalledWith('/api/inventory/9');
  });
});

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
import { warehouseApi } from './warehouse';

const r = request as unknown as Record<'get' | 'post' | 'put' | 'patch' | 'delete', ReturnType<typeof vi.fn>>;

describe('warehouseApi（严格对齐契约的 6 个端点）', () => {
  beforeEach(() => {
    Object.values(r).forEach((fn) => fn.mockClear());
  });

  it('create → POST /api/warehouses（含类型枚举）', () => {
    const data = { code: 'WH-1', name: '原料仓', type: 'RAW' as const };
    warehouseApi.create(data);
    expect(r.post).toHaveBeenCalledWith('/api/warehouses', data);
  });

  it('list → GET /api/warehouses，按类型过滤', () => {
    warehouseApi.list({ page: 1, pageSize: 20, type: 'FINISHED' });
    expect(r.get).toHaveBeenCalledWith('/api/warehouses', { params: { page: 1, pageSize: 20, type: 'FINISHED' } });
  });

  it('getById → GET /api/warehouses/:id', () => {
    warehouseApi.getById(3);
    expect(r.get).toHaveBeenCalledWith('/api/warehouses/3');
  });

  it('update → PUT /api/warehouses/:id', () => {
    const data = { name: '改名仓', type: 'SEMI' as const };
    warehouseApi.update(3, data);
    expect(r.put).toHaveBeenCalledWith('/api/warehouses/3', data);
  });

  it('delete → DELETE /api/warehouses/:id', () => {
    warehouseApi.delete(3);
    expect(r.delete).toHaveBeenCalledWith('/api/warehouses/3');
  });

  it('updateStatus → PATCH /api/warehouses/:id/status', () => {
    warehouseApi.updateStatus(3, { status: 0 });
    expect(r.patch).toHaveBeenCalledWith('/api/warehouses/3/status', { status: 0 });
  });
});

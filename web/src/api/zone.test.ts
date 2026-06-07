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
import { zoneApi } from './zone';

const r = request as unknown as Record<'get' | 'post' | 'put' | 'patch' | 'delete', ReturnType<typeof vi.fn>>;

describe('zoneApi（严格对齐契约的 6 个端点）', () => {
  beforeEach(() => {
    Object.values(r).forEach((fn) => fn.mockClear());
  });

  it('create → POST /api/zones（隶属仓库）', () => {
    const data = { warehouseId: 1, code: 'Z-A', name: '收货区' };
    zoneApi.create(data);
    expect(r.post).toHaveBeenCalledWith('/api/zones', data);
  });

  it('list → GET /api/zones，按仓库过滤', () => {
    zoneApi.list({ page: 1, pageSize: 20, warehouseId: 5 });
    expect(r.get).toHaveBeenCalledWith('/api/zones', { params: { page: 1, pageSize: 20, warehouseId: 5 } });
  });

  it('getById → GET /api/zones/:id', () => {
    zoneApi.getById(9);
    expect(r.get).toHaveBeenCalledWith('/api/zones/9');
  });

  it('update → PUT /api/zones/:id', () => {
    const data = { name: '改名区' };
    zoneApi.update(9, data);
    expect(r.put).toHaveBeenCalledWith('/api/zones/9', data);
  });

  it('delete → DELETE /api/zones/:id', () => {
    zoneApi.delete(9);
    expect(r.delete).toHaveBeenCalledWith('/api/zones/9');
  });

  it('updateStatus → PATCH /api/zones/:id/status', () => {
    zoneApi.updateStatus(9, { status: 0 });
    expect(r.patch).toHaveBeenCalledWith('/api/zones/9/status', { status: 0 });
  });
});

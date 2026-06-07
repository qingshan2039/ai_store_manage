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
import { packagingLevelApi } from './packagingLevel';

const r = request as unknown as Record<'get' | 'post' | 'put' | 'patch' | 'delete', ReturnType<typeof vi.fn>>;

describe('packagingLevelApi（严格对齐契约的 6 个端点）', () => {
  beforeEach(() => Object.values(r).forEach((fn) => fn.mockClear()));

  it('create → POST /api/packaging-levels', () => {
    const data = { skuId: 1, levelName: '箱', levelSeq: 2, unitCode: 'CTN' };
    packagingLevelApi.create(data);
    expect(r.post).toHaveBeenCalledWith('/api/packaging-levels', data);
  });
  it('list → GET /api/packaging-levels，按 SKU 过滤', () => {
    packagingLevelApi.list({ page: 1, pageSize: 20, skuId: 1 });
    expect(r.get).toHaveBeenCalledWith('/api/packaging-levels', { params: { page: 1, pageSize: 20, skuId: 1 } });
  });
  it('getById → GET /api/packaging-levels/:id', () => {
    packagingLevelApi.getById(3);
    expect(r.get).toHaveBeenCalledWith('/api/packaging-levels/3');
  });
  it('update → PUT /api/packaging-levels/:id', () => {
    packagingLevelApi.update(3, { levelName: '大箱' });
    expect(r.put).toHaveBeenCalledWith('/api/packaging-levels/3', { levelName: '大箱' });
  });
  it('delete → DELETE /api/packaging-levels/:id', () => {
    packagingLevelApi.delete(3);
    expect(r.delete).toHaveBeenCalledWith('/api/packaging-levels/3');
  });
  it('updateStatus → PATCH /api/packaging-levels/:id/status', () => {
    packagingLevelApi.updateStatus(3, { status: 0 });
    expect(r.patch).toHaveBeenCalledWith('/api/packaging-levels/3/status', { status: 0 });
  });
});

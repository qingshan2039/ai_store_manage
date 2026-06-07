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
import { locationApi } from './location';

const r = request as unknown as Record<'get' | 'post' | 'put' | 'patch' | 'delete', ReturnType<typeof vi.fn>>;

describe('locationApi（严格对齐契约的 6 个端点）', () => {
  beforeEach(() => Object.values(r).forEach((fn) => fn.mockClear()));

  it('create → POST /api/locations', () => {
    const data = { warehouseId: 1, code: 'A-01-01', locType: '货架' };
    locationApi.create(data);
    expect(r.post).toHaveBeenCalledWith('/api/locations', data);
  });
  it('list → GET /api/locations，按仓库过滤', () => {
    locationApi.list({ page: 1, pageSize: 20, warehouseId: 1 });
    expect(r.get).toHaveBeenCalledWith('/api/locations', { params: { page: 1, pageSize: 20, warehouseId: 1 } });
  });
  it('getById → GET /api/locations/:id', () => {
    locationApi.getById(3);
    expect(r.get).toHaveBeenCalledWith('/api/locations/3');
  });
  it('update → PUT /api/locations/:id', () => {
    locationApi.update(3, { locType: '地堆' });
    expect(r.put).toHaveBeenCalledWith('/api/locations/3', { locType: '地堆' });
  });
  it('delete → DELETE /api/locations/:id', () => {
    locationApi.delete(3);
    expect(r.delete).toHaveBeenCalledWith('/api/locations/3');
  });
  it('updateStatus → PATCH /api/locations/:id/status', () => {
    locationApi.updateStatus(3, { status: 0 });
    expect(r.patch).toHaveBeenCalledWith('/api/locations/3/status', { status: 0 });
  });
});

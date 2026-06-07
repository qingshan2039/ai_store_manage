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
import { vehicleApi } from './vehicle';

const r = request as unknown as Record<'get' | 'post' | 'put' | 'patch' | 'delete', ReturnType<typeof vi.fn>>;

describe('vehicleApi（严格对齐契约的 6 个端点）', () => {
  beforeEach(() => {
    Object.values(r).forEach((fn) => fn.mockClear());
  });

  it('create → POST /api/vehicles', () => {
    const data = { plateNo: '9924', defaultDriverUserId: 1 };
    vehicleApi.create(data);
    expect(r.post).toHaveBeenCalledWith('/api/vehicles', data);
  });

  it('list → GET /api/vehicles，参数透传', () => {
    vehicleApi.list({ page: 1, pageSize: 20, keyword: '99', status: 1 });
    expect(r.get).toHaveBeenCalledWith('/api/vehicles', { params: { page: 1, pageSize: 20, keyword: '99', status: 1 } });
  });

  it('getById → GET /api/vehicles/:id', () => {
    vehicleApi.getById(7);
    expect(r.get).toHaveBeenCalledWith('/api/vehicles/7');
  });

  it('update → PUT /api/vehicles/:id', () => {
    const data = { defaultDriverOther: '临时工' };
    vehicleApi.update(7, data);
    expect(r.put).toHaveBeenCalledWith('/api/vehicles/7', data);
  });

  it('delete → DELETE /api/vehicles/:id', () => {
    vehicleApi.delete(7);
    expect(r.delete).toHaveBeenCalledWith('/api/vehicles/7');
  });

  it('updateStatus → PATCH /api/vehicles/:id/status', () => {
    vehicleApi.updateStatus(7, { status: 0 });
    expect(r.patch).toHaveBeenCalledWith('/api/vehicles/7/status', { status: 0 });
  });
});

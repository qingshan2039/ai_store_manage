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
import { fuelRecordApi } from './fuelRecord';

const r = request as unknown as Record<'get' | 'post' | 'put' | 'patch' | 'delete', ReturnType<typeof vi.fn>>;

describe('fuelRecordApi（严格对齐契约的 5 个端点，流水无状态切换）', () => {
  beforeEach(() => {
    Object.values(r).forEach((fn) => fn.mockClear());
  });

  it('create → POST /api/fuel-records（含图片数组）', () => {
    const data = { vehicleId: 1, fuelDate: '2026-06-01', images: ['/api/files/a.jpg'] };
    fuelRecordApi.create(data);
    expect(r.post).toHaveBeenCalledWith('/api/fuel-records', data);
  });

  it('list → GET /api/fuel-records，按车辆过滤', () => {
    fuelRecordApi.list({ page: 1, pageSize: 20, vehicleId: 3 });
    expect(r.get).toHaveBeenCalledWith('/api/fuel-records', { params: { page: 1, pageSize: 20, vehicleId: 3 } });
  });

  it('getById → GET /api/fuel-records/:id', () => {
    fuelRecordApi.getById(9);
    expect(r.get).toHaveBeenCalledWith('/api/fuel-records/9');
  });

  it('update → PUT /api/fuel-records/:id', () => {
    const data = { amount: 500 };
    fuelRecordApi.update(9, data);
    expect(r.put).toHaveBeenCalledWith('/api/fuel-records/9', data);
  });

  it('delete → DELETE /api/fuel-records/:id', () => {
    fuelRecordApi.delete(9);
    expect(r.delete).toHaveBeenCalledWith('/api/fuel-records/9');
  });
});

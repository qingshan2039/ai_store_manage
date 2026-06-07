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
import { unitConversionApi } from './unitConversion';

const r = request as unknown as Record<'get' | 'post' | 'put' | 'patch' | 'delete', ReturnType<typeof vi.fn>>;

describe('unitConversionApi（严格对齐契约的 6 个端点）', () => {
  beforeEach(() => Object.values(r).forEach((fn) => fn.mockClear()));

  it('create → POST /api/unit-conversions', () => {
    const data = { skuId: 1, fromUnit: 'ROLL', toUnit: 'M2', factor: 300 };
    unitConversionApi.create(data);
    expect(r.post).toHaveBeenCalledWith('/api/unit-conversions', data);
  });
  it('list → GET /api/unit-conversions，按 SKU 过滤', () => {
    unitConversionApi.list({ page: 1, pageSize: 20, skuId: 1 });
    expect(r.get).toHaveBeenCalledWith('/api/unit-conversions', { params: { page: 1, pageSize: 20, skuId: 1 } });
  });
  it('getById → GET /api/unit-conversions/:id', () => {
    unitConversionApi.getById(6);
    expect(r.get).toHaveBeenCalledWith('/api/unit-conversions/6');
  });
  it('update → PUT /api/unit-conversions/:id', () => {
    unitConversionApi.update(6, { factor: 320 });
    expect(r.put).toHaveBeenCalledWith('/api/unit-conversions/6', { factor: 320 });
  });
  it('delete → DELETE /api/unit-conversions/:id', () => {
    unitConversionApi.delete(6);
    expect(r.delete).toHaveBeenCalledWith('/api/unit-conversions/6');
  });
  it('updateStatus → PATCH /api/unit-conversions/:id/status', () => {
    unitConversionApi.updateStatus(6, { status: 0 });
    expect(r.patch).toHaveBeenCalledWith('/api/unit-conversions/6/status', { status: 0 });
  });
});

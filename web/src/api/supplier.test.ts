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
import { supplierApi } from './supplier';

const r = request as unknown as Record<'get' | 'post' | 'put' | 'patch' | 'delete', ReturnType<typeof vi.fn>>;

describe('supplierApi（严格对齐契约的 6 个端点）', () => {
  beforeEach(() => {
    Object.values(r).forEach((fn) => fn.mockClear());
  });

  it('create → POST /api/suppliers', () => {
    const data = { code: 'SUP-1', name: '供应商A', address: '地址' };
    supplierApi.create(data);
    expect(r.post).toHaveBeenCalledWith('/api/suppliers', data);
  });

  it('list → GET /api/suppliers，参数透传', () => {
    supplierApi.list({ page: 1, pageSize: 20, keyword: '塑源' });
    expect(r.get).toHaveBeenCalledWith('/api/suppliers', { params: { page: 1, pageSize: 20, keyword: '塑源' } });
  });

  it('getById → GET /api/suppliers/:id', () => {
    supplierApi.getById(7);
    expect(r.get).toHaveBeenCalledWith('/api/suppliers/7');
  });

  it('update → PUT /api/suppliers/:id', () => {
    const data = { name: '新名称' };
    supplierApi.update(7, data);
    expect(r.put).toHaveBeenCalledWith('/api/suppliers/7', data);
  });

  it('delete → DELETE /api/suppliers/:id', () => {
    supplierApi.delete(7);
    expect(r.delete).toHaveBeenCalledWith('/api/suppliers/7');
  });

  it('updateStatus → PATCH /api/suppliers/:id/status', () => {
    supplierApi.updateStatus(7, { status: 0 });
    expect(r.patch).toHaveBeenCalledWith('/api/suppliers/7/status', { status: 0 });
  });
});

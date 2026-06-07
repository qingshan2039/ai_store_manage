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
import { customerApi } from './customer';

const r = request as unknown as Record<'get' | 'post' | 'put' | 'patch' | 'delete', ReturnType<typeof vi.fn>>;

describe('customerApi（严格对齐契约的 6 个端点）', () => {
  beforeEach(() => {
    Object.values(r).forEach((fn) => fn.mockClear());
  });

  it('create → POST /api/customers', () => {
    const data = { code: 'C1', name: '公司A', address: '地址', shipAddress: '收货' };
    customerApi.create(data);
    expect(r.post).toHaveBeenCalledWith('/api/customers', data);
  });

  it('list → GET /api/customers，参数透传', () => {
    customerApi.list({ page: 1, pageSize: 20, keyword: '上海' });
    expect(r.get).toHaveBeenCalledWith('/api/customers', { params: { page: 1, pageSize: 20, keyword: '上海' } });
  });

  it('getById → GET /api/customers/:id', () => {
    customerApi.getById(7);
    expect(r.get).toHaveBeenCalledWith('/api/customers/7');
  });

  it('update → PUT /api/customers/:id', () => {
    customerApi.update(7, { shipAddress: '新收货地址' });
    expect(r.put).toHaveBeenCalledWith('/api/customers/7', { shipAddress: '新收货地址' });
  });

  it('delete → DELETE /api/customers/:id', () => {
    customerApi.delete(7);
    expect(r.delete).toHaveBeenCalledWith('/api/customers/7');
  });

  it('updateStatus → PATCH /api/customers/:id/status', () => {
    customerApi.updateStatus(7, { status: 0 });
    expect(r.patch).toHaveBeenCalledWith('/api/customers/7/status', { status: 0 });
  });
});

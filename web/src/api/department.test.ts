import { describe, it, expect, vi, beforeEach } from 'vitest';

// 用 mock 替换底层 axios 实例，只断言 departmentApi 调用了正确的方法与 URL
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
import { departmentApi } from './department';

const r = request as unknown as Record<'get' | 'post' | 'put' | 'patch' | 'delete', ReturnType<typeof vi.fn>>;

describe('departmentApi（严格对齐契约的 6 个端点）', () => {
  beforeEach(() => {
    Object.values(r).forEach((fn) => fn.mockClear());
  });

  it('create → POST /api/departments', () => {
    departmentApi.create({ name: 'X', code: 'X1', type: 'SALES' });
    expect(r.post).toHaveBeenCalledWith('/api/departments', { name: 'X', code: 'X1', type: 'SALES' });
  });

  it('list → GET /api/departments，参数透传', () => {
    departmentApi.list({ page: 1, pageSize: 20, type: 'WAREHOUSE' });
    expect(r.get).toHaveBeenCalledWith('/api/departments', { params: { page: 1, pageSize: 20, type: 'WAREHOUSE' } });
  });

  it('getById → GET /api/departments/:id', () => {
    departmentApi.getById(5);
    expect(r.get).toHaveBeenCalledWith('/api/departments/5');
  });

  it('update → PUT /api/departments/:id', () => {
    departmentApi.update(5, { name: 'Y' });
    expect(r.put).toHaveBeenCalledWith('/api/departments/5', { name: 'Y' });
  });

  it('delete → DELETE /api/departments/:id', () => {
    departmentApi.delete(5);
    expect(r.delete).toHaveBeenCalledWith('/api/departments/5');
  });

  it('updateStatus → PATCH /api/departments/:id/status', () => {
    departmentApi.updateStatus(5, { status: 0 });
    expect(r.patch).toHaveBeenCalledWith('/api/departments/5/status', { status: 0 });
  });
});

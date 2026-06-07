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
import { materialCategoryApi } from './materialCategory';

const r = request as unknown as Record<'get' | 'post' | 'put' | 'patch' | 'delete', ReturnType<typeof vi.fn>>;

describe('materialCategoryApi（严格对齐契约的 6 个端点）', () => {
  beforeEach(() => {
    Object.values(r).forEach((fn) => fn.mockClear());
  });

  it('create → POST /api/material-categories', () => {
    const data = { code: 'CORE', name: '纸管' };
    materialCategoryApi.create(data);
    expect(r.post).toHaveBeenCalledWith('/api/material-categories', data);
  });

  it('list → GET /api/material-categories，参数透传', () => {
    materialCategoryApi.list({ page: 1, pageSize: 20, keyword: '纸' });
    expect(r.get).toHaveBeenCalledWith('/api/material-categories', { params: { page: 1, pageSize: 20, keyword: '纸' } });
  });

  it('getById → GET /api/material-categories/:id', () => {
    materialCategoryApi.getById(3);
    expect(r.get).toHaveBeenCalledWith('/api/material-categories/3');
  });

  it('update → PUT /api/material-categories/:id', () => {
    const data = { name: '改名', sortOrder: 9 };
    materialCategoryApi.update(3, data);
    expect(r.put).toHaveBeenCalledWith('/api/material-categories/3', data);
  });

  it('delete → DELETE /api/material-categories/:id', () => {
    materialCategoryApi.delete(3);
    expect(r.delete).toHaveBeenCalledWith('/api/material-categories/3');
  });

  it('updateStatus → PATCH /api/material-categories/:id/status', () => {
    materialCategoryApi.updateStatus(3, { status: 0 });
    expect(r.patch).toHaveBeenCalledWith('/api/material-categories/3/status', { status: 0 });
  });
});

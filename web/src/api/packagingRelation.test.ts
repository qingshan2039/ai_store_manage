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
import { packagingRelationApi } from './packagingRelation';

const r = request as unknown as Record<'get' | 'post' | 'put' | 'patch' | 'delete', ReturnType<typeof vi.fn>>;

describe('packagingRelationApi（严格对齐契约的 6 个端点）', () => {
  beforeEach(() => Object.values(r).forEach((fn) => fn.mockClear()));

  it('create → POST /api/packaging-relations（每托500）', () => {
    const data = { parentLevelId: 3, childLevelId: 2, childQty: 500, isFixedQty: 1 as const };
    packagingRelationApi.create(data);
    expect(r.post).toHaveBeenCalledWith('/api/packaging-relations', data);
  });
  it('list → GET /api/packaging-relations，按父层过滤', () => {
    packagingRelationApi.list({ page: 1, pageSize: 20, parentLevelId: 3 });
    expect(r.get).toHaveBeenCalledWith('/api/packaging-relations', { params: { page: 1, pageSize: 20, parentLevelId: 3 } });
  });
  it('getById → GET /api/packaging-relations/:id', () => {
    packagingRelationApi.getById(5);
    expect(r.get).toHaveBeenCalledWith('/api/packaging-relations/5');
  });
  it('update → PUT /api/packaging-relations/:id', () => {
    packagingRelationApi.update(5, { childQty: 480, isFixedQty: 0 });
    expect(r.put).toHaveBeenCalledWith('/api/packaging-relations/5', { childQty: 480, isFixedQty: 0 });
  });
  it('delete → DELETE /api/packaging-relations/:id', () => {
    packagingRelationApi.delete(5);
    expect(r.delete).toHaveBeenCalledWith('/api/packaging-relations/5');
  });
  it('updateStatus → PATCH /api/packaging-relations/:id/status', () => {
    packagingRelationApi.updateStatus(5, { status: 0 });
    expect(r.patch).toHaveBeenCalledWith('/api/packaging-relations/5/status', { status: 0 });
  });
});

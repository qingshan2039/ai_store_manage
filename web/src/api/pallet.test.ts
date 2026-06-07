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
import { palletTypeApi } from './pallet';

const r = request as unknown as Record<'get' | 'post' | 'put' | 'patch' | 'delete', ReturnType<typeof vi.fn>>;

describe('palletTypeApi（严格对齐契约的 6 个端点）', () => {
  beforeEach(() => {
    Object.values(r).forEach((fn) => fn.mockClear());
  });

  it('create → POST /api/pallet-types（ISO 规格）', () => {
    const data = { code: 'PLT-L', name: '大托盘 1200×1000', length: 1200, width: 1000 };
    palletTypeApi.create(data);
    expect(r.post).toHaveBeenCalledWith('/api/pallet-types', data);
  });

  it('list → GET /api/pallet-types，参数透传', () => {
    palletTypeApi.list({ page: 1, pageSize: 20, keyword: '欧标' });
    expect(r.get).toHaveBeenCalledWith('/api/pallet-types', { params: { page: 1, pageSize: 20, keyword: '欧标' } });
  });

  it('getById → GET /api/pallet-types/:id', () => {
    palletTypeApi.getById(2);
    expect(r.get).toHaveBeenCalledWith('/api/pallet-types/2');
  });

  it('update → PUT /api/pallet-types/:id', () => {
    const data = { maxLoad: 2000 };
    palletTypeApi.update(2, data);
    expect(r.put).toHaveBeenCalledWith('/api/pallet-types/2', data);
  });

  it('delete → DELETE /api/pallet-types/:id', () => {
    palletTypeApi.delete(2);
    expect(r.delete).toHaveBeenCalledWith('/api/pallet-types/2');
  });

  it('updateStatus → PATCH /api/pallet-types/:id/status', () => {
    palletTypeApi.updateStatus(2, { status: 0 });
    expect(r.patch).toHaveBeenCalledWith('/api/pallet-types/2/status', { status: 0 });
  });
});

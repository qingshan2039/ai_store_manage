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
import { itemImageApi } from './itemImage';

const r = request as unknown as Record<'get' | 'post' | 'put' | 'patch' | 'delete', ReturnType<typeof vi.fn>>;

describe('itemImageApi（严格对齐契约的 6 个端点）', () => {
  beforeEach(() => Object.values(r).forEach((fn) => fn.mockClear()));

  it('create → POST /api/item-images', () => {
    const data = { skuId: 1, imageUrl: 'https://x/a.jpg', imageType: '实体' };
    itemImageApi.create(data);
    expect(r.post).toHaveBeenCalledWith('/api/item-images', data);
  });
  it('list → GET /api/item-images，按 SKU 过滤', () => {
    itemImageApi.list({ page: 1, pageSize: 20, skuId: 1 });
    expect(r.get).toHaveBeenCalledWith('/api/item-images', { params: { page: 1, pageSize: 20, skuId: 1 } });
  });
  it('getById → GET /api/item-images/:id', () => {
    itemImageApi.getById(7);
    expect(r.get).toHaveBeenCalledWith('/api/item-images/7');
  });
  it('update → PUT /api/item-images/:id', () => {
    itemImageApi.update(7, { isPrimary: 1 });
    expect(r.put).toHaveBeenCalledWith('/api/item-images/7', { isPrimary: 1 });
  });
  it('delete → DELETE /api/item-images/:id', () => {
    itemImageApi.delete(7);
    expect(r.delete).toHaveBeenCalledWith('/api/item-images/7');
  });
  it('updateStatus → PATCH /api/item-images/:id/status', () => {
    itemImageApi.updateStatus(7, { status: 0 });
    expect(r.patch).toHaveBeenCalledWith('/api/item-images/7/status', { status: 0 });
  });
});

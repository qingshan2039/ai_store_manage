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
import { barcodeApi } from './barcode';

const r = request as unknown as Record<'get' | 'post' | 'put' | 'patch' | 'delete', ReturnType<typeof vi.fn>>;

describe('barcodeApi（严格对齐契约的 6 个端点）', () => {
  beforeEach(() => Object.values(r).forEach((fn) => fn.mockClear()));

  it('create → POST /api/barcodes', () => {
    const data = { levelId: 2, barcode: '6901234500015', barcodeType: 'EAN13' as const };
    barcodeApi.create(data);
    expect(r.post).toHaveBeenCalledWith('/api/barcodes', data);
  });
  it('list → GET /api/barcodes，关键词+层过滤', () => {
    barcodeApi.list({ page: 1, pageSize: 20, keyword: '690', levelId: 2 });
    expect(r.get).toHaveBeenCalledWith('/api/barcodes', { params: { page: 1, pageSize: 20, keyword: '690', levelId: 2 } });
  });
  it('getById → GET /api/barcodes/:id', () => {
    barcodeApi.getById(4);
    expect(r.get).toHaveBeenCalledWith('/api/barcodes/4');
  });
  it('update → PUT /api/barcodes/:id', () => {
    barcodeApi.update(4, { isPrimary: 1 });
    expect(r.put).toHaveBeenCalledWith('/api/barcodes/4', { isPrimary: 1 });
  });
  it('delete → DELETE /api/barcodes/:id', () => {
    barcodeApi.delete(4);
    expect(r.delete).toHaveBeenCalledWith('/api/barcodes/4');
  });
  it('updateStatus → PATCH /api/barcodes/:id/status', () => {
    barcodeApi.updateStatus(4, { status: 0 });
    expect(r.patch).toHaveBeenCalledWith('/api/barcodes/4/status', { status: 0 });
  });
});

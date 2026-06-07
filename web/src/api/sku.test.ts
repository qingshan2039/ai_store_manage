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
import { skuApi } from './sku';

const r = request as unknown as Record<'get' | 'post' | 'put' | 'patch' | 'delete', ReturnType<typeof vi.fn>>;

describe('skuApi（严格对齐契约的 6 个端点）', () => {
  beforeEach(() => {
    Object.values(r).forEach((fn) => fn.mockClear());
  });

  it('create → POST /api/skus（含 jsonb spec）', () => {
    const data = {
      spuId: 1,
      skuCode: 'PC-340480-A',
      skuName: '纸管 340x480x5mm 规格A',
      itemType: 'RAW' as const,
      lengthMm: 340,
      widthMm: 480,
      spec: { material: '再生纸', grade: 'A' },
    };
    skuApi.create(data);
    expect(r.post).toHaveBeenCalledWith('/api/skus', data);
  });

  it('list → GET /api/skus，按 SPU 与阶段过滤', () => {
    skuApi.list({ page: 1, pageSize: 20, spuId: 1, itemType: 'FINISHED' });
    expect(r.get).toHaveBeenCalledWith('/api/skus', { params: { page: 1, pageSize: 20, spuId: 1, itemType: 'FINISHED' } });
  });

  it('getById → GET /api/skus/:id', () => {
    skuApi.getById(8);
    expect(r.get).toHaveBeenCalledWith('/api/skus/8');
  });

  it('update → PUT /api/skus/:id（替换 spec）', () => {
    const data = { skuName: '改名', spec: { grade: 'B' } };
    skuApi.update(8, data);
    expect(r.put).toHaveBeenCalledWith('/api/skus/8', data);
  });

  it('delete → DELETE /api/skus/:id', () => {
    skuApi.delete(8);
    expect(r.delete).toHaveBeenCalledWith('/api/skus/8');
  });

  it('updateStatus → PATCH /api/skus/:id/status', () => {
    skuApi.updateStatus(8, { status: 0 });
    expect(r.patch).toHaveBeenCalledWith('/api/skus/8/status', { status: 0 });
  });
});

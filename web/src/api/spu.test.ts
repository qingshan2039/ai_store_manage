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
import { spuApi } from './spu';

const r = request as unknown as Record<'get' | 'post' | 'put' | 'patch' | 'delete', ReturnType<typeof vi.fn>>;

describe('spuApi（严格对齐契约的 6 个端点）', () => {
  beforeEach(() => {
    Object.values(r).forEach((fn) => fn.mockClear());
  });

  it('create → POST /api/spus', () => {
    const data = { spuCode: 'CORE-3IN', spuName: '3寸纸管', categoryCode: 'CORE', baseUnit: 'PCS' };
    spuApi.create(data);
    expect(r.post).toHaveBeenCalledWith('/api/spus', data);
  });

  it('list → GET /api/spus，按品类过滤', () => {
    spuApi.list({ page: 1, pageSize: 20, categoryCode: 'CORE' });
    expect(r.get).toHaveBeenCalledWith('/api/spus', { params: { page: 1, pageSize: 20, categoryCode: 'CORE' } });
  });

  it('getById → GET /api/spus/:id', () => {
    spuApi.getById(5);
    expect(r.get).toHaveBeenCalledWith('/api/spus/5');
  });

  it('update → PUT /api/spus/:id', () => {
    const data = { spuName: '改名纸管' };
    spuApi.update(5, data);
    expect(r.put).toHaveBeenCalledWith('/api/spus/5', data);
  });

  it('delete → DELETE /api/spus/:id', () => {
    spuApi.delete(5);
    expect(r.delete).toHaveBeenCalledWith('/api/spus/5');
  });

  it('updateStatus → PATCH /api/spus/:id/status', () => {
    spuApi.updateStatus(5, { status: 0 });
    expect(r.patch).toHaveBeenCalledWith('/api/spus/5/status', { status: 0 });
  });
});

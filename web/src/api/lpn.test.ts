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
import { lpnApi } from './lpn';

const r = request as unknown as Record<'get' | 'post' | 'put' | 'patch' | 'delete', ReturnType<typeof vi.fn>>;

describe('lpnApi（严格对齐契约的 6 个端点）', () => {
  beforeEach(() => Object.values(r).forEach((fn) => fn.mockClear()));

  it('create → POST /api/lpns', () => {
    const data = { lpnCode: 'SSCC-0001', palletTypeId: 1, warehouseId: 1 };
    lpnApi.create(data);
    expect(r.post).toHaveBeenCalledWith('/api/lpns', data);
  });
  it('list → GET /api/lpns，按状态过滤', () => {
    lpnApi.list({ page: 1, pageSize: 20, status: 'IN_STOCK' });
    expect(r.get).toHaveBeenCalledWith('/api/lpns', { params: { page: 1, pageSize: 20, status: 'IN_STOCK' } });
  });
  it('getById → GET /api/lpns/:id', () => {
    lpnApi.getById(2);
    expect(r.get).toHaveBeenCalledWith('/api/lpns/2');
  });
  it('update → PUT /api/lpns/:id', () => {
    lpnApi.update(2, { locationId: 5 });
    expect(r.put).toHaveBeenCalledWith('/api/lpns/2', { locationId: 5 });
  });
  it('delete → DELETE /api/lpns/:id', () => {
    lpnApi.delete(2);
    expect(r.delete).toHaveBeenCalledWith('/api/lpns/2');
  });
  it('updateStatus → PATCH /api/lpns/:id/status（LpnStatus）', () => {
    lpnApi.updateStatus(2, { status: 'EMPTY' });
    expect(r.patch).toHaveBeenCalledWith('/api/lpns/2/status', { status: 'EMPTY' });
  });
});

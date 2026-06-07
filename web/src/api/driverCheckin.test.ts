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
import { driverCheckinApi } from './driverCheckin';

const r = request as unknown as Record<'get' | 'post' | 'put' | 'patch' | 'delete', ReturnType<typeof vi.fn>>;

describe('driverCheckinApi（严格对齐契约的 5 个端点，流水无状态切换）', () => {
  beforeEach(() => {
    Object.values(r).forEach((fn) => fn.mockClear());
  });

  it('create → POST /api/driver-checkins', () => {
    const data = { driverUserId: 1, checkinDate: '2026-06-01', checkinStatus: 'NORMAL' as const };
    driverCheckinApi.create(data);
    expect(r.post).toHaveBeenCalledWith('/api/driver-checkins', data);
  });

  it('list → GET /api/driver-checkins，按司机/状态过滤', () => {
    driverCheckinApi.list({ page: 1, pageSize: 20, driverUserId: 2, checkinStatus: 'LATE' });
    expect(r.get).toHaveBeenCalledWith('/api/driver-checkins', {
      params: { page: 1, pageSize: 20, driverUserId: 2, checkinStatus: 'LATE' },
    });
  });

  it('getById → GET /api/driver-checkins/:id', () => {
    driverCheckinApi.getById(9);
    expect(r.get).toHaveBeenCalledWith('/api/driver-checkins/9');
  });

  it('update → PUT /api/driver-checkins/:id', () => {
    const data = { checkinStatus: 'ABSENT' as const };
    driverCheckinApi.update(9, data);
    expect(r.put).toHaveBeenCalledWith('/api/driver-checkins/9', data);
  });

  it('delete → DELETE /api/driver-checkins/:id', () => {
    driverCheckinApi.delete(9);
    expect(r.delete).toHaveBeenCalledWith('/api/driver-checkins/9');
  });
});

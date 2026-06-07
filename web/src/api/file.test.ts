import { describe, it, expect, vi, beforeEach } from 'vitest';

vi.mock('./request', () => ({
  default: {
    post: vi.fn(() => Promise.resolve({ data: { url: '/api/files/x.jpg' } })),
  },
}));

import request from './request';
import { fileApi } from './file';

const r = request as unknown as Record<'post', ReturnType<typeof vi.fn>>;

describe('fileApi.upload（multipart，移除默认 JSON 头）', () => {
  beforeEach(() => {
    r.post.mockClear();
  });

  it('upload → POST /api/files，FormData + Content-Type 置 null', () => {
    const file = new File(['x'], 'ticket.png', { type: 'image/png' });
    fileApi.upload(file);
    expect(r.post).toHaveBeenCalledWith('/api/files', expect.any(FormData), { headers: { 'Content-Type': null } });
  });
});

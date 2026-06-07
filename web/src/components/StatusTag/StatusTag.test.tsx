import React from 'react';
import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import StatusTag from './index';

void React;

const STATUS_MAP: Record<number, { label: string; color: string }> = {
  0: { label: '禁用', color: 'red' },
  1: { label: '启用', color: 'green' },
};

describe('StatusTag', () => {
  it('按映射渲染状态文字', () => {
    render(<StatusTag value={1} statusMap={STATUS_MAP} />);
    expect(screen.getByText('启用')).toBeInTheDocument();
  });

  it('未知状态回退显示原值', () => {
    render(<StatusTag value={9} statusMap={STATUS_MAP} />);
    expect(screen.getByText('9')).toBeInTheDocument();
  });
});

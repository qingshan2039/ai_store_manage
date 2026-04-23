/* ========================================
   通用弹窗 Hook
   ======================================== */
import { useState, useCallback } from 'react';
import type { ModalMode } from '@/types/common';

interface UseModalReturn<T> {
  visible: boolean;
  mode: ModalMode;
  data: T | null;
  open: (mode: ModalMode, data?: T) => void;
  close: () => void;
}

export function useModal<T = unknown>(): UseModalReturn<T> {
  const [visible, setVisible] = useState(false);
  const [mode, setMode] = useState<ModalMode>('create');
  const [data, setData] = useState<T | null>(null);

  const open = useCallback((newMode: ModalMode, newData?: T) => {
    setMode(newMode);
    setData(newData ?? null);
    setVisible(true);
  }, []);

  const close = useCallback(() => {
    setVisible(false);
    setData(null);
  }, []);

  return { visible, mode, data, open, close };
}

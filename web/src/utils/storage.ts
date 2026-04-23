/* ========================================
   localStorage 封装
   ======================================== */

export const storage = {
  get<T = string>(key: string): T | null {
    try {
      const value = localStorage.getItem(key);
      if (value === null) return null;
      return JSON.parse(value) as T;
    } catch {
      return localStorage.getItem(key) as T | null;
    }
  },

  set(key: string, value: unknown): void {
    localStorage.setItem(key, JSON.stringify(value));
  },

  remove(key: string): void {
    localStorage.removeItem(key);
  },

  clear(): void {
    localStorage.clear();
  },
};

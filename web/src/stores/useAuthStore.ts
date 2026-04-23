/* ========================================
   认证状态 Store
   ======================================== */
import { create } from 'zustand';
import type { UserInfo } from '@/types/auth';
import { storage } from '@/utils/storage';
import { TOKEN_KEY, USER_INFO_KEY } from '@/constants/config';

interface AuthState {
  token: string | null;
  user: UserInfo | null;
  isAuthenticated: boolean;
  login: (token: string, user: UserInfo) => void;
  logout: () => void;
  setUser: (user: UserInfo) => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  token: storage.get<string>(TOKEN_KEY),
  user: storage.get<UserInfo>(USER_INFO_KEY),
  isAuthenticated: !!storage.get<string>(TOKEN_KEY),

  login: (token: string, user: UserInfo) => {
    storage.set(TOKEN_KEY, token);
    storage.set(USER_INFO_KEY, user);
    set({ token, user, isAuthenticated: true });
  },

  logout: () => {
    storage.remove(TOKEN_KEY);
    storage.remove(USER_INFO_KEY);
    set({ token: null, user: null, isAuthenticated: false });
  },

  setUser: (user: UserInfo) => {
    storage.set(USER_INFO_KEY, user);
    set({ user });
  },
}));

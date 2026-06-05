import { setToken } from '../api/client.js';

const STORAGE_KEYS = {
  ROLE: 'auth_role',
  RESTAURANT_ID: 'auth_restaurantId',
  USER_INFO: 'auth_userInfo',
};

/**
 * @param {{ role: string, restaurantId: number|null, userInfo: { email: string, username: string } }} auth
 */
export function saveAuthToStorage({ role, restaurantId, userInfo }) {
  localStorage.setItem(STORAGE_KEYS.ROLE, role);
  localStorage.setItem(
    STORAGE_KEYS.RESTAURANT_ID,
    restaurantId === null ? 'null' : String(restaurantId),
  );
  localStorage.setItem(STORAGE_KEYS.USER_INFO, JSON.stringify(userInfo));
}

/**
 * @returns {{ role: string, restaurantId: number|null, userInfo: { email: string, username: string } } | null}
 */
export function loadAuthFromStorage() {
  const role = localStorage.getItem(STORAGE_KEYS.ROLE);
  const restaurantIdRaw = localStorage.getItem(STORAGE_KEYS.RESTAURANT_ID);
  const userInfoRaw = localStorage.getItem(STORAGE_KEYS.USER_INFO);

  if (!role || !userInfoRaw) {
    return null;
  }

  let userInfo;
  try {
    userInfo = JSON.parse(userInfoRaw);
  } catch {
    return null;
  }

  const restaurantId =
    restaurantIdRaw === null || restaurantIdRaw === 'null'
      ? null
      : Number(restaurantIdRaw);

  return { role, restaurantId, userInfo };
}

export function clearAuthStorage() {
  localStorage.removeItem(STORAGE_KEYS.ROLE);
  localStorage.removeItem(STORAGE_KEYS.RESTAURANT_ID);
  localStorage.removeItem(STORAGE_KEYS.USER_INFO);
  localStorage.removeItem('user');
  setToken(null);
}

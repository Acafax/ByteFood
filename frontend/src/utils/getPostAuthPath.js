/**
 * @param {{ isAuthenticated: boolean, role: string|null, restaurantId: number|null }} auth
 * @returns {string}
 */
export function getPostAuthPath({ isAuthenticated, restaurantId }) {
  if (!isAuthenticated) return '/login';
  if (restaurantId != null) return '/dashboard';
  return '/welcome';
}

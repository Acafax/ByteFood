import { post, setToken } from './client.js';

/**
 * Login user
 */
export const login = async (email, password) => {
  const response = await post('/auth/login', { email, password });
  setToken(response.token);
  return response;
};

/**
 * Register user
 */
export const register = async (email, password, name, lastName) => {
  const response = await post('/auth/register', { email, password, name, lastName });
  setToken(response.token);
  return response;
};

import API_CONFIG from './config.js';
import mockApiClient from './mockClient.js';

const MOCK_MODE = import.meta.env.VITE_MOCK_MODE === 'true';

/**
 * Get JWT token from localStorage
 */
const getToken = () => localStorage.getItem('token');

/**
 * Set JWT token in localStorage
 */
const setToken = (token) => {
  if (token) {
    localStorage.setItem('token', token);
  } else {
    localStorage.removeItem('token');
  }
};

/**
 * Base API client function
 */
const apiClient = async (endpoint, options = {}) => {
  if (MOCK_MODE) {
    return mockApiClient(endpoint, options);
  }

  const token = getToken();

  const config = {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(token && { Authorization: `Bearer ${token}` }),
      ...options.headers,
    },
  };

  const url = endpoint.startsWith('http')
    ? endpoint
    : `${API_CONFIG.BASE_URL}${endpoint}`;

  try {
    const response = await fetch(url, config);

    const contentType = response.headers.get('content-type');
    const isJson = contentType && contentType.includes('application/json');
    const data = isJson ? await response.json() : await response.text();

    if (!response.ok) {
      if (response.status === 401) {
        setToken(null);
        const error = new Error(data.message || 'Authentication failed. Please log in again.');
        error.status = 401;
        error.data = data;
        throw error;
      }

      const error = new Error(data.message || data.error || `HTTP error! status: ${response.status}`);
      error.status = response.status;
      error.data = data;
      throw error;
    }

    return data;
  } catch (error) {
    if (error.name === 'TypeError' && error.message.includes('fetch')) {
      throw new Error('Network error. Please check your connection.');
    }
    throw error;
  }
};

/** GET request */
export const get = (endpoint, options = {}) =>
  apiClient(endpoint, { ...options, method: 'GET' });

/** POST request */
export const post = (endpoint, data, options = {}) =>
  apiClient(endpoint, { ...options, method: 'POST', body: JSON.stringify(data) });

/** PUT request */
export const put = (endpoint, data, options = {}) =>
  apiClient(endpoint, { ...options, method: 'PUT', body: JSON.stringify(data) });

/** DELETE request */
export const del = (endpoint, options = {}) =>
  apiClient(endpoint, { ...options, method: 'DELETE' });

export { getToken, setToken };
export default apiClient;

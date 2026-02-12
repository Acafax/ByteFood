// API Configuration
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api';

export const API_CONFIG = {
  BASE_URL: API_BASE_URL,
  TIMEOUT: 30000, // 30 seconds
};

export default API_CONFIG;

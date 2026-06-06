import mockData from './mockData.js';

/** Simulate network latency */
const delay = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

const MOCK_DELAY_MIN = 200;
const MOCK_DELAY_RANGE = 300;

let mockOnboardingRestaurantId = null;

/**
 * Mock API client – returns predefined responses from mockData.
 */
const mockApiClient = async (endpoint, options = {}) => {
  await delay(MOCK_DELAY_MIN + Math.random() * MOCK_DELAY_RANGE);

  const normalizedEndpoint = endpoint.startsWith('/') ? endpoint : `/${endpoint}`;

  if (normalizedEndpoint === '/onboarding/restaurant' && options.method === 'POST') {
    const body = options.body ? JSON.parse(options.body) : {};
    mockOnboardingRestaurantId = 99;
    return {
      restaurantId: 99,
      restaurantName: body.restaurantName || 'Mock Restaurant',
      stockId: 99,
      stockName: body.stockName || 'Mock Warehouse',
    };
  }

  if (normalizedEndpoint === '/auth/me' && options.method === 'GET') {
    return {
      restaurantId: mockOnboardingRestaurantId,
      email: 'dev@example.com',
      username: 'Dev',
      role: 'MANAGER',
    };
  }

  // Exact match first
  let mockResponse = mockData[normalizedEndpoint];

  // Prefix match fallback
  if (mockResponse === undefined) {
    for (const [key, value] of Object.entries(mockData)) {
      if (normalizedEndpoint.startsWith(key)) {
        mockResponse = value;
        break;
      }
    }
  }

  // For write operations return generic success when no mock exists
  if (mockResponse === undefined && ['POST', 'PUT', 'DELETE'].includes(options.method)) {
    return {
      success: true,
      message: 'Mock operation successful',
      id: Math.floor(Math.random() * 1000),
    };
  }

  // For GET return empty collection when no mock exists
  if (mockResponse === undefined) {
    return {};
  }

  return mockResponse;
};

export default mockApiClient;

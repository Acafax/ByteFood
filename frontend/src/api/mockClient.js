import mockData, { mockHandlers } from './mockData.js';

/** Simulate network latency */
const delay = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

const MOCK_DELAY_MIN = 200;
const MOCK_DELAY_RANGE = 300;

let mockOnboardingRestaurantId = null;

const POST_HANDLERS = {
  '/products': mockHandlers.postProducts,
  '/subcategory': mockHandlers.postSubcategory,
  '/modifications': mockHandlers.postModifications,
  '/combos': mockHandlers.postCombos,
  '/semi-products': mockHandlers.postSemiProducts,
};

/**
 * Mock API client – returns predefined responses from mockData.
 */
const mockApiClient = async (endpoint, options = {}) => {
  await delay(MOCK_DELAY_MIN + Math.random() * MOCK_DELAY_RANGE);

  const normalizedEndpoint = endpoint.startsWith('/') ? endpoint : `/${endpoint}`;
  const method = options.method || 'GET';

  if (normalizedEndpoint === '/onboarding/restaurant' && method === 'POST') {
    const body = options.body ? JSON.parse(options.body) : {};
    mockOnboardingRestaurantId = 99;
    return {
      restaurantId: 99,
      restaurantName: body.restaurantName || 'Mock Restaurant',
      stockId: 99,
      stockName: body.stockName || 'Mock Warehouse',
    };
  }

  if (normalizedEndpoint === '/auth/me' && method === 'GET') {
    return {
      restaurantId: mockOnboardingRestaurantId,
      email: 'dev@example.com',
      username: 'Dev',
      role: 'MANAGER',
    };
  }

  if (method === 'POST' && POST_HANDLERS[normalizedEndpoint]) {
    return POST_HANDLERS[normalizedEndpoint](options.body);
  }

  let mockResponse = mockData[normalizedEndpoint];

  if (mockResponse === undefined) {
    for (const [key, value] of Object.entries(mockData)) {
      if (normalizedEndpoint.startsWith(key)) {
        mockResponse = value;
        break;
      }
    }
  }

  if (mockResponse === undefined && ['POST', 'PUT', 'DELETE'].includes(method)) {
    return {
      success: true,
      message: 'Mock operation successful',
      id: Math.floor(Math.random() * 1000),
    };
  }

  if (mockResponse === undefined) {
    return {};
  }

  return mockResponse;
};

export default mockApiClient;

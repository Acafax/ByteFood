import { post } from './client.js';

/**
 * @typedef {Object} CreateRestaurantPayload
 * @property {string} restaurantName
 * @property {string} stockName
 */

/**
 * @typedef {Object} RestaurantOnboardingResponse
 * @property {number} restaurantId
 * @property {string} restaurantName
 * @property {number} stockId
 * @property {string} stockName
 */

/**
 * @param {CreateRestaurantPayload} payload
 * @returns {Promise<RestaurantOnboardingResponse>}
 */
export const createRestaurant = (payload) => post('/onboarding/restaurant', payload);

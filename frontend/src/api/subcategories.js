import { get, post } from './client.js';

/**
 * @typedef {Object} CreateSubcategoryDto
 * @property {string} subcategoryName
 */

/**
 * @typedef {Object} SubcategoryDto
 * @property {number} id
 * @property {string} subcategory_name
 * @property {number} restaurantId
 */

/**
 * @returns {Promise<SubcategoryDto[]>}
 */
export const getSubcategories = () => get('/subcategory');

/**
 * @param {CreateSubcategoryDto} payload
 * @returns {Promise<SubcategoryDto>}
 */
export const createSubcategory = (payload) => post('/subcategory', payload);

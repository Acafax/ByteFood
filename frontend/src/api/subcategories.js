import { get, post } from './client.js';

/**
 * @typedef {Object} CreateSubcategoryDto
 * @property {string} subcategoryName - Nazwa nowej subkategorii (@NotBlank)
 */

/**
 * @typedef {Object} SubcategoryDto
 * @property {number} id
 * @property {string} subcategory_name - Nazwa subkategorii (snake_case z backendu)
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

import { get, post } from './client.js';

/**
 * @typedef {Object} SubcategoryRef
 * @property {number} id - ID subkategorii (referencja do encji Subcategory)
 */

/**
 * @typedef {Object} CreateSemiProductDto
 * @property {string} name - Nazwa (@NotBlank)
 * @property {number} carbohydrate - Węglowodany w g (@NotNull, >= 0)
 * @property {number} fat - Tłuszcz w g (@NotNull, >= 0)
 * @property {number} protein - Białko w g (@NotNull, >= 0)
 * @property {string} unit - Jednostka miary, np. G, ML, PCS (@NotNull)
 * @property {SubcategoryRef} subcategory - Subkategoria składnika (@NotNull)
 * @property {number} minimalStockQuantity - Minimalny zapas (@NotNull, >= 0)
 */

/**
 * @typedef {Object} SemiProductDto
 * @property {number} id
 * @property {string} name
 * @property {number} carbohydrate
 * @property {number} fat
 * @property {number} protein
 * @property {string} unit
 * @property {Object} subcategory - Encja Subcategory z backendu
 * @property {number} restaurantId
 */

/**
 * @returns {Promise<SemiProductDto[]>}
 */
export const getSemiProducts = () => get('/semi-products');

/**
 * @param {CreateSemiProductDto} payload
 * @returns {Promise<SemiProductDto>}
 */
export const createSemiProduct = (payload) => post('/semi-products', payload);

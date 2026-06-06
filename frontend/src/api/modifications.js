import { post } from './client.js';

/**
 * @typedef {import('./semiProducts.js').SubcategoryRef} SubcategoryRef
 */

/**
 * @typedef {Object} CreateModificationTemplateDto
 * @property {string} name - Nazwa modyfikacji (@NotBlank)
 * @property {number} price - Cena (@NotNull, >= 0)
 * @property {SubcategoryRef} subcategory - Subkategoria, do której należy modyfikacja (@NotNull)
 * @property {number|null} semiProductId - ID półproduktu; null przy zamianie/operacji bez składnika
 */

/**
 * @typedef {Object} ModificationTemplateDto
 * @property {number} id
 * @property {string} name
 * @property {number} price
 * @property {Object} subcategory - Encja Subcategory
 * @property {number|null} semiProductId
 * @property {number} restaurantId
 */

/**
 * @param {CreateModificationTemplateDto} payload
 * @returns {Promise<ModificationTemplateDto>}
 */
export const createModification = (payload) => post('/modifications', payload);

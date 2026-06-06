import { post } from './client.js';

/**
 * @typedef {Object} CreateComboProductDto
 * @property {number} productId - ID produktu (min 1)
 * @property {number} quantity - Ilość sztuk (min 1)
 */

/**
 * @typedef {Object} CreateComboDto
 * @property {string} name - Nazwa zestawu (@NotBlank)
 * @property {number} price - Cena (@NotNull, min 0.01)
 * @property {CreateComboProductDto[]} components - Składniki zestawu (@NotEmpty)
 */

/**
 * @typedef {Object} ComboProductDto
 * @property {number} quantity
 * @property {import('./products.js').ProductDto} productDto
 */

/**
 * @typedef {Object} ComboDetailsDto
 * @property {string} name
 * @property {number} price
 * @property {ComboProductDto[]} comboProduct
 */

/**
 * @param {CreateComboDto} payload
 * @returns {Promise<ComboDetailsDto>}
 */
export const createCombo = (payload) => post('/combos', payload);

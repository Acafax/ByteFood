import { get, post } from './client.js';

/**
 * @typedef {Object} CreateProductSemiProductsDto
 * @property {number} semiProductId - ID półproduktu (min 1)
 * @property {number} quantity - Ilość w gramach (musi być > 0)
 */

/**
 * @typedef {Object} CreateProductDto
 * @property {string} name - Nazwa produktu (@NotBlank)
 * @property {string} category - Kategoria menu, np. BURGER, DRINK (@NotBlank)
 * @property {number} price - Cena (@NotNull, >= 0)
 * @property {CreateProductSemiProductsDto[]} productsSemiProducts - Składniki (@NotEmpty)
 * @property {string} [image] - Opcjonalny base64 data URL zdjęcia produktu
 */

/**
 * @typedef {Object} ProductDto
 * @property {number} id
 * @property {string} name
 * @property {string} category
 * @property {number} price
 * @property {number} restaurantId
 * @property {string} [imagePath] - Ścieżka lub data URL zdjęcia
 */

/**
 * @param {CreateProductDto} payload
 * @returns {Promise<ProductDto>}
 */
export const createProduct = (payload) => post('/products', payload);

/**
 * @returns {Promise<ProductDto[]>}
 */
export const getProducts = () => get('/products');

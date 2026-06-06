import { post } from './client.js';

/**
 * @typedef {Object} CreateEmployeePayload
 * @property {string} email
 * @property {string} password
 * @property {string} name
 * @property {string} lastName
 */

/**
 * @typedef {Object} EmployeeResponse
 * @property {number} id
 * @property {string} email
 * @property {string} name
 * @property {string} lastName
 * @property {'EMPLOYEE'} role
 */

/**
 * @param {CreateEmployeePayload} payload
 * @returns {Promise<EmployeeResponse>}
 */
export const createEmployee = (payload) => post('/employees', payload);

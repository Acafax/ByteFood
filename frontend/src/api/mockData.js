/**
 * Mock responses for different API endpoints.
 * Used only when VITE_MOCK_MODE=true.
 *
 * Field names match the real backend DTOs (English names, enum unit strings).
 */
const mockData = {
  '/auth/login': {
    token: 'mock-token-123',
    restaurantId: 1,
    expirationTime: Date.now() + 86400000,
  },
  '/auth/register': {
    token: 'mock-token-123',
    restaurantId: 1,
    expirationTime: Date.now() + 86400000,
  },
  '/units': ['G', 'ML', 'PCS'],
  '/categories': ['BURGER', 'DRINK', 'SITE'],
  '/semi-products': [
    { id: 1, name: 'Mąka', unit: 'KG', fat: 1.0, carbohydrate: 75.0, protein: 10.0 },
    { id: 2, name: 'Pomidory', unit: 'KG', fat: 0.2, carbohydrate: 3.9, protein: 0.9 },
    { id: 3, name: 'Ser Cheddar', unit: 'KG', fat: 33.0, carbohydrate: 1.3, protein: 25.0 },
    { id: 4, name: 'Mięso wołowe', unit: 'KG', fat: 15.0, carbohydrate: 0.0, protein: 26.0 },
    { id: 5, name: 'Sałata', unit: 'KG', fat: 0.2, carbohydrate: 2.9, protein: 1.4 },
    { id: 6, name: 'Cebula', unit: 'KG', fat: 0.1, carbohydrate: 9.3, protein: 1.1 },
  ],
  '/products': {
    success: true,
    message: 'Produkt został pomyślnie utworzony',
    id: Math.floor(Math.random() * 1000),
  },
  '/modification/': {
    success: true,
    message: 'Modyfikacja została pomyślnie utworzona',
    id: Math.floor(Math.random() * 1000),
  },
  '/stock': [
    {
      id: 3,
      purchasePrice: 8.00,
      quantity: 0.500,
      expirationDate: '2026-02-09T22:15:07.126157',
      stockItemDictionary: {
        id: 100,
        name: 'Sałata Lodowa (Luz)',
        price: 8.00,
        minimalStockQuantity: 5.000,
        unit: 'KG',
      },
      restaurantId: 1,
    },
    {
      id: 2,
      purchasePrice: 12.00,
      quantity: 3.000,
      expirationDate: '2026-02-12T22:15:07.126157',
      stockItemDictionary: {
        id: 101,
        name: 'Worek Sałaty 1.5kg',
        price: 12.00,
        minimalStockQuantity: 10.000,
        unit: 'PCS',
      },
      restaurantId: 1,
    },
    {
      id: 1,
      purchasePrice: 65.00,
      quantity: 2.000,
      expirationDate: '2026-02-14T22:15:07.126157',
      stockItemDictionary: {
        id: 102,
        name: 'Karton Sałaty (6 worków)',
        price: 65.00,
        minimalStockQuantity: 2.000,
        unit: 'PCS',
      },
      restaurantId: 1,
    },
    {
      id: 6,
      purchasePrice: 1.50,
      quantity: 5.000,
      expirationDate: '2026-02-09T22:15:07.126157',
      stockItemDictionary: {
        id: 200,
        name: 'Bułka Brioche (Sztuka)',
        price: 1.50,
        minimalStockQuantity: 50.000,
        unit: 'PCS',
      },
      restaurantId: 1,
    },
    {
      id: 5,
      purchasePrice: 16.00,
      quantity: 2.000,
      expirationDate: '2026-02-10T22:15:07.126157',
      stockItemDictionary: {
        id: 201,
        name: 'Zgrzewka Brioche (12 szt)',
        price: 16.00,
        minimalStockQuantity: 10.000,
        unit: 'PCS',
      },
      restaurantId: 1,
    },
    {
      id: 4,
      purchasePrice: 150.00,
      quantity: 5.000,
      expirationDate: '2026-02-10T22:15:07.126157',
      stockItemDictionary: {
        id: 202,
        name: 'Kosz Brioche (120 szt)',
        price: 150.00,
        minimalStockQuantity: 1.000,
        unit: 'PCS',
      },
      restaurantId: 1,
    },
    {
      id: 7,
      purchasePrice: 95.00,
      quantity: 10.000,
      expirationDate: '2026-02-17T22:15:07.126157',
      stockItemDictionary: {
        id: 301,
        name: 'Paczka Wołowina Vacuum 2.5kg',
        price: 95.00,
        minimalStockQuantity: 8.000,
        unit: 'PCS',
      },
      restaurantId: 1,
    },
    {
      id: 9,
      purchasePrice: 10.00,
      quantity: 0.800,
      expirationDate: '2026-03-07T22:15:07.126157',
      stockItemDictionary: {
        id: 400,
        name: 'Ketchup (Luz/Dystrybutor)',
        price: 10.00,
        minimalStockQuantity: 2.000,
        unit: 'L',
      },
      restaurantId: 1,
    },
    {
      id: 8,
      purchasePrice: 55.00,
      quantity: 4.000,
      expirationDate: '2026-08-07T22:15:07.126157',
      stockItemDictionary: {
        id: 402,
        name: 'Zgrzewka Ketchup (6x1L)',
        price: 55.00,
        minimalStockQuantity: 2.000,
        unit: 'PCS',
      },
      restaurantId: 1,
    },
  ],
};

export default mockData;

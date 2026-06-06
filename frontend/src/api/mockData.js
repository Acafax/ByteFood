/**
 * Mock responses for different API endpoints.
 * Used only when VITE_MOCK_MODE=true.
 *
 * Field names match the real backend DTOs (English names, enum unit strings).
 */
const mockData = {
  '/auth/login': {
    time: new Date().toISOString(),
    token: 'mock-token-123',
    expirationTime: Date.now() + 86400000,
  },
  '/auth/register': {
    time: new Date().toISOString(),
    token: 'mock-token-123',
    expirationTime: Date.now() + 86400000,
  },
  '/auth/me': {
    restaurantId: null,
    email: 'dev@example.com',
    username: 'Dev',
    role: 'MANAGER',
  },
  '/onboarding/restaurant': {
    restaurantId: 99,
    restaurantName: 'Mock Restaurant',
    stockId: 99,
    stockName: 'Mock Warehouse',
  },
  '/employees': {
    id: 100,
    email: 'employee@mock.com',
    name: 'Jan',
    lastName: 'Kowalski',
    role: 'EMPLOYEE',
  },
  '/units': ['G', 'ML', 'PCS'],
  '/categories': ['BURGER', 'DRINK', 'SITE'],
  '/subcategory': [
    { id: 1, subcategory_name: 'buns', restaurantId: 1 },
    { id: 2, subcategory_name: 'patties', restaurantId: 1 },
    { id: 3, subcategory_name: 'cheeses', restaurantId: 1 },
    { id: 4, subcategory_name: 'vegetables', restaurantId: 1 },
    { id: 5, subcategory_name: 'addons', restaurantId: 1 },
    { id: 6, subcategory_name: 'sauces', restaurantId: 1 },
  ],
  '/semi-products': [
    { id: 1, name: 'Mąka', unit: 'G', fat: 1.0, carbohydrate: 75.0, protein: 10.0, restaurantId: 1 },
    { id: 2, name: 'Pomidory', unit: 'G', fat: 0.2, carbohydrate: 3.9, protein: 0.9, restaurantId: 1 },
    { id: 3, name: 'Ser Cheddar', unit: 'G', fat: 33.0, carbohydrate: 1.3, protein: 25.0, restaurantId: 1 },
    { id: 4, name: 'Mięso wołowe', unit: 'G', fat: 15.0, carbohydrate: 0.0, protein: 26.0, restaurantId: 1 },
    { id: 5, name: 'Sałata', unit: 'G', fat: 0.2, carbohydrate: 2.9, protein: 1.4, restaurantId: 1 },
    { id: 6, name: 'Cebula', unit: 'G', fat: 0.1, carbohydrate: 9.3, protein: 1.1, restaurantId: 1 },
  ],
  '/products': [
    { id: 1, name: 'Classic Burger 180g', category: 'BURGER', price: 32.00, restaurantId: 1, imagePath: 'default_product_image.png' },
    { id: 2, name: 'Junior Classic Burger 120g', category: 'BURGER', price: 26.00, restaurantId: 1, imagePath: 'default_product_image.png' },
    { id: 3, name: 'Cola 0.5L', category: 'DRINK', price: 8.00, restaurantId: 1, imagePath: 'default_product_image.png' },
    { id: 4, name: 'Frytki', category: 'SITE', price: 12.00, restaurantId: 1, imagePath: 'default_product_image.png' },
  ],
  '/stock': [
    {
      id: 3,
      purchasePrice: 8.00,
      quantity: 0.500,
      expirationDate: '2026-02-09T22:15:07.126157',
      semiProductDTO: {
        id: 100,
        name: 'Sałata Lodowa (Luz)',
        minimalStockQuantity: 5.000,
        unit: 'KG',
        restaurantId: 1,
      },
      restaurantId: 1,
    },
    {
      id: 2,
      purchasePrice: 12.00,
      quantity: 3.000,
      expirationDate: '2026-02-12T22:15:07.126157',
      semiProductDTO: {
        id: 101,
        name: 'Worek Sałaty 1.5kg',
        minimalStockQuantity: 10.000,
        unit: 'PCS',
        restaurantId: 1,
      },
      restaurantId: 1,
    },
    {
      id: 1,
      purchasePrice: 65.00,
      quantity: 2.000,
      expirationDate: '2026-02-14T22:15:07.126157',
      semiProductDTO: {
        id: 102,
        name: 'Karton Sałaty (6 worków)',
        minimalStockQuantity: 2.000,
        unit: 'PCS',
        restaurantId: 1,
      },
      restaurantId: 1,
    },
    {
      id: 6,
      purchasePrice: 1.50,
      quantity: 5.000,
      expirationDate: '2026-02-09T22:15:07.126157',
      semiProductDTO: {
        id: 200,
        name: 'Bułka Brioche (Sztuka)',
        minimalStockQuantity: 50.000,
        unit: 'PCS',
        restaurantId: 1,
      },
      restaurantId: 1,
    },
    {
      id: 5,
      purchasePrice: 16.00,
      quantity: 2.000,
      expirationDate: '2026-02-10T22:15:07.126157',
      semiProductDTO: {
        id: 201,
        name: 'Zgrzewka Brioche (12 szt)',
        minimalStockQuantity: 10.000,
        unit: 'PCS',
        restaurantId: 1,
      },
      restaurantId: 1,
    },
    {
      id: 4,
      purchasePrice: 150.00,
      quantity: 5.000,
      expirationDate: '2026-02-10T22:15:07.126157',
      semiProductDTO: {
        id: 202,
        name: 'Kosz Brioche (120 szt)',
        minimalStockQuantity: 1.000,
        unit: 'PCS',
        restaurantId: 1,
      },
      restaurantId: 1,
    },
    {
      id: 7,
      purchasePrice: 95.00,
      quantity: 10.000,
      expirationDate: '2026-02-17T22:15:07.126157',
      semiProductDTO: {
        id: 301,
        name: 'Paczka Wołowina Vacuum 2.5kg',
        minimalStockQuantity: 8.000,
        unit: 'PCS',
        restaurantId: 1,
      },
      restaurantId: 1,
    },
    {
      id: 9,
      purchasePrice: 10.00,
      quantity: 0.800,
      expirationDate: '2026-03-07T22:15:07.126157',
      semiProductDTO: {
        id: 400,
        name: 'Ketchup (Luz/Dystrybutor)',
        minimalStockQuantity: 2.000,
        unit: 'L',
        restaurantId: 1,
      },
      restaurantId: 1,
    },
    {
      id: 8,
      purchasePrice: 55.00,
      quantity: 4.000,
      expirationDate: '2026-08-07T22:15:07.126157',
      semiProductDTO: {
        id: 402,
        name: 'Zgrzewka Ketchup (6x1L)',
        minimalStockQuantity: 2.000,
        unit: 'PCS',
        restaurantId: 1,
      },
      restaurantId: 1,
    },
  ],
};

let mockSubcategoryIdCounter = 100;
let mockProductIdCounter = 100;
let mockComboIdCounter = 10;

export const mockHandlers = {
  postProducts(body) {
    const payload = body ? JSON.parse(body) : {};
    return {
      id: mockProductIdCounter++,
      name: payload.name,
      category: payload.category,
      price: payload.price,
      restaurantId: 1,
      imagePath: payload.image || 'default_product_image.png',
    };
  },

  postSubcategory(body) {
    const payload = body ? JSON.parse(body) : {};
    return {
      id: mockSubcategoryIdCounter++,
      subcategory_name: payload.subcategoryName,
      restaurantId: 1,
    };
  },

  postModifications(body) {
    const payload = body ? JSON.parse(body) : {};
    return {
      id: Math.floor(Math.random() * 1000),
      name: payload.name,
      price: payload.price,
      subcategory: payload.subcategory,
      semiProductId: payload.semiProductId,
      restaurantId: 1,
    };
  },

  postCombos(body) {
    const payload = body ? JSON.parse(body) : {};
    return {
      name: payload.name,
      price: payload.price,
      comboProduct: (payload.components || []).map((c) => ({
        quantity: c.quantity,
        productDto: mockData['/products'].find((p) => p.id === c.productId) || {
          id: c.productId,
          name: 'Mock Product',
          category: 'BURGER',
          price: 10,
          restaurantId: 1,
          imagePath: 'default_product_image.png',
        },
      })),
    };
  },

  postSemiProducts(body) {
    const payload = body ? JSON.parse(body) : {};
    return {
      id: Math.floor(Math.random() * 1000),
      name: payload.name,
      carbohydrate: payload.carbohydrate,
      fat: payload.fat,
      protein: payload.protein,
      unit: payload.unit,
      subcategory: payload.subcategory,
      restaurantId: 1,
    };
  },
};

export default mockData;

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { createProduct, getProducts } from '../api/products.js';

/**
 * Fetches the list of products from the API.
 * Data is cached and only re-fetched when explicitly invalidated.
 */
export function useProducts() {
  const { data: products = [], isLoading, isError, error } = useQuery({
    queryKey: ['products'],
    queryFn: getProducts,
  });

  return { products, isLoading, isError, error };
}

/**
 * Mutation hook for creating a new product (POST /products).
 * On success, invalidates the 'products' and 'semi-products' query caches
 * so that the next read triggers a fresh fetch from the server.
 */
export function useCreateProduct() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: createProduct,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['products'] });
      queryClient.invalidateQueries({ queryKey: ['semi-products'] });
    },
  });
}

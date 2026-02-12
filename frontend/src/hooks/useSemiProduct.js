import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { get, post } from '../api/client.js';

/**
 * Shared hook for fetching the semi-products list.
 * Uses TanStack Query for caching — data is only re-fetched
 * when the cache is empty or explicitly invalidated.
 */
export function useSemiProducts() {
  const {
    data: semiProducts = [],
    isLoading: loading,
    isError,
    error: queryError,
  } = useQuery({
    queryKey: ['semi-products'],
    queryFn: () => get('/semi-products'),
  });

  const error = isError ? (queryError?.message || 'Błąd podczas pobierania półproduktów') : '';

  return { semiProducts, loading, error };
}

/**
 * Mutation hook for creating a new semi-product (POST /semi-products).
 * On success, invalidates the 'semi-products' query cache
 * so that the next read triggers a fresh fetch from the server.
 */
export function useCreateSemiProduct() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (requestBody) => post('/semi-products', requestBody),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['semi-products'] });
    },
  });
}

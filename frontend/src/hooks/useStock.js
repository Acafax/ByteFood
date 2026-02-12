import { useQuery } from '@tanstack/react-query';
import { get } from '../api/client.js';

/**
 * Fetches current stock data from the API.
 * Data is cached and only re-fetched when explicitly invalidated.
 */
export function useStock() {
  const { data: stock = [], isLoading, isError, error } = useQuery({
    queryKey: ['stock'],
    queryFn: () => get('/stock'),
    retry: false,
  });

  return { stock, isLoading, isError, error };
}

import { useQuery } from '@tanstack/react-query';
import { get } from '../api/client.js';

/**
 * Fetches the list of product/modification categories from the API.
 * The backend returns a plain string array (e.g. ["BURGER", "DRINK", "SITE"]).
 * This hook maps it to { value, label } objects for SelectField compatibility.
 */
export function useCategories() {
  const { data: raw = [], isLoading, isError, error } = useQuery({
    queryKey: ['categories'],
    queryFn: () => get('/categories'),
  });

  const categories = Array.isArray(raw)
    ? raw.map((c) => ({ value: c, label: c }))
    : [];

  return { categories, isLoading, isError, error };
}

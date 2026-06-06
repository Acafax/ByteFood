import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { getSubcategories, createSubcategory } from '../api/subcategories.js';

/**
 * Fetches subcategories for semi-products and modifications.
 * Maps backend snake_case `subcategory_name` to SelectField-compatible options.
 */
export function useSubcategories() {
  const { data: raw = [], isLoading, isError, error } = useQuery({
    queryKey: ['subcategories'],
    queryFn: getSubcategories,
  });

  const subcategories = Array.isArray(raw)
    ? raw.map((s) => ({
        value: s.id,
        label: s.subcategory_name,
        raw: s,
      }))
    : [];

  return { subcategories, isLoading, isError, error };
}

/**
 * Mutation hook for creating a new subcategory (POST /subcategory).
 */
export function useCreateSubcategory() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: createSubcategory,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['subcategories'] });
    },
  });
}

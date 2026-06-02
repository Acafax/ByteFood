import { useQuery } from '@tanstack/react-query';
import { get } from '../api/client.js';

/**
 * Fetches the list of available units from the API.
 * The backend returns a plain string array (e.g. ["G", "ML", "PCS"]).
 * This hook maps it to { value, label } objects for SelectField compatibility.
 */
export function useUnits() {
  const { data: raw = [], isLoading, isError, error } = useQuery({
    queryKey: ['units'],
    queryFn: () => get('/units'),
  });

  const units = Array.isArray(raw)
    ? raw.map((u) => ({ value: u, label: u }))
    : [];

  return { units, isLoading, isError, error };
}

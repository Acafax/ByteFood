import { useState, useCallback, useEffect } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { createCombo } from '../api/combos.js';
import { useProducts } from './useProduct.js';

const INITIAL_FORM_DATA = { name: '', price: '' };
const SUCCESS_TIMEOUT_MS = 3000;

/**
 * Hook encapsulating all business logic for the "Create Combo" form.
 */
export function useCreateComboForm() {
  const { products, isLoading: productsLoading, error: fetchError } = useProducts();
  const queryClient = useQueryClient();

  const createComboMutation = useMutation({
    mutationFn: createCombo,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['products'] });
    },
  });

  const [formData, setFormData] = useState(INITIAL_FORM_DATA);
  const [quantities, setQuantities] = useState({});
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);

  useEffect(() => {
    if (products.length === 0) return;
    const initial = {};
    products.forEach((p) => { initial[p.id] = 0; });
    setQuantities(initial);
  }, [products]);

  useEffect(() => {
    if (fetchError) setError(fetchError?.message || 'Błąd podczas pobierania produktów');
  }, [fetchError]);

  const handleInputChange = useCallback((e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  }, []);

  const handleQuantityChange = useCallback((id, delta) => {
    setQuantities((prev) => {
      const newQuantity = (prev[id] || 0) + delta;
      if (newQuantity < 0) return prev;
      return { ...prev, [id]: newQuantity };
    });
  }, []);

  const resetForm = useCallback(() => {
    setFormData(INITIAL_FORM_DATA);
    const reset = {};
    products.forEach((p) => { reset[p.id] = 0; });
    setQuantities(reset);
  }, [products]);

  const handleSubmit = useCallback(async (e) => {
    e.preventDefault();
    setError('');
    setSuccess(false);

    if (!formData.name || !formData.price) {
      setError('Proszę wypełnić wszystkie wymagane pola');
      return;
    }

    if (parseFloat(formData.price) < 0.01) {
      setError('Cena musi wynosić co najmniej 0.01 PLN');
      return;
    }

    const components = Object.entries(quantities)
      .filter(([, qty]) => qty > 0)
      .map(([id, qty]) => ({ productId: parseInt(id, 10), quantity: qty }));

    if (components.length === 0) {
      setError('Dodaj przynajmniej jeden produkt do zestawu');
      return;
    }

    /** @type {import('../api/combos.js').CreateComboDto} */
    const requestBody = {
      name: formData.name,
      price: parseFloat(formData.price),
      components,
    };

    createComboMutation.mutate(requestBody, {
      onSuccess: () => {
        setSuccess(true);
        resetForm();
        setTimeout(() => setSuccess(false), SUCCESS_TIMEOUT_MS);
      },
      onError: (err) => {
        setError(err.message || 'Wystąpił błąd podczas tworzenia zestawu');
      },
    });
  }, [formData, quantities, resetForm, createComboMutation]);

  return {
    formData,
    products,
    quantities,
    loading: productsLoading,
    submitting: createComboMutation.isPending,
    error,
    success,
    handleInputChange,
    handleQuantityChange,
    handleSubmit,
    resetForm,
  };
}

import { useState, useCallback, useEffect } from 'react';
import { useSemiProducts } from './useSemiProduct.js';
import { useCreateProduct } from './useProduct.js';

const INITIAL_FORM_DATA = { name: '', category: '', price: '' };
const SUCCESS_TIMEOUT_MS = 3000;

/**
 * Hook encapsulating all business logic for the "Create Product" form.
 */
export function useCreateProductForm() {
  const {
    semiProducts,
    loading: semiProductsLoading,
    error: fetchError,
  } = useSemiProducts();

  const createProductMutation = useCreateProduct();

  const [formData, setFormData] = useState(INITIAL_FORM_DATA);
  const [quantities, setQuantities] = useState({});
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);

  // Initialise quantities map once semi-products are loaded
  useEffect(() => {
    if (semiProducts.length === 0) return;
    const initial = {};
    semiProducts.forEach((sp) => { initial[sp.id] = 0; });
    setQuantities(initial);
  }, [semiProducts]);

  // Propagate fetch error
  useEffect(() => {
    if (fetchError) setError(fetchError);
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
    semiProducts.forEach((sp) => { reset[sp.id] = 0; });
    setQuantities(reset);
  }, [semiProducts]);

  const handleSubmit = useCallback(async (e) => {
    e.preventDefault();
    setError('');
    setSuccess(false);

    if (!formData.name || !formData.category || !formData.price) {
      setError('Proszę wypełnić wszystkie wymagane pola');
      return;
    }

    if (parseFloat(formData.price) <= 0) {
      setError('Cena musi być większa niż 0');
      return;
    }

    const selectedSemiProducts = Object.entries(quantities)
      .filter(([, qty]) => qty > 0)
      .map(([id, qty]) => ({ semiProductId: parseInt(id, 10), quantity: qty }));

    if (selectedSemiProducts.length === 0) {
      setError('Dodaj przynajmniej jeden składnik do produktu');
      return;
    }

    const requestBody = {
      name: formData.name,
      category: formData.category,
      price: parseFloat(formData.price),
      productsSemiProducts: selectedSemiProducts,
    };

    createProductMutation.mutate(requestBody, {
      onSuccess: () => {
        setSuccess(true);
        resetForm();
        setTimeout(() => setSuccess(false), SUCCESS_TIMEOUT_MS);
      },
      onError: (err) => {
        setError(err.message || 'Wystąpił błąd podczas tworzenia produktu');
      },
    });
  }, [formData, quantities, resetForm, createProductMutation]);

  return {
    formData,
    semiProducts,
    quantities,
    loading: semiProductsLoading,
    submitting: createProductMutation.isPending,
    error,
    success,
    handleInputChange,
    handleQuantityChange,
    handleSubmit,
    resetForm,
  };
}

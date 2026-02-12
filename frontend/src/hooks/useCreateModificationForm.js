import { useState, useCallback, useEffect } from 'react';
import { post } from '../api/client.js';
import { useSemiProducts } from './useSemiProduct.js';

const INITIAL_FORM_DATA = { name: '', category: '', price: '' };
const SUCCESS_TIMEOUT_MS = 3000;

/**
 * Hook encapsulating all business logic for the "Create Modification" form.
 * Uses the TanStack-Query-based useSemiProducts for data fetching.
 */
export function useCreateModificationForm() {
  const { semiProducts, loading: semiProductsLoading, error: fetchError } = useSemiProducts();

  const [formData, setFormData] = useState(INITIAL_FORM_DATA);
  const [modifications, setModifications] = useState({});
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);

  // Initialise modifications map once semi-products are loaded
  useEffect(() => {
    if (semiProducts.length === 0) return;
    const initial = {};
    semiProducts.forEach((sp) => { initial[sp.id] = { action: null, quantity: 0 }; });
    setModifications(initial);
  }, [semiProducts]);

  // Propagate fetch error
  useEffect(() => {
    if (fetchError) setError(fetchError);
  }, [fetchError]);

  const handleInputChange = useCallback((e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  }, []);

  const handleActionSelect = useCallback((id, action) => {
    setModifications((prev) => {
      // Reset all semi-products, then toggle the selected one
      const reset = {};
      Object.keys(prev).forEach((key) => {
        reset[key] = { action: null, quantity: 0 };
      });
      return {
        ...reset,
        [id]: {
          action: prev[id].action === action ? null : action,
          quantity: 0,
        },
      };
    });
  }, []);

  const handleQuantityChange = useCallback((id, delta) => {
    setModifications((prev) => {
      const current = prev[id];
      if (!current.action || current.action === 'replace') return prev;

      const newQuantity = current.quantity + delta;
      if (newQuantity < 0) return prev;

      return { ...prev, [id]: { ...current, quantity: newQuantity } };
    });
  }, []);

  const resetForm = useCallback(() => {
    setFormData(INITIAL_FORM_DATA);
    const reset = {};
    semiProducts.forEach((sp) => { reset[sp.id] = { action: null, quantity: 0 }; });
    setModifications(reset);
  }, [semiProducts]);

  const handleSubmit = useCallback(async (e) => {
    e.preventDefault();
    setError('');
    setSuccess(false);

    if (!formData.name || !formData.category || formData.price === '') {
      setError('Proszę wypełnić wszystkie wymagane pola');
      return;
    }

    const selectedModification = Object.entries(modifications).find(
      ([, mod]) => mod.action !== null,
    );

    if (!selectedModification) {
      setError('Wybierz półprodukt i akcję (dodaj, zamień lub odejmij)');
      return;
    }

    const [semiProductId, modData] = selectedModification;

    const requestBody = {
      name: formData.name,
      category: formData.category,
      price: parseFloat(formData.price),
      semiProduct: modData.action === 'replace' ? 0 : parseInt(semiProductId, 10),
      action: modData.action,
      quantity: modData.quantity,
    };

    setSubmitting(true);
    try {
      await post('/modification/', requestBody);
      setSuccess(true);
      resetForm();
      setTimeout(() => setSuccess(false), SUCCESS_TIMEOUT_MS);
    } catch (err) {
      setError(err.message || 'Wystąpił błąd podczas tworzenia modyfikacji');
    } finally {
      setSubmitting(false);
    }
  }, [formData, modifications, resetForm]);

  return {
    formData,
    semiProducts,
    modifications,
    loading: semiProductsLoading,
    submitting,
    error,
    success,
    handleInputChange,
    handleActionSelect,
    handleQuantityChange,
    handleSubmit,
    resetForm,
  };
}

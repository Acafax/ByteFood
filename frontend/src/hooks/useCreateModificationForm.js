import { useState, useCallback, useEffect } from 'react';
import { useMutation } from '@tanstack/react-query';
import { createModification } from '../api/modifications.js';
import { useSemiProducts } from './useSemiProduct.js';

const INITIAL_FORM_DATA = { name: '', subcategoryId: '', price: '' };
const SUCCESS_TIMEOUT_MS = 3000;

/**
 * Hook encapsulating all business logic for the "Create Modification" form.
 */
export function useCreateModificationForm() {
  const { semiProducts, loading: semiProductsLoading, error: fetchError } = useSemiProducts();

  const createModificationMutation = useMutation({
    mutationFn: createModification,
  });

  const [formData, setFormData] = useState(INITIAL_FORM_DATA);
  const [modifications, setModifications] = useState({});
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);

  useEffect(() => {
    if (semiProducts.length === 0) return;
    const initial = {};
    semiProducts.forEach((sp) => { initial[sp.id] = { action: null, quantity: 0 }; });
    setModifications(initial);
  }, [semiProducts]);

  useEffect(() => {
    if (fetchError) setError(fetchError);
  }, [fetchError]);

  const handleInputChange = useCallback((e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  }, []);

  const handleActionSelect = useCallback((id, action) => {
    setModifications((prev) => {
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

    if (!formData.name || !formData.subcategoryId || formData.price === '') {
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

    /** @type {import('../api/modifications.js').CreateModificationTemplateDto} */
    const requestBody = {
      name: formData.name,
      price: parseFloat(formData.price),
      subcategory: { id: parseInt(formData.subcategoryId, 10) },
      semiProductId: modData.action === 'replace' ? null : parseInt(semiProductId, 10),
    };

    createModificationMutation.mutate(requestBody, {
      onSuccess: () => {
        setSuccess(true);
        resetForm();
        setTimeout(() => setSuccess(false), SUCCESS_TIMEOUT_MS);
      },
      onError: (err) => {
        setError(err.message || 'Wystąpił błąd podczas tworzenia modyfikacji');
      },
    });
  }, [formData, modifications, resetForm, createModificationMutation]);

  return {
    formData,
    semiProducts,
    modifications,
    loading: semiProductsLoading,
    submitting: createModificationMutation.isPending,
    error,
    success,
    handleInputChange,
    handleActionSelect,
    handleQuantityChange,
    handleSubmit,
    resetForm,
  };
}

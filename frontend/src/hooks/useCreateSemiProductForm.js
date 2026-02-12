import { useState, useCallback } from 'react';
import { useAuth } from '../context/AuthContext.jsx';
import { useCreateSemiProduct } from './useSemiProduct.js';

const INITIAL_FORM_DATA = {
  name: '',
  unit: '',
  fat: '',
  carbohydrate: '',
  protein: '',
};

const SUCCESS_TIMEOUT_MS = 3000;

/**
 * Hook encapsulating all business logic for the "Create Semi-Product" form.
 * Field names match the backend CreateSemiProductDto.
 */
export function useCreateSemiProductForm() {
  const { user } = useAuth();
  const createSemiProductMutation = useCreateSemiProduct();

  const [formData, setFormData] = useState(INITIAL_FORM_DATA);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);

  const handleChange = useCallback((e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  }, []);

  const resetForm = useCallback(() => {
    setFormData(INITIAL_FORM_DATA);
  }, []);

  const handleSubmit = useCallback(async (e) => {
    e.preventDefault();
    setError('');
    setSuccess(false);

    if (!formData.name || !formData.unit || !formData.fat || !formData.carbohydrate || !formData.protein) {
      setError('Proszę wypełnić wszystkie wymagane pola');
      return;
    }

    const fat = parseFloat(formData.fat);
    const carbohydrate = parseFloat(formData.carbohydrate);
    const protein = parseFloat(formData.protein);

    if (fat < 0.1 || carbohydrate < 0.1 || protein < 0.1) {
      setError('Wartości makroskładników muszą wynosić co najmniej 0.1');
      return;
    }

    const requestBody = {
      name: formData.name,
      carbohydrate,
      fat,
      protein,
      unit: formData.unit,
      restaurantId: user?.restaurantId ?? null,
    };

    createSemiProductMutation.mutate(requestBody, {
      onSuccess: () => {
        setSuccess(true);
        resetForm();
        setTimeout(() => setSuccess(false), SUCCESS_TIMEOUT_MS);
      },
      onError: (err) => {
        setError(err.message || 'Wystąpił błąd podczas tworzenia półproduktu');
      },
    });
  }, [formData, resetForm, user, createSemiProductMutation]);

  return {
    formData,
    loading: createSemiProductMutation.isPending,
    error,
    success,
    handleChange,
    handleSubmit,
    resetForm,
  };
}

import { useState, useCallback, useEffect } from 'react';
import { useSemiProducts } from './useSemiProduct.js';
import { useCreateProduct } from './useProduct.js';

const INITIAL_FORM_DATA = { name: '', category: '', price: '' };
const SUCCESS_TIMEOUT_MS = 3000;

/**
 * Converts a File to base64 data URL for the CreateProductDto.image field.
 * @param {File} file
 * @returns {Promise<string>}
 */
function fileToDataUrl(file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => resolve(reader.result);
    reader.onerror = () => reject(new Error('Nie udało się odczytać pliku'));
    reader.readAsDataURL(file);
  });
}

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
  const [imageFile, setImageFile] = useState(null);
  const [imagePreview, setImagePreview] = useState(null);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);

  useEffect(() => {
    if (semiProducts.length === 0) return;
    const initial = {};
    semiProducts.forEach((sp) => { initial[sp.id] = 0; });
    setQuantities(initial);
  }, [semiProducts]);

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

  const handleImageChange = useCallback((file, previewUrl) => {
    if (imagePreview) {
      URL.revokeObjectURL(imagePreview);
    }
    setImageFile(file);
    setImagePreview(previewUrl);
  }, [imagePreview]);

  const handleImageError = useCallback((message) => {
    setError(message);
  }, []);

  const resetForm = useCallback(() => {
    setFormData(INITIAL_FORM_DATA);
    const reset = {};
    semiProducts.forEach((sp) => { reset[sp.id] = 0; });
    setQuantities(reset);
    if (imagePreview) {
      URL.revokeObjectURL(imagePreview);
    }
    setImageFile(null);
    setImagePreview(null);
  }, [semiProducts, imagePreview]);

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

    let image;
    if (imageFile) {
      try {
        image = await fileToDataUrl(imageFile);
      } catch {
        setError('Nie udało się przetworzyć zdjęcia');
        return;
      }
    }

    /** @type {import('../api/products.js').CreateProductDto} */
    const requestBody = {
      name: formData.name,
      category: formData.category,
      price: parseFloat(formData.price),
      productsSemiProducts: selectedSemiProducts,
      ...(image && { image }),
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
  }, [formData, quantities, imageFile, resetForm, createProductMutation]);

  return {
    formData,
    semiProducts,
    quantities,
    imageFile,
    imagePreview,
    loading: semiProductsLoading,
    submitting: createProductMutation.isPending,
    error,
    success,
    handleInputChange,
    handleQuantityChange,
    handleImageChange,
    handleImageError,
    handleSubmit,
    resetForm,
  };
}

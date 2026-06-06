import { useState, useCallback } from 'react';
import { useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { createRestaurant } from '../api/onboarding.js';
import { fetchCurrentUser } from '../store/authSlice.js';

export function useOnboarding() {
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const [currentStep, setCurrentStep] = useState(1);
  const [restaurantName, setRestaurantName] = useState('');
  const [stockName, setStockName] = useState('');
  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const goToNextStep = useCallback(() => {
    setError('');
    setCurrentStep((prev) => Math.min(prev + 1, 2));
  }, []);

  const goToPreviousStep = useCallback(() => {
    setError('');
    setCurrentStep((prev) => Math.max(prev - 1, 1));
  }, []);

  const submitRestaurantStep = useCallback(async () => {
    setError('');

    if (!restaurantName.trim()) {
      setError('Podaj nazwę restauracji');
      return false;
    }

    if (!stockName.trim()) {
      setError('Podaj nazwę magazynu');
      return false;
    }

    setIsSubmitting(true);
    try {
      await createRestaurant({
        restaurantName: restaurantName.trim(),
        stockName: stockName.trim(),
      });
      await dispatch(fetchCurrentUser()).unwrap();
      navigate('/dashboard');
      return true;
    } catch (err) {
      setError(err.data?.message || err.message || 'Nie udało się utworzyć restauracji');
      return false;
    } finally {
      setIsSubmitting(false);
    }
  }, [restaurantName, stockName, dispatch, navigate]);

  return {
    currentStep,
    restaurantName,
    setRestaurantName,
    stockName,
    setStockName,
    error,
    isSubmitting,
    goToNextStep,
    goToPreviousStep,
    submitRestaurantStep,
  };
}

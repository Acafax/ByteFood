import { useState, useCallback } from 'react';
import { createEmployee } from '../api/employees.js';

const INITIAL_FORM_DATA = {
  email: '',
  password: '',
  name: '',
  lastName: '',
};

const SUCCESS_TIMEOUT_MS = 3000;

export function useCreateEmployeeForm() {
  const [formData, setFormData] = useState(INITIAL_FORM_DATA);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);

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

    const { email, password, name, lastName } = formData;
    if (!email || !password || !name || !lastName) {
      setError('Proszę wypełnić wszystkie pola');
      return;
    }

    if (password.length < 12) {
      setError('Hasło musi mieć co najmniej 12 znaków');
      return;
    }

    setIsSubmitting(true);
    try {
      await createEmployee({
        email: email.trim(),
        password,
        name: name.trim(),
        lastName: lastName.trim(),
      });
      setSuccess(true);
      resetForm();
      setTimeout(() => setSuccess(false), SUCCESS_TIMEOUT_MS);
    } catch (err) {
      setError(err.data?.message || err.message || 'Nie udało się utworzyć pracownika');
    } finally {
      setIsSubmitting(false);
    }
  }, [formData, resetForm]);

  return {
    formData,
    isSubmitting,
    error,
    success,
    handleChange,
    handleSubmit,
    resetForm,
  };
}

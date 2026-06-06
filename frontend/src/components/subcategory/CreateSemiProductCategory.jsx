import React, { useState, useCallback, useEffect } from 'react';
import { X, Save } from 'lucide-react';
import { useCreateSubcategory } from '../../hooks/useSubcategories.js';
import FormField from '../ui/FormField.jsx';
import SubmitButton from '../ui/SubmitButton.jsx';
import AlertMessage from '../ui/AlertMessage.jsx';

/**
 * Reusable modal for creating a new subcategory inline.
 *
 * @param {Object} props
 * @param {boolean} props.open - Whether the modal is visible
 * @param {() => void} props.onClose - Called when modal is dismissed
 * @param {(subcategory: import('../../api/subcategories.js').SubcategoryDto) => void} props.onCreated
 */
function CreateSemiProductCategory({ open, onClose, onCreated }) {
  const [name, setName] = useState('');
  const [error, setError] = useState('');
  const createSubcategoryMutation = useCreateSubcategory();

  useEffect(() => {
    if (open) {
      setName('');
      setError('');
    }
  }, [open]);

  const handleClose = useCallback(() => {
    if (!createSubcategoryMutation.isPending) {
      onClose();
    }
  }, [createSubcategoryMutation.isPending, onClose]);

  const handleSubmit = useCallback(async (e) => {
    e.preventDefault();
    setError('');

    const trimmed = name.trim();
    if (!trimmed) {
      setError('Nazwa subkategorii jest wymagana');
      return;
    }

    createSubcategoryMutation.mutate(
      { subcategoryName: trimmed },
      {
        onSuccess: (created) => {
          onCreated(created);
          onClose();
        },
        onError: (err) => {
          setError(err.message || 'Nie udało się utworzyć subkategorii');
        },
      },
    );
  }, [name, createSubcategoryMutation, onCreated, onClose]);

  if (!open) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div
        className="absolute inset-0 bg-slate-900/50 backdrop-blur-sm"
        onClick={handleClose}
        aria-hidden="true"
      />

      <div
        role="dialog"
        aria-modal="true"
        aria-labelledby="create-subcategory-title"
        className="relative w-full max-w-md bg-white rounded-xl border border-gray-200 shadow-xl p-6"
      >
        <div className="flex items-start justify-between mb-4">
          <div>
            <h3 id="create-subcategory-title" className="text-lg font-bold text-slate-900">
              Nowa subkategoria
            </h3>
            <p className="text-sm text-slate-500 mt-1">
              Dodaj subkategorię bez opuszczania formularza
            </p>
          </div>
          <button
            type="button"
            onClick={handleClose}
            className="p-1.5 text-slate-400 hover:text-slate-600 rounded-lg hover:bg-slate-100 transition-colors"
            aria-label="Zamknij"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <AlertMessage variant="error" message={error} />

        <form onSubmit={handleSubmit} className="space-y-4">
          <FormField
            label="Nazwa subkategorii"
            required
            type="text"
            id="subcategoryName"
            name="subcategoryName"
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="np. sosy, dodatki"
            autoFocus
          />

          <div className="flex justify-end gap-3 pt-2">
            <button
              type="button"
              onClick={handleClose}
              disabled={createSubcategoryMutation.isPending}
              className="px-4 py-2 border border-gray-300 text-slate-600 rounded-lg hover:bg-slate-50 transition-colors font-medium disabled:opacity-50"
            >
              Anuluj
            </button>
            <SubmitButton loading={createSubcategoryMutation.isPending} icon={Save}>
              Utwórz
            </SubmitButton>
          </div>
        </form>
      </div>
    </div>
  );
}

export default CreateSemiProductCategory;

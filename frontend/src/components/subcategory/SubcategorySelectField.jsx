import React, { useState, useCallback, useRef, useEffect } from 'react';
import { Plus, Check, X } from 'lucide-react';
import SelectField from '../ui/SelectField.jsx';
import { useCreateSubcategory } from '../../hooks/useSubcategories.js';

const ADD_NEW_VALUE = '__add_new_subcategory__';

/**
 * Subcategory dropdown with an inline "quick create" field.
 *
 * Unlike product categories (which are free-text), subcategories are real
 * entities created via POST /subcategory, so confirming the inline field fires
 * the create mutation and selects the newly created subcategory on success.
 * The UX mirrors the category quick-create field in the product form (inline
 * input + confirm/cancel, no modal).
 */
function SubcategorySelectField({
  label = 'Subkategoria',
  required = true,
  value,
  onChange,
  options = [],
  loading = false,
  id = 'subcategoryId',
  name = 'subcategoryId',
}) {
  const [adding, setAdding] = useState(false);
  const [newName, setNewName] = useState('');
  const [error, setError] = useState('');
  const inputRef = useRef(null);
  const createSubcategoryMutation = useCreateSubcategory();
  const isPending = createSubcategoryMutation.isPending;

  useEffect(() => {
    if (adding && inputRef.current) {
      inputRef.current.focus();
    }
  }, [adding]);

  const openAdd = useCallback(() => {
    setNewName('');
    setError('');
    setAdding(true);
  }, []);

  const cancelAdd = useCallback(() => {
    if (isPending) return;
    setAdding(false);
    setNewName('');
    setError('');
  }, [isPending]);

  const handleSelectChange = useCallback((e) => {
    if (e.target.value === ADD_NEW_VALUE) {
      openAdd();
      return;
    }
    onChange(e);
  }, [onChange, openAdd]);

  const confirmAdd = useCallback(() => {
    const trimmed = newName.trim();
    if (!trimmed) {
      setError('Nazwa subkategorii jest wymagana');
      return;
    }
    setError('');

    createSubcategoryMutation.mutate(
      { subcategoryName: trimmed },
      {
        onSuccess: (created) => {
          onChange({ target: { name, value: created.id } });
          setAdding(false);
          setNewName('');
        },
        onError: (err) => {
          setError(err.message || 'Nie udało się utworzyć subkategorii');
        },
      },
    );
  }, [newName, createSubcategoryMutation, onChange, name]);

  const handleKeyDown = useCallback((e) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      confirmAdd();
    } else if (e.key === 'Escape') {
      e.preventDefault();
      cancelAdd();
    }
  }, [confirmAdd, cancelAdd]);

  const enrichedOptions = [
    ...options,
    { value: ADD_NEW_VALUE, label: '+ Nowa subkategoria' },
  ];

  return (
    <div>
      <div className="relative">
        <SelectField
          label={label}
          required={required}
          id={id}
          name={name}
          value={value}
          onChange={handleSelectChange}
          options={enrichedOptions}
          placeholder={loading ? 'Ładowanie subkategorii...' : 'Wybierz subkategorię'}
        />
        <button
          type="button"
          onClick={openAdd}
          className="absolute right-10 top-[2.125rem] p-1 text-orange-500 hover:text-orange-600 hover:bg-orange-50 rounded transition-colors"
          title="Dodaj nową subkategorię"
          aria-label="Dodaj nową subkategorię"
        >
          <Plus className="w-4 h-4" />
        </button>
      </div>

      {adding && (
        <div className="mt-2">
          <div className="flex items-center gap-2">
            <input
              ref={inputRef}
              type="text"
              value={newName}
              onChange={(e) => setNewName(e.target.value)}
              onKeyDown={handleKeyDown}
              disabled={isPending}
              placeholder="np. sosy, dodatki"
              className="flex-1 px-4 py-2.5 border border-gray-300 rounded-lg bg-gray-50 text-slate-900 placeholder:text-slate-400 outline-none transition-all duration-200 focus:bg-white focus:border-orange-500 focus:ring-4 focus:ring-orange-500/10 disabled:opacity-50"
            />
            <button
              type="button"
              onClick={confirmAdd}
              disabled={isPending || !newName.trim()}
              className="p-2.5 bg-[#FF6600] hover:bg-[#e55b00] text-white rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              title="Zatwierdź"
              aria-label="Zatwierdź nową subkategorię"
            >
              {isPending ? (
                <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white" />
              ) : (
                <Check className="w-5 h-5" />
              )}
            </button>
            <button
              type="button"
              onClick={cancelAdd}
              disabled={isPending}
              className="p-2.5 border border-gray-300 text-slate-600 rounded-lg hover:bg-slate-50 transition-colors disabled:opacity-50"
              title="Anuluj"
              aria-label="Anuluj"
            >
              <X className="w-5 h-5" />
            </button>
          </div>
          {error && <p className="mt-1 text-sm text-red-600">{error}</p>}
        </div>
      )}
    </div>
  );
}

export default SubcategorySelectField;

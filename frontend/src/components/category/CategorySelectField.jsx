import React, { useState, useCallback, useMemo, useRef, useEffect } from 'react';
import { Plus, Check, X } from 'lucide-react';
import SelectField from '../ui/SelectField.jsx';

const ADD_NEW_VALUE = '__add_new_category__';

/**
 * Category dropdown for the product form with an inline "quick create" option.
 *
 * Product categories are free-text strings on the backend (a product's
 * `category` field). The list returned by GET /categories is simply the set of
 * categories already used by existing products, so a brand new category is
 * "created" the moment a product is saved with that name — no dedicated
 * endpoint is required. This field lets the user add such a name inline
 * (locally) and select it without leaving the form.
 *
 * @param {Object} props
 * @param {string} props.value - Currently selected category.
 * @param {(e: { target: { name: string, value: string } }) => void} props.onChange
 * @param {{ value: string, label: string }[]} props.options - Existing categories.
 * @param {boolean} [props.loading]
 */
function CategorySelectField({
  label = 'Kategoria',
  required = true,
  value,
  onChange,
  options = [],
  loading = false,
  id = 'category',
  name = 'category',
}) {
  const [localOptions, setLocalOptions] = useState([]);
  const [adding, setAdding] = useState(false);
  const [newName, setNewName] = useState('');
  const inputRef = useRef(null);

  useEffect(() => {
    if (adding && inputRef.current) {
      inputRef.current.focus();
    }
  }, [adding]);

  const mergedOptions = useMemo(
    () => [...options, ...localOptions],
    [options, localOptions],
  );

  const openAdd = useCallback(() => {
    setNewName('');
    setAdding(true);
  }, []);

  const cancelAdd = useCallback(() => {
    setAdding(false);
    setNewName('');
  }, []);

  const handleSelectChange = useCallback((e) => {
    if (e.target.value === ADD_NEW_VALUE) {
      openAdd();
      return;
    }
    onChange(e);
  }, [onChange, openAdd]);

  const confirmAdd = useCallback(() => {
    const trimmed = newName.trim();
    if (!trimmed) return;

    const exists = mergedOptions.some(
      (o) => String(o.value).toLowerCase() === trimmed.toLowerCase(),
    );
    if (!exists) {
      setLocalOptions((prev) => [...prev, { value: trimmed, label: trimmed }]);
    }

    onChange({ target: { name, value: trimmed } });
    setAdding(false);
    setNewName('');
  }, [newName, mergedOptions, onChange, name]);

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
    ...mergedOptions,
    { value: ADD_NEW_VALUE, label: '+ Nowa kategoria' },
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
          placeholder={loading ? 'Ładowanie kategorii...' : 'Wybierz kategorię'}
        />
        <button
          type="button"
          onClick={openAdd}
          className="absolute right-10 top-[2.125rem] p-1 text-orange-500 hover:text-orange-600 hover:bg-orange-50 rounded transition-colors"
          title="Dodaj nową kategorię"
          aria-label="Dodaj nową kategorię"
        >
          <Plus className="w-4 h-4" />
        </button>
      </div>

      {adding && (
        <div className="mt-2 flex items-center gap-2">
          <input
            ref={inputRef}
            type="text"
            value={newName}
            onChange={(e) => setNewName(e.target.value)}
            onKeyDown={handleKeyDown}
            placeholder="np. PIZZA, SAŁATKA"
            className="flex-1 px-4 py-2.5 border border-gray-300 rounded-lg bg-gray-50 text-slate-900 placeholder:text-slate-400 outline-none transition-all duration-200 focus:bg-white focus:border-orange-500 focus:ring-4 focus:ring-orange-500/10"
          />
          <button
            type="button"
            onClick={confirmAdd}
            disabled={!newName.trim()}
            className="p-2.5 bg-[#FF6600] hover:bg-[#e55b00] text-white rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            title="Zatwierdź"
            aria-label="Zatwierdź nową kategorię"
          >
            <Check className="w-5 h-5" />
          </button>
          <button
            type="button"
            onClick={cancelAdd}
            className="p-2.5 border border-gray-300 text-slate-600 rounded-lg hover:bg-slate-50 transition-colors"
            title="Anuluj"
            aria-label="Anuluj"
          >
            <X className="w-5 h-5" />
          </button>
        </div>
      )}
    </div>
  );
}

export default CategorySelectField;

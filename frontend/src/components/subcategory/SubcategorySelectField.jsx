import React, { useState, useCallback } from 'react';
import { Plus } from 'lucide-react';
import SelectField from '../ui/SelectField.jsx';
import CreateSemiProductCategory from './CreateSemiProductCategory.jsx';

const ADD_NEW_VALUE = '__add_new_subcategory__';

/**
 * Subcategory dropdown with inline "add new" option that opens CreateSemiProductCategory modal.
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
  const [modalOpen, setModalOpen] = useState(false);

  const handleSelectChange = useCallback((e) => {
    if (e.target.value === ADD_NEW_VALUE) {
      setModalOpen(true);
      return;
    }
    onChange(e);
  }, [onChange]);

  const handleCreated = useCallback((created) => {
    onChange({ target: { name, value: created.id } });
  }, [onChange, name]);

  const enrichedOptions = [
    ...options,
    { value: ADD_NEW_VALUE, label: '+ Nowa subkategoria' },
  ];

  return (
    <>
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
          onClick={() => setModalOpen(true)}
          className="absolute right-10 top-[2.125rem] p-1 text-orange-500 hover:text-orange-600 hover:bg-orange-50 rounded transition-colors"
          title="Dodaj nową subkategorię"
          aria-label="Dodaj nową subkategorię"
        >
          <Plus className="w-4 h-4" />
        </button>
      </div>

      <CreateSemiProductCategory
        open={modalOpen}
        onClose={() => setModalOpen(false)}
        onCreated={handleCreated}
      />
    </>
  );
}

export default SubcategorySelectField;

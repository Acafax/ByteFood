import React from 'react';
import { Plus, Minus, Replace, Save } from 'lucide-react';
import { useCreateModificationForm } from '../hooks/useCreateModificationForm.js';
import { useCategories } from '../hooks/useCategories.js';
import AlertMessage from '../components/ui/AlertMessage.jsx';
import LoadingSpinner from '../components/ui/LoadingSpinner.jsx';
import FormField from '../components/ui/FormField.jsx';
import SelectField from '../components/ui/SelectField.jsx';
import SubmitButton from '../components/ui/SubmitButton.jsx';

function CreateModification() {
  const {
    formData,
    semiProducts,
    modifications,
    loading,
    submitting,
    error,
    success,
    handleInputChange,
    handleActionSelect,
    handleQuantityChange,
    handleSubmit,
    resetForm,
  } = useCreateModificationForm();

  const { categories } = useCategories();

  if (loading) {
    return (
      <div className="bg-white rounded-xl border border-gray-200 shadow-card p-8">
        <LoadingSpinner text="Ładowanie półproduktów..." colorClass="text-orange-500" />
      </div>
    );
  }

  return (
    <div className="bg-white rounded-xl border border-gray-200 shadow-card p-8">
      <div className="mb-6">
        <h2 className="text-2xl font-bold text-slate-900">Tworzenie Modyfikacji</h2>
        <p className="text-slate-500 font-medium mt-1">Formularz do tworzenia nowej modyfikacji</p>
      </div>

      <AlertMessage variant="error" message={error} />
      <AlertMessage variant="success" message={success ? 'Modyfikacja została pomyślnie utworzona!' : ''} />

      <form onSubmit={handleSubmit} className="space-y-8">
        {/* Basic fields */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <FormField
            label="Nazwa modyfikacji"
            required
            type="text"
            id="name"
            name="name"
            value={formData.name}
            onChange={handleInputChange}
            placeholder="np. Dodatkowy ser Cheddar"
          />

          <SelectField
            label="Kategoria"
            required
            id="category"
            name="category"
            value={formData.category}
            onChange={handleInputChange}
            options={categories}
            placeholder="Wybierz kategorię"
          />

          <FormField
            label="Cena (PLN)"
            required
            type="number"
            id="price"
            name="price"
            value={formData.price}
            onChange={handleInputChange}
            step="0.01"
            min="0"
            placeholder="0.00"
          />
        </div>

        {/* Semi-products + action section */}
        <div>
          <h3 className="text-xl font-bold text-slate-900 mb-2">Półprodukt i Akcja</h3>
          <p className="text-slate-500 font-medium mb-4">
            Wybierz półprodukt i akcję: Dodaj (+), Zamień (↔), Odejmij (-)
          </p>

          {semiProducts.length === 0 ? (
            <div className="border-2 border-dashed border-gray-300 rounded-xl p-8 text-center">
              <p className="text-slate-500">Brak dostępnych półproduktów</p>
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {semiProducts.map((sp) => (
                <ModificationCard
                  key={sp.id}
                  semiProduct={sp}
                  mod={modifications[sp.id]}
                  onActionSelect={handleActionSelect}
                  onQuantityChange={handleQuantityChange}
                />
              ))}
            </div>
          )}
        </div>

        {/* Actions */}
        <div className="flex justify-end gap-4 pt-4 border-t border-gray-200">
          <button
            type="button"
            onClick={resetForm}
            className="px-6 py-2.5 border border-gray-300 text-slate-600 rounded-lg hover:bg-slate-50 transition-colors font-medium"
            disabled={submitting}
          >
            Wyczyść formularz
          </button>

          <SubmitButton loading={submitting} icon={Save}>
            Utwórz Modyfikację
          </SubmitButton>
        </div>
      </form>
    </div>
  );
}

/* ─── Sub-components ─── */

function ModificationCard({ semiProduct, mod, onActionSelect, onQuantityChange }) {
  const isActive = mod?.action !== null;

  return (
    <div
      className={`rounded-xl p-4 transition-all duration-200 ${
        isActive
          ? 'border border-orange-400 bg-orange-50 shadow-sm'
          : 'border border-slate-200 bg-slate-50'
      }`}
    >
      <h4 className="font-semibold text-slate-800 mb-3">
        {semiProduct.name}
      </h4>

      {/* Action buttons */}
      <div className="flex gap-2 mb-3">
        <ActionButton
          active={mod?.action === 'add'}
          activeClass="bg-green-600 text-white shadow-sm"
          onClick={() => onActionSelect(semiProduct.id, 'add')}
          title="Dodaj"
          Icon={Plus}
        />
        <ActionButton
          active={mod?.action === 'replace'}
          activeClass="bg-blue-600 text-white shadow-sm"
          onClick={() => onActionSelect(semiProduct.id, 'replace')}
          title="Zamień"
          Icon={Replace}
        />
        <ActionButton
          active={mod?.action === 'remove'}
          activeClass="bg-red-600 text-white shadow-sm"
          onClick={() => onActionSelect(semiProduct.id, 'remove')}
          title="Odejmij"
          Icon={Minus}
        />
      </div>

      {/* Quantity counter for add/remove */}
      {mod?.action && mod.action !== 'replace' && (
        <div className="flex items-center justify-between bg-white rounded-lg p-2 border border-gray-200">
          <button
            type="button"
            onClick={() => onQuantityChange(semiProduct.id, -1)}
            className="w-8 h-8 flex items-center justify-center bg-red-500 hover:bg-red-600 text-white rounded transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            disabled={mod.quantity === 0}
          >
            <Minus className="w-4 h-4" />
          </button>

          <span className="text-lg font-bold text-slate-800 min-w-[2rem] text-center">
            {mod.quantity}
          </span>

          <button
            type="button"
            onClick={() => onQuantityChange(semiProduct.id, 1)}
            className="w-8 h-8 flex items-center justify-center bg-[#FF6600] hover:bg-[#e55b00] text-white rounded transition-colors"
          >
            <Plus className="w-4 h-4" />
          </button>
        </div>
      )}

      {mod?.action === 'replace' && (
        <div className="bg-blue-50 border border-blue-200 rounded-lg p-2 text-center">
          <p className="text-sm text-blue-700 font-medium">Zamiana aktywna</p>
        </div>
      )}
    </div>
  );
}

function ActionButton({ active, activeClass, onClick, title, Icon }) {
  return (
    <button
      type="button"
      onClick={onClick}
      className={`flex-1 px-3 py-2 rounded-lg font-medium transition-all duration-200 ${
        active ? activeClass : 'bg-white text-slate-600 hover:bg-slate-100 border border-slate-200'
      }`}
      title={title}
    >
      <Icon className="w-5 h-5 mx-auto" />
    </button>
  );
}

export default CreateModification;

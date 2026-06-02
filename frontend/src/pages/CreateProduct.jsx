import React, { useRef, useCallback } from 'react';
import { Plus, Minus, Save } from 'lucide-react';
import { useCreateProductForm } from '../hooks/useCreateProductForm.js';
import { useCategories } from '../hooks/useCategories.js';
import AlertMessage from '../components/ui/AlertMessage.jsx';
import LoadingSpinner from '../components/ui/LoadingSpinner.jsx';
import FormField from '../components/ui/FormField.jsx';
import SelectField from '../components/ui/SelectField.jsx';
import SubmitButton from '../components/ui/SubmitButton.jsx';

const LONG_PRESS_INITIAL_DELAY_MS = 400;
const LONG_PRESS_REPEAT_INTERVAL_MS = 100;

function CreateProduct() {
  const {
    formData,
    semiProducts,
    quantities,
    loading,
    submitting,
    error,
    success,
    handleInputChange,
    handleQuantityChange,
    handleSubmit,
    resetForm,
  } = useCreateProductForm();

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
        <h2 className="text-2xl font-bold text-slate-900">Tworzenie Produktu</h2>
        <p className="text-slate-500 font-medium mt-1">Formularz do tworzenia nowego produktu</p>
      </div>

      <AlertMessage variant="error" message={error} />
      <AlertMessage variant="success" message={success ? 'Produkt został pomyślnie utworzony!' : ''} />

      <form onSubmit={handleSubmit} className="space-y-8">
        {/* Basic fields */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <FormField
            label="Nazwa produktu"
            required
            type="text"
            id="name"
            name="name"
            value={formData.name}
            onChange={handleInputChange}
            placeholder="np. Pizza Margherita"
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
            min="0.01"
            placeholder="0.00"
          />
        </div>

        {/* Semi-products section */}
        <div>
          <h3 className="text-xl font-bold text-slate-900 mb-2">Składniki (Półprodukty)</h3>
          <p className="text-slate-500 font-medium mb-4">Wybierz składniki i określ ich ilość dla produktu</p>

          {semiProducts.length === 0 ? (
            <div className="border-2 border-dashed border-gray-300 rounded-xl p-8 text-center">
              <p className="text-slate-500">Brak dostępnych półproduktów</p>
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {semiProducts.map((sp) => (
                <SemiProductQuantityCard
                  key={sp.id}
                  semiProduct={sp}
                  quantity={quantities[sp.id] || 0}
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
            Utwórz Produkt
          </SubmitButton>
        </div>
      </form>
    </div>
  );
}

/* ─── Sub-component with long-press support ─── */

function SemiProductQuantityCard({ semiProduct, quantity, onQuantityChange }) {
  const intervalRef = useRef(null);
  const timeoutRef = useRef(null);

  // Quantities in the product form are always in grams
  const unitLabel = 'g';

  const startLongPress = useCallback((delta) => {
    // Immediate first tick
    onQuantityChange(semiProduct.id, delta);
    // After initial delay, start repeating
    timeoutRef.current = setTimeout(() => {
      intervalRef.current = setInterval(() => {
        onQuantityChange(semiProduct.id, delta);
      }, LONG_PRESS_REPEAT_INTERVAL_MS);
    }, LONG_PRESS_INITIAL_DELAY_MS);
  }, [onQuantityChange, semiProduct.id]);

  const stopLongPress = useCallback(() => {
    clearTimeout(timeoutRef.current);
    clearInterval(intervalRef.current);
    timeoutRef.current = null;
    intervalRef.current = null;
  }, []);

  return (
    <div
      className={`rounded-xl p-4 transition-all duration-200 ${
        quantity > 0
          ? 'border border-orange-400 bg-orange-50 shadow-sm'
          : 'border border-slate-200 bg-slate-50'
      }`}
    >
      <h4 className="font-semibold text-slate-800 mb-3">
        {semiProduct.name}
      </h4>

      <div className="flex items-center justify-between bg-white rounded-lg p-2 border border-gray-200">
        <button
          type="button"
          onMouseDown={() => startLongPress(-1)}
          onMouseUp={stopLongPress}
          onMouseLeave={stopLongPress}
          onTouchStart={() => startLongPress(-1)}
          onTouchEnd={stopLongPress}
          className="w-10 h-10 flex items-center justify-center bg-red-500 hover:bg-red-600 text-white rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed select-none"
          disabled={quantity === 0}
        >
          <Minus className="w-5 h-5" />
        </button>

        <span className="text-xl font-bold text-slate-800 min-w-[3rem] text-center">
          {quantity}
        </span>

        <button
          type="button"
          onMouseDown={() => startLongPress(1)}
          onMouseUp={stopLongPress}
          onMouseLeave={stopLongPress}
          onTouchStart={() => startLongPress(1)}
          onTouchEnd={stopLongPress}
          className="w-10 h-10 flex items-center justify-center bg-[#FF6600] hover:bg-[#e55b00] text-white rounded-lg transition-colors select-none"
        >
          <Plus className="w-5 h-5" />
        </button>
      </div>

      {semiProduct.unit && (
        <p className="text-sm text-slate-500 mt-2 text-center">
          Jednostka: {unitLabel}
        </p>
      )}
    </div>
  );
}

export default CreateProduct;

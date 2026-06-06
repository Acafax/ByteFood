import React from 'react';
import { Plus, Minus, Save } from 'lucide-react';
import { useCreateComboForm } from '../hooks/useCreateComboForm.js';
import AlertMessage from '../components/ui/AlertMessage.jsx';
import LoadingSpinner from '../components/ui/LoadingSpinner.jsx';
import FormField from '../components/ui/FormField.jsx';
import SubmitButton from '../components/ui/SubmitButton.jsx';

function CreateCombo() {
  const {
    formData,
    products,
    quantities,
    loading,
    submitting,
    error,
    success,
    handleInputChange,
    handleQuantityChange,
    handleSubmit,
    resetForm,
  } = useCreateComboForm();

  if (loading) {
    return (
      <div className="bg-white rounded-xl border border-gray-200 shadow-card p-8">
        <LoadingSpinner text="Ładowanie produktów..." colorClass="text-orange-500" />
      </div>
    );
  }

  return (
    <div className="bg-white rounded-xl border border-gray-200 shadow-card p-8">
      <div className="mb-6">
        <h2 className="text-2xl font-bold text-slate-900">Tworzenie Zestawu</h2>
        <p className="text-slate-500 font-medium mt-1">Formularz do tworzenia nowego combo / zestawu</p>
      </div>

      <AlertMessage variant="error" message={error} />
      <AlertMessage variant="success" message={success ? 'Zestaw został pomyślnie utworzony!' : ''} />

      <form onSubmit={handleSubmit} className="space-y-8">
        <section>
          <h3 className="text-lg font-bold text-slate-900 mb-4">Dane podstawowe</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <FormField
              label="Nazwa zestawu"
              required
              type="text"
              id="name"
              name="name"
              value={formData.name}
              onChange={handleInputChange}
              placeholder="np. Zestaw Burger + Frytki"
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
        </section>

        <section>
          <h3 className="text-xl font-bold text-slate-900 mb-2">Produkty w zestawie</h3>
          <p className="text-slate-500 font-medium mb-4">
            Wybierz produkty i określ ich ilość w zestawie
          </p>

          {products.length === 0 ? (
            <div className="border-2 border-dashed border-gray-300 rounded-xl p-8 text-center">
              <p className="text-slate-500">Brak dostępnych produktów</p>
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {products.map((product) => (
                <ProductQuantityCard
                  key={product.id}
                  product={product}
                  quantity={quantities[product.id] || 0}
                  onQuantityChange={handleQuantityChange}
                />
              ))}
            </div>
          )}
        </section>

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
            Utwórz Zestaw
          </SubmitButton>
        </div>
      </form>
    </div>
  );
}

function ProductQuantityCard({ product, quantity, onQuantityChange }) {
  return (
    <div
      className={`rounded-xl p-4 transition-all duration-200 ${
        quantity > 0
          ? 'border border-orange-400 bg-orange-50 shadow-sm'
          : 'border border-slate-200 bg-slate-50'
      }`}
    >
      <h4 className="font-semibold text-slate-800 mb-1">{product.name}</h4>
      <p className="text-xs text-slate-500 mb-3">
        {product.category} · {product.price} PLN
      </p>

      <div className="flex items-center justify-between bg-white rounded-lg p-2 border border-gray-200">
        <button
          type="button"
          onClick={() => onQuantityChange(product.id, -1)}
          className="w-10 h-10 flex items-center justify-center bg-red-500 hover:bg-red-600 text-white rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
          disabled={quantity === 0}
        >
          <Minus className="w-5 h-5" />
        </button>

        <span className="text-xl font-bold text-slate-800 min-w-[3rem] text-center">
          {quantity}
        </span>

        <button
          type="button"
          onClick={() => onQuantityChange(product.id, 1)}
          className="w-10 h-10 flex items-center justify-center bg-[#FF6600] hover:bg-[#e55b00] text-white rounded-lg transition-colors"
        >
          <Plus className="w-5 h-5" />
        </button>
      </div>
    </div>
  );
}

export default CreateCombo;

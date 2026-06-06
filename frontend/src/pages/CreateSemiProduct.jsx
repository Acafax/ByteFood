import React from 'react';
import { Save } from 'lucide-react';
import { useCreateSemiProductForm } from '../hooks/useCreateSemiProductForm.js';
import { useUnits } from '../hooks/useUnits.js';
import { useSubcategories } from '../hooks/useSubcategories.js';
import AlertMessage from '../components/ui/AlertMessage.jsx';
import FormField from '../components/ui/FormField.jsx';
import SelectField from '../components/ui/SelectField.jsx';
import SubcategorySelectField from '../components/subcategory/SubcategorySelectField.jsx';
import SubmitButton from '../components/ui/SubmitButton.jsx';

function CreateSemiProduct() {
  const {
    formData,
    loading,
    error,
    success,
    handleChange,
    handleSubmit,
    resetForm,
  } = useCreateSemiProductForm();

  const { units } = useUnits();
  const { subcategories, isLoading: subcategoriesLoading } = useSubcategories();

  return (
    <div className="bg-white rounded-xl border border-gray-200 shadow-card p-8">
      <div className="mb-6">
        <h2 className="text-2xl font-bold text-slate-900">Tworzenie Półproduktu</h2>
        <p className="text-slate-500 font-medium mt-1">Formularz do tworzenia nowego półproduktu</p>
      </div>

      <AlertMessage variant="error" message={error} />
      <AlertMessage variant="success" message={success ? 'Półprodukt został pomyślnie utworzony!' : ''} />

      <form onSubmit={handleSubmit} className="space-y-8">
        <section>
          <h3 className="text-lg font-bold text-slate-900 mb-4">Dane podstawowe</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <FormField
              label="Nazwa"
              required
              type="text"
              id="name"
              name="name"
              value={formData.name}
              onChange={handleChange}
              placeholder="Wprowadź nazwę półproduktu"
            />

            <SelectField
              label="Jednostka"
              required
              id="unit"
              name="unit"
              value={formData.unit}
              onChange={handleChange}
              options={units}
              placeholder="Wybierz jednostkę"
            />

            <SubcategorySelectField
              value={formData.subcategoryId}
              onChange={handleChange}
              options={subcategories}
              loading={subcategoriesLoading}
            />

            <FormField
              label="Minimalna ilość na stanie"
              required
              helpText="Wprowadź minimalny zapas w jednostce miary produktu. Przykład: jeśli jednostką jest gram (G), a chcesz ustawić próg na 7 kg, wpisz 7000."
              type="number"
              id="minimalStockQuantity"
              name="minimalStockQuantity"
              value={formData.minimalStockQuantity}
              onChange={handleChange}
              min="0"
            />
          </div>
        </section>

        <section>
          <h3 className="text-lg font-bold text-slate-900 mb-4">Makroskładniki (na 100g)</h3>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <FormField
              label="Tłuszcz (g)"
              required
              type="number"
              id="fat"
              name="fat"
              value={formData.fat}
              onChange={handleChange}
              step="0.1"
              min="0.1"
              placeholder="0.0"
            />

            <FormField
              label="Węglowodany (g)"
              required
              type="number"
              id="carbohydrate"
              name="carbohydrate"
              value={formData.carbohydrate}
              onChange={handleChange}
              step="0.1"
              min="0.1"
              placeholder="0.0"
            />

            <FormField
              label="Białko (g)"
              required
              type="number"
              id="protein"
              name="protein"
              value={formData.protein}
              onChange={handleChange}
              step="0.1"
              min="0.1"
              placeholder="0.0"
            />
          </div>
        </section>

        <div className="flex justify-end gap-4 pt-4 border-t border-gray-200">
          <button
            type="button"
            onClick={resetForm}
            className="px-6 py-2.5 border border-gray-300 text-slate-600 rounded-lg hover:bg-slate-50 transition-colors font-medium"
            disabled={loading}
          >
            Wyczyść
          </button>

          <SubmitButton loading={loading} icon={Save}>
            Utwórz Półprodukt
          </SubmitButton>
        </div>
      </form>
    </div>
  );
}

export default CreateSemiProduct;

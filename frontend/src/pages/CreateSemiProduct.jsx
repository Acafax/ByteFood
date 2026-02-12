import React from 'react';
import { Save } from 'lucide-react';
import { useCreateSemiProductForm } from '../hooks/useCreateSemiProductForm.js';
import { useUnits } from '../hooks/useUnits.js';
import AlertMessage from '../components/ui/AlertMessage.jsx';
import FormField from '../components/ui/FormField.jsx';
import SelectField from '../components/ui/SelectField.jsx';
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

  return (
    <div className="bg-white rounded-xl border border-gray-200 shadow-card p-8">
      <div className="mb-6">
        <h2 className="text-2xl font-bold text-slate-900">Tworzenie Półproduktu</h2>
        <p className="text-slate-500 font-medium mt-1">Formularz do tworzenia nowego półproduktu</p>
      </div>

      <AlertMessage variant="error" message={error} />
      <AlertMessage variant="success" message={success ? 'Półprodukt został pomyślnie utworzony!' : ''} />

      <form onSubmit={handleSubmit} className="space-y-6">
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
          placeholder="Wprowadź ilość tłuszczu"
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
          placeholder="Wprowadź ilość węglowodanów"
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
          placeholder="Wprowadź ilość białka"
        />

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

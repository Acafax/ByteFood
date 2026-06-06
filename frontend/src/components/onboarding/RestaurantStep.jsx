import React from 'react';
import { Building2 } from 'lucide-react';
import FormField from '../ui/FormField.jsx';

function RestaurantStep({ restaurantName, onChange }) {
  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold text-slate-900">Dane restauracji</h2>
        <p className="text-slate-500 mt-1">
          Podaj podstawowe informacje o Twoim lokalu gastronomicznym.
        </p>
      </div>

      <FormField
        label="Nazwa restauracji"
        icon={Building2}
        id="restaurantName"
        name="restaurantName"
        type="text"
        value={restaurantName}
        onChange={onChange}
        placeholder="np. Burger House"
      />
    </div>
  );
}

export default RestaurantStep;

import React from 'react';
import { Warehouse } from 'lucide-react';
import FormField from '../ui/FormField.jsx';

function StockStep({ stockName, onChange }) {
  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold text-slate-900">Magazyn początkowy</h2>
        <p className="text-slate-500 mt-1">
          Utwórz główny magazyn, w którym będziesz prowadzić stany produktów.
        </p>
      </div>

      <FormField
        label="Nazwa magazynu"
        icon={Warehouse}
        id="stockName"
        name="stockName"
        type="text"
        value={stockName}
        onChange={onChange}
        placeholder="np. Magazyn główny"
      />
    </div>
  );
}

export default StockStep;

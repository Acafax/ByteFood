import React from 'react';
import { Package, AlertTriangle, CheckCircle, FileDown } from 'lucide-react';
import { useStock } from '../hooks/useStock.js';
import { generateInventoryReport } from '../utils/pdfReport.js';
import LoadingSpinner from '../components/ui/LoadingSpinner.jsx';
import AlertMessage from '../components/ui/AlertMessage.jsx';

/**
 * Derives a status string by comparing current quantity to the minimum stock level.
 * - critical: quantity <= 25 % of minimalStockQuantity
 * - low:      quantity <= 100 % of minimalStockQuantity
 * - ok:       quantity > minimalStockQuantity
 */
function deriveStatus(quantity, minimalStockQuantity) {
  if (quantity <= minimalStockQuantity * 0.25) return 'critical';
  if (quantity <= minimalStockQuantity) return 'low';
  return 'ok';
}

function Magazyn() {
  const { stock, isLoading, isError, error } = useStock();

  if (isLoading) {
    return <LoadingSpinner text="Ładowanie danych magazynowych..." colorClass="text-orange-500" />;
  }

  if (isError) {
    return (
      <div className="space-y-6">
        <AlertMessage variant="error" message={error?.message || 'Błąd podczas pobierania danych magazynowych'} />
      </div>
    );
  }

  // Map DTO to view-friendly objects (guard against non-array responses)
  const safeStock = Array.isArray(stock) ? stock : [];
  const inventory = safeStock.map((item) => {
    const dict = item.stockItemDictionary || {};
    const unitLabel = dict.unit ? dict.unit.toLowerCase() : '';
    return {
      id: item.id,
      name: dict.name || '—',
      quantity: item.quantity ?? 0,
      minStock: dict.minimalStockQuantity ?? 0,
      unit: unitLabel,
      status: deriveStatus(item.quantity ?? 0, dict.minimalStockQuantity ?? 0),
    };
  });

  const criticalItems = inventory.filter((i) => i.status === 'critical').length;
  const lowStockItems = inventory.filter((i) => i.status === 'low').length;

  return (
    <div className="space-y-6">
      {/* Summary Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <SummaryCard
          label="Wszystkie Produkty"
          value={inventory.length}
          valueClass="text-slate-900"
          borderClass="border-[#FF6600]"
          Icon={Package}
          iconClass="text-[#FF6600]"
        />
        <SummaryCard
          label="Niski Stan"
          value={lowStockItems}
          valueClass="text-yellow-600"
          borderClass="border-yellow-500"
          Icon={AlertTriangle}
          iconClass="text-yellow-500"
        />
        <SummaryCard
          label="Stan Krytyczny"
          value={criticalItems}
          valueClass="text-red-600"
          borderClass="border-red-500"
          Icon={AlertTriangle}
          iconClass="text-red-500"
        />
      </div>

      {/* Inventory Table */}
      <div className="bg-white rounded-xl border border-gray-200 shadow-card overflow-hidden">
        <div className="bg-gradient-to-r from-[#FF6600] to-orange-500 px-8 py-5">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <Package className="w-7 h-7 text-white" />
              <h2 className="text-xl font-bold text-white">Stan Magazynowy</h2>
            </div>

            <button
              onClick={() => generateInventoryReport(inventory)}
              className="flex items-center gap-2 px-5 py-2.5 bg-white text-[#FF6600] rounded-lg font-semibold hover:bg-orange-50 transition-colors shadow-sm text-sm"
            >
              <FileDown className="w-4 h-4" />
              Generuj raport PDF
            </button>
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="bg-slate-50 border-b border-gray-200">
                <th className="px-8 py-4 text-left text-xs font-bold text-slate-500 uppercase tracking-wider">Składnik</th>
                <th className="px-8 py-4 text-right text-xs font-bold text-slate-500 uppercase tracking-wider">Ilość Obecna</th>
                <th className="px-8 py-4 text-right text-xs font-bold text-slate-500 uppercase tracking-wider">Min. Stan</th>
                <th className="px-8 py-4 text-center text-xs font-bold text-slate-500 uppercase tracking-wider">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200">
              {inventory.length === 0 ? (
                <tr>
                  <td colSpan={4} className="px-8 py-12 text-center text-slate-500">
                    Brak danych magazynowych
                  </td>
                </tr>
              ) : (
                inventory.map((item) => (
                  <tr key={item.id} className="hover:bg-slate-50 transition-colors duration-150">
                    <td className="px-8 py-4">
                      <p className="text-slate-900 font-semibold">{item.name}</p>
                    </td>
                    <td className="px-8 py-4 text-right">
                      <p className="text-slate-700 font-medium">{item.quantity} {item.unit}</p>
                    </td>
                    <td className="px-8 py-4 text-right">
                      <p className="text-slate-500 font-medium">{item.minStock} {item.unit}</p>
                    </td>
                    <td className="px-8 py-4 text-center">
                      <StatusBadge status={item.status} />
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}

/* ─── Sub-components ─── */

function SummaryCard({ label, value, valueClass, borderClass, Icon, iconClass }) {
  return (
    <div className={`bg-white rounded-xl border border-gray-200 shadow-sm p-6 border-l-4 ${borderClass}`}>
      <div className="flex items-center justify-between">
        <div>
          <p className="text-slate-500 text-sm font-medium">{label}</p>
          <p className={`text-3xl font-bold mt-2 ${valueClass}`}>{value}</p>
        </div>
        <Icon className={`w-10 h-10 opacity-70 ${iconClass}`} />
      </div>
    </div>
  );
}

function StatusBadge({ status }) {
  const CONFIG = {
    ok: { bg: 'bg-green-50 text-green-700 border border-green-200', Icon: CheckCircle, label: 'OK' },
    low: { bg: 'bg-yellow-50 text-yellow-700 border border-yellow-200', Icon: AlertTriangle, label: 'Niski Stan' },
    critical: { bg: 'bg-red-50 text-red-700 border border-red-200', Icon: AlertTriangle, label: 'Krytyczny' },
  };

  const cfg = CONFIG[status];
  if (!cfg) return null;

  return (
    <span className={`inline-flex items-center gap-1 px-3 py-1 rounded-full text-xs font-semibold ${cfg.bg}`}>
      <cfg.Icon className="w-3.5 h-3.5" />
      {cfg.label}
    </span>
  );
}

export default Magazyn;

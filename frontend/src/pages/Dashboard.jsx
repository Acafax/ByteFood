import React from 'react';
import { TrendingUp, DollarSign, Award } from 'lucide-react';

function Dashboard() {
  const currentSales = 0;
  const topProducts = [];

  return (
    <div className="space-y-8">
      {/* Sales Card */}
      <div className="flex justify-center">
        <div className="bg-white rounded-xl border border-gray-200 shadow-card p-8 w-full max-w-2xl">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-2xl font-bold text-slate-900">Aktualna Sprzedaż</h2>
            <TrendingUp className="w-10 h-10 bf-text-primary" />
          </div>
          <div className="flex items-baseline gap-2">
            <p className="text-6xl font-bold text-slate-900">{currentSales.toFixed(2)}</p>
            <span className="text-2xl ml-2 text-slate-500">PLN</span>
          </div>
          <p className="mt-4 text-lg text-slate-500 font-medium">Dzisiejszy całkowity przychód</p>
        </div>
      </div>

      {/* Top Products Table */}
      <div className="bg-white rounded-xl border border-gray-200 shadow-card overflow-hidden">
        <div className="bg-gradient-to-r from-[#FF6600] to-orange-500 px-8 py-5">
          <div className="flex items-center gap-3">
            <Award className="w-7 h-7 text-white" />
            <h2 className="text-xl font-bold text-white">
              Top 5 Najczęściej Zamawianych Produktów
            </h2>
          </div>
        </div>
        
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="bg-slate-50 border-b border-gray-200">
                <th className="px-8 py-4 text-left text-xs font-bold text-slate-500 uppercase tracking-wider">
                  Ranking
                </th>
                <th className="px-8 py-4 text-left text-xs font-bold text-slate-500 uppercase tracking-wider">
                  Nazwa Produktu
                </th>
                <th className="px-8 py-4 text-right text-xs font-bold text-slate-500 uppercase tracking-wider">
                  Ilość Sprzedana
                </th>
                <th className="px-8 py-4 text-right text-xs font-bold text-slate-500 uppercase tracking-wider">
                  Przychód (PLN)
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200">
              {topProducts.map((product) => (
                <tr
                  key={product.rank}
                  className="hover:bg-slate-50 transition-colors duration-150"
                >
                  <td className="px-8 py-4">
                    <div className="flex items-center justify-center w-10 h-10 rounded-full bg-[#FF6600] text-white font-bold text-lg">
                      {product.rank}
                    </div>
                  </td>
                  <td className="px-8 py-4">
                    <p className="text-slate-900 font-semibold">
                      {product.name}
                    </p>
                  </td>
                  <td className="px-8 py-4 text-right">
                    <p className="text-slate-700 font-medium">
                      {product.quantity}
                    </p>
                  </td>
                  <td className="px-8 py-4 text-right">
                    <p className="text-green-600 font-bold">
                      {product.revenue.toFixed(2)} PLN
                    </p>
                  </td>
                </tr>
              ))}
            </tbody>
            <tfoot className="bg-slate-50">
              <tr className="border-t-2 border-gray-300">
                <td colSpan="2" className="px-8 py-4">
                  <p className="text-slate-900 font-bold">SUMA</p>
                </td>
                <td className="px-8 py-4 text-right">
                  <p className="text-slate-900 font-bold">
                    {topProducts.reduce((sum, p) => sum + p.quantity, 0)}
                  </p>
                </td>
                <td className="px-8 py-4 text-right">
                  <p className="text-green-600 font-bold">
                    {topProducts.reduce((sum, p) => sum + p.revenue, 0).toFixed(2)} PLN
                  </p>
                </td>
              </tr>
            </tfoot>
          </table>
        </div>
      </div>
    </div>
  );
}

export default Dashboard;

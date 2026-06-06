import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Sparkles } from 'lucide-react';
import ikonkaBF from '../assets/ikonka.png';

function WelcomePage() {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen bg-slate-100 flex items-center justify-center p-4">
      <div className="bg-white rounded-xl border border-gray-200 shadow-sm w-full max-w-md p-8 text-center">
        <div className="inline-flex items-center justify-center w-16 h-16 bg-slate-50 rounded-full mb-4 border border-gray-200">
          <img src={ikonkaBF} alt="ByteFood" className="w-12 h-12 object-contain" />
        </div>

        <div className="inline-flex items-center justify-center w-12 h-12 bg-orange-50 rounded-full mb-4">
          <Sparkles className="w-6 h-6 text-[#FF6600]" />
        </div>

        <h1 className="text-2xl font-bold text-slate-900">Witaj w systemie!</h1>
        <p className="text-slate-500 mt-3 leading-relaxed">
          Skonfiguruj swoją restaurację, aby rozpocząć sprzedaż.
        </p>

        <button
          type="button"
          onClick={() => navigate('/onboarding')}
          className="mt-8 w-full bg-[#FF6600] hover:bg-[#e55b00] text-white font-semibold py-2.5 rounded-lg transition-colors duration-200 shadow-sm"
        >
          Rozpocznij konfigurację
        </button>
      </div>
    </div>
  );
}

export default WelcomePage;

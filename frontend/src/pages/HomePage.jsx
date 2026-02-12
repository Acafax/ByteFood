import React from 'react';
import { Link } from 'react-router-dom';
import { ChefHat, ArrowRight } from 'lucide-react';

function HomePage() {
  return (
    <div className="min-h-screen bg-slate-100 flex items-center justify-center p-4">
      <div className="text-center max-w-4xl">
        <div className="inline-flex items-center justify-center w-24 h-24 bg-white rounded-full mb-8 border border-gray-200 shadow-card">
          <ChefHat className="w-14 h-14 text-[#FF6600]" />
        </div>
        
        <h1 className="text-5xl md:text-6xl font-bold mb-6 text-slate-900">
          ByteFood – Panel Restauracji
        </h1>
        
        <p className="text-2xl mb-12 text-slate-500 font-medium">
          Profesjonalny system zarządzania Twoją restauracją
        </p>
        
        <div className="flex gap-6 justify-center flex-wrap">
          <Link
            to="/login"
            className="inline-flex items-center gap-2 px-8 py-4 bg-[#FF6600] hover:bg-[#e55b00] text-white rounded-xl font-bold text-lg shadow-md shadow-orange-500/20 hover:-translate-y-0.5 transition-all duration-200"
          >
            Zaloguj się
            <ArrowRight className="w-5 h-5" />
          </Link>
          
          <Link
            to="/register"
            className="inline-flex items-center gap-2 px-8 py-4 bg-white text-slate-800 rounded-xl font-bold text-lg border border-gray-200 hover:bg-slate-50 hover:-translate-y-0.5 transition-all duration-200 shadow-card"
          >
            Zarejestruj się
            <ArrowRight className="w-5 h-5" />
          </Link>
        </div>

        <div className="mt-16 grid grid-cols-1 md:grid-cols-3 gap-6 text-left">
          <div className="bg-white rounded-xl p-6 border border-gray-200 shadow-card">
            <h3 className="text-xl font-bold text-slate-900 mb-2">Panel główny</h3>
            <p className="text-slate-500 font-medium">Monitoruj sprzedaż i topowe produkty w czasie rzeczywistym</p>
          </div>
          
          <div className="bg-white rounded-xl p-6 border border-gray-200 shadow-card">
            <h3 className="text-xl font-bold text-slate-900 mb-2">Magazyn</h3>
            <p className="text-slate-500 font-medium">Śledź stany magazynowe i zarządzaj dostawami</p>
          </div>
          
          <div className="bg-white rounded-xl p-6 border border-gray-200 shadow-card">
            <h3 className="text-xl font-bold text-slate-900 mb-2">Zarządzanie</h3>
            <p className="text-slate-500 font-medium">Twórz produkty, zestawy i modyfikacje</p>
          </div>
        </div>
      </div>
    </div>
  );
}

export default HomePage;

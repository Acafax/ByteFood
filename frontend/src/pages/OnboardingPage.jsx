import React from 'react';
import AlertMessage from '../components/ui/AlertMessage.jsx';
import OnboardingStepper from '../components/onboarding/OnboardingStepper.jsx';
import RestaurantStep from '../components/onboarding/RestaurantStep.jsx';
import StockStep from '../components/onboarding/StockStep.jsx';
import { useOnboarding } from '../hooks/useOnboarding.js';
import ikonkaBF from '../assets/ikonka.png';

function OnboardingPage() {
  const {
    currentStep,
    restaurantName,
    setRestaurantName,
    stockName,
    setStockName,
    error,
    isSubmitting,
    goToNextStep,
    goToPreviousStep,
    submitRestaurantStep,
  } = useOnboarding();

  const handleRestaurantNameChange = (e) => setRestaurantName(e.target.value);
  const handleStockNameChange = (e) => setStockName(e.target.value);

  const handlePrimaryAction = async () => {
    if (currentStep === 1) {
      if (!restaurantName.trim()) return;
      goToNextStep();
      return;
    }

    await submitRestaurantStep();
  };

  const primaryLabel = currentStep === 1 ? 'Dalej' : 'Utwórz restaurację i magazyn';

  return (
    <div className="min-h-screen bg-slate-100 flex items-center justify-center p-4">
      <div className="bg-white rounded-xl border border-gray-200 shadow-sm w-full max-w-2xl p-8">
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-16 h-16 bg-slate-50 rounded-full mb-4 border border-gray-200">
            <img src={ikonkaBF} alt="ByteFood" className="w-12 h-12 object-contain" />
          </div>
          <h1 className="text-3xl font-bold text-slate-900">Konfiguracja restauracji</h1>
          <p className="text-slate-500 mt-2">Ukończ dwa kroki, aby rozpocząć pracę w ByteFood</p>
        </div>

        <OnboardingStepper currentStep={currentStep} />
        <AlertMessage variant="error" message={error} />

        {currentStep === 1 && (
          <RestaurantStep
            restaurantName={restaurantName}
            onChange={handleRestaurantNameChange}
          />
        )}

        {currentStep === 2 && (
          <StockStep stockName={stockName} onChange={handleStockNameChange} />
        )}

        <div className="flex flex-col-reverse sm:flex-row gap-3 mt-8">
          {currentStep === 2 && (
            <button
              type="button"
              onClick={goToPreviousStep}
              disabled={isSubmitting}
              className="flex-1 bg-white hover:bg-slate-50 text-slate-700 font-semibold py-2.5 rounded-lg transition-colors duration-200 border border-gray-300 disabled:opacity-50"
            >
              Wstecz
            </button>
          )}

          <button
            type="button"
            onClick={handlePrimaryAction}
            disabled={isSubmitting || (currentStep === 1 && !restaurantName.trim())}
            className="flex-1 bg-[#FF6600] hover:bg-[#e55b00] text-white font-semibold py-2.5 rounded-lg transition-colors duration-200 shadow-sm disabled:opacity-50"
          >
            {isSubmitting ? 'Zapisywanie...' : primaryLabel}
          </button>
        </div>
      </div>
    </div>
  );
}

export default OnboardingPage;

import React from 'react';

const STEPS = [
  { id: 1, label: 'Restauracja' },
  { id: 2, label: 'Magazyn' },
];

function OnboardingStepper({ currentStep }) {
  return (
    <div className="flex items-center justify-between gap-2 mb-8">
      {STEPS.map((step, index) => {
        const isActive = currentStep === step.id;
        const isCompleted = currentStep > step.id;

        return (
          <React.Fragment key={step.id}>
            <div className="flex flex-col items-center flex-1 min-w-0">
              <div
                className={`w-10 h-10 rounded-full flex items-center justify-center text-sm font-semibold border-2 transition-colors ${
                  isActive
                    ? 'bg-[#FF6600] border-[#FF6600] text-white'
                    : isCompleted
                      ? 'bg-orange-50 border-[#FF6600] text-[#FF6600]'
                      : 'bg-white border-gray-300 text-slate-400'
                }`}
              >
                {step.id}
              </div>
              <span
                className={`mt-2 text-xs font-medium text-center ${
                  isActive ? 'text-slate-900' : 'text-slate-500'
                }`}
              >
                {step.label}
              </span>
            </div>
            {index < STEPS.length - 1 && (
              <div
                className={`h-0.5 flex-1 mb-6 ${
                  isCompleted ? 'bg-[#FF6600]' : 'bg-gray-200'
                }`}
              />
            )}
          </React.Fragment>
        );
      })}
    </div>
  );
}

export default OnboardingStepper;

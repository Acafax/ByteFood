import React from 'react';
import { AlertCircle, CheckCircle } from 'lucide-react';

const VARIANTS = {
  error: {
    container: 'bg-red-50 border-red-200 text-red-700',
    Icon: AlertCircle,
  },
  success: {
    container: 'bg-green-50 border-green-200 text-green-700',
    Icon: CheckCircle,
  },
};

function AlertMessage({ variant = 'error', message }) {
  if (!message) return null;

  const { container, Icon } = VARIANTS[variant];

  return (
    <div className={`mb-6 border px-4 py-3 rounded-xl flex items-center gap-2 ${container}`}>
      <Icon className="w-5 h-5 flex-shrink-0" />
      <span className="text-sm font-medium">{message}</span>
    </div>
  );
}

export default AlertMessage;

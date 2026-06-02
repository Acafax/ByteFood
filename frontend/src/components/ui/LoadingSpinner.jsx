import React from 'react';
import { Loader } from 'lucide-react';

function LoadingSpinner({ text = 'Ładowanie...', colorClass = 'text-green-600' }) {
  return (
    <div className="flex items-center justify-center h-64">
      <div className="text-center">
        <Loader className={`w-12 h-12 ${colorClass} animate-spin mx-auto mb-4`} />
        <p className="text-gray-600 text-lg">{text}</p>
      </div>
    </div>
  );
}

export default LoadingSpinner;

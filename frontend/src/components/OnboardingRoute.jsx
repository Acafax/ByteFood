import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth.js';
import LoadingSpinner from './ui/LoadingSpinner.jsx';

function OnboardingRoute({ children }) {
  const { isAuthenticated, loading, restaurantId } = useAuth();

  if (loading) {
    return <LoadingSpinner text="Ładowanie..." colorClass="text-indigo-600" />;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (restaurantId != null) {
    return <Navigate to="/dashboard" replace />;
  }

  return children;
}

export default OnboardingRoute;

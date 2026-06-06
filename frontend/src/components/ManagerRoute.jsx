import React from 'react';
import { Navigate } from 'react-router-dom';
import ProtectedRoute from './ProtectedRoute.jsx';
import { useAuth } from '../hooks/useAuth.js';

function ManagerRoute({ children }) {
  const { role } = useAuth();

  return (
    <ProtectedRoute>
      {role === 'MANAGER' ? children : <Navigate to="/dashboard" replace />}
    </ProtectedRoute>
  );
}

export default ManagerRoute;

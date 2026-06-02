import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import Layout from './components/Layout';
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import Dashboard from './pages/Dashboard';
import Magazyn from './pages/Magazyn';
import CreateSemiProduct from './pages/CreateSemiProduct';
import CreateModification from './pages/CreateModification';
import CreateProduct from './pages/CreateProduct';
import CreateSet from './pages/CreateSet';
import './App.css';

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          {/* Public Routes */}
          <Route path="/" element={<HomePage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />

          {/* Protected Routes with Layout */}
          <Route element={<ProtectedRoute><Layout /></ProtectedRoute>}>
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/stock" element={<Magazyn />} />
            <Route path="/create-semi-product" element={<CreateSemiProduct />} />
            <Route path="/create-modification" element={<CreateModification />} />
            <Route path="/create-product" element={<CreateProduct />} />
            <Route path="/create-set" element={<CreateSet />} />
          </Route>

          {/* Legacy redirect */}
          <Route path="/main-admin-panel" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;

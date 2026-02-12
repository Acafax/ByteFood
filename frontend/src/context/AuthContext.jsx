import React, { createContext, useContext, useState, useEffect } from 'react';
import { login as apiLogin, register as apiRegister } from '../api/auth.js';
import { getToken, setToken } from '../api/client.js';

const AuthContext = createContext(null);

// Check if mock mode is enabled
const MOCK_MODE = import.meta.env.VITE_MOCK_MODE === 'true';

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // W trybie mock automatycznie zaloguj użytkownika deweloperskiego
    if (MOCK_MODE) {
      const mockUser = {
        email: 'dev@example.com',
        name: 'Dev',
        lastName: 'User',
        restaurantId: 1,
        token: 'mock-token-123',
        expirationTime: Date.now() + 86400000 // 24h
      };
      setUser(mockUser);
      localStorage.setItem('user', JSON.stringify(mockUser));
      setToken('mock-token-123');
      setLoading(false);
      return;
    }

    // Check if user is already logged in (from localStorage)
    const savedUser = localStorage.getItem('user');
    const token = getToken();
    if (savedUser && token) {
      try {
        setUser(JSON.parse(savedUser));
      } catch (e) {
        // Invalid data, clear it
        localStorage.removeItem('user');
        setToken(null);
      }
    }
    setLoading(false);
  }, []);

  const login = async (email, password) => {
    // W trybie mock zwróć od razu mock usera
    if (MOCK_MODE) {
      const mockUser = {
        email: email || 'dev@example.com',
        name: 'Dev',
        lastName: 'User',
        restaurantId: 1,
        token: 'mock-token-123',
        expirationTime: Date.now() + 86400000 // 24h
      };
      setUser(mockUser);
      localStorage.setItem('user', JSON.stringify(mockUser));
      setToken('mock-token-123');
      return mockUser;
    }

    try {
      const response = await apiLogin(email, password);
      const userData = {
        email: email,
        restaurantId: response.restaurantId,
        token: response.token,
        expirationTime: response.expirationTime
      };
      setUser(userData);
      localStorage.setItem('user', JSON.stringify(userData));
      return userData;
    } catch (error) {
      const errorMessage = error.data?.message || error.message || 'Logowanie nie powiodło się';
      throw new Error(errorMessage);
    }
  };

  const register = async (email, password, name, lastName) => {
    // W trybie mock zwróć od razu mock usera
    if (MOCK_MODE) {
      const mockUser = {
        email: email || 'dev@example.com',
        name: name || 'Dev',
        lastName: lastName || 'User',
        restaurantId: 1,
        token: 'mock-token-123',
        expirationTime: Date.now() + 86400000 // 24h
      };
      setUser(mockUser);
      localStorage.setItem('user', JSON.stringify(mockUser));
      setToken('mock-token-123');
      return mockUser;
    }

    try {
      const response = await apiRegister(email, password, name, lastName);
      const userData = {
        email: email,
        name: name,
        lastName: lastName,
        restaurantId: response.restaurantId,
        token: response.token,
        expirationTime: response.expirationTime
      };
      setUser(userData);
      localStorage.setItem('user', JSON.stringify(userData));
      return userData;
    } catch (error) {
      const errorMessage = error.data?.message || error.message || 'Rejestracja nie powiodła się';
      throw new Error(errorMessage);
    }
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem('user');
    setToken(null);
  };

  const value = {
    user,
    login,
    register,
    logout,
    loading,
    isAuthenticated: !!user
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

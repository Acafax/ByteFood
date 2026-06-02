import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Mail, Lock } from 'lucide-react';
import FormField from '../components/ui/FormField.jsx';
import AlertMessage from '../components/ui/AlertMessage.jsx';
import ikonkaBF from '../assets/ikonka.png';

function LoginPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const { login } = useAuth();

  const handleLogin = async (e) => {
    e.preventDefault();
    setError('');

    if (!email || !password) {
      setError('Proszę wypełnić wszystkie pola');
      return;
    }

    try {
      await login(email, password);
      navigate('/dashboard');
    } catch (err) {
      setError('Logowanie nie powiodło się. Spróbuj ponownie.');
    }
  };

  return (
    <div className="min-h-screen bg-slate-100 flex items-center justify-center p-4">
      <div className="bg-white rounded-xl border border-gray-200 shadow-[0_2px_8px_rgba(0,0,0,0.04)] w-full max-w-md p-8">
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-20 h-20 bg-slate-50 rounded-full mb-4 border border-gray-200">
            <img src={ikonkaBF} alt="ByteFood" className="w-16 h-16 object-contain" />
          </div>
          <h1 className="text-3xl font-bold text-slate-900">ByteFood</h1>
          <p className="text-slate-500 mt-2">Zaloguj się do swojego konta</p>
        </div>

        <AlertMessage variant="error" message={error} />

        <form onSubmit={handleLogin} className="space-y-6">
          <FormField
            label="Adres Email"
            icon={Mail}
            id="email"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="admin@restauracja.pl"
          />

          <FormField
            label="Hasło"
            icon={Lock}
            id="password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="••••••••"
          />

          <button
            type="submit"
            className="w-full bg-[#FF6600] hover:bg-[#e55b00] text-white font-semibold py-2.5 rounded-lg transition-all duration-200 shadow-md shadow-orange-500/20 hover:-translate-y-0.5"
          >
            Zaloguj się
          </button>
        </form>

        <div className="mt-6 text-center">
          <p className="text-slate-500">
            Nie masz konta?{' '}
            <Link to="/register" className="text-[#FF6600] hover:text-[#e55b00] font-semibold transition-colors">
              Zarejestruj się tutaj
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}

export default LoginPage;

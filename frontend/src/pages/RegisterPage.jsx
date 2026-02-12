import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Mail, Lock, User } from 'lucide-react';
import FormField from '../components/ui/FormField.jsx';
import AlertMessage from '../components/ui/AlertMessage.jsx';
import ikonkaBF from '../assets/ikonka.png';

function RegisterPage() {
  const [name, setName] = useState('');
  const [lastName, setLastName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const { register } = useAuth();

  const handleRegister = async (e) => {
    e.preventDefault();
    setError('');

    if (!name || !lastName || !email || !password || !confirmPassword) {
      setError('Proszę wypełnić wszystkie pola');
      return;
    }

    if (password !== confirmPassword) {
      setError('Hasła nie są identyczne');
      return;
    }

    if (password.length < 6) {
      setError('Hasło musi mieć co najmniej 6 znaków');
      return;
    }

    try {
      await register(email, password, name, lastName);
      navigate('/dashboard');
    } catch (err) {
      setError(err.message || 'Rejestracja nie powiodła się. Spróbuj ponownie.');
    }
  };

  return (
    <div className="min-h-screen bg-slate-100 flex items-center justify-center p-4">
      <div className="bg-white rounded-xl border border-gray-200 shadow-[0_2px_8px_rgba(0,0,0,0.04)] w-full max-w-md p-8">
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-20 h-20 bg-slate-50 rounded-full mb-4 border border-gray-200">
            <img src={ikonkaBF} alt="ByteFood" className="w-16 h-16 object-contain" />
          </div>
          <h1 className="text-3xl font-bold text-slate-900">Utwórz konto</h1>
          <p className="text-slate-500 mt-2">Dołącz do ByteFood</p>
        </div>

        <AlertMessage variant="error" message={error} />

        <form onSubmit={handleRegister} className="space-y-5">
          <FormField
            label="Imię"
            icon={User}
            id="name"
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="Jan"
          />

          <FormField
            label="Nazwisko"
            icon={User}
            id="lastName"
            type="text"
            value={lastName}
            onChange={(e) => setLastName(e.target.value)}
            placeholder="Kowalski"
          />

          <FormField
            label="Adres Email"
            icon={Mail}
            id="email"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="jan@restauracja.com"
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

          <FormField
            label="Potwierdź hasło"
            icon={Lock}
            id="confirmPassword"
            type="password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            placeholder="••••••••"
          />

          <button
            type="submit"
            className="w-full bg-[#FF6600] hover:bg-[#e55b00] text-white font-semibold py-2.5 rounded-lg transition-all duration-200 shadow-md shadow-orange-500/20 hover:-translate-y-0.5"
          >
            Utwórz konto
          </button>
        </form>

        <div className="mt-6 text-center">
          <p className="text-slate-500">
            Masz już konto?{' '}
            <Link to="/login" className="text-[#FF6600] hover:text-[#e55b00] font-semibold transition-colors">
              Zaloguj się tutaj
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}

export default RegisterPage;

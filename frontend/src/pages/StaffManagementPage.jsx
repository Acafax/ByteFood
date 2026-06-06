import React from 'react';
import { Mail, Lock, User, UserPlus } from 'lucide-react';
import FormField from '../components/ui/FormField.jsx';
import AlertMessage from '../components/ui/AlertMessage.jsx';
import SubmitButton from '../components/ui/SubmitButton.jsx';
import { useCreateEmployeeForm } from '../hooks/useCreateEmployeeForm.js';

function StaffManagementPage() {
  const { formData, isSubmitting, error, success, handleChange, handleSubmit } =
    useCreateEmployeeForm();

  return (
    <div className="bg-white rounded-xl border border-gray-200 shadow-sm p-8">
      <div className="mb-6">
        <h2 className="text-2xl font-bold text-slate-900">Zarządzanie personelem</h2>
        <p className="text-slate-500 font-medium mt-1">
          Utwórz konto pracownika z rolą EMPLOYEE przypisane do Twojej restauracji.
        </p>
      </div>

      <AlertMessage variant="error" message={error} />
      <AlertMessage variant="success" message={success ? 'Pracownik został pomyślnie utworzony' : ''} />

      <form onSubmit={handleSubmit} className="space-y-5 max-w-xl">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
          <FormField
            label="Imię"
            icon={User}
            id="name"
            name="name"
            type="text"
            value={formData.name}
            onChange={handleChange}
            placeholder="Jan"
          />
          <FormField
            label="Nazwisko"
            icon={User}
            id="lastName"
            name="lastName"
            type="text"
            value={formData.lastName}
            onChange={handleChange}
            placeholder="Kowalski"
          />
        </div>

        <FormField
          label="Adres email"
          icon={Mail}
          id="email"
          name="email"
          type="email"
          value={formData.email}
          onChange={handleChange}
          placeholder="jan@restauracja.pl"
        />

        <FormField
          label="Hasło"
          icon={Lock}
          id="password"
          name="password"
          type="password"
          value={formData.password}
          onChange={handleChange}
          placeholder="Minimum 12 znaków"
        />

        <div className="flex items-center gap-2 px-4 py-3 bg-slate-50 rounded-lg border border-gray-200">
          <UserPlus className="w-4 h-4 text-[#FF6600]" />
          <span className="text-sm text-slate-600">
            Rola: <span className="font-semibold text-slate-900">EMPLOYEE</span> (przypisana automatycznie)
          </span>
        </div>

        <SubmitButton loading={isSubmitting} loadingText="Tworzenie...">
          Utwórz pracownika
        </SubmitButton>
      </form>
    </div>
  );
}

export default StaffManagementPage;

import React, { useState } from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { LogOut, User } from 'lucide-react';
import { MENU_ITEMS } from '../constants/navigation.js';
import SidebarNavList from './SidebarNavList.jsx';
import ikonkaBF from '../assets/ikonka.png';

function Layout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const handleNavClick = (path) => {
    navigate(path);
    setIsSidebarOpen(false);
  };

  const currentLabel =
    MENU_ITEMS.find((item) => item.path === location.pathname)?.label || 'Panel główny';

  return (
    <div className="min-h-screen w-full bg-slate-50 flex flex-col md:flex-row">
      {/* Desktop sidebar */}
      <aside className="hidden md:flex w-64 flex-shrink-0 bg-white flex-col border-r border-gray-200/80">
        <div className="p-6 border-b border-gray-200/80 flex items-center justify-center">
          <img src={ikonkaBF} alt="ByteFood" className="h-12 w-auto object-contain" />
        </div>

        <nav className="flex-1 p-4 overflow-y-auto">
          <SidebarNavList items={MENU_ITEMS} onNavigate={handleNavClick} />
        </nav>

        <div className="p-4 border-t border-gray-200/80">
          <p className="text-xs text-slate-400 text-center">© 2025 ByteFood</p>
        </div>
      </aside>

      {/* Mobile sidebar (slide-over) */}
      {isSidebarOpen && (
        <>
          <div
            className="fixed inset-0 bg-black/40 z-40 md:hidden"
            onClick={() => setIsSidebarOpen(false)}
          />
          <aside className="fixed inset-y-0 left-0 w-64 bg-white flex-col border-r border-gray-200/80 z-50 md:hidden flex">
            <div className="p-6 border-b border-gray-200/80 flex items-center justify-between">
              <div className="flex items-center gap-3">
                <img src={ikonkaBF} alt="ByteFood" className="h-10 w-auto object-contain" />
                <p className="text-sm text-slate-500">Panel zarządzania</p>
              </div>
              <button
                type="button"
                onClick={() => setIsSidebarOpen(false)}
                className="text-slate-400 hover:text-slate-600 transition-colors"
              >
                ✕
              </button>
            </div>

            <nav className="flex-1 p-4 overflow-y-auto">
              <SidebarNavList items={MENU_ITEMS} onNavigate={handleNavClick} />
            </nav>

            <div className="p-4 border-t border-gray-200/80">
              <p className="text-xs text-slate-400 text-center">© 2025 ByteFood</p>
            </div>
          </aside>
        </>
      )}

      {/* Main content area */}
      <div className="flex-1 flex flex-col overflow-hidden">
        {/* Top bar */}
        <header className="sticky top-0 z-30 bg-white/80 backdrop-blur-md border-b border-gray-200/80">
          <div className="flex items-center justify-between px-4 md:px-8 py-3 gap-4">
            <div className="flex items-center gap-3 min-w-0">
              <button
                type="button"
                className="md:hidden inline-flex items-center justify-center w-10 h-10 rounded-lg border border-gray-200 bg-white text-slate-600 hover:bg-slate-50 transition-colors"
                onClick={() => setIsSidebarOpen(true)}
              >
                ☰
              </button>
              <h2 className="text-xl md:text-2xl font-bold text-slate-900 truncate">
                {currentLabel}
              </h2>
            </div>

            <div className="flex items-center gap-3 flex-shrink-0">
              <div className="flex items-center gap-3 px-4 py-2 bg-slate-50 rounded-lg border border-slate-200/60">
                <User className="w-5 h-5 text-slate-500" />
                <div>
                  <p className="text-sm font-semibold text-slate-800">{user?.name}</p>
                  <p className="text-xs text-slate-500">{user?.email}</p>
                </div>
              </div>

              <button
                onClick={handleLogout}
                className="flex items-center gap-2 px-4 py-2 bg-red-50 text-red-600 hover:bg-red-100 rounded-lg transition-colors duration-200 border border-red-200/60"
              >
                <LogOut className="w-4 h-4" />
                <span className="font-medium text-sm">Wyloguj</span>
              </button>
            </div>
          </div>
        </header>

        {/* Page content */}
        <main className="flex-1 overflow-y-auto p-6 md:p-8 bg-slate-50">
          <Outlet />
        </main>
      </div>
    </div>
  );
}

export default Layout;

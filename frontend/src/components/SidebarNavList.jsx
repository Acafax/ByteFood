import React from 'react';
import { useLocation } from 'react-router-dom';

function SidebarNavList({ items, onNavigate }) {
  const location = useLocation();
  const isActive = (path) => location.pathname === path;

  return (
    <ul className="space-y-1">
      {items.map((item) => (
        <li key={item.path}>
          <button
            type="button"
            onClick={() => onNavigate(item.path)}
            className={`flex items-center gap-3 px-4 py-2.5 rounded-lg w-full text-left transition-all duration-200 ${
              isActive(item.path)
                ? 'bg-orange-50 bf-text-primary font-semibold border-l-[3px] border-[#FF6600]'
                : 'text-slate-600 hover:bg-slate-50 hover:text-slate-900 border-l-[3px] border-transparent'
            }`}
          >
            {item.icon && <item.icon className="w-5 h-5" />}
            <span className="text-sm">{item.label}</span>
          </button>
        </li>
      ))}
    </ul>
  );
}

export default SidebarNavList;

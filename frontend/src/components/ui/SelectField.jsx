import React, { useState, useRef, useEffect, useCallback } from 'react';

const TRIGGER_BASE =
  'w-full px-4 py-2.5 border border-gray-300 rounded-lg bg-gray-50 text-slate-900 outline-none transition-all duration-200 text-left flex items-center justify-between cursor-pointer';
const TRIGGER_FOCUS =
  'bg-white border-orange-500 ring-4 ring-orange-500/10';

function SelectField({
  label,
  required,
  options = [],
  placeholder = 'Wybierz...',
  value,
  onChange,
  name,
  id,
  className = '',
}) {
  const [open, setOpen] = useState(false);
  const containerRef = useRef(null);

  const selectedOption = options.find((o) => String(o.value) === String(value));

  const handleSelect = useCallback(
    (optValue) => {
      if (onChange) {
        onChange({ target: { name, value: optValue } });
      }
      setOpen(false);
    },
    [onChange, name],
  );

  // Close on outside click
  useEffect(() => {
    if (!open) return;
    const handler = (e) => {
      if (containerRef.current && !containerRef.current.contains(e.target)) {
        setOpen(false);
      }
    };
    document.addEventListener('mousedown', handler);
    return () => document.removeEventListener('mousedown', handler);
  }, [open]);

  // Close on Escape
  const handleKeyDown = useCallback(
    (e) => {
      if (e.key === 'Escape') setOpen(false);
    },
    [],
  );

  return (
    <div ref={containerRef} className="relative">
      <label
        htmlFor={id}
        className="block text-sm font-medium text-slate-700 mb-1.5"
      >
        {label} {required && <span className="text-red-500">*</span>}
      </label>

      {/* Hidden native select for form validation / accessibility */}
      <select
        id={id}
        name={name}
        value={value}
        required={required}
        onChange={() => {}}
        tabIndex={-1}
        aria-hidden="true"
        className="sr-only"
      >
        <option value="">{placeholder}</option>
        {options.map((opt) => (
          <option key={opt.value} value={opt.value}>
            {opt.label}
          </option>
        ))}
      </select>

      {/* Custom trigger button */}
      <button
        type="button"
        onClick={() => setOpen((prev) => !prev)}
        onKeyDown={handleKeyDown}
        className={`${TRIGGER_BASE} ${open ? TRIGGER_FOCUS : 'focus:bg-white focus:border-orange-500 focus:ring-4 focus:ring-orange-500/10'} ${className}`}
        aria-haspopup="listbox"
        aria-expanded={open}
      >
        <span className={selectedOption ? 'text-slate-900' : 'text-slate-400'}>
          {selectedOption ? selectedOption.label : placeholder}
        </span>
        <svg
          className={`w-4 h-4 text-slate-400 transition-transform duration-200 ${open ? 'rotate-180' : ''}`}
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
        </svg>
      </button>

      {/* Dropdown list */}
      {open && (
        <ul
          role="listbox"
          className="absolute z-50 mt-1 w-full bg-white border border-gray-200 rounded-lg shadow-lg max-h-60 overflow-auto py-1"
        >
          {/* Placeholder option – only show when not required (so user can deselect) */}
          {!required && (
            <li
              role="option"
              aria-selected={!value}
              onClick={() => handleSelect('')}
              className="px-4 py-2 text-slate-400 cursor-pointer hover:bg-orange-50 transition-colors duration-150"
            >
              {placeholder}
            </li>
          )}
          {options.map((opt) => {
            const isSelected = String(opt.value) === String(value);
            return (
              <li
                key={opt.value}
                role="option"
                aria-selected={isSelected}
                onClick={() => handleSelect(opt.value)}
                className={`px-4 py-2 cursor-pointer transition-colors duration-150
                  ${isSelected
                    ? 'bg-orange-500 text-white font-medium'
                    : 'text-slate-900 hover:bg-orange-50 hover:text-orange-700'
                  }`}
              >
                {opt.label}
              </li>
            );
          })}
        </ul>
      )}
    </div>
  );
}

export default SelectField;

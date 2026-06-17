import React, { useRef, useState, useCallback } from 'react';
import { Eye, EyeOff } from 'lucide-react';
import FieldTooltip from './FieldTooltip';

const INPUT_BASE =
  'w-full py-2.5 border border-gray-300 rounded-lg bg-gray-50 text-slate-900 placeholder:text-slate-400 outline-none transition-all duration-200 focus:bg-white focus:border-orange-500 focus:ring-4 focus:ring-orange-500/10';

const LONG_PRESS_INITIAL_DELAY_MS = 400;
const LONG_PRESS_REPEAT_INTERVAL_MS = 80;

const ArrowUp = () => (
  <svg viewBox="0 0 10 6" xmlns="http://www.w3.org/2000/svg">
    <path d="M1 5l4-4 4 4" stroke="white" strokeWidth="1.5" fill="none" strokeLinecap="round" strokeLinejoin="round" />
  </svg>
);

const ArrowDown = () => (
  <svg viewBox="0 0 10 6" xmlns="http://www.w3.org/2000/svg">
    <path d="M1 1l4 4 4-4" stroke="white" strokeWidth="1.5" fill="none" strokeLinecap="round" strokeLinejoin="round" />
  </svg>
);

function triggerReactChange(input) {
  const nativeSetter = Object.getOwnPropertyDescriptor(
    window.HTMLInputElement.prototype,
    'value'
  ).set;
  nativeSetter.call(input, input.value);
  input.dispatchEvent(new Event('input', { bubbles: true }));
}

function NumberSpinner({ inputRef }) {
  const intervalRef = useRef(null);
  const timeoutRef = useRef(null);

  const doStep = useCallback((direction) => {
    if (!inputRef.current) return;
    if (direction === 'up') {
      inputRef.current.stepUp();
    } else {
      inputRef.current.stepDown();
    }
    triggerReactChange(inputRef.current);
  }, [inputRef]);

  const startLongPress = useCallback((direction) => {
    doStep(direction);
    timeoutRef.current = setTimeout(() => {
      intervalRef.current = setInterval(() => {
        doStep(direction);
      }, LONG_PRESS_REPEAT_INTERVAL_MS);
    }, LONG_PRESS_INITIAL_DELAY_MS);
  }, [doStep]);

  const stopLongPress = useCallback(() => {
    clearTimeout(timeoutRef.current);
    clearInterval(intervalRef.current);
    timeoutRef.current = null;
    intervalRef.current = null;
  }, []);

  return (
    <div className="number-spinner">
      <button
        type="button"
        onMouseDown={() => startLongPress('up')}
        onMouseUp={stopLongPress}
        onMouseLeave={stopLongPress}
        onTouchStart={() => startLongPress('up')}
        onTouchEnd={stopLongPress}
        tabIndex={-1}
        aria-label="Zwiększ"
        className="select-none"
      >
        <ArrowUp />
      </button>
      <button
        type="button"
        onMouseDown={() => startLongPress('down')}
        onMouseUp={stopLongPress}
        onMouseLeave={stopLongPress}
        onTouchStart={() => startLongPress('down')}
        onTouchEnd={stopLongPress}
        tabIndex={-1}
        aria-label="Zmniejsz"
        className="select-none"
      >
        <ArrowDown />
      </button>
    </div>
  );
}

function PasswordToggle({ visible, onToggle }) {
  return (
    <button
      type="button"
      onClick={onToggle}
      tabIndex={-1}
      aria-label={visible ? 'Ukryj hasło' : 'Pokaż hasło'}
      title={visible ? 'Ukryj hasło' : 'Pokaż hasło'}
      className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 transition-colors"
    >
      {visible ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
    </button>
  );
}

function FormField({ label, helpText ,required, icon: Icon, ...inputProps }) {
  const numberRef = useRef(null);
  const isNumber = inputProps.type === 'number';
  const isPassword = inputProps.type === 'password';
  const [showPassword, setShowPassword] = useState(false);

  const { type, className: inputClassName, ...restInputProps } = inputProps;
  const effectiveType = isPassword && showPassword ? 'text' : type;
  const togglePassword = useCallback(() => setShowPassword((prev) => !prev), []);

  return (
    <div>
      <label
        htmlFor={inputProps.id}
        className="flex items-center text-sm font-medium text-slate-700 mb-1.5"
      >
        {label}
        {required && <span className="text-red-500">*</span>}
        {helpText && <FieldTooltip text={helpText} />}
      </label>

      {Icon ? (
        <div className="relative">
          <Icon className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400" />
          <input
            {...restInputProps}
            type={effectiveType}
            ref={isNumber ? numberRef : undefined}
            required={required}
            className={`${INPUT_BASE} pl-10 ${isNumber || isPassword ? 'pr-10' : 'pr-4'} ${inputClassName || ''}`}
          />
          {isNumber && <NumberSpinner inputRef={numberRef} />}
          {isPassword && <PasswordToggle visible={showPassword} onToggle={togglePassword} />}
        </div>
      ) : isNumber ? (
        <div className="number-field-wrapper">
          <input
            {...restInputProps}
            type={effectiveType}
            ref={numberRef}
            required={required}
            className={`${INPUT_BASE} px-4 pr-10 ${inputClassName || ''}`}
          />
          <NumberSpinner inputRef={numberRef} />
        </div>
      ) : isPassword ? (
        <div className="relative">
          <input
            {...restInputProps}
            type={effectiveType}
            required={required}
            className={`${INPUT_BASE} pl-4 pr-10 ${inputClassName || ''}`}
          />
          <PasswordToggle visible={showPassword} onToggle={togglePassword} />
        </div>
      ) : (
        <input
          {...restInputProps}
          type={effectiveType}
          required={required}
          className={`${INPUT_BASE} px-4 ${inputClassName || ''}`}
        />
      )}
    </div>
  );
}

export default FormField;

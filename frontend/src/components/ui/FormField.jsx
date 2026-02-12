import React, { useRef, useCallback } from 'react';

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

function FormField({ label, required, icon: Icon, ...inputProps }) {
  const numberRef = useRef(null);
  const isNumber = inputProps.type === 'number';

  return (
    <div>
      <label
        htmlFor={inputProps.id}
        className="block text-sm font-medium text-slate-700 mb-1.5"
      >
        {label} {required && <span className="text-red-500">*</span>}
      </label>

      {Icon ? (
        <div className="relative">
          <Icon className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400" />
          <input
            {...inputProps}
            ref={isNumber ? numberRef : undefined}
            required={required}
            className={`${INPUT_BASE} pl-10 ${isNumber ? 'pr-10' : 'pr-4'} ${inputProps.className || ''}`}
          />
          {isNumber && <NumberSpinner inputRef={numberRef} />}
        </div>
      ) : isNumber ? (
        <div className="number-field-wrapper">
          <input
            {...inputProps}
            ref={numberRef}
            required={required}
            className={`${INPUT_BASE} px-4 pr-10 ${inputProps.className || ''}`}
          />
          <NumberSpinner inputRef={numberRef} />
        </div>
      ) : (
        <input
          {...inputProps}
          required={required}
          className={`${INPUT_BASE} px-4 ${inputProps.className || ''}`}
        />
      )}
    </div>
  );
}

export default FormField;

import React, { useState, useRef, useEffect } from 'react';
import { X } from 'lucide-react';

function FieldTooltip({ text }) {
  const [open, setOpen] = useState(false);
  const ref = useRef(null);

  useEffect(() => {
    if (!open) return;
    const handler = (e) => {
      if (ref.current && !ref.current.contains(e.target)) {
        setOpen(false);
      }
    };
    document.addEventListener('mousedown', handler);
    return () => document.removeEventListener('mousedown', handler);
  }, [open]);

  return (
    <span ref={ref} className="relative inline-flex items-center ml-1.5">
      <button
        type="button"
        onClick={() => setOpen(v => !v)}
        className="flex items-center justify-center w-4 h-4 rounded-full bg-slate-300 hover:bg-orange-400 text-white text-[10px] font-bold transition-colors cursor-pointer select-none"
        aria-label="Pokaż pomoc"
      >
        ?
      </button>

      {open && (
        <div className="absolute left-6 top-1/2 -translate-y-1/2 z-50 w-64 rounded-lg border border-gray-200 bg-white shadow-xl px-3 py-2.5">
          <button
            type="button"
            onClick={() => setOpen(false)}
            className="float-right ml-2 -mt-0.5 text-slate-400 hover:text-slate-600 transition-colors"
            aria-label="Zamknij"
          >
            <X className="w-3 h-3" />
          </button>
          <p className="text-xs text-slate-600 leading-relaxed">{text}</p>
        </div>
      )}
    </span>
  );
}

export default FieldTooltip;

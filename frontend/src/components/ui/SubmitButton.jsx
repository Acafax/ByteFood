import React from 'react';

function SubmitButton({ loading, loadingText = 'Tworzenie...', icon: Icon, children, className = '', ...rest }) {
  return (
    <button
      type="submit"
      disabled={loading}
      className={`px-8 py-2.5 bg-[#FF6600] hover:bg-[#e55b00] text-white rounded-lg font-medium flex items-center gap-2 shadow-md shadow-orange-500/20 hover:-translate-y-0.5 transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:translate-y-0 ${className}`}
      {...rest}
    >
      {loading ? (
        <>
          <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white" />
          {loadingText}
        </>
      ) : (
        <>
          {Icon && <Icon className="w-5 h-5" />}
          {children}
        </>
      )}
    </button>
  );
}

export default SubmitButton;

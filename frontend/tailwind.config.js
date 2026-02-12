/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        brand: {
          DEFAULT: '#FF6600',
          50: '#FFF7ED',
          100: '#FFEDD5',
          200: '#FED7AA',
          300: '#FDBA74',
          400: '#FB923C',
          500: '#FF6600',
          600: '#e55b00',
          700: '#C2410C',
          800: '#9A3412',
          900: '#7C2D12',
        },
      },
      boxShadow: {
        card: '0 2px 8px rgba(0, 0, 0, 0.04)',
      },
    },
  },
  plugins: [],
}

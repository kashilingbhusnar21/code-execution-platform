/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        vscode: {
          bg: '#1e1e1e',
          sidebar: '#252526',
          panel: '#1e1e1e',
          border: '#3c3c3c',
          text: '#cccccc',
          textDim: '#858585',
          accent: '#007acc',
          accentHover: '#0062a3',
          success: '#4ec9b0',
          error: '#f48771',
          warning: '#cca700',
        },
      },
      fontFamily: {
        mono: ['"Fira Code"', '"Consolas"', '"Courier New"', 'monospace'],
        sans: ['"Inter"', '-apple-system', 'BlinkMacSystemFont', '"Segoe UI"', 'Roboto', 'sans-serif'],
      },
    },
  },
  plugins: [],
}

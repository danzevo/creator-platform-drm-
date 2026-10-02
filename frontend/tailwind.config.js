/** @type {import('tailwindcss').Config} */
export default {
    content: [
        "./index.html",
        "./src/**/*.{vue,js,ts,jsx,tsx}",
    ],
    theme: {
        extend: {
            colors: {
                brand: {
                    50: '#f0f9ff',
                    500: '#0ea5e9',
                    600: '#0284c7',
                    900: '#0c4a6e',
                    accent: '#38bdf8',
                    dark: '#0b0f19',
                    card: '#111827',
                    border: '#1f2937'
                }
            },
            fontFamily: {
                sans: ['Inter', 'sans-serif'],
                display: ['Plus Jakarta Sans', 'sans-serif']
            }
        },
    },
    plugins: [],
}

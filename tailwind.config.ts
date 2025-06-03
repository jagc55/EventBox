import type { Config } from "tailwindcss";

const config: Config = {
  content: ["./index.html", "./src/**/*.{vue,ts,js}"],
  theme: {
    extend: {
      fontFamily: {
        lato: ['"Lato"', "sans-serif"],
      },
      colors: {
        "dark-primary": "#1E1E22",
        "black-secundary": "#383434",
        "blue-secundary": "#4A628A",
        "blue-primary": "#D8E8F2",
        "white-bg": "#FFFFFF",
        "red-primary": "#FB6B6B",
        "purple-primary": "#602c8d",
        "purple-secundary": "#3a005f",
      },
    },
  },
  plugins: [],
};

export default config;

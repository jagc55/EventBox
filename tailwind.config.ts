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
        "blue-secundary": "#4A628A",
        "blue-header": "#7AB2D3",
        "blue-primary": "#D8E8F2",
        "white-bg": "#FFFFFF",
      },
    },
  },
  plugins: [],
};

export default config;

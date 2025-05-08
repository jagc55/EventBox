import type { Config } from "tailwindcss";

const config: Config = {
  content: ["./index.html", "./src/**/*.{vue,ts,js}"],
  theme: {
    extend: {
      fontFamily: {
        lato: ['"Lato"', "sans-serif"],
      },
      colors: {
        darknav: "#1E1E22",
        mainBlue: "#D8E8F2",
      },
    },
  },
  plugins: [],
};

export default config;

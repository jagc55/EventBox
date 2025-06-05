import type { ConfigEnv } from "vitest/config";
import { fileURLToPath } from "node:url";
import { configDefaults, defineConfig, mergeConfig } from "vitest/config";
import viteConfig from "./vite.config";

const vitestExcludedFiles = [
  ...configDefaults.exclude,
  "**/__stubs__/**",
  "**/__tests__/**",
  "**/assets/**",
];

export default mergeConfig(
  viteConfig,
  defineConfig({
    test: {
      setupFiles: ["./vitest-setup.ts"],
      environment: "jsdom",
      exclude: vitestExcludedFiles,
      coverage: {
        reporter: ["text", "lcov"],
        exclude: vitestExcludedFiles,
      },
      root: fileURLToPath(new URL("./", import.meta.url)),
    },
  })
);

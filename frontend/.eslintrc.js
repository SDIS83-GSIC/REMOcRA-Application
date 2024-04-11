/* eslint-env node */
/* eslint import/no-commonjs: off */
module.exports = {
  root: true,
  env: {
    browser: true,
    es2021: true,
  },
  extends: [
    "eslint:recommended",
    "plugin:react/recommended",
    "plugin:react-hooks/recommended",
    "plugin:import/errors",
    "plugin:@typescript-eslint/recommended",
  ],
  parserOptions: {
    ecmaVersion: 12,
    sourceType: "module",
  },

  plugins: ["compat", "react", "react-hooks", "@typescript-eslint"],
  settings: {
    react: {
      version: require("react/package.json").version,
    },
  },
  parser: "@typescript-eslint/parser",
  rules: {
    eqeqeq: ["error", "smart"],
    curly: "error",
    "no-console": "error",

    "compat/compat": "error",

    "react-hooks/rules-of-hooks": "error",
    "react/self-closing-comp": "error",
    // On utilise le New JSX Transform
    "react/react-in-jsx-scope": "off",
    "react/jsx-uses-react": "off",

    "no-duplicate-imports": "error",
    "import/no-extraneous-dependencies": [
      "error",
      { devDependencies: ["stories/**"] },
    ],

    "@typescript-eslint/no-explicit-any": "off",
    "import/no-commonjs": ["error"],
    "import/no-amd": "error",
    "import/no-nodejs-modules": "error",
    "import/first": "error",
    "import/no-namespace": "error",
    "import/extensions": ["error", "always", { ignorePackages: true }],
    "import/order": ["error", { "newlines-between": "never" }],
    "import/prefer-default-export": "error",
    "react/jsx-uses-react": "off",
    "react/react-in-jsx-scope": "off",
    "@typescript-eslint/no-namespace": "off",
  },
};

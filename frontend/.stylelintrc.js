/* eslint-env node */
/* eslint import/no-commonjs: off */
module.exports = {
  plugins: ["stylelint-no-unsupported-browser-features", "stylelint-prettier"],
  extends: ["stylelint-config-recommended", "stylelint-config-css-modules"],
  rules: {
    "plugin/no-unsupported-browser-features": true,
    "prettier/prettier": true,
  },
};

import { parse } from "cookie";

// Keep in sync with csrf.kt
const csrfTokenHeaderName = "X-XTok";
const csrfCookieName = location.protocol === "https:" ? "__Host-xt" : "xt";

function getCsrfTokenFromCookie(cookie) {
  return parse(cookie)[csrfCookieName];
}

let oldCookie = document.cookie;
let csrfToken = getCsrfTokenFromCookie(oldCookie);
function getCsrfToken() {
  const cookie = document.cookie;
  if (cookie !== oldCookie) {
    csrfToken = getCsrfTokenFromCookie(cookie);
    oldCookie = cookie;
  }
  return csrfToken;
}

function filterParams(obj: object) {
  return Object.entries(obj)
    .filter((e) => e[1] != null)
    .map((v) => (typeof v[1] == "object" ? [v[0], JSON.stringify(v[1])] : v));
}

export default function url(
  strings: TemplateStringsArray,
  ...values: object[]
) {
  return strings.slice(1).reduce((res, string, i) => {
    const value = values[i];
    if (typeof value == "object") {
      res += decodeURIComponent(
        (value instanceof URLSearchParams
          ? value
          : new URLSearchParams(filterParams(value))
        ).toString(),
      );
    } else {
      res += encodeURI(value);
    }
    res = res.concat(string);
    return res;
  }, strings[0]);
}

export function getFetchOptions(options = {}): RequestInit {
  return {
    ...options,
    credentials: "same-origin",
    headers: {
      Accept: "*/*",
      ...options.headers,
      [csrfTokenHeaderName]: getCsrfToken(),
    },
  };
}

import { useAsync } from "react-async";
import { useCallback } from "react";
import { getFetchOptions } from "../../module/fetch.tsx";

export const doFetch = async (url, options = {}) => {
  if (!url) {
    return null;
  }
  const res = await fetch(
    url === decodeURIComponent(url) ? encodeURI(url) : url,
    options,
  );
  if (res.status === 401) {
    // FIXME : utiliser React Router ?
    document.location.href = "/";
  }
  if (!res.ok) {
    throw res;
  }
  if (res.status === 204) {
    return null;
  }
  return res.text().then((text) => {
    return text ? JSON.parse(text) : null;
  });
};

export function useGet(url, asyncOptions) {
  return useAsync({
    ...asyncOptions,
    promiseFn: useCallback(
      (props, { signal }) => {
        const options = getFetchOptions({
          signal,
          method: "GET",
        });
        return doFetch(url, options);
      },
      [url],
    ),
  });
}

/**
 * Permet de faire un appel à une URL en GET, au lancement via .run()
 */
export function useGetRun(url, asyncOptions) {
  return useAsync({
    ...asyncOptions,
    deferFn: useCallback(
      (props, { signal }) => {
        const options = getFetchOptions({
          signal,
          method: "GET",
        });
        return doFetch(url, options);
      },
      [url],
    ),
  });
}

export function usePost(url, asyncOptions) {
  return useAsync({
    ...asyncOptions,
    deferFn: useCallback(
      (args, props, { signal }) => {
        const options = getFetchOptions({
          signal,
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(args[0]),
        });
        return doFetch(url, options);
      },
      [url],
    ),
  });
}

export function useDelete(url, asyncOptions) {
  return useAsync({
    ...asyncOptions,
    deferFn: useCallback(
      (args, props, { signal }) => {
        const options = getFetchOptions({
          signal,
          method: "DELETE",
        });
        return doFetch(url, options);
      },
      [url],
    ),
  });
}

export function usePut(url, asyncOptions, isMultipartFormData) {
  return useAsync({
    ...asyncOptions,
    deferFn: useCallback(
      isMultipartFormData
        ? (args, props, { signal }) => {
            const options = getFetchOptions({
              signal,
              method: "PUT",
              body: args[0],
            });
            return doFetch(url, options);
          }
        : (args, props, { signal }) => {
            const options = getFetchOptions({
              signal,
              method: "PUT",
              headers: { "Content-Type": "application/json" },
              body: JSON.stringify(args[0]),
            });
            return doFetch(url, options);
          },
      [url],
    ),
  });
}

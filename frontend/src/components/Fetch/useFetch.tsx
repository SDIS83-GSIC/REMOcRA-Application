import {
  type AsyncOptions,
  type DeferFn,
  type PromiseFn,
  useAsync,
} from "react-async";
import { useCallback } from "react";
import { getFetchOptions } from "../../module/fetch.tsx";
import { URLS } from "../../routes.tsx";

export const doFetch = async (
  url: string,
  options = {},
): Promise<undefined | any> => {
  if (!url) {
    return null;
  }
  const res = await fetch(
    url === decodeURIComponent(url) ? encodeURI(url) : url,
    options,
  );

  if (res.status === 401) {
    window.location.reload();
  }

  if (res.status === 403) {
    document.location.href = URLS.ACCUEIL;
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

export function useGet(url: string, asyncOptions: AsyncOptions<any> = {}) {
  return useAsync({
    ...asyncOptions,
    promiseFn: useCallback<PromiseFn<any>>(
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
export function useGetRun(url: string, asyncOptions: AsyncOptions<any> = {}) {
  return useAsync({
    ...asyncOptions,
    deferFn: useCallback<DeferFn<any>>(
      (args, props, { signal }) => {
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

export function usePost(
  url: string,
  asyncOptions: AsyncOptions<any> = {},
  isMultipartFormData = false,
) {
  return useAsync({
    ...asyncOptions,
    deferFn: useCallback<DeferFn<any>>(
      ([body], props, { signal }) => {
        const options = isMultipartFormData
          ? getFetchOptions({
              signal,
              method: "POST",
              body,
            })
          : getFetchOptions({
              signal,
              method: "POST",
              headers: { "Content-Type": "application/json" },
              body: JSON.stringify(body),
            });
        return doFetch(url, options);
      },
      [isMultipartFormData, url],
    ),
  });
}

export function useDelete(url: string, asyncOptions: AsyncOptions<any> = {}) {
  return useAsync({
    ...asyncOptions,
    deferFn: useCallback<DeferFn<any>>(
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

export function usePut(
  url: string,
  asyncOptions: AsyncOptions<any> = {},
  isMultipartFormData: boolean,
) {
  return useAsync({
    ...asyncOptions,
    deferFn: useCallback<DeferFn<any>>(
      ([body], props, { signal }) => {
        const options = isMultipartFormData
          ? getFetchOptions({
              signal,
              method: "PUT",
              body,
            })
          : getFetchOptions({
              signal,
              method: "PUT",
              headers: { "Content-Type": "application/json" },
              body: JSON.stringify(body),
            });
        return doFetch(url, options);
      },
      [isMultipartFormData, url],
    ),
  });
}

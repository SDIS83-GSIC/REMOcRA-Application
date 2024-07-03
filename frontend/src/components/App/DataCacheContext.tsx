import { ReactNode, createContext, useContext, useReducer } from "react";
import { IdLibelle } from "../../utils/typeUtils.tsx";

/**
 * Cette classe permet de mettre en cache les nomenclatures que l'on veut utiliser sur plusieurs pages.
 * Toutes les clés de cet objet seront définies au fur à mesure des appels au back.
 * Toutes les propriétés de l'object seront sous la forme de "list_"+ TYPE_DATA_CACHE
 */

export interface DataCacheType {
  [key: string]: IdLibelle[];
}

export type DataCacheToUpdate = {
  list: IdLibelle[];
  nameProperty?: string | null;
};

const dataCacheInitial: DataCacheType = {};

const reducer = (dataCache: DataCacheType, newDataCache: DataCacheToUpdate) => {
  if (newDataCache === null || newDataCache.nameProperty == null) {
    localStorage.removeItem("dataCache");
    return dataCacheInitial;
  }

  dataCache[newDataCache.nameProperty] = newDataCache.list;
  localStorage.setItem("dataCache", JSON.stringify(dataCache));
  return dataCache;
};

const DataCacheContext = createContext({});

export const useDataCacheContext = () => {
  return useContext(DataCacheContext);
};

const localState =
  localStorage.getItem("dataCache") != null
    ? JSON.parse(localStorage.getItem("dataCache"))
    : dataCacheInitial;

export const DataCacheProvider = ({ children }: { children: ReactNode }) => {
  const [dataCache, setDataCache] = useReducer(
    reducer,
    localState || dataCacheInitial,
  );

  return (
    <DataCacheContext.Provider
      value={{ dataCache: dataCache, setDataCache: setDataCache }}
    >
      {children}
    </DataCacheContext.Provider>
  );
};

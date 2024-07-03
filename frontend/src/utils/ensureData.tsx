import { useGet } from "../components/Fetch/useFetch.tsx";
import TYPE_DATA_CACHE from "../enums/NomenclaturesEnum.tsx";
import url from "../module/fetch.tsx";
import {
  DataCacheToUpdate,
  DataCacheType,
  useDataCacheContext,
} from "../components/App/DataCacheContext.tsx";

/**
 * Regarde si on a dans le cache les informations d'un type précis
 * @param typeDataCache : type de nomenclature
 * @return la liste du type de nomenclature demandé
 */

const ensureDataCache = (typeDataCache: TYPE_DATA_CACHE) => {
  return EnsureData(typeDataCache);
};

export default ensureDataCache;

const EnsureData = (typeDataCache: TYPE_DATA_CACHE) => {
  const {
    dataCache,
    setDataCache,
  }: {
    dataCache: DataCacheType;
    setDataCache: (e: DataCacheToUpdate) => void;
  } = useDataCacheContext();

  const libelleList = "list_" + typeDataCache;

  if (dataCache !== null && dataCache[libelleList] != null) {
    return dataCache[libelleList];
  } else {
    // eslint-disable-next-line react-hooks/rules-of-hooks
    const response = useGet(url`/api/nomenclatures/list/` + typeDataCache);
    response.isResolved &&
      setDataCache({ list: response.data, nameProperty: libelleList });
    return response.data;
  }
};

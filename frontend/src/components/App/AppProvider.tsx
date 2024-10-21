/*global userInfo */

import { createContext, ReactNode, useContext } from "react";
import { get as getProjection } from "ol/proj";
import proj4 from "proj4";
import { register } from "ol/proj/proj4";
import { useGet } from "../Fetch/useFetch.tsx";
import url from "../../module/fetch.tsx";
import Loading from "../Elements/Loading/Loading.tsx";

const AppContext = createContext({});

export const useAppContext = () => {
  return useContext(AppContext);
};

export const AppProvider = ({ children }: { children: ReactNode }) => {
  const {
    isLoading,
    data: epsg,
  }: { isLoading: boolean; data: { name: string; projection: string } } =
    useGet(url`/api/app-settings/epsg`);
  if (isLoading && !epsg) {
    return <Loading />;
  }
  if (!getProjection(epsg.name)) {
    proj4.defs(epsg.name, epsg.projection);
    register(proj4);
  }

  return (
    <AppContext.Provider
      value={{
        user: userInfo,
        epsg: epsg,
        srid: epsg.name.split(":").pop(),
      }}
    >
      {children}
    </AppContext.Provider>
  );
};

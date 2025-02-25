/*global userInfo */

import { createContext, ReactNode, useContext } from "react";
import { get as getProjection } from "ol/proj";
import proj4 from "proj4";
import { register } from "ol/proj/proj4";
import { useGet } from "../Fetch/useFetch.tsx";
import url from "../../module/fetch.tsx";
import Loading from "../Elements/Loading/Loading.tsx";

type AppContextProps = {
  user: any;
  epsg: { name: string; projection: string };
  srid: number;
  extent: number[];
};

const AppContext = createContext({});

export const useAppContext = (): AppContextProps => {
  return useContext(AppContext) as AppContextProps;
};

export const AppProvider = ({ children }: { children: ReactNode }) => {
  const {
    isLoading,
    data: projectionProps,
  }: {
    isLoading: boolean;
    data: { name: string; projection: string; extent: number[] };
  } = useGet(url`/api/app-settings/epsg`);
  if (isLoading && !projectionProps) {
    return <Loading />;
  }
  if (!getProjection(projectionProps.name)) {
    proj4.defs(projectionProps.name, projectionProps.projection);
    register(proj4);
  }

  return (
    <AppContext.Provider
      value={{
        user: userInfo,
        epsg: {
          name: projectionProps.name,
          projection: projectionProps.projection,
        },
        srid: projectionProps.name.split(":").pop(),
        extent: projectionProps.extent,
      }}
    >
      {children}
    </AppContext.Provider>
  );
};

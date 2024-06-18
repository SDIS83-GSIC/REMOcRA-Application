import { useRoutes } from "react-router-dom";
import { ReactNode } from "react";

const RouteConfig = ({ routes }: RouteConfigEntity) => {
  return useRoutes(routes);
};

type RouteConfigEntity = {
  routes: {
    path: string;
    element: ReactNode;
  }[];
};

export default RouteConfig;

import { ReactNode } from "react";
import { useRoutes } from "react-router-dom";

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

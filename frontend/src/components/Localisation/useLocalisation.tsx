import { WKT } from "ol/format";
import { GeometryCollection } from "ol/geom";
import { useCallback } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import url, { getFetchOptions } from "../../module/fetch.tsx";
import { URLS } from "../../routes.tsx";

export enum GET_TYPE_GEOMETRY {
  COMMUNE = "/api/commune",
  TOURNEE = "/api/tournee",
  PEI = "/api/pei",
  VOIE = "/api/voie",
  INDISPONIBILITE_TEMP = "/api/indisponibilite-temporaire",
}

/**
 * Permet de localiser les PEI, les tournées ou les communes et voie
 */
const useLocalisation = () => {
  const navigate = useNavigate();
  const {
    pathname: currentPathname,
    search,
    state: currentState,
  } = useLocation();

  const fetchGeometry = useCallback(
    async (typeGeometry: string, idType: string) => {
      (
        await fetch(
          url`${typeGeometry}/${idType}/geometrie`,
          getFetchOptions({
            method: "GET",
          }),
        )
      )
        .json()
        .then((resData) => {
          let extent, srid;
          if (GET_TYPE_GEOMETRY.PEI === typeGeometry) {
            // PEI
            const [rawSrid, rawFeature] = resData.split(";");
            srid = rawSrid.split("=").pop();
            extent = new WKT().readGeometry(rawFeature).getExtent();
          } else if (
            GET_TYPE_GEOMETRY.TOURNEE === typeGeometry ||
            GET_TYPE_GEOMETRY.INDISPONIBILITE_TEMP === typeGeometry
          ) {
            // Tournée
            extent = new GeometryCollection(
              resData.map((pei: string) => {
                const [rawSrid, rawFeature] = pei.split(";");
                srid = rawSrid.split("=").pop();
                return new WKT().readGeometry(rawFeature);
              }),
            ).getExtent();
          } else if (GET_TYPE_GEOMETRY.COMMUNE === typeGeometry) {
            // Commune
            const [rawSrid, rawFeature] = resData.communeGeometry.split(";");
            srid = rawSrid.split("=").pop();
            extent = new WKT().readGeometry(rawFeature).getExtent();
          } else {
            // Voie
            const [rawSrid, rawFeature] = resData.voieGeometry.split(";");
            srid = rawSrid.split("=").pop();
            extent = new WKT().readGeometry(rawFeature).getExtent();
          }

          navigate(URLS.DECI_CARTE, {
            state: {
              ...currentState,
              from: [
                ...(currentState?.from ?? []),
                `${currentPathname}${search}`,
              ],
              target: {
                extent,
                srid,
              },
            },
          });
        });
    },
    [navigate, currentState, currentPathname, search],
  );

  return { fetchGeometry };
};

export default useLocalisation;

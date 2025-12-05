import { WKT } from "ol/format";
import { GeometryCollection } from "ol/geom";
import { useCallback } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import url, { getFetchOptions } from "../../module/fetch.tsx";
import { URLS } from "../../routes.tsx";

export enum GET_TYPE_GEOMETRY {
  COMMUNE = "/api/commune",
  COMMUNE_ETUDE = "/api/couverture-hydraulique/etude",
  COMMUNE_CRISE = "/api/crise",
  TOURNEE = "/api/tournee",
  PEI = "/api/pei",
  VOIE = "/api/voie",
  INDISPONIBILITE_TEMP = "/api/indisponibilite-temporaire",
  OLDEB = "/api/oldeb",
}

const BUFFER_LOCALISATION = 100;

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
    async (
      typeGeometry: string,
      idType: string,
      urlDestination = URLS.DECI_CARTE,
    ) => {
      let listePeiId: [] | undefined;
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
          const parseGeometry = (raw: string) => {
            const [rawSrid, rawFeature] = raw.split(";");
            const srid = rawSrid.split("=").pop();
            const geometry = new WKT().readGeometry(rawFeature);
            return { geometry, srid };
          };

          // Fonction utilitaire pour buffer un extent
          const bufferExtent = (extent: number[], buffer: number) => {
            // extent: [minX, minY, maxX, maxY]
            return [
              extent[0] - buffer,
              extent[1] - buffer,
              extent[2] + buffer,
              extent[3] + buffer,
            ];
          };

          let extent, srid;

          switch (typeGeometry) {
            case GET_TYPE_GEOMETRY.PEI: {
              const { geometry, srid: parsedSrid } = parseGeometry(resData);
              extent = geometry.getExtent();
              srid = parsedSrid;
              listePeiId = [idType];
              break;
            }
            case GET_TYPE_GEOMETRY.TOURNEE:
            case GET_TYPE_GEOMETRY.INDISPONIBILITE_TEMP: {
              const geometries = resData.map(
                (pei: { peiGeometrie: string }) =>
                  parseGeometry(pei.peiGeometrie).geometry,
              );
              srid = parseGeometry(resData[0].peiGeometrie).srid;
              extent = bufferExtent(
                new GeometryCollection(geometries).getExtent(),
                BUFFER_LOCALISATION,
              );
              listePeiId = resData.map((e: { peiId: string }) => e.peiId);
              break;
            }
            case GET_TYPE_GEOMETRY.COMMUNE: {
              const { geometry, srid: parsedSrid } = parseGeometry(
                resData.communeGeometry,
              );
              extent = bufferExtent(geometry.getExtent(), BUFFER_LOCALISATION);
              srid = parsedSrid;
              break;
            }
            case GET_TYPE_GEOMETRY.COMMUNE_ETUDE:
            case GET_TYPE_GEOMETRY.COMMUNE_CRISE: {
              const geometries = resData.map(
                (commune: string) => parseGeometry(commune).geometry,
              );
              srid = parseGeometry(resData[0]).srid;
              extent = bufferExtent(
                new GeometryCollection(geometries).getExtent(),
                BUFFER_LOCALISATION,
              );
              break;
            }
            case GET_TYPE_GEOMETRY.VOIE: {
              const { geometry, srid: parsedSrid } = parseGeometry(
                resData.voieGeometry,
              );
              extent = bufferExtent(geometry.getExtent(), BUFFER_LOCALISATION);
              srid = parsedSrid;
              break;
            }
            case GET_TYPE_GEOMETRY.OLDEB: {
              const { geometry, srid: parsedSrid } = parseGeometry(
                resData.geometrie,
              );
              extent = bufferExtent(geometry.getExtent(), BUFFER_LOCALISATION);
              srid = parsedSrid;
              break;
            }
          }

          // On sauvegarde les nouveaux search de la page courrante dans le localStorage
          localStorage.setItem(currentPathname, search);

          navigate(urlDestination, {
            state: {
              ...currentState,
              from: [
                ...(currentState?.from ?? []),
                `${currentPathname}${search}`,
              ],
              listePeiId: listePeiId,
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

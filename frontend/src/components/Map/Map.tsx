import { getUid } from "ol";
import Map from "ol/Map";
import View from "ol/View";
import { MousePosition, ScaleLine } from "ol/control";
import { defaults as defaultControls, FullScreen } from "ol/control.js";
import { createStringXY, degreesToStringHDMS } from "ol/coordinate";
import { getCenter, getTopLeft, getWidth, isEmpty } from "ol/extent";
import { GeoJSON, WKT } from "ol/format";
import { MouseWheelZoom } from "ol/interaction";
import TileLayer from "ol/layer/Tile";
import VectorLayer from "ol/layer/Vector";
import { bbox } from "ol/loadingstrategy";
import "ol/ol.css";
import { get as getProjection, transform, transformExtent } from "ol/proj";
import { OSM, TileWMS, WMTS } from "ol/source";
import TileSource from "ol/source/Tile";
import VectorSource from "ol/source/Vector";
import { Fill, Stroke, Style } from "ol/style";
import CircleStyle from "ol/style/Circle";
import WMTSTileGrid from "ol/tilegrid/WMTS";
import { MutableRefObject, ReactNode, useEffect, useMemo, useRef } from "react";
import { Col, Row } from "react-bootstrap";
import { useLocation, useNavigate } from "react-router-dom";
import PARAMETRE from "../../enums/ParametreEnum.tsx";
import SOURCE_CARTO from "../../enums/SourceCartoEnum.tsx";
import { TYPE_AFFICHAGE_COORDONNEES } from "../../enums/TypeAffichageCoordonnees.tsx";
import url from "../../module/fetch.tsx";
import { EPSG_3857, EPSG_4326 } from "../../utils/constantsUtils.tsx";
import { useAppContext } from "../App/AppProvider.tsx";
import { useGet } from "../Fetch/useFetch.tsx";
import { TypeModuleRemocra } from "../ModuleRemocra/ModuleRemocra.tsx";
import MapLegend from "./MapLegend.tsx";
import MapToolbar from "./MapToolbar.tsx";
import { createPointLayer } from "./MapUtils.tsx";
import "./map.css";

const resolutions = [];
const matrixIds = [];
const proj3857 = getProjection(EPSG_3857)!;
const maxResolution = getWidth(proj3857.getExtent()) / 256;

// Matrices TileGrid 0..19 pour afficher les tuiles suivant le zoom, les services (IGN, etc.) ne vont pas au-delà de 20 niveaux
for (let i = 0; i < 20; i++) {
  matrixIds[i] = i.toString();
  resolutions[i] = maxResolution / Math.pow(2, i);
}

// Échelle sur la carte suivant le niveau zoom
const scaleControl = new ScaleLine({
  units: "metric",
  maxWidth: 200,
  minWidth: 100,
});

// Grille de tuiles
const tileGrid = new WMTSTileGrid({
  origin: getTopLeft(proj3857.getExtent()), // [-20037508, 20037508]
  resolutions: resolutions,
  matrixIds: matrixIds,
});

// Déclaration OL depuis une définition de couche
export function toOpenLayer(
  layer: any,
): TileSource | WMTS | VectorSource | undefined {
  switch (layer.source) {
    case SOURCE_CARTO.WMS:
      return new TileWMS({
        url: layer.url,
        params: {
          LAYERS: layer.layer,
          TILED: true,
          projection: layer.projection,
          matrixSet: "PM",
          format: layer.format ?? "image/png",
          tileGrid: tileGrid,
          style: "normal",
        },
      });
    case SOURCE_CARTO.WMTS:
      return new WMTS({
        crossOrigin: layer.crossOrigin ?? "anonymous",
        url: layer.url,
        layer: layer.layer,
        projection: layer.projection,
        matrixSet: "PM",
        format: layer.format ?? "image/png",
        tileGrid: tileGrid,
        style: "normal",
      });
    case SOURCE_CARTO.GEOJSON:
      return new VectorSource({
        url: layer.url,
        loader: layer.loader,
        strategy: layer.strategy,
        format: new GeoJSON({
          dataProjection: layer.projection,
          featureProjection: layer.projection,
        }),
      });
    case SOURCE_CARTO.OSM:
      return new OSM({
        url: layer.url,
        crossOrigin: layer.crossOrigin ?? null,
      });

    case SOURCE_CARTO.WFS:
      return new VectorSource({
        url:
          layer.url +
          "&request=GetFeature&typename=" +
          layer.layer +
          "&outputFormat=" +
          (layer.format ?? "application/json") +
          "&srsname=" +
          layer.projection,
        format: new GeoJSON({}),
        strategy: bbox,
      });
    default:
      return undefined;
  }
}

const MapComponent = ({
  map,
  workingLayer,
  availableLayers,
  addOrRemoveLayer,
  layerListRef,
  mapToolbarRef,
  mapElement,
  toolbarElement,
  toggleTool,
  activeTool,
  variant = "primary",
}: {
  map?: Map;
  workingLayer: any;
  availableLayers: any[];
  addOrRemoveLayer: (layer: any) => void;
  layerListRef: any;
  mapToolbarRef: any;
  mapElement: any;
  toolbarElement?: ReactNode;
  toggleTool: any;
  activeTool: any;
  variant: string;
}) => {
  useEffect(() => {
    const mapContainer = document.getElementById("map-container");

    const handleFullscreenChange = () => {
      if (document.fullscreenElement === mapContainer) {
        mapContainer?.classList.add("is-fullscreen");
      } else {
        mapContainer?.classList.remove("is-fullscreen");
      }
    };

    document.addEventListener("fullscreenchange", handleFullscreenChange);
    return () => {
      document.removeEventListener("fullscreenchange", handleFullscreenChange);
    };
  }, []);

  useEffect(() => {
    if (!map) {
      return;
    }
    toggleTool("move-view");
  }, [map]);

  return (
    <div className={"map-wrapper"} id={"map-container"}>
      {map && mapElement && (
        <Row className={"map-toolbar noprint"}>
          <Col xs={"auto"}>
            {/* Commun à toutes les cartes */}
            <MapToolbar
              ref={mapToolbarRef}
              map={map}
              workingLayer={workingLayer}
              toggleTool={toggleTool}
              activeTool={activeTool}
              variant={variant}
            />
          </Col>
          <Col xs={"auto"}>{toolbarElement && toolbarElement}</Col>
        </Row>
      )}
      <div ref={mapElement} className={"map-map border border-" + variant} />
      <div className={"map-layers noprint"}>
        <MapLegend
          ref={layerListRef}
          layers={availableLayers}
          addOrRemoveLayer={addOrRemoveLayer}
        />
      </div>
    </div>
  );
};

export const useMapComponent = ({
  mapElement,
  typeModule,
  displayPei = true,
}: {
  mapElement: MutableRefObject<HTMLDivElement | undefined>;
  typeModule: TypeModuleRemocra;
  displayPei?: boolean;
}) => {
  const { state, search } = useLocation();
  const navigate = useNavigate();

  const { epsg: projection, extent: defaultExtent, user } = useAppContext();
  const layersState = useGet(url`/api/layers/${typeModule}`, {});
  const afficheCoordonneesState = useGet(
    url`/api/parametres?${{
      listeParametreCode: JSON.stringify(
        PARAMETRE.COORDONNEES_FORMAT_AFFICHAGE,
      ),
    }}`,
    {},
  );
  const layerListRef = useRef<MapLegend>();
  const mapToolbarRef = useRef<MapToolbar>();

  const map = useMemo(() => {
    if (!projection || !mapElement.current || !afficheCoordonneesState.data) {
      return;
    }

    const initialMap = new Map({
      controls: defaultControls().extend([
        new FullScreen({ source: "map-container" }),
        new MousePosition({
          coordinateFormat:
            afficheCoordonneesState.data?.[
              PARAMETRE.COORDONNEES_FORMAT_AFFICHAGE
            ].parametreValeur === TYPE_AFFICHAGE_COORDONNEES.DEGRES_DECIMAUX
              ? (coordinate) => {
                  const coord = transform(
                    coordinate,
                    projection.name,
                    EPSG_4326,
                  );
                  return (
                    degreesToStringHDMS("NS", coord[1], 4) +
                    " " +
                    degreesToStringHDMS("EO", coord[0], 4)
                  );
                }
              : createStringXY(4),
          projection: projection.name,
        }),
        scaleControl,
      ]),
      interactions: [
        new MouseWheelZoom({ useAnchor: true, constrainResolution: true }),
      ],
      target: mapElement.current,
      layers: [],
      view: new View({
        zoom: 6,
        projection: EPSG_3857,
        center: getCenter(transformExtent(defaultExtent, EPSG_4326, EPSG_3857)), // Centre depuis l'étendue fournie par le serveur
      }),
    });

    if (user?.zoneIntegrationExtent) {
      const rawExtent = new WKT()
        .readGeometry(user.zoneIntegrationExtent.split(";").pop())
        .getExtent();
      initialMap
        ?.getView()
        .fit(transformExtent(rawExtent, projection.name, EPSG_3857), {
          maxZoom: 20,
        });
    }

    return initialMap;
  }, [mapElement.current, projection, afficheCoordonneesState.data]);

  // Ajout des couches disponibles depuis le serveur
  const availableLayers = useMemo(() => {
    if (!layersState.data) {
      return [];
    }
    return layersState.data.map((group) => {
      return {
        libelle: group.libelle,
        ordre: group.ordre,
        layers: group.layers.map((layer) => {
          const openlayer =
            layer.source === SOURCE_CARTO.WFS
              ? new VectorLayer({
                  source: toOpenLayer(layer),
                  zIndex: layer.ordre,
                })
              : new TileLayer({
                  source: toOpenLayer(layer),
                  zIndex: layer.ordre,
                });
          if (layer.active) {
            map?.addLayer(openlayer);
            layerListRef.current?.addActiveLayer(getUid(openlayer));
          }
          return {
            ...layer,
            openlayer: openlayer,
          };
        }),
      };
    });
  }, [layersState.data, map]);

  // Ajout / retrait d'une couche sur la carte
  const addOrRemoveLayer = (layer: any) => {
    const index = map?.getLayers().getArray().indexOf(layer.openlayer);
    if (index > -1) {
      map?.removeLayer(layer.openlayer);
      layerListRef.current?.removeActiveLayer(getUid(layer.openlayer));
    } else {
      map?.addLayer(layer.openlayer);
      layerListRef.current?.addActiveLayer(getUid(layer.openlayer));
    }
  };

  // Ajout de la couche de travail
  function createWorkingLayer() {
    const wl = new VectorLayer({
      source: new VectorSource(),
      style: () => {
        return new Style({
          fill: new Fill({
            color: "rgba(255, 255, 255, 0.2)",
          }),
          stroke: new Stroke({
            color: "rgba(255, 0, 0, 0.7)",
            width: 2,
          }),
          image: new CircleStyle({
            radius: 7,
            fill: new Fill({
              color: "#ffcc33",
            }),
          }),
        });
      },
      visibility: true,
      opacity: 1,
      zIndex: 9000,
    });

    map?.addLayer(wl);

    return wl;
  }

  function createDataPeiLayer() {
    return createPointLayer(
      map,
      (extent, projection) =>
        url`/api/pei/layer?bbox=` +
        extent.join(",") +
        "&srid=" +
        projection.getCode(),
      projection,
    );
  }

  const workingLayer = useMemo(() => {
    if (map && projection) {
      return createWorkingLayer();
    }
  }, [map, projection]);

  const dataPeiLayer = useMemo(() => {
    if (map && projection && displayPei) {
      return createDataPeiLayer();
    }
  }, [map, projection]);

  useEffect(() => {
    if (state?.target && map) {
      map
        .getView()
        .fit(
          transformExtent(
            state.target.extent,
            `EPSG:${state.target.srid}`,
            map.getView().getProjection().getCode(),
          ),
          { maxZoom: 20 },
        );
      window.history.replaceState({ from: state.from }, "");
    }
  }, [state, map]);

  useEffect(() => {
    // On met dans l'URL le extent pour simplifier la navigation
    if (map) {
      // On mémorise la dernière valeur connue de l'étendue pour éviter les navigations inutiles
      let lastExtent = "";

      const handleMoveEnd = () => {
        const view = map.getView();
        const extent = view.calculateExtent().join(",");

        // On vérifie si l'étendue a réellement changée pour éviter une boucle infinie
        if (extent !== lastExtent) {
          // Si elle a réellement changé, on la mémorise pour le prochain tour
          lastExtent = extent;
          const params = new URLSearchParams();
          params.set("extent", extent);
          navigate(`?${params.toString()}`, { replace: true, state: state });
        }
      };

      map.on("moveend", handleMoveEnd);

      // Récupération de l'étendue depuis les paramètres d'URL au chargement initial
      const params = new URLSearchParams(search);
      const extentParam = params.get("extent");
      if (
        extentParam &&
        typeModule !== TypeModuleRemocra.RAPPORT_PERSONNALISE
      ) {
        const geom = extentParam.split(",").map(Number);
        if (!isEmpty(geom)) {
          // On adapte la vue à l'étendue si elle est présente dans l'URL
          map.getView().fit(geom, { maxZoom: 20 });
        }
      }

      // Nettoyage du listener lorsque le composant est démonté ou que la carte change
      return () => {
        map.un("moveend", handleMoveEnd);
      };
    }
  }, [state, map, navigate, search, typeModule]);

  return {
    map,
    workingLayer,
    dataPeiLayer,
    availableLayers,
    addOrRemoveLayer,
    layerListRef,
    mapToolbarRef,
    projection,
  };
};

export default MapComponent;

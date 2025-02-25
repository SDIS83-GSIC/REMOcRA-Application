import { getUid } from "ol";
import Map from "ol/Map";
import View from "ol/View";
import { MousePosition, ScaleLine } from "ol/control";
import { defaults as defaultControls, FullScreen } from "ol/control.js";
import { createStringXY } from "ol/coordinate";
import { getCenter, getTopLeft, getWidth } from "ol/extent";
import { GeoJSON } from "ol/format";
import { MouseWheelZoom } from "ol/interaction";
import TileLayer from "ol/layer/Tile";
import VectorLayer from "ol/layer/Vector";
import "ol/ol.css";
import { get as getProjection, transformExtent } from "ol/proj";
import { TileWMS, WMTS } from "ol/source";
import TileSource from "ol/source/Tile";
import VectorSource from "ol/source/Vector";
import { Circle, Fill, Stroke, Style } from "ol/style";
import CircleStyle from "ol/style/Circle";
import WMTSTileGrid from "ol/tilegrid/WMTS";
import { MutableRefObject, ReactNode, useEffect, useMemo, useRef } from "react";
import { Col, Row } from "react-bootstrap";
import url from "../../module/fetch.tsx";
import { useAppContext } from "../App/AppProvider.tsx";
import { useGet } from "../Fetch/useFetch.tsx";
import { TypeModuleRemocra } from "../ModuleRemocra/ModuleRemocra.tsx";
import MapLegend from "./MapLegend.tsx";
import MapToolbar from "./MapToolbar.tsx";
import { createPointLayer } from "./MapUtils.tsx";
import "./map.css";

const EPSG_4326 = "EPSG:4326"; // WGS84 utilisé par l'étendue fournie par le serveur
const EPSG_3857 = "EPSG:3857"; // Web Mercator pour l'affichage
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
  bar: true,
  steps: 5,
  text: true,
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
    case "WMS":
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
    case "WMTS":
      return new WMTS({
        url: layer.url,
        layer: layer.layer,
        projection: layer.projection,
        matrixSet: "PM",
        format: layer.format ?? "image/png",
        tileGrid: tileGrid,
        style: "normal",
      });
    case "GSON":
      return new VectorSource({
        url: layer.url,
        loader: layer.loader,
        strategy: layer.strategy,
        format: new GeoJSON({
          dataProjection: layer.projection,
          featureProjection: layer.projection,
        }),
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
}) => {
  useEffect(() => {
    if (!map) {
      return;
    }
    toggleTool("move-view");
  }, [map]);
  return (
    <div className={"map-wrapper"}>
      {map && mapElement && (
        <Row className={"map-toolbar noprint "}>
          <Col xs={"auto"}>
            {/* Commun à toutes les cartes */}
            <MapToolbar
              ref={mapToolbarRef}
              map={map}
              workingLayer={workingLayer}
              toggleTool={toggleTool}
              activeTool={activeTool}
            />
          </Col>
          <Col xs={"auto"}>{toolbarElement && toolbarElement}</Col>
        </Row>
      )}
      <div ref={mapElement} className={"map-map border border-primary"} />
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
  const { epsg: projection, extent } = useAppContext();
  const layersState = useGet(url`/api/layers/${typeModule}`, {});
  const layerListRef = useRef<MapLegend>();
  const mapToolbarRef = useRef<MapToolbar>();

  const map = useMemo(() => {
    if (!projection || !mapElement.current) {
      return;
    }
    const initialMap = new Map({
      controls: defaultControls().extend([
        new FullScreen(),
        new MousePosition({
          coordinateFormat: createStringXY(4),
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
        center: getCenter(transformExtent(extent, EPSG_4326, EPSG_3857)), // Centre depuis l'étendue fournie par le serveur
      }),
    });
    return initialMap;
  }, [mapElement.current, projection]);

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
          const openlayer = new TileLayer({
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
            color: "blue",
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
      new Style({
        image: new Circle({
          radius: 5,
          fill: new Fill({ color: "black" }),
          stroke: new Stroke({
            color: [255, 0, 0],
            width: 2,
          }),
        }),
      }),
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

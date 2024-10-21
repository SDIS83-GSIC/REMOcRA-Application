import Map from "ol/Map";
import View from "ol/View";
import { MousePosition, ScaleLine } from "ol/control";
import { defaults as defaultControls, FullScreen } from "ol/control.js";
import { createStringXY } from "ol/coordinate";
import { getWidth } from "ol/extent";
import { GeoJSON } from "ol/format";
import { DragPan, MouseWheelZoom } from "ol/interaction";
import TileLayer from "ol/layer/Tile";
import VectorLayer from "ol/layer/Vector";
import { bbox as bboxStrategy } from "ol/loadingstrategy";
import "ol/ol.css";
import { fromLonLat, get as getProjection } from "ol/proj";
import { TileWMS, WMTS } from "ol/source";
import TileSource from "ol/source/Tile";
import VectorSource from "ol/source/Vector";
import { Circle, Fill, Stroke, Style } from "ol/style";
import CircleStyle from "ol/style/Circle";
import WMTSTileGrid from "ol/tilegrid/WMTS";
import { useMemo, useRef } from "react";
import { Col, Container, Row } from "react-bootstrap";
import { useAppContext } from "../App/AppProvider.tsx";
import url, { getFetchOptions } from "../../module/fetch.tsx";
import { useGet } from "../Fetch/useFetch.tsx";
import MapLegend from "./MapLegend.tsx";
import MapToolbar from "./MapToolbar.tsx";
import "./map.css";

const EPSG_3857 = "EPSG:3857"; // Web Mercator par défaut
const resolutions = [];
const matrixIds = [];
const proj3857 = getProjection(EPSG_3857)!;
const maxResolution = getWidth(proj3857.getExtent()) / 256;

// Matrices / résolutions
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
  origin: [-20037508, 20037508],
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
}: {
  map: Map;
  workingLayer: any;
  availableLayers: any[];
  addOrRemoveLayer: (layer: any) => void;
  layerListRef: any;
  mapToolbarRef: any;
  mapElement: any;
}) => {
  return (
    <Container fluid>
      {map && mapElement && (
        <>
          {/* Commun à toutes les cartes */}
          <MapToolbar
            ref={mapToolbarRef}
            map={map}
            workingLayer={workingLayer}
          />
        </>
      )}
      <Row className={"gutt-0"}>
        <Col>
          <div ref={mapElement} style={{ width: "100%", height: "800px" }} />
        </Col>
        <Col xs={3}>
          <MapLegend
            ref={layerListRef}
            layers={availableLayers}
            addOrRemoveLayer={addOrRemoveLayer}
          />
        </Col>
      </Row>
    </Container>
  );
};

export const useMapComponent = ({ mapElement }) => {
  const { epsg: projection } = useAppContext();
  const layersState = useGet(url`/api/layers`, {});
  const layerListRef = useRef<MapLegend>();
  const mapToolbarRef = useRef<MapToolbar>();

  const map = useMemo(() => {
    if (!projection) {
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
        new DragPan(),
        new MouseWheelZoom({ useAnchor: false, constrainResolution: true }),
      ],
      target: mapElement.current,
      layers: [],
      view: new View({
        zoom: 6,
        projection: projection.name,
        center: fromLonLat([6, 52]), // Environ le centre de la France
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
            layerListRef.current?.addActiveLayer(openlayer.ol_uid);
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
      layerListRef.current?.removeActiveLayer(layer.openlayer.ol_uid);
    } else {
      map?.addLayer(layer.openlayer);
      layerListRef.current?.addActiveLayer(layer.openlayer.ol_uid);
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
    const vectorSource = toOpenLayer({
      source: "GSON",
      loader: async (extent, resolution, projection, success, failure) => {
        const res = await fetch(
          url`/api/pei/layer?bbox=` +
            extent.join(",") +
            "&srid=" +
            projection.getCode(),
          getFetchOptions({ method: "GET" }),
        );
        res
          .text()
          .then((text) => {
            const features = vectorSource
              .getFormat()
              .readFeatures(JSON.parse(text));
            vectorSource.addFeatures(features);
            success(features);
          })
          .catch(() => {
            vectorSource.removeLoadedExtent(extent);
            failure();
          });
      },
      extent: map?.getView().calculateExtent(),
      projection: projection.name,
      strategy: bboxStrategy,
    });

    const dl = new VectorLayer({
      source: vectorSource,
      style: new Style({
        image: new Circle({
          radius: 5,
          fill: new Fill({ color: "black" }),
          stroke: new Stroke({
            color: [255, 0, 0],
            width: 2,
          }),
        }),
      }),
      extent: map?.getView().calculateExtent(),
      opacity: 1,
      visible: true,
      minResolution: 0,
      maxResolution: 99999,
      zIndex: 9999,
    });

    map?.addLayer(dl);

    return dl;
  }

  const workingLayer = useMemo(() => {
    if (map && projection) {
      return createWorkingLayer();
    }
  }, [map, projection]);

  const dataPeiLayer = useMemo(() => {
    if (map && projection) {
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
  };
};

export default MapComponent;

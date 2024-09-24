import { useEffect, useRef, useState } from "react";
import Map from "ol/Map";
import View from "ol/View";
import TileLayer from "ol/layer/Tile";
import { TileWMS, WMTS } from "ol/source";
import { fromLonLat, get as getProjection } from "ol/proj";
import { getWidth } from "ol/extent";
import WMTSTileGrid from "ol/tilegrid/WMTS";
import { defaults as defaultControls, FullScreen } from "ol/control.js";
import { MousePosition, ScaleLine } from "ol/control";
import { createStringXY } from "ol/coordinate";
import { register } from "ol/proj/proj4";
import proj4 from "proj4";
import "ol/ol.css";
import TileSource from "ol/source/Tile";
import { Col, Container, Row } from "react-bootstrap";
import "./map.css";
import { DragPan, MouseWheelZoom } from "ol/interaction";
import { Circle, Fill, Stroke, Style } from "ol/style";
import VectorLayer from "ol/layer/Vector";
import CircleStyle from "ol/style/Circle";
import VectorSource from "ol/source/Vector";
import { GeoJSON } from "ol/format";
import { bbox as bboxStrategy } from "ol/loadingstrategy";
import url, { getFetchOptions } from "../../module/fetch.tsx";
import { useGet } from "../Fetch/useFetch.tsx";
import MapLegend from "./MapLegend.tsx";
import MapToolbar from "./MapToolbar.tsx";

proj4.defs(
  "EPSG:2154",
  "+proj=lcc +lat_1=49 +lat_2=44 +lat_0=46.5 +lon_0=3 +x_0=700000 +y_0=6600000 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs",
);
register(proj4);

const MapComponent = () => {
  const [availableLayers, setAvailableLayers] = useState([]);
  const [workingLayer, setWorkingLayer] = useState();
  const [dataLayer, setDataLayer] = useState();
  const [map, setMap] = useState<Map>();
  const mapElement = useRef<HTMLDivElement>();
  const layersState = useGet(url`/api/layers`, {});
  const layerListRef = useRef<MapLegend>();
  const mapToolbarRef = useRef<MapToolbar>();

  const projection = "EPSG:2154";
  const resolutions = [];
  const matrixIds = [];
  const proj3857 = getProjection("EPSG:3857")!;
  const maxResolution = getWidth(proj3857.getExtent()) / 256;

  // Matrices / résolutions
  for (let i = 0; i < 20; i++) {
    matrixIds[i] = i.toString();
    resolutions[i] = maxResolution / Math.pow(2, i);
  }

  // Grille de tuiles
  const tileGrid = new WMTSTileGrid({
    origin: [-20037508, 20037508],
    resolutions: resolutions,
    matrixIds: matrixIds,
  });

  // Échelle sur la carte suivant le niveau zoom
  const scaleControl = new ScaleLine({
    units: "metric",
    bar: true,
    steps: 5,
    text: true,
    maxWidth: 200,
    minWidth: 100,
  });

  // Coordonnées du pointeur sur la carte
  const mousePosition = new MousePosition({
    coordinateFormat: createStringXY(4),
    projection: projection,
  });

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

  // Déclaration OL depuis une définition de couche
  function toOpenLayer(
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

  // Ajout des couches disponibles depuis le serveur
  useEffect(() => {
    if (!layersState.data) {
      return;
    }
    setAvailableLayers(
      layersState.data.map((group) => {
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
      }),
    );
  }, [layersState.data]);

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

  function createDataLayer() {
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
      projection: "EPSG:2154",
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

  // Initialisation de la map
  useEffect(() => {
    const initialMap = new Map({
      controls: defaultControls().extend([
        new FullScreen(),
        mousePosition,
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
        projection: projection,
        center: fromLonLat([6, 52]), // Environ le centre de la France
      }),
    });
    setMap(initialMap);
  }, []);

  useEffect(() => {
    setWorkingLayer(createWorkingLayer());
    setDataLayer(createDataLayer());
  }, [map]);

  return (
    <Container fluid>
      {map && (
        <MapToolbar
          ref={mapToolbarRef}
          map={map}
          workingLayer={workingLayer}
          dataLayer={dataLayer}
        />
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

export default MapComponent;

import { Feature, Map } from "ol";
import { WKT } from "ol/format";
import { DragPan, Interaction, Modify, Select } from "ol/interaction";
import VectorLayer from "ol/layer/Vector";
import { bbox as bboxStrategy } from "ol/loadingstrategy";
import { transformExtent } from "ol/proj";
import { Fill, Stroke, Style } from "ol/style";
import CircleStyle from "ol/style/Circle";
import SOURCE_CARTO from "../../enums/SourceCartoEnum.tsx";
import url, { getFetchOptions } from "../../module/fetch.tsx";
import { EPSG_3857 } from "../../utils/constantsUtils.tsx";
import { toOpenLayer } from "./Map.tsx";

/**
 * Permet de déplacer un objet
 * @param active : si le toggle est séléctionné ou non
 * @param selectCtrl : le select qui sera utilisé
 * @param map : la carte
 * @param urlApi : Le point d'api pour permet le déplacement de l'object
 * @param successToast
 * @param errorToast
 * @param conditionObjetSelectionne : La condition pour pouvoir déplacer l'objet (par exemple, il doit s'agit d'un PEI en projet pour la couverture hydraulique)
 */

function toggleDeplacerPoint(
  active = false,
  selectCtrl: Select,
  map: Map,
  urlApi: string,
  dataLayer: any,
  successToast: (e: string) => void,
  errorToast: (e: string) => void,
  conditionObjetSelectionne = (feature: Feature) => feature != null, // unused ?
) {
  const idx1 = map?.getInteractions().getArray().indexOf(selectCtrl);
  let modifyCtrl = new Modify({
    features: selectCtrl.getFeatures(),
    source: dataLayer,
    snapToPointer: true,
  });
  if (active) {
    const idx2 = map?.getInteractions().getArray().indexOf(modifyCtrl);
    if (idx1 === -1 && idx2 === -1) {
      map.addInteraction(selectCtrl);

      selectCtrl.on("select", function (evt) {
        evt.selected.forEach(async function (feature) {
          if (conditionObjetSelectionne(feature)) {
            map.addInteraction(modifyCtrl);
          } else {
            map.addInteraction(modifyCtrl);
          }
        });
      });

      modifyCtrl.on("modifyend", function (evt) {
        evt.features.forEach(async function (feature) {
          if (conditionObjetSelectionne(feature)) {
            const res = await fetch(
              url`${urlApi}` + feature.getProperties().elementId,
              getFetchOptions({
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                  geometry:
                    "SRID=" +
                    map.getView().getProjection().getCode().split(":").pop() +
                    ";" +
                    new WKT().writeFeature(feature),
                }),
              }),
            );

            if (res.ok) {
              successToast("L'élément a bien été déplacé.");
            } else {
              res.text().then((reason: string) => {
                errorToast(reason);
                dataLayer.getSource().refresh();
              });
            }
          }
          refreshLayerGeoserver(map);
        });
      });
    }
  } else {
    map.removeInteraction(selectCtrl);
    map.removeInteraction(modifyCtrl);
    modifyCtrl = null;
  }
}

/**
 * Permet d'ajouter une couche de points à la carte
 * @param map : carte
 * @param urlApi : url pour récupérer les points à afficher
 * @param style : style du point
 * @param projection : projection
 * @returns
 */
export function createPointLayer(
  map: Map,
  urlApi: (extent, projection) => string,
  projection: { name: string },
  style?: Style,
) {
  const vectorSource = toOpenLayer({
    source: SOURCE_CARTO.GEOJSON,
    loader: async (
      extent: any,
      resolution: any,
      projection: any,
      success: (arg0: any) => void,
      failure: () => void,
    ) => {
      const res = await fetch(
        url`${urlApi(extent, projection)}`,
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
    extent: map.getView().calculateExtent(),
    projection: projection.name,
    strategy: bboxStrategy,
  });

  const dl = new VectorLayer({
    source: vectorSource,
    style:
      style ??
      new Style({
        fill: new Fill({
          color: "rgba(0, 0, 0, 0)",
        }),
        stroke: new Stroke({
          color: "rgba(0, 0, 0, 0)",
          width: 4,
        }),
        image: new CircleStyle({
          radius: 4,
          stroke: new Stroke({
            color: "rgba(0, 0, 0, 0)",
          }),
          fill: new Fill({
            color: "rgba(0, 0, 0, 0)",
          }),
        }),
      }),
    extent: map.getView().calculateExtent(),
    opacity: 1,
    visible: true,
    minZoom: 12,
    minResolution: 0,
    maxResolution: 99999,
    zIndex: 9999,
  });

  map.addLayer(dl);

  return dl;
}

export default toggleDeplacerPoint;

export function addWktLayer(
  map: Map,
  wktString: string,
  workingLayer: any,
  projection: { name: string },
) {
  workingLayer.getSource().clear();

  const wktFormat = new WKT();
  const feature = wktFormat.readFeature(wktString, {
    dataProjection: projection.name,
    featureProjection: projection.name,
  });

  // On transforme en 3857
  feature.getGeometry().transform(projection.name, EPSG_3857);

  workingLayer.getSource().addFeature(feature);

  const vectorLayer = new VectorLayer({
    source: workingLayer.getSource(),
    style: new Style({
      stroke: new Stroke({
        color: "green",
        width: 2,
      }),
      fill: new Fill({
        color: "rgba(0, 255, 0, 0.2)",
      }),
      image: new CircleStyle({
        radius: 8,
        fill: new Fill({
          color: "#ffb412",
        }),
      }),
    }),
    zIndex: 9999,
  });

  // Ajouter la couche à la carte existante
  map.addLayer(vectorLayer);

  // On zoom sur la carte avce un maxZoom
  const extent = workingLayer.getSource().getExtent();

  if (map.getSize()?.every((e) => e === 0)) {
    map.setSize([1000, 800]);
  }

  map.getView().fit(extent, {
    padding: [50, 50, 50, 50],
    size: map.getSize(),
    maxZoom: 18,
  });
}

export function refreshLayerGeoserver(map: Map) {
  map.getLayers().forEach((layer) => {
    if (layer.getSource().updateParams) {
      layer.getSource().updateParams({
        time: Date.now(),
      });
      layer.getSource().refresh();
    }
  });
}

export function desactiveMoveMap(map: Map) {
  map
    .getInteractions()
    .getArray()
    .forEach((interaction: Interaction) => {
      if (interaction instanceof DragPan) {
        interaction.setActive(false);
      }
    });
}

export function centrerToExtent(extent: any, map: Map, sridFrom: string) {
  map
    ?.getView()
    .fit(
      transformExtent(
        extent,
        `EPSG:${sridFrom}`,
        map.getView().getProjection().getCode(),
      ),
      {
        padding: [50, 50, 50, 50],
        maxZoom: 20,
      },
    );
}

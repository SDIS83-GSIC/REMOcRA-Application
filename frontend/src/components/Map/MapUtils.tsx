import { Map } from "ol";
import { Modify, Select } from "ol/interaction";
import VectorLayer from "ol/layer/Vector";
import { bbox as bboxStrategy } from "ol/loadingstrategy";
import { Style } from "ol/style";
import url, { getFetchOptions } from "../../module/fetch.tsx";
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
  conditionObjetSelectionne = () => true,
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
            const coordinate = feature.getGeometry().getCoordinates();

            const res = await fetch(
              url`${urlApi}` + feature.getProperties().pointId,
              getFetchOptions({
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                  coordonneeX: coordinate[0],
                  coordonneeY: coordinate[1],
                  srid: map.getView().getProjection().getCode().split(":")[1],
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
  style: Style,
  projection: { name: string },
) {
  const vectorSource = toOpenLayer({
    source: "GSON",
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
    style: style,
    extent: map.getView().calculateExtent(),
    opacity: 1,
    visible: true,
    minResolution: 0,
    maxResolution: 99999,
    zIndex: 9999,
  });

  map.addLayer(dl);

  return dl;
}

export default toggleDeplacerPoint;

import { Map } from "ol";
import { Modify, Select } from "ol/interaction";
import url, { getFetchOptions } from "../../module/fetch.tsx";

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

export default toggleDeplacerPoint;

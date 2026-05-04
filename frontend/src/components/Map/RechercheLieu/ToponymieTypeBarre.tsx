import { WKT } from "ol/format";
import OLMap from "ol/Map";
import { transformExtent } from "ol/proj";
import { useState } from "react";
import { AsyncTypeahead } from "react-bootstrap-typeahead";
import { getFetchOptions } from "../../../module/fetch.tsx";
import { useToastContext } from "../../../module/Toast/ToastProvider.tsx";
import { RESOLUTION_ZOOM } from "../../../utils/constantsUtils.tsx";
import { ItemSearch } from "../../Localisation/useLocalisation.tsx";

const ToponymieTypeBarre = ({
  map,
  urlAPI,
  dependentObject,
}: {
  urlAPI: string;
  dependentObject?: ItemSearch | null;
  map: OLMap;
}) => {
  const [state, setState] = useState({ isLoading: false, options: [] });
  const { error: errorToast } = useToastContext();

  return (
    <AsyncTypeahead
      className="h-100"
      minLength={2}
      placeholder={"Zoomer sur le lieu..."}
      emptyLabel={"Aucun résultat"}
      promptText={"Saisissez au moins 2 lettres"}
      searchText={"Recherche en cours"}
      isLoading={state.isLoading}
      options={state.options}
      labelKey={(feature) => `${feature.properties.label}`}
      onSearch={(query) => {
        if (query.length < 2) {
          return;
        }

        setState({ ...state, isLoading: true });
        const params = new URLSearchParams();
        params.append("libelle", query);
        if (dependentObject) {
          params.append("dependenceObjId", dependentObject.id);
        }

        fetch(
          `${urlAPI}/get-toponymies?${params.toString()}`,
          getFetchOptions(),
        )
          .then((response) => response.json())
          .then((json) => {
            const features = json.map(
              (item: {
                toponymieGeometrie: string;
                toponymieLibelle: any;
                toponymieId: any;
              }) => {
                const [sridStr, wkt] = item.toponymieGeometrie.split(";");

                return {
                  type: "Feature",
                  geometry: {
                    type: "Point", // On prend le premier point pour représenter le lieu
                    coordinates: new WKT().readGeometry(wkt).getExtent(),
                    srid: sridStr.split("=").pop(),
                  },
                  properties: {
                    id: item.toponymieId,
                    label: item.toponymieLibelle,
                  },
                };
              },
            );

            setState({
              isLoading: false,
              options: features,
            });
          });
      }}
      onChange={(features) => {
        if (features.length === 0) {
          return;
        }
        const feature: any = features[0];

        if (feature.geometry.srid !== "0") {
          map
            .getView()
            .fit(
              transformExtent(
                feature.geometry.coordinates,
                `EPSG:${feature.geometry.srid}`,
                map.getView().getViewStateAndExtent().viewState.projection,
              ),
            );
          map
            .getView()
            .setZoom(map.getView().getZoomForResolution(RESOLUTION_ZOOM)!);
        } else {
          errorToast("La géométrie présente une anomalie : l'SRID vaut 0.");
        }
      }}
    />
  );
};

export default ToponymieTypeBarre;

import { useState } from "react";
import Map from "ol/Map";
import { AsyncTypeahead } from "react-bootstrap-typeahead";
import { transformExtent } from "ol/proj";
import { WKT } from "ol/format";
import url, { getFetchOptions } from "../../module/fetch.tsx";

const ToponymieTypeBarre = ({
  map,
  criseId,
}: {
  map: Map;
  criseId: string;
}) => {
  const [state, setState] = useState({ isLoading: false, options: [] });

  return (
    <AsyncTypeahead
      minLength={3}
      placeholder={"Zoomer sur le lieu..."}
      emptyLabel={"Aucun résultat"}
      promptText={"Saisissez au moins 3 lettres"}
      searchText={"Recherche en cours"}
      isLoading={state.isLoading}
      options={state.options}
      labelKey={(feature) => `${feature.properties.label}`}
      onSearch={(query) => {
        if (query.length < 3) {
          return;
        }

        setState({ ...state, isLoading: true });
        fetch(
          url`/api/crise/${criseId}/get-toponymies?${{ libelle: query }}`,
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
                const geom = new WKT().readGeometry(wkt);
                const srid = sridStr.split("=").pop();

                return {
                  type: "Feature",
                  geometry: {
                    type: "Point", // On prend le premier point pour représenter le lieu
                    coordinates: geom.getExtent(),
                    srid: srid,
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
        const coordSource = transformExtent(
          feature.geometry.coordinates,
          `EPSG:${feature.geometry.srid}`,
          map.getView().getViewStateAndExtent().viewState.projection,
        );
        map.getView().setCenter(coordSource);
        map
          .getView()
          .setZoom(map.getView().getZoomForResolution(0.29858214173896974));
      }}
    />
  );
};

export default ToponymieTypeBarre;

import Map from "ol/Map";
import { transform } from "ol/proj";
import { useState } from "react";
import { AsyncTypeahead } from "react-bootstrap-typeahead";
import { EPSG_4326 } from "../../utils/constantsUtils.tsx";

const AdresseTypeahead = ({ map }: { map: Map }) => {
  const [state, setState] = useState({ isLoading: false, options: [] });
  const apiEpsg = EPSG_4326; // ESPG utilisée pour data.gouv

  return (
    <AsyncTypeahead
      minLength={3}
      placeholder={"Zoomer sur la voie"}
      emptyLabel={"Aucun résultat"}
      promptText={"Saisissez au moins 3 lettres"}
      searchText={"Recherche en cours"}
      isLoading={state.isLoading}
      options={state.options}
      filterBy={
        (options) => options // On récupère toutes les options que la requête nous renvoie !
      }
      labelKey={(feature) => `${feature.properties.label}`}
      onSearch={(query) => {
        if (query.length < 3) {
          return;
        }
        const coord4326 = transform(
          map.getView().getCenter(),
          map.getView().getViewStateAndExtent().viewState.projection,
          apiEpsg,
        );
        setState({ ...state, isLoading: true });
        fetch(
          `https://data.geopf.fr/geocodage/search?q=${query}&lat=${coord4326[0]}&lon=${coord4326[1]}`,
        )
          .then((response) => response.json())
          .then((json) =>
            setState({
              isLoading: false,
              options: json.features,
            }),
          );
      }}
      onChange={(features) => {
        if (features.length === 0) {
          return;
        }
        const coordSource = transform(
          features[0].geometry.coordinates,
          apiEpsg,
          map.getView().getViewStateAndExtent().viewState.projection,
        );
        map.getView().setCenter(coordSource);
        map
          .getView()
          .setZoom(map.getView().getZoomForResolution(0.29858214173896974));
      }}
      style={{ width: 400 }}
    />
  );
};

export default AdresseTypeahead;

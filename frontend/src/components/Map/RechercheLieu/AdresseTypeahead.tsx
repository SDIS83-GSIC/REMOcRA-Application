import OLMap from "ol/Map";
import { transform } from "ol/proj";
import { useMemo, useState } from "react";
import { AsyncTypeahead } from "react-bootstrap-typeahead";
import PARAMETRE from "../../../enums/ParametreEnum.tsx";
import url from "../../../module/fetch.tsx";
import { EPSG_4326, RESOLUTION_ZOOM } from "../../../utils/constantsUtils.tsx";
import { useGet } from "../../Fetch/useFetch.tsx";

const AdresseTypeahead = ({ map }: { map: OLMap }) => {
  const [state, setState] = useState({ isLoading: false, options: [] });
  const apiEpsg = EPSG_4326; // ESPG utilisée pour data.gouv

  const parametresState = useGet(
    url`/api/parametres?${{
      listeParametreCode: JSON.stringify([PARAMETRE.BAN_CODE_DEPARTEMENT]),
    }}`,
  );

  const banCodeDepartement = useMemo<string | undefined>(() => {
    if (!parametresState.isResolved) {
      return undefined;
    }

    return parametresState?.data[PARAMETRE.BAN_CODE_DEPARTEMENT]
      .parametreValeur;
  }, [parametresState]);

  return (
    <AsyncTypeahead
      minLength={3}
      className={"ms-3"}
      placeholder={"Zoomer sur la voie"}
      emptyLabel={"Aucun résultat"}
      promptText={"Saisissez au moins 3 lettres"}
      searchText={"Recherche en cours"}
      isLoading={state.isLoading}
      options={state.options}
      filterBy={
        () => true // On récupère toutes les options que la requête nous renvoie !
      }
      labelKey={(feature) => `${feature.properties.label}`}
      onSearch={(query) => {
        if (query.length < 2) {
          return;
        }
        const coord4326 = transform(
          map.getView().getCenter(),
          map.getView().getViewStateAndExtent().viewState.projection,
          apiEpsg,
        );
        setState({ ...state, isLoading: true });
        fetch(
          `https://data.geopf.fr/geocodage/search?q=${query}&lat=${coord4326[0]}&lon=${coord4326[1]}${banCodeDepartement ? `&depcode=${banCodeDepartement}` : ""}`,
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
          .setZoom(map.getView().getZoomForResolution(RESOLUTION_ZOOM));
      }}
      style={{ width: 400 }}
    />
  );
};

export default AdresseTypeahead;

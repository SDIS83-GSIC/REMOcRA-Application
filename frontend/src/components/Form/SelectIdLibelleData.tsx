import { Typeahead } from "react-bootstrap-typeahead";
import { useGet } from "../Fetch/useFetch.tsx";
import Loading from "../Elements/Loading/Loading.tsx";
import { SelectIdLibelleDataType } from "../../utils/typeUtils.tsx";

/**
 * Composant Select qui attend un Endpoint renvoyant un objet de type List<IdLibelleData>
 *     pour faire un select avec toutes ces données.
 *
 * @param {Function} onChange - Fonction de rappel pour gérer les changements de sélection.
 * @param {string} name - Nom du composant de sélection.
 * @param url
 */
const SelectIdLibelleData = ({
  onChange,
  name,
  url,
}: SelectIdLibelleDataType) => {
  const stateData = useGet(url);

  const {
    isResolved: isResolvedListData,
    // eslint-disable-next-line no-empty-pattern
    data: listData = ([] = {}),
  } = stateData;
  const defaultValue = {
    id: undefined,
    libelle: "Tous",
  };
  if (!isResolvedListData) {
    return <Loading />;
  } else {
    const data = [defaultValue].concat(listData);
    return (
      <Typeahead
        placeholder={"Sélectionnez..."}
        size={"sm"}
        clearButton
        options={data}
        labelKey={"libelle"}
        onChange={(data) => {
          onChange({ name: name, value: data[0]?.id });
        }}
      />
    );
  }
};
export default SelectIdLibelleData;

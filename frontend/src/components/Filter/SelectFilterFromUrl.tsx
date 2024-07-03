import { useGet } from "../Fetch/useFetch.tsx";
import Loading from "../Elements/Loading/Loading.tsx";
import { SelectFilterFromUrlType } from "../../utils/typeUtils.tsx";
import SelectFilterFromList from "./SelectFilterFromList.tsx";

/**
 * Composant Select qui attend un Endpoint renvoyant un objet de type List<IdLibelleData>
 *     pour faire un select avec toutes ces données.
 *
 * @param {Function} onChange - Fonction de rappel pour gérer les changements de sélection.
 * @param {string} name - Nom du composant de sélection.
 * @param url
 */
const SelectFilterFromUrl = ({
  onChange,
  name,
  url,
  defaultValue,
}: SelectFilterFromUrlType) => {
  const stateData = useGet(url);

  const {
    isResolved: isResolvedListData,
    // eslint-disable-next-line no-empty-pattern
    data: listData = ([] = {}),
  } = stateData;

  if (!isResolvedListData) {
    return <Loading />;
  } else {
    return (
      <SelectFilterFromList
        name={name}
        listIdCodeLibelle={listData}
        onChange={onChange}
        defaultValue={defaultValue}
      />
    );
  }
};
export default SelectFilterFromUrl;

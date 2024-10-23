import url from "../../module/fetch.tsx";
import { SelectNomenclaturesType } from "../../utils/typeUtils.tsx";
import { useGet } from "../Fetch/useFetch.tsx";
import SelectFilterFromList from "./SelectFilterFromList.tsx";
/**
 * Composant de sélection pour les nomenclatures.
 *
 * @param {Function} onChange - Fonction de rappel pour gérer les changements de sélection.
 * @param {string} name - Nom du composant de sélection.
 * @param {NOMENCLATURE} nomenclature - La nomenclature voulue
 */
const SelectNomenclaturesFilter = ({
  onChange,
  name,
  nomenclature,
}: SelectNomenclaturesType) => {
  const response = useGet(url`/api/nomenclatures/list/` + nomenclature);
  return (
    response.data && (
      <SelectFilterFromList
        name={name}
        listIdCodeLibelle={response.data}
        onChange={onChange}
      />
    )
  );
};

export default SelectNomenclaturesFilter;

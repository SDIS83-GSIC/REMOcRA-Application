import ensureDataCache from "../../utils/ensureData.tsx";
import { SelectNomenclaturesType } from "../../utils/typeUtils.tsx";
import SelectFilterFromList from "./SelectFilterFromList.tsx";
/**
 * Composant de sélection pour les nomenclatures.
 *
 * @param {Function} onChange - Fonction de rappel pour gérer les changements de sélection.
 * @param {string} name - Nom du composant de sélection.
 * @param {TYPE_DATA_CACHE} nomenclature - La nomenclature voulue
 */
const SelectNomenclaturesFilter = ({
  onChange,
  name,
  nomenclature,
}: SelectNomenclaturesType) => {
  const list = ensureDataCache(nomenclature);
  return (
    list && (
      <SelectFilterFromList
        name={name}
        listIdCodeLibelle={list}
        onChange={onChange}
      />
    )
  );
};

export default SelectNomenclaturesFilter;

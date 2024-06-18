import url from "../../module/fetch.tsx";
import { SelectNomenclaturesType } from "../../utils/typeUtils.tsx";
import SelectIdLibelleData from "./SelectIdLibelleData.tsx";

/**
 * Composant de sélection pour les nomenclatures.
 *
 * @param {Function} onChange - Fonction de rappel pour gérer les changements de sélection.
 * @param {string} name - Nom du composant de sélection.
 * @param {NOMENCLATURES} nomenclature - La nomenclature voulue
 */
const SelectNomenclatures = ({
  onChange,
  name,
  nomenclature,
}: SelectNomenclaturesType) => {
  return (
    <SelectIdLibelleData
      name={name}
      url={url`/api/nomenclatures/list/` + nomenclature}
      onChange={onChange}
    />
  );
};

export default SelectNomenclatures;

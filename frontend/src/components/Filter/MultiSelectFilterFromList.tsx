import ReactSelect from "react-select";
import { SelectFilterFromListType } from "../../utils/typeUtils.tsx";

/**
 * Composant Select qui attend un Endpoint renvoyant un objet de type List<IdLibelleData>
 *     pour faire un select avec toutes ces données.
 *
 * @param {Function} onChange - Fonction de rappel pour gérer les changements de sélection.
 * @param {string} name - Nom du composant de sélection.
 * @param {list} listIdCodeLibelle - Liste ayant des objets id-Libellé.
 */
const MultiSelectFilterFromList = ({
  onChange,
  name,
  listIdCodeLibelle,
}: SelectFilterFromListType) => {
  return (
    <>
      <ReactSelect
        isMulti={true}
        placeholder={"Sélectionnez"}
        name={name}
        options={listIdCodeLibelle}
        getOptionValue={(t) => t.id}
        getOptionLabel={(t) => t.libelle}
        onChange={(data) => {
          onChange({ name: name, value: data.map((e) => e?.id) });
        }}
      />
    </>
  );
};
export default MultiSelectFilterFromList;

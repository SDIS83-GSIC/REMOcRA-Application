import ReactSelect from "react-select";
import {
  IdCodeLibelleType,
  SelectFilterFromListType,
} from "../../utils/typeUtils.tsx";
/**
 * Composant Select qui attend un Endpoint renvoyant un objet de type List<IdLibelleData>
 *     pour faire un select avec toutes ces données.
 *
 * @param {Function} onChange - Fonction de rappel pour gérer les changements de sélection.
 * @param {string} name - Nom du composant de sélection.
 * @param {list} listIdLibelle - Liste ayant des objets id-Libellé.
 * @param {IdCodeLibelleType} value - Value sélectionnée par défaut.
 * @param {string} label - Label du select.
 * @param {boolean} required - Valeur obligatoire ?
 */
const SelectFilterFromList = ({
  onChange,
  name,
  listIdCodeLibelle,
  value,
  disabled = false,
}: SelectFilterFromListType) => {
  const defaultValueTous: IdCodeLibelleType = {
    id: "",
    code: "",
    libelle: "Tous",
  };

  const data = [defaultValueTous].concat(listIdCodeLibelle);

  return (
    <>
      <ReactSelect
        placeholder={"Sélectionnez"}
        name={name}
        options={data}
        value={data.find((e) => e.id === value)}
        getOptionValue={(t) => t.id}
        getOptionLabel={(t) => t.libelle}
        onChange={(data) => {
          onChange({ name: name, value: data.id });
        }}
        isDisabled={disabled}
      />
    </>
  );
};
export default SelectFilterFromList;

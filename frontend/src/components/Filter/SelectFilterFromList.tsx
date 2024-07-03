import { Typeahead } from "react-bootstrap-typeahead";
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
  defaultValue: value,
}: SelectFilterFromListType) => {
  const defaultValue: IdCodeLibelleType = {
    id: "",
    code: "",
    libelle: "Tous",
  };

  const data = [defaultValue].concat(listIdCodeLibelle);

  return (
    <>
      <Typeahead
        className="d-flex"
        placeholder={"Sélectionnez..."}
        size={"sm"}
        options={data}
        labelKey={"libelle"}
        onChange={(data) => {
          onChange({ name: name, value: data[0]?.id });
        }}
        defaultSelected={value ? [value] : data[0]?.id === "" ? [data[0]] : []}
        clearButton
      />
    </>
  );
};
export default SelectFilterFromList;

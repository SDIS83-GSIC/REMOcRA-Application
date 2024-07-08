import { Typeahead } from "react-bootstrap-typeahead";
import { SelectFormType } from "../../utils/typeUtils.tsx";
import { FormLabel } from "./Form.tsx";

/**
 * Composant Select qui attend un Endpoint renvoyant un objet de type List<IdLibelleData>
 *     pour faire un select dans les formulaires
 *
 * @param {Function} onChange - Fonction de rappel pour gérer les changements de sélection.
 * @param {string} name - Nom du composant de sélection.
 * @param {list} listIdLibelle - Liste ayant des objets id-Libellé.
 * @param {IdLibelleType} value - Value sélectionnée par défaut.
 * @param {string} label - Label du select.
 * @param {boolean} required - Valeur obligatoire ?
 */
const SelectForm = ({
  name,
  listIdCodeLibelle,
  value,
  label,
  required = false,
  disabled = false,
  setValues,
}: SelectFormType) => {
  // Si on est pas dans un filtre de tableau, on doit définir la fonction là pour set la valeur modifiée
  const onChange = ({ name, value }) => {
    setValues((prevValues) => ({
      ...prevValues,
      [name]: value,
    }));
  };
  const list = listIdCodeLibelle ?? [];
  return (
    <>
      {label && <FormLabel label={label} required={required} />}
      <Typeahead
        className="d-flex"
        placeholder={"Aucune valeur saisie"}
        size={"sm"}
        options={list}
        labelKey={"libelle"}
        onChange={(data) => {
          onChange({ name: name, value: data[0]?.id });
        }}
        defaultSelected={value ? [value] : list[0]?.id === "" ? [list[0]] : []}
        clearButton
        disabled={disabled}
      />
    </>
  );
};

export default SelectForm;

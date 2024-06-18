import Form from "react-bootstrap/Form";
import { SelectType } from "../../utils/typeUtils.tsx";

/**
 * Composant Select pour les enums.
 *
 * @param {Object} options - Les options à afficher dans le menu déroulant, sous forme de paires clé-valeur (enum).
 * @param {string} name - Le nom attribué au composant de sélection.
 * @param {Function} onChange - Fonction de rappel pour gérer les changements de sélection.
 * @typedef {Object} OptionSelectType - Représente une option avec une valeur et un libellé.
 * @property {string} value - La valeur de l'option.
 * @property {string} libelle - Le libellé à afficher pour l'option.
 */
function SelectEnumOption({
  options,
  name,
  onChange: onChangeCallback,
}: SelectEnumOptionType) {
  const optionsArray: OptionSelectType[] = [];
  for (const key in options) {
    // eslint-disable-next-line no-prototype-builtins
    if (options.hasOwnProperty(key)) {
      optionsArray.push({ value: key, libelle: options[key] });
    }
  }
  return (
    <Form.Select
      size={"sm"}
      name={name}
      onChange={(e) => {
        onChangeCallback({ name: name, value: e.target.value });
      }}
    >
      <option value={""}>Sélectionnez...</option>

      {optionsArray.map((option: OptionSelectType) => {
        return (
          <option key={option.value} value={option.value}>
            {option.libelle}
          </option>
        );
      })}
    </Form.Select>
  );
}

type OptionSelectType = {
  value: string;
  libelle: string;
};

type SelectEnumOptionType = SelectType & { options: any };

export default SelectEnumOption;

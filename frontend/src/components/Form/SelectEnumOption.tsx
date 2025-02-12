import ReactSelect from "react-select";
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
  value,
}: SelectEnumOptionType) {
  const optionsArray: OptionSelectType[] = [];
  optionsArray.push({ value: "", libelle: "Tous" });
  for (const key in options) {
    // eslint-disable-next-line no-prototype-builtins
    if (options.hasOwnProperty(key)) {
      optionsArray.push({ value: key, libelle: options[key] });
    }
  }

  const customStyles = {
    control: (provided) => ({
      ...provided,
      width: 150, // Largeur fixe en pixels
    }),
    menu: (provided) => ({
      ...provided,
      width: 300, // Largeur du menu déroulant
    }),
  };

  return (
    <ReactSelect
      styles={customStyles}
      placeholder={"Sélectionnez"}
      noOptionsMessage={() => "Aucune donnée trouvée"}
      name={name}
      value={optionsArray.find((e) => e.value === value)}
      options={optionsArray}
      getOptionValue={(t) => t.value}
      getOptionLabel={(t) => t.libelle}
      onChange={(data) => {
        onChangeCallback({ name: name, value: data.value });
      }}
    />
  );
}

type OptionSelectType = {
  value: string;
  libelle: string;
};

type SelectEnumOptionType = SelectType & { options: any };

export default SelectEnumOption;

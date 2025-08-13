import { SelectFormType } from "../../utils/typeUtils.tsx";
import { SelectInput } from "./Form.tsx";
/**
 * Composant Select qui attend un Endpoint renvoyant un objet de type List<IdLibelleData>
 *     pour faire un select dans les formulaires
 *
 * @param {Function} onChange - Fonction de rappel pour gérer les changements de sélection.
 * @param {string} name - Nom du composant de sélection.
 * @param {list} listIdCodeLibelle - Liste ayant des objets id-Libellé.
 * @param {IdLibelleType} value - Value sélectionnée par défaut.
 * @param {string} label - Label du select.
 * @param {boolean} required - Valeur obligatoire ?
 * @param {Function} setValues - Permet de mettre à jour la valeur du select
 * @param {Function} setFieldValue - Permet de mettre à jour individuellement la valeur du select
 * @param {Function} setOtherValues - Permet de mettre à jour une / des autres valeurs du formulaire
 */
const SelectForm = ({
  name,
  listIdCodeLibelle,
  defaultValue,
  label,
  required = false,
  disabled = false,
  onChange: onChangeCustom,
  optionDisabled,
  setValues,
  setFieldValue,
  setOtherValues,
}: SelectFormType) => {
  const onChange = ({ name, value }: { name: string; value: string }) => {
    setValues != null &&
      setValues((prevValues: any) => ({
        ...prevValues,
        [name]: value,
      }));
    setFieldValue != null && setFieldValue(name, value);
    setOtherValues != null && setOtherValues();
  };
  const list = listIdCodeLibelle ?? [];
  return (
    <SelectInput
      name={name}
      label={label}
      required={required}
      disabled={disabled}
      options={list}
      getOptionLabel={(option) => option.libelle}
      getOptionValue={(option) => option.id ?? ""}
      defaultValue={defaultValue}
      onChange={(e) => {
        onChangeCustom
          ? onChangeCustom(e)
          : onChange({ name: name, value: e.id });
      }}
      noOptionsMessage={optionDisabled}
    />
  );
};
export default SelectForm;

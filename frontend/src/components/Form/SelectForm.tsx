import { Form } from "react-bootstrap";
import { useField } from "formik";
import { SelectFormType } from "../../utils/typeUtils.tsx";
import { DivWithError, FormLabel } from "./Form.tsx";
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
  const [, meta] = useField(name);
  const error = meta.touched ? meta.error : null;
  const onChange = ({ name, value }) => {
    setValues != null &&
      setValues((prevValues) => ({
        ...prevValues,
        [name]: value,
      }));
    setFieldValue != null && setFieldValue(name, value);
    setOtherValues != null && setOtherValues();
  };
  const list = listIdCodeLibelle ?? [];
  return (
    <DivWithError name={name} error={error}>
      {label && <FormLabel label={label} required={required} />}
      <Form.Select
        name={name}
        disabled={disabled}
        required={required}
        onChange={(e) => {
          onChangeCustom
            ? onChangeCustom(e)
            : onChange({ name: name, value: e.target.value });
        }}
      >
        <option value={""}>
          {disabled ? optionDisabled : "Aucune valeur saisie"}
        </option>
        {list.map((e, key) => (
          <option key={key} value={e.id} selected={defaultValue?.id === e.id}>
            {e.libelle}
          </option>
        ))}
      </Form.Select>
    </DivWithError>
  );
};
export default SelectForm;

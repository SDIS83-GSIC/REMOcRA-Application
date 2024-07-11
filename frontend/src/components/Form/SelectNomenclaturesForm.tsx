import ensureDataCache from "../../utils/ensureData.tsx";
import {
  IdCodeLibelleType,
  SelectNomenclaturesFormType,
} from "../../utils/typeUtils.tsx";
import SelectForm from "./SelectForm.tsx";

const SelectNomenclaturesForm = ({
  onChange,
  name,
  valueId,
  label,
  required = false,
  disabled = false,
  setValues,
  nomenclature,
}: SelectNomenclaturesFormType) => {
  const list = ensureDataCache(nomenclature);
  return (
    list && (
      <SelectForm
        name={name}
        listIdCodeLibelle={list}
        label={label}
        defaultValue={list.find((e: IdCodeLibelleType) => e.id === valueId)}
        required={required}
        disabled={disabled}
        onChange={onChange}
        setValues={setValues}
      />
    )
  );
};

export default SelectNomenclaturesForm;

import url from "../../module/fetch.tsx";
import {
  IdCodeLibelleType,
  SelectNomenclaturesFormType,
} from "../../utils/typeUtils.tsx";
import { useGet } from "../Fetch/useFetch.tsx";
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
  setOtherValues,
}: SelectNomenclaturesFormType) => {
  const response = useGet(url`/api/nomenclatures/list/` + nomenclature);
  return (
    response.data && (
      <SelectForm
        name={name}
        listIdCodeLibelle={response.data}
        label={label}
        defaultValue={response.data.find(
          (e: IdCodeLibelleType) => e.id === valueId,
        )}
        required={required}
        disabled={disabled}
        onChange={onChange}
        setValues={setValues}
        setOtherValues={setOtherValues}
      />
    )
  );
};

export default SelectNomenclaturesForm;

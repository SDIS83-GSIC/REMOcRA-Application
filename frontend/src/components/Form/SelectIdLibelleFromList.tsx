import ReactSelect from "react-select";
import { SelectFilterFromListType } from "../../utils/typeUtils.tsx";

const SelectIdLibelleDataFromList = ({
  onChange,
  name,
  listIdLibelle,
}: SelectFilterFromListType) => {
  const defaultValue = {
    id: undefined,
    code: undefined,
    libelle: "Tous",
  };

  const data = [defaultValue].concat(listIdLibelle);
  return (
    <ReactSelect
      placeholder={"Sélectionnez"}
      name={name}
      options={data}
      getOptionValue={(t) => t.id}
      getOptionLabel={(t) => t.libelle}
      onChange={(data) => {
        onChange({ name: name, value: data.id });
      }}
    />
  );
};
export default SelectIdLibelleDataFromList;

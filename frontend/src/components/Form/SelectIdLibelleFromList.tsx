import { Typeahead } from "react-bootstrap-typeahead";
import { SelectIdLibelleDataFromListType } from "../../utils/typeUtils.tsx";

const SelectIdLibelleDataFromList = ({
  onChange,
  name,
  listIdLibelle,
}: SelectIdLibelleDataFromListType) => {
  const defaultValue = {
    id: undefined,
    code: undefined,
    libelle: "Tous",
  };

  const data = [defaultValue].concat(listIdLibelle);
  return (
    <Typeahead
      placeholder={"Sélectionnez..."}
      size={"sm"}
      clearButton
      options={data}
      labelKey={"libelle"}
      onChange={(data) => {
        onChange({ name: name, value: data[0]?.id });
      }}
    />
  );
};
export default SelectIdLibelleDataFromList;

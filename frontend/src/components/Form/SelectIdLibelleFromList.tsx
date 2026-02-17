import ReactSelect from "react-select";
import {
  IdCodeLibelleType,
  SelectFilterFromListType,
} from "../../utils/typeUtils.tsx";

const SelectIdLibelleDataFromList = ({
  onChange,
  name,
  listIdCodeLibelle,
  value,
}: SelectFilterFromListType) => {
  const defaultValue: IdCodeLibelleType = {
    id: undefined,
    code: undefined,
    libelle: "Tous",
  };

  const data = [defaultValue].concat(
    (listIdCodeLibelle || []).map((item) => ({
      id: item.id,
      code: item.code ?? item.id,
      libelle: item.libelle,
    })),
  );
  return (
    <ReactSelect
      placeholder={"Sélectionnez"}
      noOptionsMessage={() => "Aucune donnée trouvée"}
      name={name}
      options={data}
      value={data.find((e) => e?.id === value)}
      getOptionValue={(t) => t.id}
      getOptionLabel={(t) => t.libelle}
      onChange={(selected) => {
        onChange({ name, value: selected?.id });
      }}
    />
  );
};
export default SelectIdLibelleDataFromList;

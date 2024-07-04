import { ReactNode } from "react";

const FilterAnomalies = ({
  type,
  name,
  onChange: onChangeCallback,
}: FilterInputType): ReactNode => {
  return (
    <FilterAnomalies type={type} name={name} onChange={onChangeCallback} />
  );
};
type FilterInputType = { type: string; name: string; onChange: any };

export default FilterAnomalies;

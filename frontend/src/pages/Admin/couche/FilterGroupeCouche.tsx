type FilterGroupeCoucheType = {
  groupeCoucheCode?: string;
  groupeCoucheLibelle?: string;
  groupeCoucheProtected?: string;
};

const filterValuesToVariable = ({
  groupeCoucheCode,
  groupeCoucheLibelle,
  groupeCoucheProtected,
}: FilterGroupeCoucheType) => {
  const filter: FilterGroupeCoucheType = {};

  filterPropertyStyle(filter, groupeCoucheCode, "groupeCoucheCode");
  filterPropertyStyle(filter, groupeCoucheLibelle, "groupeCoucheLibelle");
  filterPropertyStyle(filter, groupeCoucheProtected, "groupeCoucheProtected");

  return filter;
};

export default filterValuesToVariable;

function filterPropertyStyle(
  filter: FilterGroupeCoucheType,
  value: string | undefined,
  name: string,
) {
  if (value != null && value?.trim().length > 0) {
    filter[name] = value;
  }
}

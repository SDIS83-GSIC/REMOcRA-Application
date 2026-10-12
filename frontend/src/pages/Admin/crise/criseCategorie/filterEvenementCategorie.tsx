type FilterType = {
  evenementCategorieCode?: string;
  evenementCategorieLibelle?: string;
  evenementCategorieTypeCriseId?: string;
  [key: string]: string | undefined;
};

const filterValuesToVariable = ({
  evenementCategorieCode,
  evenementCategorieLibelle,
  evenementCategorieTypeCriseId,
}: FilterType) => {
  const filter: FilterType = {};

  filterProperty(filter, evenementCategorieCode, "evenementCategorieCode");
  filterProperty(
    filter,
    evenementCategorieLibelle,
    "evenementCategorieLibelle",
  );
  filterProperty(
    filter,
    evenementCategorieTypeCriseId,
    "evenementCategorieTypeCriseId",
  );

  return filter;
};

export default filterValuesToVariable;

function filterProperty(
  filter: FilterType,
  value: string | undefined,
  name: string,
) {
  if (value && value.trim().length > 0) {
    filter[name] = value;
  }
}

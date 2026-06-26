type FilterDfciConflitType = {
  dfciConflitTable?: string;
  dfciConflitElementId?: string;
  dfciConflitChamp?: string;
};

export const FilterDfciConflitProperty = ({
  dfciConflitTable,
  dfciConflitElementId,
  dfciConflitChamp,
}: FilterDfciConflitType) => {
  const filter: FilterDfciConflitType = {};

  filterPropertyDfciConflit(filter, dfciConflitTable, "dfciConflitTable");
  filterPropertyDfciConflit(
    filter,
    dfciConflitElementId,
    "dfciConflitElementId",
  );
  filterPropertyDfciConflit(filter, dfciConflitChamp, "dfciConflitChamp");

  return filter;
};

function filterPropertyDfciConflit(
  filter: FilterDfciConflitType,
  value: string | undefined,
  name: string,
) {
  if (value?.trim().length > 0) {
    filter[name] = value;
  }
}

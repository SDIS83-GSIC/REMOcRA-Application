type FilterStyleType = {
  groupeCoucheLibelle?: string;
  coucheLibelle?: string;
  groupeFonctionnaliteList?: string;
  coucheStyleActif?: string;
};

const filterValuesToVariable = ({
  groupeCoucheLibelle,
  coucheLibelle,
  groupeFonctionnaliteList,
  coucheStyleActif,
}: FilterStyleType) => {
  const filter: FilterStyleType = {};

  filterPropertyStyle(filter, groupeCoucheLibelle, "groupeCoucheLibelle");
  filterPropertyStyle(filter, coucheLibelle, "coucheLibelle");
  filterPropertyStyle(
    filter,
    groupeFonctionnaliteList,
    "groupeFonctionnaliteList",
  );
  filterPropertyStyle(filter, coucheStyleActif, "coucheStyleActif");

  return filter;
};

export default filterValuesToVariable;

function filterPropertyStyle(
  filter: FilterStyleType,
  value: string | undefined,
  name: string,
) {
  if (value != null && value?.trim().length > 0) {
    filter[name] = value;
  }
}

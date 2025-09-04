type FilterStyleType = {
  groupeCoucheLibelle?: string;
  coucheLibelle?: string;
  profilDroitList?: string;
  coucheStyleActif?: string;
};

const filterValuesToVariable = ({
  groupeCoucheLibelle,
  coucheLibelle,
  profilDroitList,
  coucheStyleActif,
}: FilterStyleType) => {
  const filter: FilterStyleType = {};

  filterPropertyStyle(filter, groupeCoucheLibelle, "groupeCoucheLibelle");
  filterPropertyStyle(filter, coucheLibelle, "coucheLibelle");
  filterPropertyStyle(filter, profilDroitList, "profilDroitList");
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

type FilterNatureT = {
  natureActif?: string | undefined;
  natureCode?: string | undefined;
  natureLibelle?: string | undefined;
  diametreId?: string | undefined;
  natureTypePei?: string | undefined;
  natureProtected?: string | undefined;
};

const filterValuesNature = ({
  natureActif,
  natureCode,
  natureLibelle,
  diametreId,
  natureTypePei,
  natureProtected,
}: FilterNatureT) => {
  const filter: FilterNatureT = {};
  filterPropertyNature(filter, natureActif, "natureActif");
  filterPropertyNature(filter, natureCode, "natureCode");
  filterPropertyNature(filter, natureLibelle, "natureLibelle");
  filterPropertyNature(filter, diametreId, "diametreId");
  filterPropertyNature(filter, natureTypePei, "natureTypePei");
  filterPropertyNature(filter, natureProtected, "natureProtected");
  return filter;
};

function filterPropertyNature(
  filter: FilterNatureT,
  value: string | undefined,
  name: string,
) {
  if (value != null && value.trim() !== "") {
    filter[name as keyof FilterNatureT] = value;
  }
}

export default filterValuesNature;

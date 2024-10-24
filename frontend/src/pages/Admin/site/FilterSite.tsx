type FilterNomenclatureType = {
  siteActif?: boolean | undefined;
  siteCode?: string | undefined;
  siteLibelle?: string | undefined;
  siteGestionnaireId?: string | undefined;
};

const FilterValues = ({
  siteCode,
  siteLibelle,
  siteGestionnaireId,
  siteActif,
}: FilterNomenclatureType) => {
  const filter: FilterNomenclatureType = {};

  filterProperty(filter, siteCode, "siteCode");
  filterProperty(filter, siteLibelle, "siteLibelle");
  filterProperty(filter, siteActif, "siteActif");
  filterProperty(filter, siteGestionnaireId, "siteGestionnaireId");

  return filter;
};

function filterProperty(
  filter: FilterNomenclatureType,
  value: string | undefined,
  name: string,
) {
  if (value != null && value.trim() !== "") {
    filter[name] = value;
  }
}

export default FilterValues;

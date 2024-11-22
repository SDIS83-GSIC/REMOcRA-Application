type FilterNomenclatureType = {
  zoneIntegrationActif?: boolean | undefined;
  zoneIntegrationCode?: string | undefined;
  zoneIntegrationLibelle?: string | undefined;
};

const FilterValues = ({
  zoneIntegrationCode,
  zoneIntegrationLibelle,
  zoneIntegrationActif,
}: FilterNomenclatureType) => {
  const filter: FilterNomenclatureType = {};

  filterProperty(filter, zoneIntegrationCode, "zoneIntegrationCode");
  filterProperty(filter, zoneIntegrationLibelle, "zoneIntegrationLibelle");
  filterProperty(filter, zoneIntegrationActif, "zoneIntegrationActif");

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

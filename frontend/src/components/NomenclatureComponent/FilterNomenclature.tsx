type FilterNomenclatureType = {
  actif?: string | undefined;
  code?: string | undefined;
  libelle?: string | undefined;
  protected1?: boolean | undefined;
};

const FilterValues = ({
  code,
  libelle,
  protected,
  actif,
}: FilterNomenclatureType) => {
  const filter: FilterNomenclatureType = {};

  filterProperty(filter, code, "code");
  filterProperty(filter, libelle, "libelle");
  filterProperty(filter, actif, "actif");
  filterProperty(filter, protected, "protected");

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

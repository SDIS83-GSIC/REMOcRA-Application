type FilterNomenclatureType = {
  actif?: boolean | undefined;
  code?: string | undefined;
  libelle?: string | undefined;
  protected1?: boolean | undefined;
  idFk?: string | undefined;
};

const FilterValues = ({
  code,
  libelle,
  protected: isProtected,
  actif,
  idFk,
}: FilterNomenclatureType) => {
  const filter: FilterNomenclatureType = {};

  filterProperty(filter, code, "code");
  filterProperty(filter, libelle, "libelle");
  filterProperty(filter, actif, "actif");
  filterProperty(filter, isProtected, "protected");
  filterProperty(filter, idFk, "idFk");

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

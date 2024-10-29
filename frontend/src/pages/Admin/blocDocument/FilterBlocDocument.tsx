type FilterBlocDocumentType = {
  blocDocumentLibelle?: string | undefined;
  listThematiqueId?: string[] | undefined;
  listProfilDroitId?: string[] | undefined;
};

const FilterValues = ({
  blocDocumentLibelle,
  listThematiqueId,
  listProfilDroitId,
}: FilterBlocDocumentType) => {
  const filter: FilterBlocDocumentType = {};

  filterProperty(filter, blocDocumentLibelle, "blocDocumentLibelle");

  if (listThematiqueId !== null && listThematiqueId?.length > 0) {
    filter.listThematiqueId = listThematiqueId;
  }
  if (listProfilDroitId !== null && listProfilDroitId?.length > 0) {
    filter.listProfilDroitId = listProfilDroitId;
  }

  return filter;
};

function filterProperty(
  filter: FilterBlocDocumentType,
  value: string | undefined,
  name: string,
) {
  if (value != null && value.trim() !== "") {
    filter[name] = value;
  }
}

export default FilterValues;

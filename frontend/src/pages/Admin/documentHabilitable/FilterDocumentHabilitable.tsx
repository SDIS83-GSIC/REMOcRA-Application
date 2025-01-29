type FilterDocumentHabilitableType = {
  documentHabilitableLibelle?: string | undefined;
  listThematiqueId?: string[] | undefined;
  listProfilDroitId?: string[] | undefined;
};

const FilterValues = ({
  documentHabilitableLibelle,
  listThematiqueId,
  listProfilDroitId,
}: FilterDocumentHabilitableType) => {
  const filter: FilterDocumentHabilitableType = {};

  filterProperty(
    filter,
    documentHabilitableLibelle,
    "documentHabilitableLibelle",
  );

  if (listThematiqueId !== null && listThematiqueId?.length > 0) {
    filter.listThematiqueId = listThematiqueId;
  }
  if (listProfilDroitId !== null && listProfilDroitId?.length > 0) {
    filter.listProfilDroitId = listProfilDroitId;
  }

  return filter;
};

function filterProperty(
  filter: FilterDocumentHabilitableType,
  value: string | undefined,
  name: string,
) {
  if (value != null && value.trim() !== "") {
    filter[name] = value;
  }
}

export default FilterValues;

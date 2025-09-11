type FilterDocumentHabilitableType = {
  documentHabilitableLibelle?: string | undefined;
  listThematiqueId?: string[] | undefined;
  listGroupeFonctionnalitesId?: string[] | undefined;
};

const FilterValues = ({
  documentHabilitableLibelle,
  listThematiqueId,
  listGroupeFonctionnalitesId,
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
  if (
    listGroupeFonctionnalitesId !== null &&
    listGroupeFonctionnalitesId?.length > 0
  ) {
    filter.listGroupeFonctionnalitesId = listGroupeFonctionnalitesId;
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

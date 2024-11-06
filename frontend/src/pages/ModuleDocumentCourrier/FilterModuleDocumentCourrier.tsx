type FilterModuleDocumentType = {
  libelle?: string | undefined;
};

const FilterValues = ({ libelle }: FilterModuleDocumentType) => {
  const filter: FilterModuleDocumentType = {};

  filterProperty(filter, libelle, "libelle");

  return filter;
};

function filterProperty(
  filter: FilterModuleDocumentType,
  value: string | undefined,
  name: string,
) {
  if (value != null && value.trim() !== "") {
    filter[name] = value;
  }
}

export default FilterValues;

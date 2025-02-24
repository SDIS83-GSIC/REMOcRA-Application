type FilterCriseDocumentType = {
  documentDate?: string;
  documentNomFichier?: string;
  type?: string;
};

const filterValuesToVariable = ({
  documentDate,
  type,
  documentNomFichier,
}: FilterCriseDocumentType) => {
  const filter: FilterCriseDocumentType = {};

  filterPropertyCrise(filter, documentDate, "documentDate");
  filterPropertyCrise(filter, type, "type");
  filterPropertyCrise(filter, documentNomFichier, "documentNomFichier");

  return filter;
};

export default filterValuesToVariable;

function filterPropertyCrise(
  filter: FilterCriseDocumentType,
  value: string | undefined,
  name: string,
) {
  if (value?.trim().length > 0) {
    filter[name] = value;
  }
}

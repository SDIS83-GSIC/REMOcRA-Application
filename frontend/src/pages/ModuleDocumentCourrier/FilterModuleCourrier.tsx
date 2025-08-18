type FilterModuleCourrierType = {
  courrierObjet?: string | undefined;
  courrierReference?: string | undefined;
  courrierExpediteur?: string | undefined;
  emailDestinataire?: string | undefined;
  accuse?: string | undefined;
  documentDate?: string | undefined;
};

const FilterValues = ({
  courrierObjet,
  courrierReference,
  courrierExpediteur,
  emailDestinataire,
  accuse,
  documentDate,
}: FilterModuleCourrierType) => {
  const filter: FilterModuleCourrierType = {};

  filterProperty(filter, courrierObjet, "courrierObjet");
  filterProperty(filter, courrierReference, "courrierReference");
  filterProperty(filter, courrierExpediteur, "courrierExpediteur");
  filterProperty(filter, accuse, "accuse");
  filterProperty(filter, documentDate, "documentDate");

  if (emailDestinataire && emailDestinataire?.length > 0) {
    filter.emailDestinataire = emailDestinataire;
  }

  return filter;
};

function filterProperty(
  filter: FilterModuleCourrierType,
  value: string | undefined,
  name: string,
) {
  if (value != null && value.trim() !== "") {
    filter[name] = value;
  }
}

export default FilterValues;

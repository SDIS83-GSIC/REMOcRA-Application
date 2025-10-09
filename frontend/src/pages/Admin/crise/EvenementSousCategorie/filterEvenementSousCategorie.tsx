type FilterType = {
  evenementSousCategorieCode?: string;
  evenementSousCategorieTypeGeometrie?: string;
  evenementSousCategorieLibelle?: string;
  evenementSousCategorieActif?: string;
  evenementCategorieId?: string;
};

const filterValuesToVariable = ({
  evenementSousCategorieCode,
  evenementSousCategorieTypeGeometrie,
  evenementSousCategorieLibelle,
  evenementSousCategorieActif,
  evenementCategorieId,
}: FilterType) => {
  const filter: FilterType = {};

  filterProperty(
    filter,
    evenementSousCategorieCode,
    "evenementSousCategorieCode",
  );
  filterProperty(
    filter,
    evenementSousCategorieTypeGeometrie,
    "evenementSousCategorieTypeGeometrie",
  );
  filterProperty(
    filter,
    evenementSousCategorieLibelle,
    "evenementSousCategorieLibelle",
  );
  filterProperty(
    filter,
    evenementSousCategorieActif,
    "evenementSousCategorieActif",
  );
  filterProperty(filter, evenementCategorieId, "evenementCategorieId");

  return filter;
};

export default filterValuesToVariable;

function filterProperty(
  filter: FilterType,
  value: string | undefined,
  name: string,
) {
  if (value && value.trim().length > 0) {
    filter[name] = value;
  }
}

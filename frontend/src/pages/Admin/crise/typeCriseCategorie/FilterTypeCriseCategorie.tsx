import TYPE_GEOMETRIE from "../../../../enums/TypeGeometrie.tsx";

type FilterEvenementSousCategorieType = {
  evenementSousCategorieCode?: string | undefined;
  evenementSousCategorieLibelle?: string | undefined;
  evenementSousCategorieTypeGeometrie?: TYPE_GEOMETRIE | undefined;
  evenementCategorieLibelle?: string | undefined;
};

const filterValuesEvenementSousCategorie = ({
  evenementSousCategorieCode: evenementSousCategorieCode,
  evenementSousCategorieLibelle: evenementSousCategorieLibelle,
  evenementSousCategorieTypeGeometrie: evenementSousCategorieTypeGeomegtrie,
  evenementCategorieLibelle: evenementCategorieLibelle,
}: FilterEvenementSousCategorieType) => {
  const filter: FilterEvenementSousCategorieType = {};
  filterPropertyTypeEvenementCategorie(
    filter,
    evenementSousCategorieCode,
    "evenementSousCategorieCode",
  );
  filterPropertyTypeEvenementCategorie(
    filter,
    evenementSousCategorieLibelle,
    "evenementSousCategorieLibelle",
  );
  filterPropertyTypeEvenementCategorie(
    filter,
    evenementSousCategorieTypeGeomegtrie,
    "evenementSousCategorieTypeGeometrie",
  );
  filterPropertyTypeEvenementCategorie(
    filter,
    evenementCategorieLibelle,
    "evenementCategorieLibelle",
  );
  return filter;
};

function filterPropertyTypeEvenementCategorie(
  filter: FilterEvenementSousCategorieType,
  value: string | undefined,
  name: string,
) {
  if (value != null && value.trim() !== "") {
    filter[name as keyof FilterEvenementSousCategorieType] = value;
  }
}

export default filterValuesEvenementSousCategorie;

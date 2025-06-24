import TYPE_GEOMETRIE from "../../../../enums/TypeGeometrie.tsx";

type FilterTypeCriseCategorieType = {
  typeCriseCategorieCode?: string | undefined;
  typeCriseCategorieLibelle?: string | undefined;
  typeCriseCategorieTypeGeometrie?: TYPE_GEOMETRIE | undefined;
  criseCategorieLibelle?: string | undefined;
};

const filterValuesTypeCriseCategorie = ({
  typeCriseCategorieCode,
  typeCriseCategorieLibelle,
  typeCriseCategorieTypeGeometrie,
  criseCategorieLibelle,
}: FilterTypeCriseCategorieType) => {
  const filter: FilterTypeCriseCategorieType = {};
  filterPropertyTypeCriseCategorie(
    filter,
    typeCriseCategorieCode,
    "typeCriseCategorieCode",
  );
  filterPropertyTypeCriseCategorie(
    filter,
    typeCriseCategorieLibelle,
    "typeCriseCategorieLibelle",
  );
  filterPropertyTypeCriseCategorie(
    filter,
    typeCriseCategorieTypeGeometrie,
    "typeCriseCategorieTypeGeometrie",
  );
  filterPropertyTypeCriseCategorie(
    filter,
    criseCategorieLibelle,
    "criseCategorieLibelle",
  );
  return filter;
};

function filterPropertyTypeCriseCategorie(
  filter: FilterTypeCriseCategorieType,
  value: string | undefined,
  name: string,
) {
  if (value != null && value.trim() !== "") {
    filter[name as keyof FilterTypeCriseCategorieType] = value;
  }
}

export default filterValuesTypeCriseCategorie;

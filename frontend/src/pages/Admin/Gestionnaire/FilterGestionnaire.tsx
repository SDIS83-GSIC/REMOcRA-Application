type FilterGestionnaireType = {
  gestionnaireActif?: boolean | undefined;
  gestionnaireCode?: string | undefined;
  gestionnaireLibelle?: string | undefined;
};

const FilterValues = ({
  gestionnaireCode,
  gestionnaireLibelle,
  gestionnaireActif,
}: FilterGestionnaireType) => {
  const filter: FilterGestionnaireType = {};

  filterProperty(filter, gestionnaireCode, "gestionnaireCode");
  filterProperty(filter, gestionnaireLibelle, "gestionnaireLibelle");
  filterProperty(filter, gestionnaireActif, "gestionnaireActif");

  return filter;
};

function filterProperty(
  filter: FilterGestionnaireType,
  value: string | undefined,
  name: string,
) {
  if (value != null && value.trim() !== "") {
    filter[name] = value;
  }
}

export default FilterValues;

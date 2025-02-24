type FilterCriseType = {
  typeCriseLibelle?: string;
  criseLibelle?: string;
  criseDescription?: string;
  criseStatutType?: string;
  criseDateDebut?: string;
  criseDateFin?: string;
};

const filterValuesToVariable = ({
  typeCriseLibelle,
  criseLibelle,
  criseDescription,
  criseStatutType,
  criseDateDebut,
  criseDateFin,
}: FilterCriseType) => {
  const filter: FilterCriseType = {};

  filterPropertyCrise(filter, typeCriseLibelle, "typeCriseLibelle");
  filterPropertyCrise(filter, criseLibelle, "criseLibelle");
  filterPropertyCrise(filter, criseDescription, "criseDescription");
  filterPropertyCrise(filter, criseStatutType, "criseStatutType");
  filterPropertyCrise(filter, criseDateDebut, "criseDateDebut");
  filterPropertyCrise(filter, criseDateFin, "criseDateFin");

  return filter;
};

export default filterValuesToVariable;

function filterPropertyCrise(
  filter: FilterCriseType,
  value: string | undefined,
  name: string,
) {
  if (value?.trim().length > 0) {
    filter[name] = value;
  }
}

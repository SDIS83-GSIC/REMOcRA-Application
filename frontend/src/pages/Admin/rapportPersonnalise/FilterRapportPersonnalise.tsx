type FilterRapportPersonnaliseType = {
  rapportPersonnaliseActif?: string | undefined;
  rapportPersonnaliseCode?: string | undefined;
  rapportPersonnaliseLibelle?: string | undefined;
  rapportPersonnaliseProtected?: string | undefined;
  rapportPersonnaliseChampGeometrie?: string | undefined;
  listeGroupeFonctionnalitesId?: string[] | undefined;
  rapportPersonnaliseModule?: string | undefined;
};

const filterValuesToVariable = ({
  rapportPersonnaliseActif,
  rapportPersonnaliseCode,
  rapportPersonnaliseLibelle,
  rapportPersonnaliseProtected,
  rapportPersonnaliseChampGeometrie,
  listeGroupeFonctionnalitesId,
  rapportPersonnaliseModule,
}: FilterRapportPersonnaliseType) => {
  const filter: FilterRapportPersonnaliseType = {};

  filterProperty(filter, rapportPersonnaliseCode, "rapportPersonnaliseCode");
  filterProperty(filter, rapportPersonnaliseActif, "rapportPersonnaliseActif");
  filterProperty(
    filter,
    rapportPersonnaliseLibelle,
    "rapportPersonnaliseLibelle",
  );
  filterProperty(
    filter,
    rapportPersonnaliseProtected,
    "rapportPersonnaliseProtected",
  );
  filterProperty(
    filter,
    rapportPersonnaliseChampGeometrie,
    "rapportPersonnaliseChampGeometrie",
  );
  filterProperty(
    filter,
    rapportPersonnaliseModule,
    "rapportPersonnaliseModule",
  );

  if (listeGroupeFonctionnalitesId?.length > 0) {
    filter.listeGroupeFonctionnalitesId = listeGroupeFonctionnalitesId;
  }

  return filter;
};

export default filterValuesToVariable;

function filterProperty(
  filter: FilterRapportPersonnaliseType,
  value: string | undefined,
  name: string,
) {
  if (value?.trim() !== "") {
    filter[name] = value;
  }
}

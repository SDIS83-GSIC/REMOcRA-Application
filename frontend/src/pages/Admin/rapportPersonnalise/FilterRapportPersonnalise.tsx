type FilterRapportPersonnaliseType = {
  rapportPersonnaliseActif?: string | undefined;
  rapportPersonnaliseCode?: string | undefined;
  rapportPersonnaliseLibelle?: string | undefined;
  rapportPersonnaliseProtected?: string | undefined;
  rapportPersonnaliseChampGeometrie?: string | undefined;
  listeProfilDroitId?: string[] | undefined;
  rapportPersonnaliseModule?: string | undefined;
};

const filterValuesToVariable = ({
  rapportPersonnaliseActif,
  rapportPersonnaliseCode,
  rapportPersonnaliseLibelle,
  rapportPersonnaliseProtected,
  rapportPersonnaliseChampGeometrie,
  listeProfilDroitId,
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

  if (listeProfilDroitId?.length > 0) {
    filter.listeProfilDroitId = listeProfilDroitId;
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

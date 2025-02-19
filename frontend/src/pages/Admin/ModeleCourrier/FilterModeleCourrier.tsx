type FilterModeleCourrierType = {
  modeleCourrierActif?: string | undefined;
  modeleCourrierCode?: string | undefined;
  modeleCourrierLibelle?: string | undefined;
  modeleCourrierProtected?: string | undefined;
  listeProfilDroitId?: string[] | undefined;
  modeleCourrierModule?: string | undefined;
};

const filterValuesToVariable = ({
  modeleCourrierActif,
  modeleCourrierCode,
  modeleCourrierLibelle,
  modeleCourrierProtected,
  listeProfilDroitId,
  modeleCourrierModule,
}: FilterModeleCourrierType) => {
  const filter: FilterModeleCourrierType = {};

  filterProperty(filter, modeleCourrierCode, "modeleCourrierCode");
  filterProperty(filter, modeleCourrierActif, "modeleCourrierActif");
  filterProperty(filter, modeleCourrierLibelle, "modeleCourrierLibelle");
  filterProperty(filter, modeleCourrierProtected, "modeleCourrierProtected");
  filterProperty(filter, modeleCourrierModule, "modeleCourrierModule");

  if (listeProfilDroitId?.length > 0) {
    filter.listeProfilDroitId = listeProfilDroitId;
  }

  return filter;
};

export default filterValuesToVariable;

function filterProperty(
  filter: FilterModeleCourrierType,
  value: string | undefined,
  name: string,
) {
  if (value?.trim() !== "") {
    filter[name] = value;
  }
}

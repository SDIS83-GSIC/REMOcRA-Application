type FilterOldebProprietaireType = {
  oldebProprietaireNom?: string;
  oldebProprietairePrenom?: string;
  oldebProprietaireVille?: string;
};

const filterValuesToVariable = ({
  oldebProprietaireNom,
  oldebProprietairePrenom,
  oldebProprietaireVille,
}: FilterOldebProprietaireType) => {
  const filter: FilterOldebProprietaireType = {};

  pushFilterItem(filter, oldebProprietaireNom, "oldebProprietaireNom");
  pushFilterItem(filter, oldebProprietairePrenom, "oldebProprietairePrenom");
  pushFilterItem(filter, oldebProprietaireVille, "oldebProprietaireVille");

  return filter;
};

export default filterValuesToVariable;

function pushFilterItem(
  filter: FilterOldebProprietaireType,
  value: string | undefined,
  name: string,
) {
  if (value?.trim().length > 0) {
    filter[name] = value;
  }
}

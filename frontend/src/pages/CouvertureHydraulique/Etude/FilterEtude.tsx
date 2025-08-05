type FilterEtudeType = {
  typeEtudeId?: string | undefined;
  etudeNumero?: string | undefined;
  etudeLibelle?: string | undefined;
  etudeDescription?: string | undefined;
  etudeStatut?: string | undefined;
};

const filterValuesToVariable = ({
  typeEtudeId,
  etudeNumero,
  etudeLibelle,
  etudeDescription,
  etudeStatut,
}: FilterEtudeType) => {
  const filter: FilterEtudeType = {};

  filterPropertyEtude(filter, typeEtudeId, "typeEtudeId");
  filterPropertyEtude(filter, etudeNumero, "etudeNumero");
  filterPropertyEtude(filter, etudeLibelle, "etudeLibelle");
  filterPropertyEtude(filter, etudeDescription, "etudeDescription");
  filterPropertyEtude(filter, etudeStatut, "etudeStatut");

  return filter;
};

export default filterValuesToVariable;

function filterPropertyEtude(
  filter: FilterEtudeType,
  value: string | undefined,
  name: keyof FilterEtudeType,
) {
  if (value != null && value.trim() !== "") {
    filter[name] = value;
  }
}

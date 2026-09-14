type filterDestinataire = {
  emailDestinataire?: string;
  listeTypeDestinataire?: string;
  nomDestinataire?: string;
  fonctionDestinataire?: string;
  useZoneCompetence?: boolean;
};

export const filterValuesToVariable = ({
  emailDestinataire,
  listeTypeDestinataire,
  nomDestinataire,
  fonctionDestinataire,
  useZoneCompetence,
}: filterDestinataire) => {
  const filter: filterDestinataire = {};
  filterProperty(filter, emailDestinataire, "emailDestinataire");
  filterProperty(filter, nomDestinataire, "nomDestinataire");
  filterProperty(filter, fonctionDestinataire, "fonctionDestinataire");
  if (useZoneCompetence != null) {
    filter.useZoneCompetence = useZoneCompetence;
  }
  if (
    Array.isArray(listeTypeDestinataire) &&
    listeTypeDestinataire.length > 0
  ) {
    filter.listeTypeDestinataire = listeTypeDestinataire;
  }
  return filter;
};

function filterProperty(
  filter: filterDestinataire,
  value: string | undefined,
  name: string,
) {
  if (value != null && value.trim() !== "") {
    filter[name] = value;
  }
}

export default filterValuesToVariable;

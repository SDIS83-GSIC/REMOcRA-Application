type filterDestinataire = {
  emailDestinataire?: string;
  listeTypeDestinataire?: string;
  nomDestinataire?: string;
  fonctionDestinataire?: string;
};

export const filterValuesToVariable = ({
  emailDestinataire,
  listeTypeDestinataire,
  nomDestinataire,
  fonctionDestinataire,
}: filterDestinataire) => {
  const filter: filterDestinataire = {};
  filterProperty(filter, emailDestinataire, "emailDestinataire");
  filterProperty(filter, nomDestinataire, "nomDestinataire");
  filterProperty(filter, fonctionDestinataire, "fonctionDestinataire");
  if (listeTypeDestinataire !== null && listeTypeDestinataire?.length > 0) {
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

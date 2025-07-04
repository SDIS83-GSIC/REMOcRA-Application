type FilterUtilisateurType = {
  utilisateurEmail?: string | undefined;
  utilisateurUsername?: string | undefined;
  utilisateurNom?: string | undefined;
  utilisateurPrenom?: string | undefined;
  utilisateurTelephone?: string | undefined;
  utilisateurCanBeNotified?: boolean | undefined;
  utilisateurOrganismeId?: string | undefined;
  utilisateurProfilUtilisateurId?: string | undefined;
  profilDroitId?: string | undefined;
  utilisateurActif?: boolean | undefined;
};

const FilterValues = ({
  utilisateurEmail,
  utilisateurUsername,
  utilisateurNom,
  utilisateurPrenom,
  utilisateurTelephone,
  utilisateurCanBeNotified,
  utilisateurOrganismeId,
  utilisateurProfilUtilisateurId,
  profilDroitId,
  utilisateurActif,
}: FilterUtilisateurType) => {
  const filter: FilterUtilisateurType = {};

  filterProperty(filter, utilisateurEmail, "utilisateurEmail");
  filterProperty(filter, utilisateurUsername, "utilisateurUsername");
  filterProperty(filter, utilisateurNom, "utilisateurNom");
  filterProperty(filter, utilisateurPrenom, "utilisateurPrenom");
  filterProperty(filter, utilisateurTelephone, "utilisateurTelephone");
  filterProperty(filter, utilisateurCanBeNotified, "utilisateurCanBeNotified");
  filterProperty(filter, utilisateurOrganismeId, "utilisateurOrganismeId");
  filterProperty(
    filter,
    utilisateurProfilUtilisateurId,
    "utilisateurProfilUtilisateurId",
  );
  filterProperty(filter, profilDroitId, "profilDroitId");
  filterProperty(filter, utilisateurActif, "utilisateurActif");

  return filter;
};

function filterProperty(
  filter: FilterUtilisateurType,
  value: string | undefined,
  name: string,
) {
  if (value != null && value.trim() !== "") {
    filter[name] = value;
  }
}

export default FilterValues;

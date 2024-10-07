type FilterOrganisme = {
  organismeActif?: string | undefined;
  organismeCode?: string | undefined;
  organismeLibelle?: string | undefined;
  organismeEmailContact?: string | undefined;
  typeOrganismeLibelle?: string | undefined;
  profilOrganismeLibelle?: string | undefined;
  zoneIntegrationLibelle?: string | undefined;
  parentLibelle?: string | undefined;
};

const filterValuesOrganisme = ({
  organismeActif,
  organismeCode,
  organismeLibelle,
  organismeEmailContact,
  typeOrganismeLibelle,
  profilOrganismeLibelle,
  zoneIntegrationLibelle,
  parentLibelle,
}: FilterOrganisme) => {
  const filter: FilterOrganisme = {};
  filterPropertyOrganisme(filter, organismeActif, "organismeActif");
  filterPropertyOrganisme(filter, organismeCode, "organismeCode");
  filterPropertyOrganisme(filter, organismeLibelle, "organismeLibelle");
  filterPropertyOrganisme(
    filter,
    organismeEmailContact,
    "organismeEmailContact",
  );
  filterPropertyOrganisme(filter, typeOrganismeLibelle, "typeOrganismeLibelle");
  filterPropertyOrganisme(
    filter,
    profilOrganismeLibelle,
    "profilOrganismeLibelle",
  );
  filterPropertyOrganisme(
    filter,
    zoneIntegrationLibelle,
    "zoneIntegrationLibelle",
  );
  filterPropertyOrganisme(filter, parentLibelle, "parentLibelle");
  return filter;
};

function filterPropertyOrganisme(
  filter: FilterOrganisme,
  value: string | undefined,
  name: string,
) {
  if (value != null && value.trim() !== "") {
    filter[name as keyof FilterOrganisme] = value;
  }
}

export default filterValuesOrganisme;

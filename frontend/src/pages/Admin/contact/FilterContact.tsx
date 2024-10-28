import TYPE_CIVILITE from "../../../enums/CiviliteEnum.tsx";

type FilterContactType = {
  contactActif?: boolean | undefined;
  contactCivilite?: TYPE_CIVILITE | undefined;
  contactNom?: string | undefined;
  contactPrenom?: string | undefined;
  fonctionContactLibelle: string | undefined;
  contactTelephone: string | undefined;
  contactEmail: string | undefined;
  siteLibelle: string | undefined;
};

const FilterValues = ({
  contactNom,
  contactCivilite,
  contactActif,
  contactPrenom,
  fonctionContactLibelle,
  contactTelephone,
  contactEmail,
  siteLibelle,
}: FilterContactType) => {
  const filter: FilterContactType = {};

  filterProperty(filter, contactNom, "contactNom");
  filterProperty(filter, contactCivilite, "contactCivilite");
  filterProperty(filter, contactActif, "contactActif");
  filterProperty(filter, contactPrenom, "contactPrenom");
  filterProperty(filter, fonctionContactLibelle, "fonctionContactLibelle");
  filterProperty(filter, contactTelephone, "contactTelephone");
  filterProperty(filter, siteLibelle, "siteLibelle");
  filterProperty(filter, contactEmail, "contactEmail");

  return filter;
};

function filterProperty(
  filter: FilterContactType,
  value: string | undefined,
  name: string,
) {
  if (value != null && value.trim() !== "") {
    filter[name] = value;
  }
}

export default FilterValues;

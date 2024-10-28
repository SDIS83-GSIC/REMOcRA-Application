import TYPE_CIVILITE from "../../../enums/CiviliteEnum.tsx";
import TYPE_FONCTION from "../../../enums/FonctionContactEnum.tsx";

type FilterContactType = {
  contactActif?: boolean | undefined;
  contactCivilite?: TYPE_CIVILITE | undefined;
  contactNom?: string | undefined;
  contactPrenom?: string | undefined;
  contactFonction: TYPE_FONCTION | undefined;
  contactTelephone: string | undefined;
  contactEmail: string | undefined;
  siteLibelle: string | undefined;
};

const FilterValues = ({
  contactNom,
  contactCivilite,
  contactActif,
  contactPrenom,
  contactFonction,
  contactTelephone,
  contactEmail,
  siteLibelle,
}: FilterContactType) => {
  const filter: FilterContactType = {};

  filterProperty(filter, contactNom, "contactNom");
  filterProperty(filter, contactCivilite, "contactCivilite");
  filterProperty(filter, contactActif, "contactActif");
  filterProperty(filter, contactPrenom, "contactPrenom");
  filterProperty(filter, contactFonction, "contactFonction");
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

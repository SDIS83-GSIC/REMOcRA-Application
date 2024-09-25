type FilterDiametreT = {
  diametreActif?: string | undefined;
  diametreCode?: string | undefined;
  diametreLibelle?: string | undefined;
  diametreProtected?: string | undefined;
};

const FilterValuesDiametre = ({
  diametreCode,
  diametreLibelle,
  diametreProtected,
  diametreActif,
}: FilterDiametreT) => {
  const filter: FilterDiametreT = {};

  if (diametreCode != null && diametreCode.trim() !== "") {
    filter.diametreCode = diametreCode;
  }
  if (diametreLibelle != null && diametreLibelle.trim() !== "") {
    filter.diametreLibelle = diametreLibelle;
  }
  if (diametreActif != null && diametreActif.trim() !== "") {
    filter.diametreActif = diametreActif;
  }
  if (diametreProtected != null && diametreProtected.trim() !== "") {
    filter.diametreProtected = diametreProtected;
  }
  return filter;
};

export default FilterValuesDiametre;

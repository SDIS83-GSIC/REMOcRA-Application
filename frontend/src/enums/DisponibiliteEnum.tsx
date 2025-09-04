enum DISPONIBILITE_PEI {
  DISPONIBLE = "DISPONIBLE",
  INDISPONIBLE = "INDISPONIBLE",
  NON_CONFORME = "NON_CONFORME",
}

type Disponibilite = {
  id: DISPONIBILITE_PEI;
  libelle: string;
};

export function getLibelleDisponibilite(
  libelleNonConforme: string,
): Disponibilite[] {
  return [
    { id: DISPONIBILITE_PEI.DISPONIBLE, libelle: "Disponible" },
    {
      id: DISPONIBILITE_PEI.INDISPONIBLE,
      libelle: "Indisponible",
    },
    { id: DISPONIBILITE_PEI.NON_CONFORME, libelle: libelleNonConforme },
  ] as const;
}

export default DISPONIBILITE_PEI;

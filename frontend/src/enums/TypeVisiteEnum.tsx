/**
 * Enumération définissant les types de Visite
 */
export enum TYPE_VISITE {
  RECEPTION = "RECEPTION",
  RECO_INIT = "RECO_INIT",
  CTP = "CTP",
  RECOP = "RECOP",
  NP = "NP",
}

type TypeVisite = {
  code: TYPE_VISITE;
  libelle: string;
};

const referenceTypeVisite: TypeVisite[] = [
  { code: TYPE_VISITE.RECEPTION, libelle: "Visite de Réception" },
  {
    code: TYPE_VISITE.RECO_INIT,
    libelle: "Reconnaissance opérationnelle initiale",
  },
  { code: TYPE_VISITE.CTP, libelle: "Contrôle technique périodique" },
  {
    code: TYPE_VISITE.RECOP,
    libelle: "Reconnaissance opérationnelle périodique",
  },
  { code: TYPE_VISITE.NP, libelle: "Non programmée" },
] as const;

export default referenceTypeVisite;

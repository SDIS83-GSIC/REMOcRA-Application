export enum DFCI_LISTE_COUCHE {
  DFCI_PISTE = "DFCI_PISTE",
  DFCI_AIRE = "DFCI_AIRE",
  DFCI_DEB = "DFCI_DEB",
  DFCI_PANNEAU = "DFCI_PANNEAU",
}

type DfciListeCouche = {
  code: DFCI_LISTE_COUCHE;
  libelle: string;
};

const referenceDfciListeCouche: DfciListeCouche[] = [
  { code: DFCI_LISTE_COUCHE.DFCI_PISTE, libelle: "Piste" },
  { code: DFCI_LISTE_COUCHE.DFCI_AIRE, libelle: "Aire" },
  { code: DFCI_LISTE_COUCHE.DFCI_DEB, libelle: "Débroussaillement" },
  { code: DFCI_LISTE_COUCHE.DFCI_PANNEAU, libelle: "Panneau" },
];
export default referenceDfciListeCouche;

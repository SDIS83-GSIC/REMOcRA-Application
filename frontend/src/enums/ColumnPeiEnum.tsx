/**
 * Enumération définissant les colonnes affichables pour le tableau des PEI
 */
enum COLUMN_PEI {
  NUMERO_COMPLET = "NUMERO_COMPLET",
  NUMERO_INTERNE = "NUMERO_INTERNE",
  TYPE_PEI = "TYPE_PEI",
  DISPONIBILITE_TERRESTRE = "DISPONIBILITE_TERRESTRE",
  DISPONIBILITE_HBE = "DISPONIBILITE_HBE",
  NATURE = "NATURE",
  COMMUNE = "COMMUNE",
  NATURE_DECI = "NATURE_DECI",
  AUTORITE_DECI = "AUTORITE_DECI",
  SERVICE_PUBLIC_DECI = "SERVICE_PUBLIC_DECI",
  ANOMALIES = "ANOMALIES",
  PEI_NEXT_ROP = "PEI_NEXT_ROP",
  PEI_NEXT_CTP = "PEI_NEXT_CTP",
  TOURNEE_LIBELLE = "TOURNEE_LIBELLE",
  PEI_ADRESSE = "ADRESSE",
}
export default COLUMN_PEI;

export const referenceColumnPei: ColumnPeiType[] = [
  { id: COLUMN_PEI.NUMERO_COMPLET, libelle: "Numéro complet" },
  { id: COLUMN_PEI.NUMERO_INTERNE, libelle: "Numéro interne" },
  { id: COLUMN_PEI.TYPE_PEI, libelle: "Type PEI (PIBI, PENA)" },
  {
    id: COLUMN_PEI.DISPONIBILITE_TERRESTRE,
    libelle: "Disponibilité terrestre",
  },
  { id: COLUMN_PEI.DISPONIBILITE_HBE, libelle: "Disponibilité HBE" },
  { id: COLUMN_PEI.NATURE, libelle: "Nature" },
  { id: COLUMN_PEI.COMMUNE, libelle: "Commune" },
  { id: COLUMN_PEI.NATURE_DECI, libelle: "Nature DECI" },
  { id: COLUMN_PEI.AUTORITE_DECI, libelle: "Autorité DECI" },
  { id: COLUMN_PEI.SERVICE_PUBLIC_DECI, libelle: "Service public DECI" },
  { id: COLUMN_PEI.ANOMALIES, libelle: "Anomalies" },
  { id: COLUMN_PEI.PEI_NEXT_ROP, libelle: "Date de la prochaine ROP" },
  { id: COLUMN_PEI.PEI_NEXT_CTP, libelle: "Date du prochain CTP" },
  { id: COLUMN_PEI.TOURNEE_LIBELLE, libelle: "Libellé de la tournée" },
  { id: COLUMN_PEI.PEI_ADRESSE, libelle: "Adresse" },
] as const;
export type ColumnPeiType = {
  id: COLUMN_PEI;
  libelle: string;
};

type PeiProjetEntity = {
  peiProjetId?: string;
  peiProjetEtudeId: string;
  peiProjetNatureDeciId?: string;
  peiProjetTypePeiProjet?: TypePeiProjet;
  peiProjetDiametreId?: string;
  peiProjetDiametreCanalisation?: number;
  peiProjetCapacite?: number;
  peiProjetDebit?: number;
  peiProjetCoordonneeX: number;
  peiProjetCoordonneeY: number;
  peiProjetSrid: string;
};

export enum TypePeiProjet {
  PIBI = "PIBI",
  RESERVE = "RESERVE",
  PA = "PA",
}

export default PeiProjetEntity;

enum CriseStatutEnum {
  EN_COURS = "En cours",
  TERMINEE = "Terminée",
  FUSIONNEE = "Fusionnée",
}

export type CoucheWMSType = {
  coucheId: string | null;
  operationnel: boolean;
  anticipation: boolean;
};

export type CriseType = {
  criseId?: string;
  criseLibelle: string;
  criseDescription: string;
  criseDateDebut: Date;
  criseDateFin: Date;
  criseStatutType: CriseStatutEnum;
  typeCriseId: string;
  listeCommuneId: string[];
  listeToponymieId: string[];
  couchesWMS: CoucheWMSType[];
};

export default CriseStatutEnum;

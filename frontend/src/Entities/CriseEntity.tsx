enum CriseStatutEnum {
  EN_COURS = "En cours",
  TERMINEE = "Terminée",
  FUSIONNEE = "Fusionnée",
}

export type CriseType = {
  criseId: string;
  criseLibelle: string;
  criseDescription: string;
  criseDateDebut: Date;
  criseDateFin: Date;
  criseStatutType: CriseStatutEnum;
  typeCriseId: string;
  listeCommune: string[];
  listeToponymie: string[];
};

export default CriseStatutEnum;

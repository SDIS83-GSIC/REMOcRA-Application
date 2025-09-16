export type IndisponibiliteTemporaireEntity = {
  indisponibiliteTemporaireId: string;
  indisponibiliteTemporaireDateDebut: Date;
  indisponibiliteTemporaireDateFin?: Date;
  indisponibiliteTemporaireMotif: string;
  indisponibiliteTemporaireObservation?: string;
  indisponibiliteTemporaireMailAvantIndisponibilite: boolean;
  indisponibiliteTemporaireMailApresIndisponibilite: boolean;
};

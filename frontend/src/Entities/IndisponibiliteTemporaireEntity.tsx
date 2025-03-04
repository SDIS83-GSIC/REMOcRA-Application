import STATUT_INDISPONIBILITE_TEMPORAIRE from "../enums/StatutIndisponibiliteTemporaireEnum.tsx";

export type IndisponibiliteTemporaireEntity = {
  indisponibiliteTemporaireId: string;
  indisponibiliteTemporaireDateDebut: Date;
  indisponibiliteTemporaireDateFin?: Date;
  indisponibiliteTemporaireMotif: string;
  indisponibiliteTemporaireObservation?: string;
  indisponibiliteTemporaireStatut: STATUT_INDISPONIBILITE_TEMPORAIRE;
  indisponibiliteTemporaireMailAvantIndisponibilite: boolean;
  indisponibiliteTemporaireMailApresIndisponibilite: boolean;
};

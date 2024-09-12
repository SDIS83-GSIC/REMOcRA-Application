type filterIndisponibiliteTemporaire = {
  indisponibiliteTemporaireMotif?: string;
  indisponibiliteTemporaireStatut?: string;
  indisponibiliteTemporaireBasculeAutoIndisponible?: string;
  indisponibiliteTemporaireBasculeAutoDisponible?: string;
  indisponibiliteTemporaireMailAvantIndisponibilite?: string;
  indisponibiliteTemporaireMailApresIndisponibilite?: string;
  indisponibiliteTemporaireObservation?: string;
  listeNumeroPei?: string;
};

const filterValuesToVariable = ({
  indisponibiliteTemporaireMotif,
  indisponibiliteTemporaireStatut,
  indisponibiliteTemporaireObservation,
  indisponibiliteTemporaireBasculeAutoIndisponible,
  indisponibiliteTemporaireBasculeAutoDisponible,
  indisponibiliteTemporaireMailAvantIndisponibilite,
  indisponibiliteTemporaireMailApresIndisponibilite,
  listeNumeroPei,
}: filterIndisponibiliteTemporaire) => {
  const filter: filterIndisponibiliteTemporaire = {};
  if (indisponibiliteTemporaireBasculeAutoDisponible?.trim().length > 0) {
    filter.indisponibiliteTemporaireBasculeAutoDisponible =
      indisponibiliteTemporaireBasculeAutoDisponible;
  }
  if (indisponibiliteTemporaireMotif?.trim().length > 0) {
    filter.indisponibiliteTemporaireMotif = indisponibiliteTemporaireMotif;
  }
  if (indisponibiliteTemporaireMailAvantIndisponibilite?.trim().length > 0) {
    filter.indisponibiliteTemporaireMailAvantIndisponibilite =
      indisponibiliteTemporaireMailAvantIndisponibilite;
  }
  if (indisponibiliteTemporaireMailApresIndisponibilite?.trim().length > 0) {
    filter.indisponibiliteTemporaireMailApresIndisponibilite =
      indisponibiliteTemporaireMailApresIndisponibilite;
  }

  if (listeNumeroPei?.trim().length > 0) {
    filter.listeNumeroPei = listeNumeroPei;
  }
  if (indisponibiliteTemporaireMotif?.trim().length > 0) {
    filter.indisponibiliteTemporaireMotif = indisponibiliteTemporaireMotif;
  }
  if (indisponibiliteTemporaireBasculeAutoIndisponible?.trim().length > 0) {
    filter.indisponibiliteTemporaireBasculeAutoIndisponible =
      indisponibiliteTemporaireBasculeAutoIndisponible;
  }
  if (indisponibiliteTemporaireObservation?.trim().length > 0) {
    filter.indisponibiliteTemporaireObservation =
      indisponibiliteTemporaireObservation;
  }
  if (indisponibiliteTemporaireStatut?.trim().length > 0) {
    filter.indisponibiliteTemporaireStatut = indisponibiliteTemporaireStatut;
  }

  return filter;
};

export default filterValuesToVariable;

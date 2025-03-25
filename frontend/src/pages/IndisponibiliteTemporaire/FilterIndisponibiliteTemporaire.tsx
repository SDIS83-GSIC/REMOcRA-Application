type filterIndisponibiliteTemporaire = {
  indisponibiliteTemporaireMotif?: string;
  indisponibiliteTemporaireStatut?: string;
  indisponibiliteTemporaireMailAvantIndisponibilite?: string;
  indisponibiliteTemporaireMailApresIndisponibilite?: string;
  indisponibiliteTemporaireObservation?: string;
  listePeiId?: string[];
};

const filterValuesToVariable = ({
  indisponibiliteTemporaireMotif,
  indisponibiliteTemporaireStatut,
  indisponibiliteTemporaireObservation,
  indisponibiliteTemporaireMailAvantIndisponibilite,
  indisponibiliteTemporaireMailApresIndisponibilite,
  listePeiId,
}: filterIndisponibiliteTemporaire) => {
  const filter: filterIndisponibiliteTemporaire = {};
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

  if (listePeiId && listePeiId?.length > 0) {
    filter.listePeiId = listePeiId;
  }
  if (indisponibiliteTemporaireMotif?.trim().length > 0) {
    filter.indisponibiliteTemporaireMotif = indisponibiliteTemporaireMotif;
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

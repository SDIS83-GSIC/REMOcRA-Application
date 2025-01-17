type filterTournee = {
  tourneeLibelle?: string;
  tourneeOrganismeLibelle?: string;
  tourneeUtilisateurReservationLibelle?: string;
  tourneeDeltaDate?: string;
  peiId?: string;
  tourneeActif?: string;
};

export const filterValuesToVariable = ({
  tourneeLibelle,
  tourneeOrganismeLibelle,
  tourneeUtilisateurReservationLibelle,
  tourneeDeltaDate,
  peiId,
  tourneeActif,
}: filterTournee) => {
  const filter: filterTournee = {};

  if (tourneeLibelle != null && tourneeLibelle.trim() !== "") {
    filter.tourneeLibelle = tourneeLibelle;
  }
  if (
    tourneeOrganismeLibelle != null &&
    tourneeOrganismeLibelle.trim() !== ""
  ) {
    filter.tourneeOrganismeLibelle = tourneeOrganismeLibelle;
  }
  if (
    tourneeUtilisateurReservationLibelle != null &&
    tourneeUtilisateurReservationLibelle.trim() !== ""
  ) {
    filter.tourneeUtilisateurReservationLibelle =
      tourneeUtilisateurReservationLibelle;
  }
  if (tourneeDeltaDate != null && tourneeDeltaDate.trim() !== "") {
    filter.tourneeDeltaDate = tourneeDeltaDate;
  }
  if (peiId != null && peiId.trim() !== "") {
    filter.peiId = peiId;
  }

  if (tourneeActif?.trim().length > 0) {
    filter.tourneeActif = tourneeActif;
  }

  return filter;
};

export default filterTournee;

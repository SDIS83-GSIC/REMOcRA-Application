type filterTournee = {
  tourneeLibelle?: string;
  tourneeOrganismeLibelle?: string;
  tourneeUtilisateurReservationLibelle?: string;
  tourneeDeltaDate?: string;
};

export const filterValuesToVariable = ({
  tourneeLibelle,
  tourneeOrganismeLibelle,
  tourneeUtilisateurReservationLibelle,
  tourneeDeltaDate,
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

  return filter;
};

export default filterTournee;

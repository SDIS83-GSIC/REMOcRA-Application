type filterTournee = {
  tourneeLibelle?: string;
  tourneeOrganismeLibelle?: string;
  tourneeUtilisateurReservationLibelle?: string;
};

export const filterValuesToVariable = ({
  tourneeLibelle,
  tourneeOrganismeLibelle,
  tourneeUtilisateurReservationLibelle,
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

  return filter;
};

export default filterTournee;

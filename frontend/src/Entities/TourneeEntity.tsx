export type TourneeEntity = {
  tourneeId: string;
  tourneeLibelle: string;
  tourneeOrganisme_id: string;
  tourneePourcentageAvancement: number;
  tourneeReservationUtilisateurId: string;
  tourneeDateSynchronisation: Date;
};

export type TourneeFormEntity = {
  tourneeId: string;
  tourneeLibelle: string;
  tourneeOrganismeId: string;
};

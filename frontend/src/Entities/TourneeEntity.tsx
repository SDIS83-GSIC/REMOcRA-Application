export type TourneeEntity = {
  tourneeId: string;
  tourneeActif: boolean;
  tourneeLibelle: string;
  tourneeOrganisme_id: string;
  tourneeEtat: number;
  tourneeReservationUtilisateurId: string;
  tourneeDateSynchronisation: Date;
};

export type TourneeFormEntity = {
  tourneeId: string;
  tourneeLibelle: string;
  tourneeOrganismeId: string;
};

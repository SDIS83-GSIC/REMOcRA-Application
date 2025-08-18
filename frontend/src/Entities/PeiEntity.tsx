import { Document } from "../components/Form/FormDocuments.tsx";
import DISPONIBILITE_PEI from "../enums/DisponibiliteEnum.tsx";
import TYPE_PEI from "../enums/TypePeiEnum.tsx";

export type PeiEntity = {
  peiId: string;
  peiNumeroComplet: string;
  peiNumeroInterne: number;
  peiTypePei: TYPE_PEI;
  peiDisponibiliteTerrestre: DISPONIBILITE_PEI;
  peiObservation: string;

  peiAutoriteDeciId: string;
  peiServicePublicDeciId: string;
  peiMaintenanceDeciId: string;

  peiCommuneId: string;
  peiVoieId: string;
  peiVoieTexte: string;
  peiNumeroVoie: string;
  peiSuffixeVoie: string;
  peiLieuDitId: string;
  peiCroisementId: string;
  peiComplementAdresse: string;
  peiEnFace: boolean;

  peiDomaineId: string;
  peiNatureId: string;
  peiSiteId: string;
  peiGestionnaireId: string;
  peiNatureDeciId: string;
  peiAnneeFabrication: number;
  peiNiveauId: string;
  peiZoneSpecialeId: string;

  coordonneeX: number;
  coordonneeY: number;
  srid: number;

  // DONNEES PIBI
  pibiDiametreId?: string;
  pibiServiceEauId?: string;
  pibiIdentifiantGestionnaire?: string;
  pibiRenversable?: boolean;
  pibiDispositifInviolabilite?: boolean;
  pibiModeleId?: string;
  pibiMarqueId?: string;
  pibiReservoirId?: string;
  pibiDebitRenforce?: boolean;
  pibiTypeCanalisationId?: string;
  pibiTypeReseauId?: string;
  pibiDiametreCanalisation?: number;
  pibiSurpresse?: boolean;
  pibiAdditive?: boolean;
  pibiJumeleId?: string;

  // DONNEES PENA
  penaCapacite?: number;
  penaCapaciteIllimitee?: boolean;
  penaCapaciteIncertaine?: boolean;
  penaMateriauId?: string;
  penaQuantiteAppoint?: number;
  penaDisponibiliteHbe?: DISPONIBILITE_PEI;
  penaEquipeHbe?: boolean;
  typeEnginIds?: string[];

  // DONNEES INITIALES
  peiNumeroInterneInitial?: number;
  peiCommuneIdInitial?: string;
  peiZoneSpecialeIdInitial?: string;
  peiNatureDeciIdInitial?: string;
  peiDomaineIdInitial?: string;

  // Documents
  documents: Document &
    {
      isPhotoPei: boolean;
    }[];
};

/** Entity utilisé dans l'affichages des informations TourneePei */
export type PeiInfoEntity = {
  id: string;
  peiNumeroComplet: string;
  natureDeciCode: string;
  natureLibelle: string;
  adresse?: string;
  communeLibelle: string;
  tourneeId: string;
};

export type PeiVisiteTourneeInformationEntity = {
  peiId: string;
  peiNumeroComplet: string;
  natureDeciCode: string;
  natureDeciLibelle: string;
  domaineLibelle: string;
  natureLibelle: string;
  peiTypePei: TYPE_PEI;
  adresse?: string;
  communeLibelle: string;
  communeCodeInsee: string;
  communeCodePostal: string;
  peiDisponibiliteTerrestre: DISPONIBILITE_PEI;
  gestionnaireLibelle?: string;
  listeAnomalies?: string;
  peiNextRop?: Date;
  peiNextCtp?: Date;
};

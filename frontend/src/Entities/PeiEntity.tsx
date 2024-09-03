import { Document } from "../components/Form/FormDocuments.tsx";
import TYPE_PEI from "../enums/TypePeiEnum.tsx";

export type PeiEntity = {
  peiId: string;
  peiNumeroComplet: string;
  peiNumeroInterne: number;
  peiTypePei: TYPE_PEI;
  peiDisponibiliteTerrestre: DISPONIBILITE_PEI;

  peiAutoriteDeciId: string;
  peiServicePublicDeciId: string;
  peiMaintenanceDeciId: string;

  peiCommuneId: string;
  peiVoieId: string;
  peiNumeroVoie: number;
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
  pibiNumeroScp?: string;
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
  natureLibelle: string;
  peiNumeroVoie?: number;
  voieLibelle?: string;
  communeLibelle: string;
  tourneeId: string;
};

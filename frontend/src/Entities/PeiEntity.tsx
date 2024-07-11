import DISPONIBILITE_PEI from "../enums/DisponibiliteEnum.tsx";
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

  // DONNEES PENA
  penaCapacite?: number;
  penaCapaciteIllimitee?: boolean;
  penaCapaciteIncertaine?: boolean;
  penaMateriauId?: string;
  penaQuantiteAppoint?: number;
  penaDisponibiliteHbe?: DISPONIBILITE_PEI;
};

import { TYPE_VISITE } from "../enums/TypeVisiteEnum.tsx";
import { AnomalieCompleteEntity } from "./AnomalieEntity.tsx";
import { CtrlDebitPressionEntity } from "./CtrlDebitPressionEntity.tsx";

export type VisiteEntity = {
  visiteId: string;
  visitePeiId: string;
  visiteDate: Date;
  visiteTypeVisite: TYPE_VISITE;
  visiteAgent1: string;
  visiteAgent2: string;
  visiteObservation: string;
};

export type VisiteCompleteEntity = {
  visiteId: string;
  visitePeiId: string;
  visiteDate: Date;
  visiteTypeVisite: TYPE_VISITE;
  visiteAgent1: string;
  visiteAgent2: string;
  visiteObservation: string;
  listeAnomalie: AnomalieCompleteEntity[];
  isCtrlDebitPression: boolean;
  ctrlDebitPression: CtrlDebitPressionEntity;
};

export type VisiteTourneeEntity = {
  tourneeId?: string;
  visiteDate?: Date;
  visiteTypeVisite?: TYPE_VISITE;
  visiteAgent1?: string;
  visiteAgent2?: string;
  isCtrlDebitPression?: boolean;
  listeSimplifiedVisite: SimplifiedVisiteEntity[];
};

export type SimplifiedVisiteEntity = {
  visiteId?: string;
  visitePeiId: string;
  visiteObservation: string;
  listeAnomalie: AnomalieCompleteEntity[];
  ctrlDebitPression: CtrlDebitPressionEntity;
};

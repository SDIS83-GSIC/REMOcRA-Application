export type AnomalieCompleteEntity = {
  anomalieId: string;
  anomalieCode: string;
  anomalieLibelle?: string;
  anomalieCommentaire?: string;
  anomalieAnomalieCategorieId?: string;
  anomalieCategorieLibelle: string;
  poidsAnomalieValIndispoTerrestre?: number;
  poidsAnomalieValIndispoHbe?: number;
  isReceptionAssignable: boolean;
  isRecoInitAssignable: boolean;
  isCTPAssignable: boolean;
  isRopAssignable: boolean;
  isNPAssignable: boolean;
  isAssigned: boolean;
};

export type MapAnomalieCompleteByPeiId = Map<string, AnomalieCompleteEntity[]>;

import { IdCodeLibelleType } from "../utils/typeUtils.tsx";

export type ModeleCourrierEntity = {
  modeleCourrierId: string;
  modeleCourrierCode: string;
  modeleCourrierLibelle: string;
  modeleCourrierDescription: string;
};

export type CourrierParametreEntity = {
  nameField: string;
  label: string;
  description: string | undefined;
  liste: IdCodeLibelleLienType[] | null;
  typeComposant: TypeComposant;
  conditionToDisplay?: ConditionToDisplay;
  defaultValue?: string;
  nameLienField: string;
};

export type ModeleCourrierWithParametres = {
  modeleCourrier: ModeleCourrierEntity;
  listParametres: CourrierParametreEntity[] | null;
};

export enum TypeComposant {
  TEXT_INPUT = "TEXT_INPUT",
  SELECT_INPUT = "SELECT_INPUT",
  CHECKBOX_INPUT = "CHECKBOX_INPUT",
}

export enum TypeOperation {
  DIFFERENT = "DIFFERENT",
  EGAL = "EGAL",
}

export type ValeurAttendue = {
  operation: TypeOperation;
  valeurAttendue: string;
};

export type ConditionToDisplay = {
  nameField: string;
  valeurAttendue: ValeurAttendue;
};

export type IdCodeLibelleLienType = IdCodeLibelleType & {
  lienId: string | null;
};

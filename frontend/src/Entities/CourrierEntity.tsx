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
  liste: IdCodeLibelleType[] | null;
  typeComposant: TypeComposant;
  conditionToDisplay?: ConditionToDisplay;
  defaultValue?: string;
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

export type ConditionToDisplay = {
  nameField: string;
  valeurAttendue: string;
};

import { TYPE_DATA_CACHE } from "../enums/NomenclaturesEnum.tsx";

export type SelectType = {
  onChange?: any;
  name: string;
  defaultValue?: IdCodeLibelleType;
};
export type SelectNomenclaturesType = SelectType & {
  nomenclature: TYPE_DATA_CACHE;
};
export type SelectFilterFromUrlType = SelectType & {
  url: string;
};
export type SelectFilterFromListType = SelectType & {
  listIdCodeLibelle: IdCodeLibelleType[];
};

export type SelectFormType = SelectType & {
  listIdCodeLibelle: IdCodeLibelleType[];
  label?: string;
  required?: boolean;
  disabled?: boolean;
  setValues: (e: any) => void;
};

export type SelectNomenclaturesFormType = SelectFormType & {
  nomenclature: TYPE_DATA_CACHE;
};

export type IdCodeLibelleType = {
  id: string;
  code: string;
  libelle: string;
};

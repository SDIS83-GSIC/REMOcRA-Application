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
  setOtherValues?: () => void;
};

export type SelectNomenclaturesFormType = {
  onChange?: any;
  name: string;
  nomenclature: TYPE_DATA_CACHE;
  label?: string;
  required?: boolean;
  disabled?: boolean;
  setValues: (e: any) => void;
  valueId: string | undefined;
};

export type SelectIdLibelleDataFromListType = SelectType & {
  listIdLibelle: IdCodeLibelleType[];
};

export type IdCodeLibelleType = {
  id: string | undefined;
  code: string | undefined;
  libelle: string;
};

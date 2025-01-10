import NOMENCLATURE from "../enums/NomenclaturesEnum.tsx";

export type SelectType = {
  onChange?: any;
  name: string;
  defaultValue?: IdCodeLibelleType;
  disabled?: boolean;
};
export type SelectNomenclaturesType = SelectType & {
  nomenclature: NOMENCLATURE;
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
  optionDisabled?: string;
  setValues?: (e: any) => void;
  setFieldValue?: (name: string, value: any) => void;
  setOtherValues?: () => void;
};

export type SelectNomenclaturesFormType = {
  onChange?: any;
  name: string;
  valueId?: string;
  nomenclature: NOMENCLATURE;
  label?: string;
  required?: boolean;
  disabled?: boolean;
  setValues: (e: any) => void;
  setOtherValues?: () => void;
};

export type IdCodeLibelleType = {
  id: string | undefined;
  code: string | undefined;
  libelle: string;
  lienId?: string | undefined;
};

export type SubmitButtonType = {
  update?: boolean;
  returnLink?: string | undefined;
  onClick?: (...args: any[]) => void;
  disabledValide?: boolean;
};

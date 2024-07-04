import NOMENCLATURES from "../enums/NomenclaturesEnum.tsx";

export type SelectType = { onChange?: any; name: string };
export type SelectNomenclaturesType = SelectType & {
  nomenclature: NOMENCLATURES;
};
export type SelectIdLibelleDataType = SelectType & {
  url: string;
};

export type IdCodeLibelleType = {
  id: string;
  code: string;
  libelle: string;
};

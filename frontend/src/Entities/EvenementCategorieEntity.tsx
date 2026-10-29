export type TypeCriseType = {
  typeCriseId: string;
  typeCriseCode: string;
  typeCriseLibelle: string;
  typeCriseActif: boolean;
};

export type EvenementCategorieType = {
  evenementCategorieId: string | null;
  evenementCategorieCode: string | null;
  evenementCategorieLibelle: string | null;
  evenementCategorieActif: boolean;
  evenementCategorieTypeCrise: TypeCriseType[];
};

import SOUS_TYPE_TYPE_GEOMETRIE from "../enums/Signalement/SousTypeTypeGeometrie.tsx";

export type SousTypeEvenementType = {
  evenementSousCategorieId: string | null;
  evenementSousCategorieCode: string | null;
  evenementSousCategorieLibelle: string | null;
  evenementSousCategorieTypeGeometrie: string | null;
  evenementSousCategorieEvenementCategorieId: string | null;
  evenementSousCategorieActif: boolean;
  evenementSousCategorieComplement: {
    evenementSousCategorieId: string | null;
    sousCategorieComplementId: string | null;
    sousCategorieComplementLibelle: string | null;
    sousCategorieComplementSql: string | null;
    sousCategorieComplementSqlId: string | null;
    sousCategorieComplementSqlLibelle: string | null;
    sousCategorieComplementValeurDefaut: string | null;
    sousCategorieComplementEstRequis: boolean | null;
    sousCategorieComplementType: TYPE_PARAMETRE_COMPOSANT;

    // pas besoin dans le back, mais utilisé dans le front
    sousCategorieComplementCode: string | null;
    sousCategorieComplementDescription: string | null;
    sousCategorieComplementSqlDebut: string | undefined;
    sousCategorieComplementSqlFin: string | undefined;
  }[];
};

export enum TYPE_PARAMETRE_COMPOSANT {
  DATE_INPUT = "Champ date",
  NUMBER_INPUT = "Champ nombre",
  SELECT_INPUT = "Liste déroulante",
  TEXT_INPUT = "Champ texte",
}

export const convertToEnum = (geometry: string): SOUS_TYPE_TYPE_GEOMETRIE => {
  switch (geometry) {
    case "POINT":
      return SOUS_TYPE_TYPE_GEOMETRIE.POINT;
    case "LINESTRING":
      return SOUS_TYPE_TYPE_GEOMETRIE.LINESTRING;
    default:
      return SOUS_TYPE_TYPE_GEOMETRIE.POLYGON;
  }
};

export default SousTypeEvenementType;

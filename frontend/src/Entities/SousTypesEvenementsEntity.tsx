import SOUS_TYPE_TYPE_GEOMETRIE from "../enums/Signalement/SousTypeTypeGeometrie.tsx";

export type SousTypeEvenementType = {
  evenementSousCategorieId?: string | null;
  evenementSousCategorieCode?: string | null;
  evenementSousCategorieLibelle?: string | null;
  evenementSousCategorieTypeGeometrie?: string | null;
  evenementSousCategorieCriseCategorieId?: string | null;
  evenementSousCategorieActif?: boolean | null;
};

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

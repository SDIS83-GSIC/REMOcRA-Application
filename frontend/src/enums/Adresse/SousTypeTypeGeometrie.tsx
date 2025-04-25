import { IdCodeLibelleType } from "../../utils/typeUtils.tsx";

/**
 * Enumération définissant les types de géométrie
 */
enum SOUS_TYPE_TYPE_GEOMETRIE {
  POINT = "POINT",
  POLYGON = "POLYGON",
  LINESTRING = "LINESTRING",
}

export default SOUS_TYPE_TYPE_GEOMETRIE;

export const referenceTypeGeometrie: IdCodeLibelleType[] = [
  {
    id: SOUS_TYPE_TYPE_GEOMETRIE.POINT,
    code: SOUS_TYPE_TYPE_GEOMETRIE.POINT,
    libelle: "Point",
  },
  {
    id: SOUS_TYPE_TYPE_GEOMETRIE.POLYGON,
    code: SOUS_TYPE_TYPE_GEOMETRIE.POLYGON,
    libelle: "Polygone",
  },
  {
    id: SOUS_TYPE_TYPE_GEOMETRIE.LINESTRING,
    code: SOUS_TYPE_TYPE_GEOMETRIE.LINESTRING,
    libelle: "Ligne",
  },
];

/**
 * Enumération des différents type possible d'une aire
 */
export enum TypeAire {
  CROISEMENT = "Croisement",
  RETOURNEMENT = "Retournement",
}

export const listTypeAire = Object.entries(TypeAire).map(([key, value]) => {
  return { id: key, code: key, libelle: value.toString() };
});

/**
 * Type représentant les données d'une aire
 */
export type DfciAireEntity = {
  dfciAireId: string;
  dfciAireAmenagement: boolean;
  dfciAireDateGps: Date;
  dfciAireGrandeDimension: number;
  dfciAirePetiteDimension: number;
  dfciAireType: TypeAire;
  dfciAireGeometrie: string;
  dfciAireDfciPisteId?: string | null;
  dfciAireRemarque: string | null;
  dfciAireCode: string;
  dfciAireVersion: number;
};

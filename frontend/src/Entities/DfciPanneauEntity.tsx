import { idCodeLibelleFromEnum } from "../utils/idCodeLibelleFromEnum.tsx";

export enum TypeEquipement {
  POINT_EAU = "Point d'eau",
  DIRECTION = "Direction",
  ENTREE_PISTE = "Entrée piste",
}

export enum TypePanneau {
  SIGNAL_COMPLEMENT = "Signal complémentaire",
  SIGNAL_PRINCIPAL = "Signal principal",
}

export enum TypePosition {
  GAUCHE = "Gauche",
  DROITE = "Droite",
}

export enum TypeBZero {
  PERMANENT = "Permanent",
  TEMPORAIRE = "Temporaire",
  AUCUN = "Aucun",
}

export const listTypePanneau = idCodeLibelleFromEnum(TypePanneau);
export const listTypeBzero = idCodeLibelleFromEnum(TypeBZero);
export const listTypePostion = idCodeLibelleFromEnum(TypePosition);
export const listTypeEquipement = idCodeLibelleFromEnum(TypeEquipement);

export type DfciPanneauEntity = {
  dfciPanneauId: string;
  dfciPanneauType: TypePanneau;
  dfciPanneauEtat: boolean;
  dfciPanneauBzero: TypeBZero;
  dfciPanneauDateGps: Date;
  dfciPanneauPosition: TypePosition;
  dfciPanneauEquipement: TypeEquipement;
  dfciPanneauDfciPisteId: string;
  dfciPanneauNumPiste: boolean;
  dfciPanneauLibellePiste: boolean;
  dfciPanneauRemarque: string | null;
  dfciPanneauGeometrie: string;
  dfciPanneauCode: string;
  dfciPanneauVersion: number;
};

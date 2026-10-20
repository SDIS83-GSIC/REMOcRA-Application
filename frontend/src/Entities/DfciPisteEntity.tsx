import { idCodeLibelleFromEnum } from "../utils/idCodeLibelleFromEnum.tsx";

export enum TypeImpraticabilite {
  GABARIT = "Gabarit",
  TONNAGE = "Tonnage",
  ETAT_BDR = "Etat Bordure",
  MULTIPLE = "Multiple",
}

export enum TypeTravaux {
  CREER = "Créer",
  CONFORMITE = "Conformité",
  ENTRETENIR = "Entretenir",
}

export enum TypeVoie {
  DFCI = "DFCI",
  MULTI = "Multi",
  INTERFACE = "Interface",
  PPRIF = "PPRIF",
}

export enum TypeImpasse {
  AMENAGEE = "Aménagée",
  NON_AMENAGEE = "Non aménagée",
  SANS = "Sans aménagement",
}

export enum TypeFoncier {
  DFCI = "DFCI",
  PUBLIC = "Public",
  PRIVE = "Privé",
  MIXTE = "Mixte",
  INCONNU = "Inconnu",
}

export enum TypeCroisement {
  GENERALISEE = "Généralisée",
  PONCTUELLE = "Ponctuelle",
  SANS = "Sans croisement",
}

export enum TypeProgramme {
  IF = "IF",
  MCO = "MCO",
  AMP = "AMP",
  AUTRE = "Autre",
}

export const listTypeImpraticabilite =
  idCodeLibelleFromEnum(TypeImpraticabilite);
export const listTypeTravaux = idCodeLibelleFromEnum(TypeTravaux);
export const listTypeVoie = idCodeLibelleFromEnum(TypeVoie);
export const listTypeImpasse = idCodeLibelleFromEnum(TypeImpasse);
export const listTypeFoncier = idCodeLibelleFromEnum(TypeFoncier);
export const listTypeCroisement = idCodeLibelleFromEnum(TypeCroisement);
export const listTypeProgramme = idCodeLibelleFromEnum(TypeProgramme);

export type DfciPisteEntity = {
  dfciPisteId: string;
  dfciPisteAdresse: string | null;
  dfciPisteAnneeProgramme: number | null;
  dfciPisteAnneeTravaux: number | null;
  dfciPisteCirculation: boolean;
  dfciPisteDateGps: Date;
  dfciPisteLibelle: string;
  dfciPisteNumero: string;
  dfciPisteOuverture: boolean;
  dfciPistePraticabilite: boolean;
  dfciPisteEstDfci: boolean;
  dfciPisteRetournement: boolean;
  dfciPisteNumTroncon: number;
  dfciPisteNumObjectif: number | null;
  dfciPisteLibelleObjectif: string | null;
  dfciPisteGeometrie: string;
  dfciPisteRemarque: string | null;
  dfciPisteImpraticabilite: TypeImpraticabilite | null;
  dfciPisteTravaux: TypeTravaux | null;
  dfciPisteVoie: TypeVoie;
  dfciPisteImpasse: TypeImpasse;
  dfciPisteFoncier: TypeFoncier | null;
  dfciPisteCroisement: TypeCroisement;
  dfciPisteProgramme: TypeProgramme | null;
  dfciPisteDfciCategoriePisteId: string;
  dfciPisteDfciMassifId: string;
  dfciPisteDfciPrestataireId: string | null;
  dfciPisteDfciOuvrageId: string | null;
  dfciPisteCode: string;
  dfciPisteVersion: number;
};

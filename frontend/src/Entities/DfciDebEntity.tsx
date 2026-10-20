import { idCodeLibelleFromEnum } from "../utils/idCodeLibelleFromEnum.tsx";
import { TypeProgramme, TypeTravaux } from "./DfciPisteEntity.tsx";

export enum TypeDebroussaillement {
  DFCI = "DFCI",
  ARCHIVE = "Archive",
  AUTRE = "Autre",
}

export const listTypeTravaux = idCodeLibelleFromEnum(TypeTravaux);
export const listTypeProgramme = idCodeLibelleFromEnum(TypeProgramme);
export const listTypeDebroussaillement = idCodeLibelleFromEnum(
  TypeDebroussaillement,
);

export type DfciDebEntity = {
  dfciDebId: string;
  dfciDebLibelle: string;
  dfciDebAnneeProgramme: number | null;
  dfciDebAnneeTravaux: number | null;
  dfciDebMoisTravaux: number | null;
  dfciDebAnneeEdition: number | null;
  dfciDebLargeur: number;
  dfciDebSurface: number;
  dfciDebType: TypeDebroussaillement;
  dfciDebGeometrie: string;
  dfciDebRemarque: string | null;
  dfciDebProgramme: TypeProgramme | null;
  dfciDebTravaux: TypeTravaux | null;
  dfciDebDfciMassifId: string;
  dfciDebDfciOuvrageId: string | null;
  dfciDebDfciPrestataireId: string | null;
  dfciDebCode: string;
  dfciDebVersion: number;
};

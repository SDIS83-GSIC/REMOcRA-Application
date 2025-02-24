import { Document } from "../components/Form/FormDocuments.tsx";

enum CriseStatutEnum {
  EN_COURS = "En cours",
  TERMINEE = "Terminée",
}

export type CriseType = {
  criseId: string;
  typeCriseId: string;
  criseNumero: string;
  criseLibelle: string;
  criseDescription: string;
  listeCommuneId: string[];
  documents: Document & {
    criseDocumentLibelle: string;
  };
};

export default CriseStatutEnum;

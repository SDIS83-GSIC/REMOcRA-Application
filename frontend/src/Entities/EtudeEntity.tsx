import { Document } from "../components/Form/FormDocuments.tsx";

enum EtudeStatutEnum {
  EN_COURS = "En cours",
  TERMINEE = "Terminée",
}

export type EtudeType = {
  etudeId: string;
  typeEtudeId: string;
  etudeNumero: string;
  etudeLibelle: string;
  etudeDescription?: string;
  listeCommuneId: string[];
  documents: Document & {
    etudeDocumentLibelle: string;
  };
};

export default EtudeStatutEnum;

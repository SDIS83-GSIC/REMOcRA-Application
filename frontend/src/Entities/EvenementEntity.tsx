export type EvenementType = {
  evenementId: string;
  evenementTypeId: string;
  evenementType: string;
  evenementLibelle: string;
  evenementDescription: string;
  evenementTag: string;
  evenementOrigine: string;
  evenementDateConstat: Date;
  evenementImportance: string;
  evenementEstFerme: boolean;
  documents: Document & {
    evenementDocumentLibelle: string;
  };
};

export default EvenementType;

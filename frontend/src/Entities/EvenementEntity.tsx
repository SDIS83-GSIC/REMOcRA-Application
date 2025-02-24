export type EvenementType = {
  evenementId: string;
  evenementTypeId: string;
  evenementType: string;
  evenementLibelle: string;
  evenementDescription: string;
  evenementTag: string;
  evenementOrigine: string;
  evenementDateDebut: Date;
  evenementImportance: string;
  evenementIsClosed: boolean;
  documents: Document & {
    evenementDocumentLibelle: string;
  };
};

export default EvenementType;

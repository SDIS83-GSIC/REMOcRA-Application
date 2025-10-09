export type EvenementType = {
  geometrieEvenement(geometrieEvenement: any): string | Blob;
  evenementId: string;
  evenementSousCategorieId: string;
  evenementType: string;
  evenementLibelle: string;
  evenementDescription: string;
  evenementTags: string[];
  evenementOrigine: string;
  evenementDateConstat: Date;
  evenementImportance: string;
  evenementEstFerme: boolean;
  documents: Document & {
    evenementDocumentLibelle: string;
  };
  evenementParametre: {
    idParam: string;
    valueParam: string;
  };
  evenementSousCategorieComplement: any;
};

export default EvenementType;

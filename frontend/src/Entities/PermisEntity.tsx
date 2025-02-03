type PermisEntity = {
  permisId?: string;
  permisLibelle: string;
  permisNumero: string;
  //permisInstructeurId: string;
  permisServiceInstructeurId: string;
  permisTypePermisInterserviceId: string;
  permisTypePermisAvisId: string;
  permisRiReceptionnee: boolean;
  permisDossierRiValide: boolean;
  permisObservations?: string;
  permisVoieText?: string;
  permisVoieId?: string;
  permisComplement?: string;
  permisCommuneId: string;
  permisAnnee: number;
  permisDatePermis: Date;
  permisCoordonneeX: number;
  permisCoordonneeY: number;
  permisSrid: string;

  voieSaisieText: boolean;
};

export default PermisEntity;

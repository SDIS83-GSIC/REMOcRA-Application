type PeiPrescritEntity = {
  peiPrescritId?: string;
  peiPrescritDate?: Date;
  peiPrescritDebit?: number;
  peiPrescritNbPoteaux?: number;
  peiPrescritCommentaire?: string;
  peiPrescritAgent?: string;
  peiPrescritNumDossier?: string;
  peiPrescritCoordonneeX: number;
  peiPrescritCoordonneeY: number;
  peiPrescritSrid: string;
};

export default PeiPrescritEntity;

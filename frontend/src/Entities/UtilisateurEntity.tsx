import TYPE_DROIT from "../enums/DroitEnum.tsx";

type UtilisateurEntity = {
  utilisateurId: string;
  nom: string;
  prenom: string;
  username: string;
  organismeId: string;
  droits: TYPE_DROIT[];
  isSuperAdmin: boolean;
};

export default UtilisateurEntity;

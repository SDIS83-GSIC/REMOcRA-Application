import { useParams } from "react-router-dom";
import UpdateNomenclature from "../../../components/NomenclatureComponent/UpdateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const UpdateProfilUtilisateur = () => {
  const { profilUtilisateurId } = useParams();

  return (
    profilUtilisateurId && (
      <UpdateNomenclature
        nomenclatureId={profilUtilisateurId}
        typeNomenclature={NOMENCLATURE.PROFIL_UTILISATEUR}
        redirectLink={URLS.LIST_PROFIL_UTILISATEUR}
        titrePage="Modification d'un profil utilisateur"
      />
    )
  );
};

export default UpdateProfilUtilisateur;

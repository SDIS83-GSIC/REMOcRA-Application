import { useParams } from "react-router-dom";
import UpdateNomenclature from "../../../../components/NomenclatureComponent/UpdateNomenclature.tsx";
import NOMENCLATURE from "../../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../../routes.tsx";

const UpdateEvenementCategorie = () => {
  const { evenementCategorieId: evenementCategorieId } = useParams();

  return (
    evenementCategorieId && (
      <UpdateNomenclature
        nomenclatureId={evenementCategorieId}
        typeNomenclature={NOMENCLATURE.EVENEMENT_CATEGORIE}
        redirectLink={URLS.LIST_EVENEMENT_CATEGORIE}
        titrePage="Mise à jour d'une catégorie d'évènement"
      />
    )
  );
};

export default UpdateEvenementCategorie;

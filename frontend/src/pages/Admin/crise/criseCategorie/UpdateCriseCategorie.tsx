import { useParams } from "react-router-dom";
import UpdateNomenclature from "../../../../components/NomenclatureComponent/UpdateNomenclature.tsx";
import NOMENCLATURE from "../../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../../routes.tsx";

const UpdateCriseCategorie = () => {
  const { criseCategorieId } = useParams();

  return (
    criseCategorieId && (
      <UpdateNomenclature
        nomenclatureId={criseCategorieId}
        typeNomenclature={NOMENCLATURE.CRISE_CATEGORIE}
        redirectLink={URLS.LIST_CRISE_CATEGORIE}
        titrePage="Mise à jour d'une catégorie de crise"
      />
    )
  );
};

export default UpdateCriseCategorie;

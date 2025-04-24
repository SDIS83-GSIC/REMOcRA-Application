import { useParams } from "react-router-dom";
import UpdateNomenclature from "../../../components/NomenclatureComponent/UpdateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const UpdateAdresseTypeElement = () => {
  const { adresseTypeElementId } = useParams();

  return (
    adresseTypeElementId && (
      <UpdateNomenclature
        nomenclatureId={adresseTypeElementId}
        typeNomenclature={NOMENCLATURE.ADRESSE_TYPE_ELEMENT}
        redirectLink={URLS.LIST_ADRESSE_TYPE_ELEMENT}
        titrePage="Adresse - Modification d'un type d'element"
      />
    )
  );
};

export default UpdateAdresseTypeElement;

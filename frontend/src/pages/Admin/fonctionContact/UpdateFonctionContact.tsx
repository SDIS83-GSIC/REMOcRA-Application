import { useParams } from "react-router-dom";
import UpdateNomenclature from "../../../components/NomenclatureComponent/UpdateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";
const UpdateFonctionContact = () => {
  const { fonctionContactId } = useParams();
  return (
    fonctionContactId && (
      <UpdateNomenclature
        nomenclatureId={fonctionContactId}
        typeNomenclature={NOMENCLATURE.FONCTION_CONTACT}
        redirectLink={URLS.LIST_FONCTION_CONTACT}
        titrePage={"Modification d'une fonction de contact"}
      />
    )
  );
};
export default UpdateFonctionContact;

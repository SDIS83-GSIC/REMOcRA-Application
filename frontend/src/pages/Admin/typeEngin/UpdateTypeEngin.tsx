import { useParams } from "react-router-dom";
import UpdateNomenclature from "../../../components/NomenclatureComponent/UpdateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const UpdateTypeEngin = () => {
  const { typeEnginId } = useParams();

  return (
    typeEnginId && (
      <UpdateNomenclature
        nomenclatureId={typeEnginId}
        typeNomenclature={NOMENCLATURE.TYPE_ENGIN}
        redirectLink={URLS.LIST_TYPE_ENGIN}
        titrePage={"Modification d'un type d'engin"}
      />
    )
  );
};

export default UpdateTypeEngin;

import { IconAdresse } from "../../../components/Icon/Icon.tsx";
import ListNomenclature from "../../../components/NomenclatureComponent/ListNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const ListAdresseTypeElement = () => {
  return (
    <>
      <ListNomenclature
        pageTitle="Adresse - Type élément"
        addButtonTitle={"Ajouter un type d'élément"}
        pageIcon={<IconAdresse />}
        lienPageAjout={URLS.ADD_ADRESSE_TYPE_ELEMENT}
        typeNomenclature={NOMENCLATURE.ADRESSE_TYPE_ELEMENT}
        lienPageUpdate={URLS.UPDATE_ADRESSE_TYPE_ELEMENT}
        hasProtectedValue={false}
      />
    </>
  );
};

export default ListAdresseTypeElement;

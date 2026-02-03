import { useGet } from "../../../components/Fetch/useFetch.tsx";
import { IconPei } from "../../../components/Icon/Icon.tsx";
import ListNomenclature from "../../../components/NomenclatureComponent/ListNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import { IdCodeLibelleType } from "../../../utils/typeUtils.tsx";

const ListTypeOrganisme = () => {
  const { data }: { data: IdCodeLibelleType[] } = useGet(
    url`/api/type-organisme/get`,
  );
  return (
    <>
      <ListNomenclature
        pageTitle="Types d'organismes"
        addButtonTitle="Ajouter un type d'organisme"
        pageIcon={<IconPei />}
        hasProtectedValue={true}
        lienPageAjout={URLS.ADD_TYPE_ORGANISME}
        typeNomenclature={NOMENCLATURE.TYPE_ORGANISME}
        lienPageUpdate={URLS.UPDATE_TYPE_ORGANISME}
        libelleFk={"Type organisme parent"}
        listeFk={data}
      />
    </>
  );
};

export default ListTypeOrganisme;

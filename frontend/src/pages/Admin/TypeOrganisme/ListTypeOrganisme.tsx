import { IconPei } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import ListNomenclature from "../../../components/NomenclatureComponent/ListNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { IdCodeLibelleType } from "../../../utils/typeUtils.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import url from "../../../module/fetch.tsx";

const ListTypeOrganisme = () => {
  const { data }: { data: IdCodeLibelleType[] } = useGet(
    url`/api/type-organisme/get`,
  );
  return (
    <>
      <ListNomenclature
        pageTitle="Liste des types d'organisme"
        pageIcon={<IconPei />}
        hasProtectedValue={false}
        lienPageAjout={URLS.ADD_TYPE_ORGANISME}
        typeNomenclature={NOMENCLATURE.TYPE_ORGANISME}
        lienPageUpdate={URLS.UPDATE_TYPE_ORGANISME}
        libelleFk={"Organisme parent"}
        listeFk={data}
      />
    </>
  );
};

export default ListTypeOrganisme;

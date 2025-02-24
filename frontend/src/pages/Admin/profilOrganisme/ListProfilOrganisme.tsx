import { IconPei } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import ListNomenclature from "../../../components/NomenclatureComponent/ListNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { IdCodeLibelleType } from "../../../utils/typeUtils.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import url from "../../../module/fetch.tsx";

const ListProfilOrganisme = () => {
  const { data }: { data: IdCodeLibelleType[] } = useGet(
    url`/api/type-organisme/get`,
  );
  return (
    <>
      <ListNomenclature
        pageTitle="Profils d'organismes"
        addButtonTitle={"Ajouter un profil d'organisme"}
        pageIcon={<IconPei />}
        hasProtectedValue={false}
        lienPageAjout={URLS.ADD_PROFIL_ORGANISME}
        typeNomenclature={NOMENCLATURE.PROFIL_ORGANISME}
        lienPageUpdate={URLS.UPDATE_PROFIL_ORGANISME}
        libelleFk={"Type d'organisme"}
        listeFk={data}
      />
    </>
  );
};

export default ListProfilOrganisme;

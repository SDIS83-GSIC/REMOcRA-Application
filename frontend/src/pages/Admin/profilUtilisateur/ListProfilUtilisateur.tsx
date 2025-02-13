import { IconPei } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import ListNomenclature from "../../../components/NomenclatureComponent/ListNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { IdCodeLibelleType } from "../../../utils/typeUtils.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import url from "../../../module/fetch.tsx";

const ListProfilUtilisateur = () => {
  const { data }: { data: IdCodeLibelleType[] } = useGet(
    url`/api/type-organisme/get`,
  );
  return (
    <>
      <ListNomenclature
        pageTitle="Liste des profils d'utilisateur"
        addButtonTitle={"Ajouter un profil d'utilisateur"}
        pageIcon={<IconPei />}
        hasProtectedValue={false}
        lienPageAjout={URLS.ADD_PROFIL_UTILISATEUR}
        typeNomenclature={NOMENCLATURE.PROFIL_UTILISATEUR}
        lienPageUpdate={URLS.UPDATE_PROFIL_UTILISATEUR}
        libelleFk={"Type d'organisme"}
        listeFk={data}
      />
    </>
  );
};

export default ListProfilUtilisateur;

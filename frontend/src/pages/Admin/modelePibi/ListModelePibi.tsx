import { IconPei } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import ListNomenclature from "../../../components/NomenclatureComponent/ListNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { IdCodeLibelleType } from "../../../utils/typeUtils.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import url from "../../../module/fetch.tsx";

const ListModelePibi = () => {
  const { data }: { data: IdCodeLibelleType[] } = useGet(
    url`/api/marque-pibi/get`,
  );
  return (
    <>
      <ListNomenclature
        pageTitle="Liste des modèles PIBI"
        pageIcon={<IconPei />}
        hasProtectedValue={false}
        lienPageAjout={URLS.ADD_MODELE_PIBI}
        typeNomenclature={NOMENCLATURE.MODELE_PIBI}
        lienPageUpdate={URLS.UPDATE_MODELE_PIBI}
        libelleFk={"Marque"}
        listeFk={data}
      />
    </>
  );
};

export default ListModelePibi;

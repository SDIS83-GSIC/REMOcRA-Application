import { IconInfo, IconPei } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import ListNomenclature from "../../../components/NomenclatureComponent/ListNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { IdCodeLibelleType } from "../../../utils/typeUtils.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import url from "../../../module/fetch.tsx";
import TooltipCustom from "../../../components/Tooltip/Tooltip.tsx";

const ListProfilUtilisateur = () => {
  const { data }: { data: IdCodeLibelleType[] } = useGet(
    url`/api/type-organisme/get`,
  );
  return (
    <>
      <ListNomenclature
        pageTitle={
          <>
            Profils d&apos;utilisateurs
            <TooltipCustom
              tooltipText={
                <>
                  Un profil d&apos;utilisateur permet de définir différentes
                  typologies d&apos;utilisateurs. Ces profils correspondront
                  naturellement aux différents corps de métier utilisant
                  l&apos;application, avec toutes les subtilités jugées utiles
                  pour leur affecter les droits d&apos;accès.
                </>
              }
              tooltipId={"tooltip-profil-utilisateur"}
            >
              <IconInfo />
            </TooltipCustom>
          </>
        }
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

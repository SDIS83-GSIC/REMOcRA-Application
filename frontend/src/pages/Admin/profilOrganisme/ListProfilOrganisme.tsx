import { IconInfo, IconPei } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import ListNomenclature from "../../../components/NomenclatureComponent/ListNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { IdCodeLibelleType } from "../../../utils/typeUtils.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import url from "../../../module/fetch.tsx";
import TooltipCustom from "../../../components/Tooltip/Tooltip.tsx";

const ListProfilOrganisme = () => {
  const { data }: { data: IdCodeLibelleType[] } = useGet(
    url`/api/type-organisme/get`,
  );
  return (
    <>
      <ListNomenclature
        pageTitle={
          <>
            Profils d&apos;organismes
            <TooltipCustom
              tooltipText={
                <>
                  Un profil d&apos;organisme permet de définir différentes
                  typologies pour un même type d&apos;organisme, par exemple
                  <ul>
                    <li>
                      une ouverture aux communes par étape (d&apos;abord une
                      partie de l&apos;application, puis le reste)
                    </li>
                    <li>
                      une différence de typologie (un accès complet pour
                      certaines communes et un accès limité pour d&apos;autres,
                      ...)
                    </li>
                  </ul>
                  Au plus simple, le profil d&apos;un organisme correspond à son
                  type.
                </>
              }
              tooltipId={"tooltip-profil-organisme"}
            >
              <IconInfo />
            </TooltipCustom>
          </>
        }
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

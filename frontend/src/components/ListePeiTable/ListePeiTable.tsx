import QueryTable, {
  useFilterContext,
} from "../../components/Table/QueryTable.tsx";
import COLUMN_PEI from "../../enums/ColumnPeiEnum.tsx";
import FILTER_PAGE from "../../enums/FilterPageEnum.tsx";
import NOMENCLATURE from "../../enums/NomenclaturesEnum.tsx";
import PARAMETRE from "../../enums/ParametreEnum.tsx";
import url from "../../module/fetch.tsx";
import { filterValuesToVariable } from "../../pages/Pei/FilterPei.tsx";
import getColumnPeiByStringArray from "../../utils/columnUtils.tsx";
import { useAppContext } from "../App/AppProvider.tsx";
import { useGet } from "../Fetch/useFetch.tsx";
import useLocalisation from "../Localisation/useLocalisation.tsx";

const ListPei = ({
  filterPage,
  filterId,
  className,
  displayNone,
}: ListePeiType) => {
  const { user } = useAppContext();
  const { fetchGeometry } = useLocalisation();

  const { data: listeAnomaliePossible } = useGet(
    url`/api/nomenclatures/list/` + NOMENCLATURE.ANOMALIE,
  );

  let peiColonnes: COLUMN_PEI[] = [];
  let isFicheResumeStandalone = false;

  let delaisWarnCTP: number,
    delaisUrgentCTP: number,
    delaisWarnRECO: number,
    delaisUrgentRECO: number,
    libelleNonConforme: string;
  const parametrePeiColonnes = PARAMETRE.PEI_COLONNES;
  const paramPeiFicheResumeStandalone = PARAMETRE.PEI_FICHE_RESUME_STANDALONE;

  const listeParametre = useGet(
    url`/api/parametres?${{
      listeParametreCode: JSON.stringify([
        parametrePeiColonnes,
        paramPeiFicheResumeStandalone,
        PARAMETRE.PEI_DELAI_CTRL_URGENT,
        PARAMETRE.PEI_DELAI_CTRL_WARN,
        PARAMETRE.PEI_DELAI_RECO_URGENT,
        PARAMETRE.PEI_DELAI_RECO_WARN,
        PARAMETRE.PEI_LIBELLE_NON_CONFORME,
      ]),
    }}`,
  );

  if (listeParametre.isResolved) {
    // Le résultat est un String, on le parse pour récupérer le tableau
    peiColonnes = listeParametre?.data?.[parametrePeiColonnes].parametreValeur
      ? JSON.parse(listeParametre?.data?.[parametrePeiColonnes].parametreValeur)
      : peiColonnes;
    isFicheResumeStandalone = JSON.parse(
      listeParametre?.data?.[paramPeiFicheResumeStandalone].parametreValeur,
    );

    delaisWarnCTP =
      listeParametre?.data?.[PARAMETRE.PEI_DELAI_CTRL_WARN].parametreValeur;
    delaisUrgentCTP =
      listeParametre?.data?.[PARAMETRE.PEI_DELAI_CTRL_URGENT].parametreValeur;
    delaisWarnRECO =
      listeParametre?.data?.[PARAMETRE.PEI_DELAI_RECO_WARN].parametreValeur;
    delaisUrgentRECO =
      listeParametre?.data?.[PARAMETRE.PEI_DELAI_RECO_URGENT].parametreValeur;
    libelleNonConforme =
      listeParametre?.data?.[PARAMETRE.PEI_LIBELLE_NON_CONFORME]
        .parametreValeur;
  }

  const filter = {
    peiNumeroComplet: "",
    peiNumeroInterne: "",
    communeId: "",
    natureId: "",
    peiDisponibiliteTerrestre: "",
    penaDisponibiliteHbe: "",
    typePei: "",
    natureDeci: "",
    autoriteDeci: "",
    servicePublicDeci: "",
    listeAnomalie: "",
    tourneeLibelle: "",
    adresse: "",
    prochaineDateRop: undefined,
    prochaineDateCtp: undefined,
  };

  let urlTable;

  switch (filterPage) {
    case FILTER_PAGE.INDISPONIBILITE_TEMPORAIRE:
      if (filterId == null) {
        //filterId obligatoire quand filterPage est renseigné
        throw new Error(
          "Le filterId est obligatoire si le filterPage est renseigné",
        );
      }
      urlTable = url`/api/pei/get-by-indispo/` + filterId;
      break;
    case FILTER_PAGE.TOURNEE:
      if (filterId == null) {
        //filterId obligatoire quand filterPage est renseigné
        throw new Error(
          "Le filterId est obligatoire si le filterPage est renseigné",
        );
      }
      urlTable = url`/api/pei/get-by-tournee/` + filterId;
      break;
    case FILTER_PAGE.PEI_LONGUE_INDISPO:
      urlTable = url`/api/message-pei-longue-indispo/pei`;
      break;
    default:
      urlTable = url`/api/pei`;
  }

  return (
    <>
      <QueryTable
        displayNone={displayNone}
        className={className}
        query={urlTable}
        columns={getColumnPeiByStringArray(
          user,
          peiColonnes,
          listeAnomaliePossible,
          fetchGeometry,
          isFicheResumeStandalone,
          delaisWarnCTP,
          delaisUrgentCTP,
          delaisWarnRECO,
          delaisUrgentRECO,
          libelleNonConforme,
        )}
        idName={"PeiTable"}
        filterValuesToVariable={filterValuesToVariable}
        filterContext={useFilterContext({ filter })}
      />
    </>
  );
};

type ListePeiType = {
  filterPage?: FILTER_PAGE;
  filterId?: string;
  className?: string;
  displayNone?: boolean;
};

export default ListPei;

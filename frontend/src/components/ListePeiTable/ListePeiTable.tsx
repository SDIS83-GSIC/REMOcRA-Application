import QueryTable, {
  useFilterContext,
} from "../../components/Table/QueryTable.tsx";
import COLUMN_PEI from "../../enums/ColumnPeiEnum.tsx";
import FILTER_PAGE from "../../enums/FilterPageEnum.tsx";
import url from "../../module/fetch.tsx";
import { filterValuesToVariable } from "../../pages/Pei/FilterPei.tsx";
import getColumnPeiByStringArray from "../../utils/columnUtils.tsx";
import { useGet } from "../Fetch/useFetch.tsx";

const ListPei = ({
  filterPage,
  filterId,
  className,
  displayNone,
}: ListePeiType) => {
  let peiColonnes: COLUMN_PEI[] = [];

  const PEI_COLONNES = "PEI_COLONNES";

  const parametrePeiColonnes = [PEI_COLONNES];

  const listeParametre = useGet(
    url`/api/parametres?${{
      listeParametreCode: JSON.stringify(parametrePeiColonnes),
    }}`,
  );

  if (listeParametre.isResolved) {
    // Le résultat est un String, on le parse pour récupérer le tableau
    peiColonnes = JSON.parse(
      listeParametre?.data[PEI_COLONNES].parametreValeur,
    );
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
    default:
      urlTable = url`/api/pei`;
  }

  return (
    <>
      <QueryTable
        displayNone={displayNone}
        className={className}
        query={urlTable}
        columns={getColumnPeiByStringArray(peiColonnes)}
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

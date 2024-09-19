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

  const listeParametre = useGet("/api/admin/parametres");
  if (listeParametre.isResolved) {
    peiColonnes = listeParametre?.data.pei.peiColonnes;
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

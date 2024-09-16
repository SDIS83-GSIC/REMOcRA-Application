import QueryTable, {
  useFilterContext,
} from "../../components/Table/QueryTable.tsx";
import getColumnPeiByStringArray from "../../utils/columnUtils.tsx";
import COLUMN_PEI from "../../enums/ColumnPeiEnum.tsx";
import url from "../../module/fetch.tsx";
import { filterValuesToVariable } from "../../pages/Pei/FilterPei.tsx";
import { useGet } from "../Fetch/useFetch.tsx";

const ListPei = () => {
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
  };

  return (
    <>
      <QueryTable
        query={url`/api/pei`}
        columns={getColumnPeiByStringArray(peiColonnes)}
        idName={"PeiTable"}
        filterValuesToVariable={filterValuesToVariable}
        filterContext={useFilterContext({ filter })}
      />
    </>
  );
};

export default ListPei;

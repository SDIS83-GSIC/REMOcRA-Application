import url from "../../module/fetch.tsx";
import QueryTable, {
  useFilterContext,
} from "../../components/Table/QueryTable.tsx";
import getColumnByStringArray from "../../utils/columnUtils.tsx";
import COLUMN_PEI from "../../enums/ColumnPeiEnum.tsx";
import { filterValuesToVariable } from "./FilterPei.tsx";

const AccueilPei = () => {
  //TODO a aller chercher en base
  const column: COLUMN_PEI[] = [
    COLUMN_PEI.NUMERO_COMPLET,
    COLUMN_PEI.NUMERO_INTERNE,
    COLUMN_PEI.DISPONIBILITE_TERRESTRE,
    COLUMN_PEI.DISPONIBILITE_HBE,
    COLUMN_PEI.COMMUNE,
    COLUMN_PEI.NATURE,
    COLUMN_PEI.TYPE_PEI,
    COLUMN_PEI.NATURE_DECI,
    COLUMN_PEI.AUTORITE_DECI,
    COLUMN_PEI.SERVICE_PUBLIC_DECI,
    COLUMN_PEI.ANOMALIES,
  ];

  return (
    <>
      <QueryTable
        query={url`/api/pei`}
        columns={getColumnByStringArray(column)}
        idName={"PeiTable"}
        filterValuesToVariable={filterValuesToVariable}
        filterContext={useFilterContext({
          peiNumeroComplet: undefined,
          peiNumeroInterne: undefined,
          communeId: undefined,
          peiDisponibiliteTerrestre: undefined,
          penaDisponibiliteHbe: undefined,
          listeAnomalie: undefined,
        })}
      />
    </>
  );
};

export default AccueilPei;

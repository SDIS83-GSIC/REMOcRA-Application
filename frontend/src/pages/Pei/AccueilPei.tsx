import { Container } from "react-bootstrap";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { IconPei } from "../../components/Icon/Icon.tsx";
import QueryTable, {
  useFilterContext,
} from "../../components/Table/QueryTable.tsx";
import COLUMN_PEI from "../../enums/ColumnPeiEnum.tsx";
import url from "../../module/fetch.tsx";
import getColumnByStringArray from "../../utils/columnUtils.tsx";
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
    <Container>
      <PageTitle icon={<IconPei />} title={"Liste des points d'eau"} />
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
    </Container>
  );
};

export default AccueilPei;

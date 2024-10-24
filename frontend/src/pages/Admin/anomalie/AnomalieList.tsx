import { Badge, Table } from "react-bootstrap";
import Container from "react-bootstrap/Container";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import url from "../../../module/fetch.tsx";
import Loading from "../../../components/Elements/Loading/Loading.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { IconAnomalie } from "../../../components/Icon/Icon.tsx";
import TooltipCustom from "../../../components/Tooltip/Tooltip.tsx";

const AnomalieList = () => {
  const anomalieListState = useGet(url`/api/anomalie/list`);

  if (!anomalieListState.isResolved) {
    return <Loading />;
  }

  const { anomalieList, categorieList, natureList, typeList } =
    anomalieListState.data;
  const typeMap = typeList.map((t) => {
    return {
      label: t,
      count: natureList.filter((n) => n.natureTypePei === t).length,
    };
  });
  const natureCount = natureList.length;

  return (
    <Container>
      <PageTitle title="Liste des anomalies" icon={<IconAnomalie />} />
      <Table striped bordered hover>
        <thead>
          <tr>
            <th rowSpan={2}>Anomalie</th>
            {typeMap.map((_type, idxNT) => (
              <th key={idxNT} colSpan={_type.count}>
                {_type.label}
              </th>
            ))}
          </tr>
          <tr>
            {natureList.map((nature, idxN) => (
              <th key={idxN}>{nature.natureLibelle}</th>
            ))}
          </tr>
        </thead>
        {categorieList.map((categorie, idxC) => (
          <tbody key={idxC}>
            <tr>
              <th colSpan={natureCount + 1}>
                {categorie.anomalieCategorieLibelle}
              </th>
            </tr>
            {anomalieList
              .filter(
                ({ anomalie }) =>
                  anomalie.anomalieAnomalieCategorieId ===
                  categorie.anomalieCategorieId,
              )
              .map(({ anomalie, anomaliePoidsList }, idxA) => (
                <tr key={idxA}>
                  <th>{anomalie.anomalieLibelle}</th>
                  {natureList.map((nature, idxN) => (
                    <td key={`${idxA}${idxN}`}>
                      {anomaliePoidsList
                        .filter(
                          (ap) => ap.poidsAnomalieNatureId === nature.natureId,
                        )
                        .map((ap) =>
                          ap.poidsAnomalieTypeVisite?.length > 0 ? (
                            <TooltipCustom
                              key={ap.anomaliePoidsId}
                              tooltipId={ap.anomaliePoidsId}
                              tooltipHeader={"Types de visite"}
                              tooltipText={ap.poidsAnomalieTypeVisite.join(
                                ", ",
                              )}
                            >
                              {ap.poidsAnomalieValIndispoTerrestre || "-"}
                              &nbsp;/&nbsp;
                              {ap.poidsAnomalieValIndispoHbe || "-"}
                              &nbsp;
                              <Badge bg="primary" pill={true}>
                                {ap.poidsAnomalieTypeVisite.length}
                              </Badge>
                            </TooltipCustom>
                          ) : (
                            <>
                              {ap.poidsAnomalieValIndispoTerrestre || "-"}
                              &nbsp;/&nbsp;
                              {ap.poidsAnomalieValIndispoHbe || "-"}
                            </>
                          ),
                        )}
                    </td>
                  ))}
                </tr>
              ))}
          </tbody>
        ))}
      </Table>
    </Container>
  );
};

export default AnomalieList;

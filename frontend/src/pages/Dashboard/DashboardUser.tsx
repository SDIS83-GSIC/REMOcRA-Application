import { Col, Container, Row } from "react-bootstrap";
import { useCallback, useEffect, useState } from "react";
import { useToastContext } from "../../module/Toast/ToastProvider.tsx";
import url, { getFetchOptions } from "../../module/fetch.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import {
  ComponentDashboard,
  DashboardComponentConfig,
  DashboardItemParam,
  formatData,
  QueryData,
} from "./Constants.tsx";
import DashboardItem from "./DashboardAdminDynamicConfig/DashboardItem.tsx";

const ComponentBoardList = () => {
  const { error: errorToast } = useToastContext();

  const [dashboardUser, setDashoardUser] =
    useState<DashboardItemParam | null>(); // Liste des onglets dashboard
  const [listQuerysData, setListQuerysData] = useState<QueryData[] | null>(); // Liste des datas des requêtes
  const [componentsListDashboard, setComponentsListDashboard] =
    useState<ComponentDashboard[]>(); // Composant sélectionner dans la grid

  const [numberRowGrid, setNumberRowGrid] = useState<number>(0);
  const heightRow = 200;

  const urlApiDataQuerys = url`/api/dashboard/get-list-data-query/`;

  // Récupère tous les dashboards en base liés au profil utilisateur
  const fetchDataDashboard = useGet(
    url`/api/dashboard/get-dashboard-user/`,
    {},
  );

  // Récupère en base les données liées à une requête et les set sur les composants
  const fetchDataQuery = useCallback(
    async (queryIds?: string[] | undefined) => {
      (
        await fetch(
          urlApiDataQuerys,
          getFetchOptions({
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ dashboardQueryIds: queryIds }),
          }),
        )
      )
        .json()
        .then((resData) => {
          const newActiveQuerysData: QueryData[] = [];
          // Formatte et stocke les datas des requêtes SQL pour usage des composants
          resData.forEach((dataQuery: any) => {
            const dataFormatted = formatData(dataQuery);
            newActiveQuerysData.push({
              id: dataQuery.queryId,
              data: dataFormatted,
            });
          });
          setListQuerysData(newActiveQuerysData);

          // Set les datas dans chaque composant
          const newComponentList = componentsListDashboard?.map(
            (component) => ({
              ...component,
              data: newActiveQuerysData.find(
                (query) => query.id === component.queryId,
              )?.data,
            }),
          );

          setComponentsListDashboard(newComponentList);
        })
        .catch((reason: string) => {
          errorToast(reason);
        });
    },
    [componentsListDashboard, errorToast, urlApiDataQuerys],
  );

  useEffect(() => {
    if (fetchDataDashboard.isResolved && fetchDataDashboard.data) {
      setDashoardUser({
        id: fetchDataDashboard.data.dashboardId,
        title: fetchDataDashboard.data.dashboardTitle,
      });
      const newComponentList: ComponentDashboard[] = [];
      fetchDataDashboard.data.dashboardComponents.forEach(
        (element: DashboardComponentConfig) => {
          // Set les composants à affiché dans la grille
          newComponentList.push({
            id: element.componentId,
            key: element.componentKey,
            title: element.componentTitle,
            config: element.componentConfig,
            configPosition: {
              x: element.componentConfigPosition
                ? element.componentConfigPosition.componentX
                : 0,
              y: element.componentConfigPosition
                ? element.componentConfigPosition.componentY
                : 0,
              size: element.componentConfigPosition
                ? element.componentConfigPosition.componentSize
                : 3,
            },
            queryId: element.componentQueryId,
          });
        },
      );
      if (newComponentList.length > 0) {
        calculateRows(newComponentList);
        setComponentsListDashboard(newComponentList);
      }
    }
  }, [fetchDataDashboard.data, fetchDataDashboard.isResolved]);

  // Tri les composants par leur position actuelle `y` (du plus haut au plus bas)
  const calculateRows = (componentList: ComponentDashboard[]) => {
    const sortedComponents = [...componentList]
      .sort(
        (a, b) =>
          (a.configPosition ? a.configPosition.y : 0) -
          (b.configPosition ? b.configPosition.y : 0),
      )
      .reverse();
    setNumberRowGrid(
      sortedComponents[0].configPosition
        ? sortedComponents[0].configPosition.y +
            sortedComponents[0].configPosition.size
        : 0,
    );
  };

  // Si pas de dashboard on les récupère en base
  useEffect(() => {
    if (!dashboardUser && !fetchDataDashboard.isLoading) {
      fetchDataDashboard.run();
    }
  }, [dashboardUser, fetchDataDashboard]);

  // Récupère les datas des requêtes et les set sur les composants
  useEffect(() => {
    if (!listQuerysData && componentsListDashboard) {
      const queryIds: string[] = [];
      componentsListDashboard.map((component) => {
        if (!queryIds.includes(component.queryId)) {
          queryIds.push(component.queryId);
        }
      });
      fetchDataQuery(queryIds);
    }
  }, [componentsListDashboard, fetchDataQuery, listQuerysData]);

  return (
    <>
      {dashboardUser && dashboardUser.id ? (
        <Container fluid>
          <Row className="my-3">
            <h3 className="text-primary">{dashboardUser?.title}</h3>
          </Row>
          {/* Grid Layout */}
          <Row className="position-relative">
            {!componentsListDashboard && (
              <div className="alert alert-primary" role="alert">
                Ce dashboard n&apos;a aucun composant pour le moment.
              </div>
            )}
            {Array.from({ length: 4 }, (_, colIndex) => (
              <Col
                id="col-grid-component"
                key={colIndex}
                className="p-0 position-relative"
                style={{ height: "100%" }}
              >
                {Array.from({ length: numberRowGrid }, (_, rowIndex) => (
                  <div
                    key={`${colIndex}-${rowIndex}`}
                    id={`${colIndex}-${rowIndex}`}
                    className="droppable"
                    style={{
                      height: heightRow + "px", // Hauteur des lignes
                    }}
                  />
                ))}
              </Col>
            ))}

            {/* Components */}
            {componentsListDashboard &&
              componentsListDashboard.length > 0 &&
              componentsListDashboard.map((component) => (
                <DashboardItem
                  key={component.id}
                  component={component}
                  isSelected={false}
                  heightRow={heightRow}
                />
              ))}
          </Row>
        </Container>
      ) : (
        <div className="alert alert-primary" role="alert">
          Aucun dashboard n&apos;est disponible pour votre profil.
        </div>
      )}
    </>
  );
};

export default ComponentBoardList;

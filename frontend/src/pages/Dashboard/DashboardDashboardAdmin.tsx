import { useEffect, useMemo, useState } from "react";
import { Col, Row } from "react-bootstrap";
import { Outlet } from "react-router-dom";
import url from "../../module/fetch.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import QueryComponentList from "./DashboardAdminDynamicConfig/QueryComponentList.tsx";
import ConfigDynamicDashboard from "./DashboardAdminDynamicConfig/ConfigDynamicDashboard.tsx";
import {
  ComponentDashboard,
  DashboardItemParam,
  DashboardParam,
} from "./Constants.tsx";

const ComponentBoardDashboardAdmin = () => {
  const [openListDashboard, setOpenListDashboard] =
    useState<DashboardItemParam[]>(); // Liste des onglets Dashboard

  const [openListQuery, setOpenListQuery] = useState<DashboardParam[]>(); // Liste des dashboard et ses composants
  const [editTabIndex, setEditTabIndex] = useState<number | null>(); // Index du dashboard à éditer
  const [activeDashboard, setActiveDashboard] =
    useState<DashboardItemParam | null>(null); // Dashboard ouvert à l'écran

  const [componentListIdSelected, setComponentListIdSelected] = useState(""); // Id du composant seléctionner dans la liste des requêtes et composant
  const [componentsListDashboard, setComponentsListDashboard] =
    useState<ComponentDashboard[]>(); // Composant sélectionner dans la grid

  const urlApiQuerys = url`/api/dashboard/get-query-list-all`;
  const urlApiDashboards = url`/api/dashboard/get-list-dashboard`;

  // Récupère les Composants de chaque requête en base
  const fetchDataQuery = useGet(urlApiQuerys);
  useMemo(() => {
    if (fetchDataQuery.isResolved) {
      const resOpenQueryList: {
        id: string;
        title: string;
        components: any;
      }[] = [];
      fetchDataQuery.data.forEach(
        (element: { queryId: any; queryTitle: any; queryComponents: any }) => {
          resOpenQueryList.push({
            id: element.queryId,
            title: element.queryTitle,
            components: element.queryComponents,
          });
        },
      );

      setOpenListQuery(resOpenQueryList);
    }
  }, [fetchDataQuery.data, fetchDataQuery.isResolved]);

  // Récupère tous les dashboard en base
  const fetchDataDashboard = useGet(urlApiDashboards);
  useMemo(() => {
    if (fetchDataDashboard.isResolved) {
      const resOpenDashboardList: {
        id: string;
        title: string;
        index: number;
      }[] = [];
      fetchDataDashboard.data.forEach(
        (
          element: {
            dashboardId: string;
            dashboardTitle: string;
          },
          index: number,
        ) => {
          resOpenDashboardList.push({
            id: element.dashboardId,
            title: element.dashboardTitle,
            index: index,
          });
        },
      );
      setOpenListDashboard(resOpenDashboardList);
    }
  }, [fetchDataDashboard.data, fetchDataDashboard.isResolved]);

  // Récupère les requêtes et dashboard en base lors du premier chargement du composant
  useEffect(() => {
    setEditTabIndex(null);
  }, [fetchDataDashboard, fetchDataQuery]);

  return (
    <>
      <Outlet />

      <Row>
        <Col sm={9}>
          <ConfigDynamicDashboard
            openListDashboard={openListDashboard}
            setOpenListDashboard={setOpenListDashboard}
            activeDashboard={activeDashboard}
            setActiveDashboard={setActiveDashboard}
            componentListIdSelected={componentListIdSelected}
            setcomponentListIdSelected={setComponentListIdSelected}
            editTabIndex={editTabIndex}
            setEditTabIndex={setEditTabIndex}
            componentsListDashboard={componentsListDashboard}
            setComponentsListDashboard={setComponentsListDashboard}
          />
        </Col>
        {activeDashboard && editTabIndex !== null && (
          <Col sm={3}>
            <QueryComponentList
              openListQuery={openListQuery}
              componentListIdSelected={componentListIdSelected}
              setComponentListIdSelected={setComponentListIdSelected}
              componentsListDashboard={componentsListDashboard}
            />
          </Col>
        )}
      </Row>
    </>
  );
};

export default ComponentBoardDashboardAdmin;

import { useEffect, useState } from "react";
import { Card, ListGroup, OverlayTrigger, Tooltip } from "react-bootstrap";
import { ComponentDashboard, ICONS, DashboardParam } from "../Constants.tsx";
import { useGetRun } from "../../../components/Fetch/useFetch.tsx";
import url from "../../../module/fetch.tsx";

type QueryComponentListProps = {
  openListQuery: DashboardParam[] | undefined;
  setComponentsListDashboard: React.Dispatch<
    React.SetStateAction<ComponentDashboard[] | undefined>
  >;
  componentsListDashboard: ComponentDashboard[] | undefined;
};

const QueryComponentList = ({
  openListQuery,
  setComponentsListDashboard,
  componentsListDashboard,
}: QueryComponentListProps) => {
  const [queryId, setQueryId] = useState<string | null>(null); // ID de la requête sélectionnée

  const { run: runDataQuerys, data: configDataQuerys } = useGetRun(
    url`/api/dashboard/get-component-config/` + queryId,
    {},
  );

  useEffect(() => {
    if (queryId) {
      runDataQuerys();
    }
  }, [queryId, runDataQuerys]);

  useEffect(() => {
    if (configDataQuerys) {
      // Position initial lors de l'ajout d'un nouveau composant
      const configPosition = {
        size: 3,
        x: 0,
        y: 0,
      };
      // Créer le nouveau composant
      const componentAdd = {
        id: configDataQuerys.dashboardComponentId,
        queryId: configDataQuerys.dashboardComponentDahsboardQueryId,
        key: configDataQuerys.dashboardComponentKey,
        title: configDataQuerys.dashboardComponentTitle,
        config: configDataQuerys.dashboardComponentConfig,
        configPosition: configPosition,
      };

      setComponentsListDashboard((prev: ComponentDashboard[] | undefined) => [
        ...(prev ?? []),
        componentAdd,
      ]);
    }
  }, [configDataQuerys, setComponentsListDashboard]);

  // Ajoute l'ID du composant sélectionné
  const handleItemClick = (id: string) => {
    setQueryId(id);
  };

  return (
    <Card bg="secondary" className="m-3">
      <Card.Header className="d-flex justify-content-center align-items-center">
        <span>Liste requêtes et composants</span>
      </Card.Header>
      <ListGroup
        variant="flush"
        className="overflow-auto"
        style={{ maxHeight: "400px" }}
      >
        {openListQuery &&
          openListQuery.map((query) => (
            <ListGroup.Item key={query.id} className="mb-3">
              <OverlayTrigger
                placement="top"
                overlay={
                  <Tooltip id={`tooltip-${query.id}`}>
                    {query.title}{" "}
                    {/* Affiche le texte complet dans le tooltip */}
                  </Tooltip>
                }
              >
                <strong className="d-block">{query.title}</strong>
              </OverlayTrigger>
              <Card>
                <ListGroup variant="flush" className="overflow-auto">
                  {query.components.map(
                    (component: {
                      componentId: string;
                      componentTitle: string;
                      componentKey: string;
                    }) => {
                      // Si composant déjà sélectionné on le désactive
                      const isInDashboard = componentsListDashboard
                        ? componentsListDashboard.find(
                            (componentInDashboard) =>
                              componentInDashboard.id === component.componentId,
                          )
                        : false;

                      return (
                        <ListGroup.Item
                          key={component.componentId}
                          role="button"
                          className={`d-flex justify-content-between align-items-center ${
                            isInDashboard
                              ? "bg-light text-muted"
                              : queryId === component.componentId
                                ? "bg-primary text-white"
                                : ""
                          }`}
                          style={{
                            cursor: isInDashboard ? "default" : "pointer",
                          }}
                          {...(!isInDashboard && {
                            onClick: () =>
                              handleItemClick(component.componentId),
                          })}
                        >
                          <span className="text-truncate">
                            {component.componentTitle}
                          </span>
                          {ICONS[component.componentKey as keyof typeof ICONS]}
                        </ListGroup.Item>
                      );
                    },
                  )}
                </ListGroup>
              </Card>
            </ListGroup.Item>
          ))}
      </ListGroup>
    </Card>
  );
};

export default QueryComponentList;

import { useState } from "react";
import { Card, ListGroup, OverlayTrigger, Tooltip } from "react-bootstrap";
import { ComponentDashboard, ICONS, DashboardParam } from "../Constants.tsx";

type QueryComponentListProps = {
  openListQuery: DashboardParam[] | undefined;
  componentListIdSelected: string;
  setComponentListIdSelected: (arg0: string) => void;
  componentsListDashboard: ComponentDashboard[] | undefined;
};

const QueryComponentList = (props: QueryComponentListProps) => {
  const [clickedItemId, setClickedItemId] = useState<string | null>(null); // ID composant sélectionné (pour visuel action click)

  // Ajoute l'ID du composant sélectionné
  const handleItemClick = (id: string) => {
    setClickedItemId(id);
    props.setComponentListIdSelected(id);

    setTimeout(() => {
      setClickedItemId(null);
    }, 200);
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
        {props.openListQuery &&
          props.openListQuery.map((query) => (
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
                      const isInDashboard = props.componentsListDashboard
                        ? props.componentsListDashboard.find(
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
                              : clickedItemId === component.componentId
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

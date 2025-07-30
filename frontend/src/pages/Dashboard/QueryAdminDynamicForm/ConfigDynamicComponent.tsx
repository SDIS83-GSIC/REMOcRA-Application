import { Card, Nav, Button, Dropdown } from "react-bootstrap";
import { SetStateAction, useState } from "react";
import {
  ComponentDashboard,
  COMPONENTS,
  INIT_DATA,
  FORM_CONFIG,
} from "../Constants.tsx";
import ConfirmModal from "../../../components/Modal/ConfirmModal.tsx";

type ConfigDynamicComponentProps = {
  data: any;
  setData: (arg0: null) => void;
  openListComponent: ComponentDashboard[] | undefined | null;
  setOpenListComponent: (arg0: any[]) => void;
  queryGlobalData: any[];
  selectedComponent: ComponentDashboard | null;
  setSelectedComponent: (arg0: any) => void;
};

const ConfigDynamicComponent = (props: ConfigDynamicComponentProps) => {
  const [disabledModal, setDisabledModal] = useState(false); // Affiche la modal de confirmation
  const [indexToRemove, setIndextoremove] = useState<number | null>(); // Stocke l'index de l'onglet à fermer

  // Ajoute un nouvel onglet avec une configuration unique
  const handleAddTab = (key: string | SetStateAction<null>) => {
    const component = COMPONENTS[key as keyof typeof COMPONENTS];
    const formConfig = FORM_CONFIG[key as keyof typeof FORM_CONFIG];
    const dataConfig = INIT_DATA[key as keyof typeof INIT_DATA];

    const newTab = {
      index: null, // Sert uniquement en front pour la gestion des onglets
      key,
      title: key,
      component: component,
      formConfig: formConfig,
      config: dataConfig, // Stockage d’une config unique
    };

    const newOpenComponent = props.openListComponent
      ? [...props.openListComponent, newTab]
      : [newTab];

    // Réinitialisation des indexKey pour chaque onglet
    const resetOpenTabs = newOpenComponent.map((tab, index) => ({
      ...tab,
      index: index,
    }));

    props.setOpenListComponent(resetOpenTabs);
    props.setSelectedComponent(resetOpenTabs[resetOpenTabs.length - 1]);
    props.setData(null);
  };

  // Ferme l'onglet sélectionné
  const handleCloseTab = () => {
    if (props.openListComponent) {
      const updatedComponent = props.openListComponent.filter(
        (tab) => tab.index !== indexToRemove,
      );
      // Réinitialisation des index pour chaque onglet
      const resetOpenTabs = updatedComponent.map((tab: any, index: any) => ({
        ...tab,
        index: index,
      }));
      props.setOpenListComponent(resetOpenTabs);
      if (resetOpenTabs.length > 0) {
        props.setSelectedComponent(0);
        handleTabClick(0);
      } else {
        props.setSelectedComponent(null);
      }
      setIndextoremove(null);
    }
  };

  // Sélectionne l'onglet
  const handleTabClick = (indexKey: number) => {
    if (props.openListComponent) {
      const updateActiveComponent = props.openListComponent.find(
        (tab) => tab.index === indexKey,
      );
      props.setSelectedComponent(updateActiveComponent);
    }
  };

  return (
    <Card bg="secondary" className="m-3">
      <Card.Body>
        <div className="flex-grow-1 d-flex flex-column">
          <div className="d-flex align-items-center">
            {/* Barre d'onglets Composant */}
            <Nav variant="tabs">
              {props.openListComponent &&
                props.openListComponent.map((component: ComponentDashboard) => (
                  <Nav.Item key={component.index}>
                    <Nav.Link
                      eventKey={component.index}
                      onClick={() => handleTabClick(component.index || 0)}
                      className={
                        props.selectedComponent &&
                        props.selectedComponent.index === component.index
                          ? "active d-flex align-items-center"
                          : "d-flex align-items-center"
                      }
                    >
                      <div
                        className="col-4d-flex align-items-center text-truncate"
                        title={component.title}
                        style={{ width: "11rem" }}
                      >
                        {component.title}
                      </div>
                      <Button
                        variant="link"
                        size="sm"
                        onClick={(e) => {
                          e.stopPropagation();
                          setIndextoremove(component.index);
                          setDisabledModal(true);
                        }}
                        className="ms-2"
                      >
                        &times;
                      </Button>
                    </Nav.Link>
                  </Nav.Item>
                ))}
            </Nav>

            {/* Bouton pour ajouter un nouvel onglet */}
            <Dropdown className="ms-2">
              <Dropdown.Toggle variant="primary">+</Dropdown.Toggle>
              <Dropdown.Menu>
                {Object.keys(COMPONENTS).map((key) => (
                  <Dropdown.Item key={key} onClick={() => handleAddTab(key)}>
                    {key}
                  </Dropdown.Item>
                ))}
              </Dropdown.Menu>
            </Dropdown>
          </div>

          {/* Contenu des onglets */}
          <div className="flex-grow-1 mt-3" style={{ height: "400px" }}>
            {props.selectedComponent && props.openListComponent ? (
              props.openListComponent.map(
                ({ index, key, component: Component, config: config }) =>
                  index === props.selectedComponent?.index && (
                    <Component
                      key={key}
                      data={props.queryGlobalData}
                      config={config}
                    />
                  ),
              )
            ) : (
              <div className="alert alert-primary" role="alert">
                Aucun composant n&apos;est sélectionné.
              </div>
            )}
          </div>
        </div>
      </Card.Body>
      <ConfirmModal
        visible={disabledModal}
        content="Supprimer le composant ?"
        closeModal={() => setDisabledModal(false)}
        query={""}
        href="#"
        onConfirm={() => handleCloseTab()}
      />
    </Card>
  );
};

export default ConfigDynamicComponent;

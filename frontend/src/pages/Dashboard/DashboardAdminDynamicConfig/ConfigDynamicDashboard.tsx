import { Nav, Button } from "react-bootstrap";
import { useState } from "react";
import { DashboardItemParam } from "../Constants.tsx";
import ConfirmModal from "../../../components/Modal/ConfirmModal.tsx";
import url, { getFetchOptions } from "../../../module/fetch.tsx";
import { useToastContext } from "../../../module/Toast/ToastProvider.tsx";
import ConfigDynamicGrid from "./ConfigDynamicGrid.tsx";

type ConfigDynamicDashboardProps = {
  openListDashboard: DashboardItemParam[] | undefined;
  setOpenListDashboard: any;
  activeDashboard: DashboardItemParam | null;
  setActiveDashboard: (arg0: any) => void;
  componentListIdSelected: string;
  setcomponentListIdSelected: (arg0: string) => void;
  editTabIndex: number | null | undefined;
  setEditTabIndex: any;
  componentsListDashboard: any;
  setComponentsListDashboard: any;
};

const ConfigDynamicDashboard = (props: ConfigDynamicDashboardProps) => {
  const { error: errorToast, success: successToast } = useToastContext();

  const [disabledModal, setDisabledModal] = useState(false); // Affiche la modal de confirmation
  const [indexToRemove, setIdtoremove] = useState<number | null>();

  const urlApiDeleteDashboard = url`/api/dashboard/delete-dashboard/`;

  // Sélectionne l'onglet du dashboard cliqué
  const handleDashboardClick = (indexKey: number) => {
    if (props.openListDashboard && props.editTabIndex === null) {
      props.setComponentsListDashboard(null);
      props.setActiveDashboard(props.openListDashboard[indexKey]);
    }
  };

  // édite le dashboard
  const handleEditTab = (indexKey: number | null) => {
    if (props.openListDashboard) {
      props.setEditTabIndex(indexKey);
      props.setActiveDashboard(props.openListDashboard[indexKey || 0]);
    }
  };

  // Ferme l'onglet sélectionné
  const handleCloseTab = (indexKey: number) => {
    if (props.openListDashboard) {
      const dahsboarToRemove = props.openListDashboard.find(
        (dashboard) => dashboard.index === indexKey,
      );

      if (dahsboarToRemove && dahsboarToRemove.id) {
        fetchDeleteDashboard(dahsboarToRemove, indexKey);
      } else {
        updateDashboardList(indexKey);
      }
    }
  };

  // Met à jour les onglets
  const updateDashboardList = (indexKey: number) => {
    if (props.openListDashboard) {
      const updatedDashboard = props.openListDashboard.filter(
        (dashboard) => dashboard?.index !== indexKey,
      );
      // Réinitialisation des indexKey pour chaque onglet
      const resetOpenListDashboard = updatedDashboard.map(
        (dashboard: any, index: any) => ({
          ...dashboard,
          index: index,
        }),
      );
      props.setActiveDashboard(null);
      props.setOpenListDashboard(resetOpenListDashboard);
    }
  };

  // Supprime le dashboard en base
  const fetchDeleteDashboard = async (
    dashboard: DashboardItemParam,
    indexKey: number,
  ) => {
    (
      await fetch(
        urlApiDeleteDashboard + dashboard.id,
        getFetchOptions({
          method: "DELETE",
          headers: { "Content-Type": "application/json" },
        }),
      )
    )
      .json()
      .then(() => {
        updateDashboardList(indexKey);
        successToast("Le dashboard à correctement été supprimé");
      })
      .catch((reason: string) => {
        errorToast(reason);
      });
  };

  // Ajoute un nouvel onglet pour un nouveau dashoard
  const handleAddDashboard = () => {
    const newDashboard = {
      index: null,
      title: "Nouveau tableau de bord",
    };

    const newOpenListDashboard = props.openListDashboard
      ? [...props.openListDashboard, newDashboard]
      : [];

    // Réinitialise les index
    const resetOpenListDashboard = newOpenListDashboard.map(
      (dashboard, index) => ({
        ...dashboard,
        index: index,
      }),
    );

    // On vide le dashboard des composants présents à l'écran
    props.setComponentsListDashboard(null);
    props.setOpenListDashboard(resetOpenListDashboard);
    props.setActiveDashboard(
      resetOpenListDashboard[resetOpenListDashboard.length - 1],
    );
  };

  // Annule l'édition du dashboard
  const handleCancelEdit = () => {
    props.setcomponentListIdSelected("");
    props.setComponentsListDashboard(null);
    props.setActiveDashboard(null);
    props.setEditTabIndex(null);
  };

  return (
    <>
      <div className="flex-grow-1 d-flex flex-column">
        <div className="d-flex align-items-center">
          <Nav variant="tabs">
            {props.openListDashboard &&
              props.openListDashboard.map((dashboard) => (
                <Nav.Item
                  key={dashboard.index}
                  className={
                    props.editTabIndex !== null &&
                    props.editTabIndex !== dashboard.index
                      ? "d-none"
                      : ""
                  }
                >
                  <Nav.Link
                    eventKey={dashboard.index}
                    onClick={() => handleDashboardClick(dashboard.index || 0)}
                    className={
                      props.activeDashboard &&
                      props.activeDashboard.index === dashboard.index
                        ? "active d-flex align-items-center"
                        : "d-flex align-items-center"
                    }
                  >
                    <div
                      className="col-4d-flex align-items-center text-truncate"
                      title={dashboard.title}
                      style={{ width: "11rem" }}
                    >
                      {dashboard.title}
                    </div>

                    {props.editTabIndex === null ? (
                      <>
                        <Button
                          variant="link"
                          size="sm"
                          onClick={(e) => {
                            e.stopPropagation();
                            handleEditTab(dashboard.index || 0);
                          }}
                          className="ms-2"
                        >
                          ✎
                        </Button>
                        <Button
                          variant="link"
                          size="sm"
                          onClick={(e) => {
                            e.stopPropagation();
                            setIdtoremove(dashboard.index || 0);
                            setDisabledModal(true);
                          }}
                          className="ms-2"
                        >
                          &times;
                        </Button>
                      </>
                    ) : null}
                  </Nav.Link>
                </Nav.Item>
              ))}
          </Nav>

          {/* Bouton pour ajouter un nouvel onglet */}
          {props.editTabIndex === null ? (
            <Button className="ms-2" onClick={handleAddDashboard}>
              Ajouter +
            </Button>
          ) : (
            <Button
              variant="secondary"
              className="ms-2"
              onClick={handleCancelEdit}
            >
              Annuler
            </Button>
          )}
        </div>
      </div>
      {/* Contenu des onglets */}
      <div className="flex-grow-1 mt-3">
        {props.activeDashboard ? (
          <ConfigDynamicGrid
            componentListIdSelected={props.componentListIdSelected}
            setcomponentListIdSelected={props.setcomponentListIdSelected}
            editTabIndex={props.editTabIndex}
            componentsListDashboard={props.componentsListDashboard}
            setComponentsListDashboard={props.setComponentsListDashboard}
            setEditTabIndex={props.setEditTabIndex}
            activeDashboard={props.activeDashboard}
          />
        ) : (
          <div className="alert alert-primary" role="alert">
            Aucun dashboard n&apos;est sélectionné, veuillez en choisir un ou en
            ajouter.
          </div>
        )}
      </div>
      {disabledModal && (
        <ConfirmModal
          visible={true}
          content="Supprimer le dashboard ?"
          closeModal={() => setDisabledModal(false)}
          query={""}
          href="#"
          onConfirm={() => handleCloseTab(indexToRemove || 0)}
        />
      )}
    </>
  );
};

export default ConfigDynamicDashboard;

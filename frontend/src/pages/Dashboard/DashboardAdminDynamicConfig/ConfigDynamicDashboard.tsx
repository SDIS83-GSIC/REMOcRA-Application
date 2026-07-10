import { Dispatch, SetStateAction, useEffect, useRef, useState } from "react";
import { Button, Nav } from "react-bootstrap";
import CreateButton from "../../../components/Button/CreateButton.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import {
  IconDelete,
  IconEdit,
  IconGaugeComponent,
} from "../../../components/Icon/Icon.tsx";
import ConfirmModal from "../../../components/Modal/ConfirmModal.tsx";
import url, { getFetchOptions } from "../../../module/fetch.tsx";
import { useToastContext } from "../../../module/Toast/ToastProvider.tsx";
import { ComponentDashboard, DashboardItemParam } from "../Constants.tsx";
import ConfigDynamicGrid from "./ConfigDynamicGrid.tsx";

// cache : tout ce qui est nécessaire pour réafficher sans fetch
type DashboardProfilApiEntry =
  | string
  | { profilUtilisateurId?: string | number; [key: string]: unknown };

export type DashboardCacheEntry = {
  components: ComponentDashboard[];
  profils: DashboardProfilApiEntry[];
};

type ConfigDynamicDashboardProps = {
  openListDashboard: DashboardItemParam[] | undefined;
  setOpenListDashboard: (arg0: DashboardItemParam[] | undefined) => void;
  activeDashboard: DashboardItemParam | null;
  setActiveDashboard: (arg0: DashboardItemParam | null) => void;
  editTabIndex: number | null | undefined;
  setEditTabIndex: (arg0: number | null | undefined) => void;
  componentsListDashboard: ComponentDashboard[] | null;
  setComponentsListDashboard: Dispatch<
    SetStateAction<ComponentDashboard[] | null>
  >;
};

const ConfigDynamicDashboard = (props: ConfigDynamicDashboardProps) => {
  const { error: errorToast, success: successToast } = useToastContext();

  const [disabledModal, setDisabledModal] = useState(false);
  const [indexToRemove, setIdtoremove] = useState<number | null>();
  const [gridReloadVersion, setGridReloadVersion] = useState(0);

  const abortControllerRef = useRef<AbortController | null>(null);

  // Cache mémoire
  // Survit aux changements d'onglet, perdu uniquement au démontage / reload
  const dashboardCacheRef = useRef<Map<string, DashboardCacheEntry>>(new Map());

  useEffect(() => {
    return () => {
      abortControllerRef.current?.abort();
    };
  }, []);

  const urlApiDeleteDashboard = url`/api/dashboard/delete-dashboard/`;

  // Sélectionne l'onglet du dashboard cliqué
  const handleDashboardClick = (indexKey: number) => {
    if (props.openListDashboard && props.editTabIndex === null) {
      const target = props.openListDashboard[indexKey];
      const cached = target?.id
        ? dashboardCacheRef.current.get(target.id)
        : null;

      if (cached) {
        //  on restaure instantanément, aucun fetch
        props.setComponentsListDashboard(cached.components);
      } else {
        // on déclenche le chargement normal
        props.setComponentsListDashboard(null);
      }
      props.setActiveDashboard(target);
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
        (dashboard: DashboardItemParam, index: number) => ({
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
    // Annuler une éventuelle requête précédente
    abortControllerRef.current?.abort();
    abortControllerRef.current = new AbortController();

    try {
      const response = await fetch(
        urlApiDeleteDashboard + dashboard.id,
        getFetchOptions({
          method: "DELETE",
          headers: { "Content-Type": "application/json" },
          signal: abortControllerRef.current.signal,
        }),
      );
      await response.json();
      // le dashboard n'existe plus
      if (dashboard.id) {
        dashboardCacheRef.current.delete(dashboard.id);
      }
      updateDashboardList(indexKey);
      successToast("Le tableau de bord a correctement été supprimé");
    } catch (e: unknown) {
      const errorName = e instanceof Error ? e.name : undefined;
      // Ne pas afficher d'erreur si c'est une annulation volontaire
      if (errorName !== "AbortError") {
        errorToast(String(e));
      }
    }
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
    //  Annuler les requêtes en cours
    abortControllerRef.current?.abort();

    if (!props.openListDashboard || props.openListDashboard.length === 0) {
      props.setComponentsListDashboard(null);
      props.setActiveDashboard(null);
      props.setEditTabIndex(null);
      return;
    }

    const editedDashboard = props.openListDashboard.find(
      (dashboard) => dashboard.index === props.editTabIndex,
    );

    const dashboardToRestore = editedDashboard ?? props.openListDashboard[0];

    if (dashboardToRestore?.id) {
      dashboardCacheRef.current.delete(dashboardToRestore.id);
    }

    props.setComponentsListDashboard(null);
    props.setActiveDashboard(dashboardToRestore);
    setGridReloadVersion((prev) => prev + 1);

    props.setEditTabIndex(null);
  };

  return (
    <>
      <PageTitle
        icon={<IconGaugeComponent />}
        title={"Édition des tableaux de bord et profils associés"}
      />
      <div className="flex-grow-1 d-flex flex-column">
        <div className="d-flex align-items-center">
          <Nav
            variant="tabs"
            activeKey={props.activeDashboard?.index ?? undefined}
          >
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
                    className="d-flex align-items-center"
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
                          className="text-info ms-2 text-decoration-none"
                          onClick={(e) => {
                            e.stopPropagation();
                            handleEditTab(dashboard.index || 0);
                          }}
                        >
                          <IconEdit />
                        </Button>
                        <Button
                          variant="link"
                          size="sm"
                          onClick={(e) => {
                            e.stopPropagation();
                            setIdtoremove(dashboard.index || 0);
                            setDisabledModal(true);
                          }}
                          className="ms-2 text-danger text-decoration-none"
                        >
                          <IconDelete />
                        </Button>
                      </>
                    ) : null}
                  </Nav.Link>
                </Nav.Item>
              ))}
          </Nav>

          {/* Bouton pour ajouter un nouvel onglet */}
          {props.editTabIndex === null ? (
            <div className="ms-auto">
              {" "}
              <CreateButton title={"Ajouter"} onClick={handleAddDashboard} />
            </div>
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
            key={
              props.activeDashboard.id
                ? `dashboard-${props.activeDashboard.id}-${gridReloadVersion}`
                : `dashboard-new-${props.activeDashboard.index ?? "default"}-${gridReloadVersion}`
            }
            editTabIndex={props.editTabIndex}
            componentsListDashboard={props.componentsListDashboard}
            setComponentsListDashboard={props.setComponentsListDashboard}
            activeDashboard={props.activeDashboard}
            setActiveDashboard={props.setActiveDashboard}
            dashboardCacheRef={dashboardCacheRef}
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
          content="Supprimer le tableau de bord ?"
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

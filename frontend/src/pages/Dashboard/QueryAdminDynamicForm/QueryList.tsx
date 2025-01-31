import {
  forwardRef,
  useCallback,
  useEffect,
  useImperativeHandle,
  useState,
} from "react";
import {
  Button,
  Card,
  ListGroup,
  OverlayTrigger,
  Tooltip,
} from "react-bootstrap";
import url, { getFetchOptions } from "../../../module/fetch.tsx";
import {
  ComponentDashboard,
  COMPONENTS,
  FORM_CONFIG,
  formatData,
  QueryParam,
} from "../Constants.tsx";
import { useToastContext } from "../../../module/Toast/ToastProvider.tsx";
import ConfirmModal from "../../../components/Modal/ConfirmModal.tsx";
import { useGetRun } from "../../../components/Fetch/useFetch.tsx";

type QueryListProps = {
  setData: (arg0: any) => void;
  activeQuery: QueryParam | undefined | null;
  setActiveQuery: (arg0: any) => void;
  openListComponent: ComponentDashboard[] | undefined;
  setOpenListComponent: (arg0: any) => void;
  setSelectedComponent: (arg0: any) => void;
  queryGlobalData: any;
  setQueryGlobalData: (arg0: any) => void;
  setAvailableOptions: (arg0: any) => void;
  formikRef: any;
  isAdding: boolean;
  setIsAdding: (arg0: boolean) => void;
};

const QueryList = forwardRef((props: QueryListProps, ref) => {
  const { error: errorToast, success: successToast } = useToastContext();
  const [disabledModal, setDisabledModal] = useState(false);
  const [idToRemove, setIdtoremove] = useState<string | null>();

  const [openListQuery, setOpenListQuery] = useState<QueryParam[] | null>(); // Liste des requêtes

  const urlApiQueryList = url`/api/dashboard/get-list-query`;
  const urlApiGetQueryComponent = url`/api/dashboard/get-components/`;
  const urlApiDeleteQuery = url`/api/dashboard/delete-query/`;
  const urlApiQuery = url`/api/dashboard/validate-query`;

  useImperativeHandle(ref, () => ({
    setOpenListQuery(value: any) {
      setOpenListQuery(value);
    },
  }));

  // Récupère la liste des requêtes en base
  const fetchData = useGetRun(urlApiQueryList, {});

  useEffect(() => {
    if (fetchData.isResolved && fetchData.data) {
      const queryList: { id: any; query: any; title: any }[] =
        fetchData.data.map(
          (element: {
            dashboardQueryId: any;
            dashboardQueryQuery: any;
            dashboardQueryTitle: any;
          }) => {
            return {
              id: element.dashboardQueryId,
              query: element.dashboardQueryQuery,
              title: element.dashboardQueryTitle,
            };
          },
        );
      setOpenListQuery(queryList);
    }
  }, [fetchData.data, fetchData.isResolved, setOpenListQuery]);

  // Définit les propriétés par défaut pour l'ajout d'une nouvelle requête
  const handleAddQuery = () => {
    const newQuery = {
      query: "",
      title: "Titre nouvelle requête",
    };
    props.setIsAdding(true); // Définit le statut en cours d'édition
    props.setOpenListComponent([]);
    props.setData(null);
    props.setActiveQuery(newQuery);
  };

  // Met à jour la requête en cas d'édition
  const handleEditQuery = (id: string | undefined) => {
    // Active la requête dans la liste
    props.setIsAdding(true); // Définit le statut en cours d'édition
    props.setActiveQuery(
      openListQuery ? openListQuery.find((query) => query.id === id) : null,
    );
  };

  // Récupère les composant liés à la requête
  const fetchQueryComponent = useGetRun(
    urlApiGetQueryComponent + props.activeQuery?.id,
    {},
  );
  useEffect(() => {
    if (
      fetchQueryComponent.isResolved &&
      fetchQueryComponent.data &&
      !props.openListComponent
    ) {
      // Set les composants de la requête sans datas
      const componentList: {
        id: string;
        queryId: string;
        index: number;
        key: string;
        component: any;
        formConfig: any;
        title: string;
        config: any;
      }[] = fetchQueryComponent.data.map(
        (
          element: {
            dashboardComponentId: string;
            dashboardComponentKey: string;
            dashboardComponentTitle: string;
            dashboardComponentConfig: any;
            dashboardComponentDahsboardQueryId: string;
          },
          index: number,
        ) => {
          return {
            id: element.dashboardComponentId,
            queryId: element.dashboardComponentDahsboardQueryId || "",
            index: index,
            key: element.dashboardComponentKey,
            component:
              COMPONENTS[
                element.dashboardComponentKey as keyof typeof COMPONENTS
              ],
            formConfig:
              FORM_CONFIG[
                element.dashboardComponentKey as keyof typeof FORM_CONFIG
              ],
            title: element.dashboardComponentTitle,
            config: element.dashboardComponentConfig,
          };
        },
      );
      props.setOpenListComponent(componentList);
    }
  }, [
    fetchQueryComponent.data,
    fetchQueryComponent.isResolved,
    props.openListComponent,
    props.setOpenListComponent,
  ]);

  // Valide la requête et récupère les datas correspondantes
  const fetchDataQuery = useCallback(async () => {
    (
      await fetch(
        urlApiQuery,
        getFetchOptions({
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            query: props.activeQuery?.query,
            queryTitle: props.activeQuery?.title,
          }),
        }),
      )
    )
      .json()
      .then((res) => {
        // Format les données pour exploitations par les composants
        const dataFormatted = formatData(res);
        props.setAvailableOptions(Object.keys(dataFormatted[0]));
        props.setQueryGlobalData(dataFormatted);
        // Set les datas à afficher dans le premier composant, pour visualisation
        if (props.openListComponent && props.openListComponent.length > 0) {
          props.setData(null);
          props.setSelectedComponent(props.openListComponent[0]);
        }
      })
      .catch((reason: string) => {
        errorToast(reason);
      });
  }, [
    errorToast,
    props.activeQuery,
    props.setAvailableOptions,
    props.setQueryGlobalData,
    props.setData,
    props.setSelectedComponent,
    props.setOpenListComponent,
    urlApiQuery,
  ]);

  // Enregistre la config pour la requête
  const handleSaveQuery = () => {
    props.formikRef.current?.submitForm(); // Ref pour l'enregistrement de la requête et composants
  };

  // Annule l'édition de la requête
  const handleCancelQuery = () => {
    props.setIsAdding(false); // Désactive le mode "Ajout"
    props.setActiveQuery(null);
    props.setSelectedComponent(null);
    props.setOpenListComponent(null);
  };

  // Supprime la requête et composants associés
  const handleDeleteQuery = () => {
    const fetchDataDelete = async () => {
      (
        await fetch(
          urlApiDeleteQuery + idToRemove,
          getFetchOptions({
            method: "DELETE",
            headers: { "Content-Type": "application/json" },
          }),
        )
      )
        .text()
        .then(() => {
          successToast("La requête a bien été supprimée.");
          // Refresh la liste des requêtes
          fetchData.run();
        })
        .catch((reason: string) => {
          errorToast(reason);
        });
    };
    fetchDataDelete();
  };

  // Récupère les requêtes en base lors du premier chargement du composant
  useEffect(() => {
    if (!openListQuery && !fetchData.isLoading) {
      fetchData.run();
    }
  }, [fetchData, openListQuery]);

  // Récupère les composant de la requête éditer
  useEffect(() => {
    if (
      props.activeQuery &&
      props.activeQuery.id &&
      !props.openListComponent &&
      !fetchQueryComponent.isLoading
    ) {
      fetchQueryComponent.run();
    }
  }, [fetchQueryComponent, props.activeQuery, props.openListComponent]);

  // Récupères les datas de la requête éditée
  useEffect(() => {
    if (
      props.activeQuery &&
      props.activeQuery.id &&
      !props.queryGlobalData &&
      props.openListComponent
    ) {
      fetchDataQuery();
    }
  }, [
    fetchDataQuery,
    props.activeQuery,
    props.openListComponent,
    props.queryGlobalData,
  ]);

  return (
    <Card className="m-3">
      <Card.Header className="d-flex justify-content-between align-items-center">
        <span>Liste des requêtes</span>
        {!props.isAdding && (
          <Button variant="primary" size="sm" onClick={handleAddQuery}>
            Ajouter
          </Button>
        )}
      </Card.Header>

      {props.isAdding && (
        <Card.Body>
          <h5 className="mb-3">{props.activeQuery?.title}</h5>
          <div className="d-flex justify-content-between">
            <Button variant="success" onClick={handleSaveQuery}>
              Enregistrer
            </Button>
            <Button variant="secondary" onClick={handleCancelQuery}>
              Annuler
            </Button>
          </div>
        </Card.Body>
      )}

      {!props.isAdding && (
        <ListGroup
          variant="flush"
          style={{ maxHeight: "400px", overflowY: "auto" }}
        >
          {openListQuery && openListQuery.length > 0 ? (
            openListQuery.map(({ id, title }) => (
              <ListGroup.Item
                key={id}
                className="justify-content-between align-items-center"
              >
                <OverlayTrigger
                  placement="top"
                  overlay={
                    <Tooltip id={`tooltip-${id}`}>
                      {title} {/* Affiche le texte complet dans le tooltip */}
                    </Tooltip>
                  }
                >
                  <span className="text-truncate d-block">{title}</span>
                </OverlayTrigger>
                <div className="mt-2 d-flex justify-content-sm-between">
                  <Button
                    variant="secondary"
                    size="sm"
                    className="me-2"
                    onClick={() => handleEditQuery(id)}
                  >
                    Éditer
                  </Button>
                  <Button
                    variant="danger"
                    size="sm"
                    onClick={() => {
                      setIdtoremove(id);
                      setDisabledModal(true);
                    }}
                  >
                    Supprimer
                  </Button>
                </div>
              </ListGroup.Item>
            ))
          ) : (
            <ListGroup.Item>Aucune requête ajoutée.</ListGroup.Item>
          )}
        </ListGroup>
      )}
      <ConfirmModal
        visible={disabledModal}
        content="Supprimer la requête ?"
        closeModal={() => setDisabledModal(false)}
        query={""}
        href="#"
        onConfirm={() => handleDeleteQuery()}
      />
    </Card>
  );
});

QueryList.displayName = "QueryList";

export default QueryList;

import {
  forwardRef,
  useCallback,
  useEffect,
  useImperativeHandle,
  useState,
} from "react";
import { Button, Card, Col, ListGroup, Row } from "react-bootstrap";
import CreateButton from "../../../components/Button/CreateButton.tsx";
import { useGetRun } from "../../../components/Fetch/useFetch.tsx";
import { IconDelete, IconEdit } from "../../../components/Icon/Icon.tsx";
import ConfirmModal from "../../../components/Modal/ConfirmModal.tsx";
import TooltipCustom from "../../../components/Tooltip/Tooltip.tsx";
import url, { getFetchOptions } from "../../../module/fetch.tsx";
import { useToastContext } from "../../../module/Toast/ToastProvider.tsx";
import {
  ComponentDashboard,
  COMPONENTS,
  FORM_CONFIG,
  formatData,
  QueryParam,
} from "../Constants.tsx";

type QueryListProps = {
  setData: (arg0: any) => void;
  activeQuery: QueryParam | undefined | null;
  setActiveQuery: (arg0: any) => void;
  openListComponent: ComponentDashboard[] | null | undefined;
  setOpenListComponent: (arg0: any) => void;
  setSelectedComponent: (arg0: any) => void;
  queryGlobalData: any;
  setQueryGlobalData: (arg0: any) => void;
  setAvailableOptions: (arg0: any) => void;
  formikRef: any;
  isAdding: boolean;
  setIsAdding: (arg0: boolean) => void;
};

const QueryList = forwardRef(
  (
    {
      setData,
      activeQuery,
      setActiveQuery,
      openListComponent,
      setOpenListComponent,
      setSelectedComponent,
      queryGlobalData,
      setQueryGlobalData,
      setAvailableOptions,
      formikRef,
      isAdding,
      setIsAdding,
    }: QueryListProps,
    ref,
  ) => {
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
      setIsAdding(true); // Définit le statut en cours d'édition
      setOpenListComponent([]);
      setData(null);
      setActiveQuery(newQuery);
    };

    // Met à jour la requête en cas d'édition
    const handleEditQuery = (id: string | undefined) => {
      // Active la requête dans la liste
      setIsAdding(true); // Définit le statut en cours d'édition
      setActiveQuery(
        openListQuery ? openListQuery.find((query) => query.id === id) : null,
      );
    };

    // Récupère les composant liés à la requête
    const fetchQueryComponent = useGetRun(
      urlApiGetQueryComponent + activeQuery?.id,
      {},
    );

    useEffect(() => {
      if (activeQuery && activeQuery.id) {
        // Si une requête est active, on récupère les composants associés
        setOpenListComponent(null); // Réinitialise la liste des composants
        fetchQueryComponent.run();
      }
    }, [activeQuery]);

    useEffect(() => {
      if (
        fetchQueryComponent.isResolved &&
        fetchQueryComponent.data &&
        !openListComponent
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
        setOpenListComponent(componentList);
      }
    }, [
      fetchQueryComponent.data,
      fetchQueryComponent.isResolved,
      openListComponent,
      setOpenListComponent,
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
              query: activeQuery?.query,
              queryTitle: activeQuery?.title,
            }),
          }),
        )
      )
        .json()
        .then((res) => {
          // Format les données pour exploitations par les composants
          const dataFormatted = formatData(res);
          setAvailableOptions(Object.keys(dataFormatted[0]));
          setQueryGlobalData(dataFormatted);
          // Set les datas à afficher dans le premier composant, pour visualisation
          if (openListComponent && openListComponent.length > 0) {
            setData(null);
            setSelectedComponent(openListComponent[0]);
          }
        })
        .catch((reason: string) => {
          errorToast(reason);
        });
    }, [
      errorToast,
      activeQuery,
      setAvailableOptions,
      setQueryGlobalData,
      setData,
      setSelectedComponent,
      setOpenListComponent,
      urlApiQuery,
    ]);

    // Enregistre la config pour la requête
    const handleSaveQuery = () => {
      formikRef.current?.submitForm(); // Ref pour l'enregistrement de la requête et composants
    };

    // Annule l'édition de la requête
    const handleCancelQuery = () => {
      setIsAdding(false); // Désactive le mode "Ajout"
      setActiveQuery(null);
      setSelectedComponent(null);
      setOpenListComponent(null);
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
        activeQuery &&
        activeQuery.id &&
        !openListComponent &&
        !fetchQueryComponent.isLoading
      ) {
        fetchQueryComponent.run();
      }
    }, [fetchQueryComponent, activeQuery, openListComponent]);

    // Récupères les datas de la requête éditée
    useEffect(() => {
      if (
        activeQuery &&
        activeQuery.id &&
        !queryGlobalData &&
        openListComponent
      ) {
        fetchDataQuery();
      }
    }, [fetchDataQuery, activeQuery, openListComponent, queryGlobalData]);

    return (
      <Card className="m-3">
        <Card.Header className="d-flex justify-content-between align-items-center">
          <h3>Liste des requêtes</h3>
          {!isAdding && (
            <CreateButton title={"Ajouter"} onClick={handleAddQuery} />
          )}
        </Card.Header>

        {isAdding && (
          <Card.Body>
            <h5 className="mb-3">{activeQuery?.title}</h5>
            <div className="d-flex justify-content-between">
              <Button variant="secondary" onClick={handleCancelQuery}>
                Annuler
              </Button>
              <Button variant="primary" onClick={handleSaveQuery}>
                Enregistrer
              </Button>
            </div>
          </Card.Body>
        )}

        {!isAdding && (
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
                  <Row>
                    <Col sm={8} className="text-truncate d-block">
                      <TooltipCustom
                        tooltipId={`query-title-tooltip-${id}`}
                        tooltipText={title}
                      >
                        <span>{title}</span>
                      </TooltipCustom>
                    </Col>
                    <Col sm={2}>
                      <Button
                        variant="link"
                        size="sm"
                        className="me-2 text-info text-decoration-none"
                        onClick={() => handleEditQuery(id)}
                      >
                        <IconEdit />
                      </Button>
                    </Col>
                    <Col sm={2}>
                      <Button
                        variant="link"
                        size="sm"
                        className="text-danger text-decoration-none"
                        onClick={() => {
                          setIdtoremove(id);
                          setDisabledModal(true);
                        }}
                      >
                        <IconDelete />
                      </Button>
                    </Col>
                  </Row>
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
  },
);

QueryList.displayName = "QueryList";

export default QueryList;

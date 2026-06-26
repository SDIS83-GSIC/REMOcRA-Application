import {
  forwardRef,
  useCallback,
  useEffect,
  useImperativeHandle,
  useRef,
  useState,
} from "react";
import { Button, Card, ListGroup } from "react-bootstrap";
import CreateButton from "../../../components/Button/CreateButton.tsx";
import DeleteButtonWithModal from "../../../components/Button/DeleteButtonWithModal.tsx";
import Loading from "../../../components/Elements/Loading/Loading.tsx";
import { IconEdit } from "../../../components/Icon/Icon.tsx";
import TooltipCustom from "../../../components/Tooltip/Tooltip.tsx";
import url, { getFetchOptions } from "../../../module/fetch.tsx";
import { useToastContext } from "../../../module/Toast/ToastProvider.tsx";
import {
  COMPONENTS,
  ComponentDashboard,
  FORM_CONFIG,
  formatData,
  normalizeComponentConfig,
  QueryParam,
} from "../Constants.tsx";

type QueryListProps = {
  validateAbortRef?: React.MutableRefObject<AbortController | null>;
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
  setSqlErrorMessage: (message: string | null) => void;
};

export type QueryListRef = {
  refreshList: () => void;
};

const QueryList = forwardRef<QueryListRef, QueryListProps>(
  (
    {
      validateAbortRef,
      setData,
      activeQuery,
      setActiveQuery,
      setOpenListComponent,
      setSelectedComponent,
      setQueryGlobalData,
      setAvailableOptions,
      formikRef,
      isAdding,
      setIsAdding,
      setSqlErrorMessage,
    },
    ref,
  ) => {
    const { error: errorToast } = useToastContext();

    const [openListQuery, setOpenListQuery] = useState<QueryParam[] | null>(
      null,
    );
    const [isLoadingList, setIsLoadingList] = useState(false);
    const [isLoadingQuery, setIsLoadingQuery] = useState(false);
    const [isValidatingQuery, setIsValidatingQuery] = useState(false);

    const urlApiQueryList = url`/api/dashboard/get-list-query`;
    const urlApiGetQueryComponent = url`/api/dashboard/get-components/`;
    const urlApiDeleteQuery = url`/api/dashboard/delete-query/`;
    const urlApiQuery = url`/api/dashboard/validate-query`;

    const validateRequestIdRef = useRef(0);

    const loadQueryList = useCallback(async () => {
      setIsLoadingList(true);
      try {
        const response = await fetch(
          urlApiQueryList,
          getFetchOptions({ method: "GET" }),
        );
        const raw = await response.json();

        const queryList: QueryParam[] = Array.isArray(raw)
          ? raw.map((element: any) => ({
              id: element.dashboardQueryId,
              query: element.dashboardQueryQuery,
              title: element.dashboardQueryTitle,
            }))
          : [];

        setOpenListQuery(queryList);
      } catch (reason: any) {
        errorToast(String(reason));
      } finally {
        setIsLoadingList(false);
      }
    }, [errorToast, urlApiQueryList]);

    const loadComponentsForQuery = useCallback(
      async (queryId: string | number) => {
        const response = await fetch(
          `${urlApiGetQueryComponent}${queryId}`,
          getFetchOptions({ method: "GET" }),
        );
        const raw = await response.json();

        const componentList: ComponentDashboard[] = Array.isArray(raw)
          ? raw.map((element: any, index: number) => {
              const key = element.dashboardComponentKey as string;
              const componentEntry = COMPONENTS[
                key as keyof typeof COMPONENTS
              ] ?? {
                component: () => null,
                label: key,
              };

              return {
                id: element.dashboardComponentId,
                queryId: element.dashboardComponentDahsboardQueryId || "",
                index,
                key,
                component: componentEntry.component,
                formConfig: FORM_CONFIG[key as keyof typeof FORM_CONFIG],
                title: element.dashboardComponentTitle || componentEntry.label,
                config: normalizeComponentConfig(
                  key,
                  element.dashboardComponentConfig,
                ),
              };
            })
          : [];

        setOpenListComponent(componentList);
      },
      [setOpenListComponent, urlApiGetQueryComponent],
    );

    const validateQuery = useCallback(
      async (query: QueryParam): Promise<boolean> => {
        validateAbortRef?.current?.abort();

        const controller = new AbortController();
        if (validateAbortRef) {
          validateAbortRef.current = controller;
        }
        const requestId = ++validateRequestIdRef.current;

        setIsValidatingQuery(true);
        try {
          const response = await fetch(urlApiQuery, {
            ...getFetchOptions({
              method: "POST",
              headers: { "Content-Type": "application/json" },
              body: JSON.stringify({
                query: query.query,
                queryTitle: query.title,
              }),
            }),
            signal: controller.signal,
          });

          if (requestId !== validateRequestIdRef.current) {
            return false;
          }

          const raw = await response.text();
          let parsed: unknown = raw;

          try {
            parsed = raw ? JSON.parse(raw) : null;
          } catch {
            //continuer
          }

          if (typeof parsed === "string") {
            setSqlErrorMessage(parsed);
            return false;
          }

          if (!response.ok) {
            setSqlErrorMessage("Erreur SQL");
            return false;
          }

          const dataFormatted = formatData(parsed as any);
          setAvailableOptions(
            dataFormatted?.[0] ? Object.keys(dataFormatted[0]) : [],
          );
          setQueryGlobalData(dataFormatted);
          setSqlErrorMessage(null);
          setData(null);
          return true;
        } catch (reason: unknown) {
          if (reason instanceof DOMException && reason.name === "AbortError") {
            return false;
          }
          errorToast(String(reason));
          return false;
        } finally {
          if (requestId === validateRequestIdRef.current) {
            setIsValidatingQuery(false);
          }
        }
      },
      [
        errorToast,
        setAvailableOptions,
        setData,
        setQueryGlobalData,
        setSqlErrorMessage,
        urlApiQuery,
        validateAbortRef,
      ],
    );

    useImperativeHandle(
      ref,
      () => ({
        refreshList() {
          void loadQueryList();
        },
      }),
      [loadQueryList],
    );

    useEffect(() => {
      void loadQueryList();
    }, [loadQueryList]);

    const handleAddQuery = () => {
      const newQuery: QueryParam = {
        query: "",
        title: "Titre nouvelle requête",
      };

      setIsAdding(true);
      setOpenListComponent([]);
      setData(null);
      setSqlErrorMessage(null);
      setActiveQuery(newQuery);
      setSelectedComponent(null);
      setAvailableOptions([]);
      setQueryGlobalData(null);
    };

    const handleEditQuery = async (id: string | number | undefined) => {
      const selected =
        openListQuery?.find((query) => String(query.id) === String(id)) ?? null;
      if (!selected || !selected.id) {
        return;
      }

      setIsAdding(true);
      setIsLoadingQuery(true);
      setSqlErrorMessage(null);
      setActiveQuery(selected);
      setSelectedComponent(null);
      setAvailableOptions([]);
      setQueryGlobalData(null);
      setData(null);
      setOpenListComponent(null);

      try {
        await loadComponentsForQuery(selected.id);
        await validateQuery(selected);
      } catch (reason: any) {
        errorToast(String(reason));
      } finally {
        setIsLoadingQuery(false);
      }
    };

    const handleSaveQuery = async () => {
      if (isValidatingQuery) {
        return;
      }

      await formikRef.current?.submitForm();
    };

    const handleCancelQuery = () => {
      setIsAdding(false);
      setActiveQuery(null);
      setSelectedComponent(null);
      setOpenListComponent(null);
      setAvailableOptions([]);
      setQueryGlobalData(null);
    };

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
              <Button
                variant="secondary"
                onClick={handleCancelQuery}
                disabled={isValidatingQuery}
              >
                Annuler
              </Button>

              <Button
                variant="primary"
                onClick={handleSaveQuery}
                disabled={isValidatingQuery}
              >
                {isValidatingQuery ? "Validation..." : "Enregistrer"}
              </Button>
            </div>
          </Card.Body>
        )}

        {!isAdding && (
          <ListGroup
            variant="flush"
            style={{ maxHeight: "65vh", overflowY: "auto" }}
          >
            {isLoadingList ? (
              <Loading />
            ) : openListQuery && openListQuery.length > 0 ? (
              openListQuery.map(({ id, title }) => (
                <ListGroup.Item
                  key={id}
                  className="d-flex justify-content-between align-items-center"
                >
                  <div className="text-truncate me-3">
                    <TooltipCustom
                      tooltipId={`query-title-tooltip-${id}`}
                      tooltipText={title}
                    >
                      <span>{title}</span>
                    </TooltipCustom>
                  </div>
                  <div className="d-flex flex-nowrap">
                    <Button
                      variant="link"
                      size="sm"
                      className="me-2 text-info text-decoration-none"
                      onClick={() => handleEditQuery(id)}
                      disabled={isLoadingQuery || isValidatingQuery}
                    >
                      <IconEdit />
                    </Button>
                    <DeleteButtonWithModal
                      path={urlApiDeleteQuery + id}
                      reload={() => {
                        loadQueryList();
                      }}
                      variant="link"
                      className="text-danger text-decoration-none"
                      title={false}
                      disabled={isLoadingQuery || isValidatingQuery}
                      header="Supprimer la requête"
                      content="Êtes-vous sûr de vouloir supprimer cette requête ?"
                    />
                  </div>
                </ListGroup.Item>
              ))
            ) : (
              <ListGroup.Item>Aucune requête ajoutée.</ListGroup.Item>
            )}
          </ListGroup>
        )}
      </Card>
    );
  },
);

QueryList.displayName = "QueryList";

export default QueryList;

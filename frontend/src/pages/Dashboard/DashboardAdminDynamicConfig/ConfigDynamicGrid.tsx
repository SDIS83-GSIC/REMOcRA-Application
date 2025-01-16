import { DndContext } from "@dnd-kit/core";
import { useCallback, useEffect, useState } from "react";
import { Container, Row, Col } from "react-bootstrap";
import url, { getFetchOptions } from "../../../module/fetch.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { useToastContext } from "../../../module/Toast/ToastProvider.tsx";
import {
  ComponentDashboard,
  DashboardComponentConfig,
  DashboardItemParam,
  formatData,
  QueryData,
} from "../Constants.tsx";
import { useGetRun } from "../../../components/Fetch/useFetch.tsx";
import DashboardItem from "./DashboardItem.tsx";
import ConfigFormDashboard from "./ConfigFormDashboard.tsx";

type ConfigDynamicGridProps = {
  componentListIdSelected: string;
  setcomponentListIdSelected: (arg0: string) => void;
  editTabIndex: number | undefined | null;
  componentsListDashboard: ComponentDashboard[];
  setComponentsListDashboard: (arg0: any) => void;
  setEditTabIndex: any;
  activeDashboard: DashboardItemParam;
};

const ConfigDynamicGrid = (props: ConfigDynamicGridProps) => {
  const { error: errorToast } = useToastContext();

  const heightRow = 100;
  const nbCol = 4;

  const [dashboardProfil, setDashboardProfil] = useState<any | null>();
  const [dashboardTitle, setDashboardtitle] = useState<string | null>(
    "Nouveau Dashbaord",
  );
  const [componentsListToMap, setComponentsListToMap] = useState<
    DashboardComponentConfig[] | []
  >();
  const [activeQuerysData, setActiveQuerysData] = useState<
    QueryData[] | null
  >(); // Liste des datas des requêtes
  const [componentSelected, setComponentSelected] =
    useState<ComponentDashboard | null>(); // Composant sélectionner dans la grid
  const [numberRowGrid, setNumberRowGrid] = useState(
    props.componentsListDashboard
      ? props.componentsListDashboard.length * nbCol
      : 0,
  ); // Nombre de ligne dans grille

  const setComponentList = useCallback(
    (newComponent: ComponentDashboard) => {
      props.setComponentsListDashboard((prevComponentList: any) =>
        prevComponentList
          ? [...prevComponentList, newComponent]
          : [newComponent],
      );
    },
    [props],
  );

  const urlApiComponentConfig = url`/api/dashboard/get-component-config/`;
  const urlApiQueryData = url`/api/dashboard/get-list-data-query/`;
  const urlApiSaveDashboard = url`/api/dashboard/create-dashboard/`;
  const urlApiUpdateDashboard = url`/api/dashboard/update-dashboard/`;
  const urlApiDashboard = url`/api/dashboard/get-dashboard-config/`;
  const urlApiDashboardProfils = url`/api/dashboard/get-dashboard-list-profil/`;

  // Récupère en base les datas lié à une requête
  const fetchDataQuery = useCallback(
    async (queryIds?: string[] | undefined) => {
      (
        await fetch(
          urlApiQueryData,
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
          // Formatte et stocke les datas des requête SQL pour usage des composants
          resData.forEach((dataQuery: any) => {
            const dataFormated = formatData(dataQuery);
            newActiveQuerysData.push({
              id: dataQuery ? dataQuery.queryId : componentSelected?.queryId,
              data: dataFormated,
            });
          });
          setActiveQuerysData(
            activeQuerysData
              ? [...activeQuerysData, ...newActiveQuerysData]
              : newActiveQuerysData,
          );

          const newComponentList: ComponentDashboard[] = [];
          props.componentsListDashboard.forEach((component) => {
            if (!component.data) {
              const dataQuery = newActiveQuerysData.find(
                (query) => query.id === component.queryId,
              );

              if (dataQuery) {
                newComponentList.push({
                  ...component,
                  data: dataQuery.data,
                });
              }
            } else {
              // Si nouveau dashboard on vide les composants à l'écran
              newComponentList.push(component);
            }
          });
          props.setComponentsListDashboard(newComponentList);
        })
        .catch((reason: string) => {
          errorToast(reason);
        });
    },
    [
      activeQuerysData,
      componentSelected?.queryId,
      errorToast,
      props,
      urlApiQueryData,
    ],
  );

  // Récupère les données en base du dashboard
  const fetchDashboardConfig = useGetRun(
    urlApiDashboard + props.activeDashboard.id,
    {},
  );
  useEffect(() => {
    if (
      fetchDashboardConfig.isResolved &&
      fetchDashboardConfig.data &&
      props.componentsListDashboard.length === 0 &&
      props.activeDashboard.id
    ) {
      const newComponentList: ComponentDashboard[] = [];
      const queryIds: string[] = [];
      fetchDashboardConfig.data.forEach((element: DashboardComponentConfig) => {
        setComponentsListToMap(
          componentsListToMap ? [...componentsListToMap, element] : [element],
        );

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
        if (!queryIds.includes(element.componentQueryId)) {
          queryIds.push(element.componentQueryId);
        }
      });
      props.setComponentsListDashboard(newComponentList);
    }
  }, [componentsListToMap, fetchDashboardConfig, props]);

  // Récupère en base les profils liés à un dashboard
  const fetchDashboardProfils = useGetRun(
    urlApiDashboardProfils + props.activeDashboard.id,
    {},
  );
  useEffect(() => {
    if (fetchDashboardProfils.isResolved) {
      setDashboardProfil(fetchDashboardProfils.data);
    }
  }, [fetchDashboardProfils.data, fetchDashboardProfils.isResolved]);

  // Récupère en base la config d'un composants
  const {
    run: fetchComponentDataRun,
    data: fetchComponentDataData,
    isResolved: fetchComponentDataResolved,
  } = useGetRun(urlApiComponentConfig + props.componentListIdSelected, {});

  useEffect(() => {
    if (
      fetchComponentDataResolved &&
      props.componentListIdSelected ===
        fetchComponentDataData.dashboardComponentId &&
      (!props.componentsListDashboard ||
        !props.componentsListDashboard.find(
          (component) => component.id === props.componentListIdSelected,
        ))
    ) {
      // Position initial lors de l'ajout d'un nouveau composant
      const configPosition = {
        size: 3,
        x: 0,
        y: 0,
      };

      // Créer le nouveau composant
      const componentAdd = {
        id: fetchComponentDataData.dashboardComponentId,
        queryId: fetchComponentDataData.dashboardComponentDahsboardQueryId,
        key: fetchComponentDataData.dashboardComponentKey,
        title: fetchComponentDataData.dashboardComponentTitle,
        config: fetchComponentDataData.dashboardComponentConfig,
        configPosition: configPosition,
      };

      // Défini le nombre de ligne max
      setNumberRowGrid(
        props.componentsListDashboard
          ? (props.componentsListDashboard.length + 1) * nbCol
          : nbCol,
      );

      setComponentList(componentAdd);
    }
  }, [
    activeQuerysData,
    fetchComponentDataData,
    fetchComponentDataResolved,
    fetchDataQuery,
    props.componentListIdSelected,
    props.componentsListDashboard,
    setComponentList,
  ]);

  const handleDragStart = (event: any) => {
    // Set le composant sélectionné
    setComponentSelected(
      props.componentsListDashboard.find(
        (component: { id: any }) => component.id === event.active.id,
      ),
    );
  };

  const removeRowUnused = (componentList: any[]) => {
    // Supprime les lignes innocupés après le composant le plus bas
    let lowestRow = -1;

    componentList.forEach(
      (component: { configPosition: { y: any; size: any } }) => {
        const { y, size } = component.configPosition;
        const bottomLine = parseInt(y) + parseInt(size); // Ligne la plus basse occupée par ce composant
        if (bottomLine > lowestRow) {
          lowestRow = bottomLine; // Mettre à jour la ligne la plus basse
        }
      },
    );

    lowestRow =
      lowestRow < componentList.length * nbCol // S'assure qu'on affiche toujours un minimum de ligne égale au nombre de composants
        ? componentList.length * nbCol
        : lowestRow;
    setNumberRowGrid(lowestRow);
  };

  const handleDragEnd = (event: {
    delta: { x: number; y: number };
    active: { id: any };
  }) => {
    const tagCol = document.getElementById("col-grid-component"); // Largeur d'une colonne (variable selon taille écran)
    const widthCol = tagCol?.offsetWidth ? tagCol.offsetWidth : 0;
    const newX = Math.round(event.delta.x / widthCol);
    const newY = Math.round(event.delta.y / heightRow);

    // S'assure que le composant ne dépasse pas de la grille
    const posX =
      componentSelected && componentSelected.configPosition
        ? componentSelected.configPosition.x + newX
        : -1;
    const posY =
      componentSelected && componentSelected.configPosition
        ? componentSelected.configPosition.y + newY
        : -1;

    if (
      props.componentsListDashboard.length > 0 &&
      componentSelected &&
      componentSelected.configPosition &&
      newX + componentSelected.configPosition.size <= nbCol &&
      posX <= nbCol &&
      posX >= 0 &&
      posY <= props.componentsListDashboard.length * nbCol &&
      posY >= 0
    ) {
      // Set les nouvelles coordonnées dans la grille
      const newComponentConfig = props.componentsListDashboard.find(
        (component: { id: any }) => component.id === event.active.id,
      );

      if (newComponentConfig && newComponentConfig.configPosition) {
        newComponentConfig.configPosition.x = posX;
        newComponentConfig.configPosition.y = posY;
        setComponentSelected(newComponentConfig);
        const newComponentList = props.componentsListDashboard.map(
          (component: { id: any }) =>
            component.id === event.active.id ? newComponentConfig : component,
        );
        props.setComponentsListDashboard(newComponentList);

        // Supprime les lignes en trop si existe
        removeRowUnused(newComponentList);
      }
    }
  };

  const getInitialValues = (componentSelected: any) => ({
    // Initialise la taille par défaut d'un nouveau composant
    sizeComponent:
      componentSelected && componentSelected.configPosition
        ? componentSelected.configPosition.size
        : 1,
  });

  const getPrepareVariables = () => {
    // Configure les données à envoyés au service
    return {
      ...(props.activeDashboard.id && {
        dashboardId: props.activeDashboard.id,
      }),
      dashboardTitle: dashboardTitle,
      dashboardComponents: props.componentsListDashboard
        ? props.componentsListDashboard.map(
            (component: ComponentDashboard) => ({
              componentId: component.id,
              componentConfig: JSON.stringify({
                componentSize: component.configPosition?.size,
                componentX: component.configPosition?.x,
                componentY: component.configPosition?.y,
              }),
            }),
          )
        : null,
      dashboardProfilsId: dashboardProfil
        ? dashboardProfil.map(
            (p: { profilUtilisateurId: any }) => p.profilUtilisateurId,
          )
        : null,
    };
  };

  // Récupère les dashboard déjà en base
  useEffect(() => {
    if (props.activeDashboard && !props.componentsListDashboard) {
      props.setComponentsListDashboard([]);
      setActiveQuerysData(null);
      setDashboardProfil(null);
      setDashboardtitle(props.activeDashboard.title);
      if (props.activeDashboard.id) {
        fetchDashboardConfig.run();
        fetchDashboardProfils.run();
      }
    }
  }, [dashboardTitle, fetchDashboardConfig, fetchDashboardProfils, props]);

  // Récupère les config du composant lors de l'ajout au dashboard
  useEffect(() => {
    setNumberRowGrid(
      props.componentsListDashboard
        ? props.componentsListDashboard.length * nbCol
        : 0,
    );

    if (
      props.componentListIdSelected !== "" &&
      (!props.componentsListDashboard ||
        !props.componentsListDashboard.find(
          (component) => component.id === props.componentListIdSelected,
        ))
    ) {
      fetchComponentDataRun();
    }
  }, [
    fetchComponentDataRun,
    props.componentListIdSelected,
    props.componentsListDashboard,
  ]);

  // On set les datas pour chaque composant s'ils n'en ont pas
  useEffect(() => {
    if (
      props.activeDashboard &&
      props.componentsListDashboard &&
      props.componentsListDashboard.find((component) => !component.data)
    ) {
      const newComponentList: ComponentDashboard[] = [];
      const queryIds: string[] = [];
      props.componentsListDashboard.forEach((component) => {
        if (!component.data) {
          const dataQuery = activeQuerysData?.find(
            (query) => query.id === component.queryId,
          );
          // console.log(activeQuerysData);
          if (dataQuery) {
            newComponentList.push({
              ...component,
              data: dataQuery.data,
            });
          } else {
            queryIds.push(component.queryId);
          }
        } else {
          // Si nouveau dashboard on vide les composants à l'écran
          newComponentList.push(component);
        }
      });

      // Récupère les datas des requêtes manquante
      if (queryIds.length > 0) {
        fetchDataQuery(queryIds);
      } else {
        props.setComponentsListDashboard(newComponentList);
      }
    }
  }, [activeQuerysData, fetchDataQuery, props]);

  return (
    <>
      <MyFormik
        initialValues={getInitialValues(componentSelected)}
        submitUrl={
          props.activeDashboard.id ? urlApiUpdateDashboard : urlApiSaveDashboard
        }
        successToastMessage="Le dahsboard à correctement été enregistré"
        errorToastMessage="Le dashbboard n'a pas été correctement enregistré"
        prepareVariables={getPrepareVariables}
        isPost={props.activeDashboard.id ? false : true}
      >
        <ConfigFormDashboard
          editTabIndex={props.editTabIndex}
          setComponentSelected={setComponentSelected}
          componentsListDashboard={props.componentsListDashboard}
          setComponentsListDashboard={props.setComponentsListDashboard}
          setcomponentListIdSelected={props.setcomponentListIdSelected}
          componentSelected={componentSelected}
          numberRowGrid={numberRowGrid}
          setNumberRowGrid={setNumberRowGrid}
          removeRowUnused={removeRowUnused}
          dashboardProfilsUtilisateur={dashboardProfil}
          setDashboardProfil={setDashboardProfil}
          dashboardTitle={props.activeDashboard.title}
          setDashboardTitle={setDashboardtitle}
        />
      </MyFormik>

      {/* D&D actif seulement en édition */}
      {props.editTabIndex !== null ? (
        <DndContext onDragStart={handleDragStart} onDragEnd={handleDragEnd}>
          <Container fluid>
            {/* Grid Layout */}
            <Row className="position-relative">
              {numberRowGrid === 0 && (
                <div className="alert alert-primary" role="alert">
                  Ce dashboard n&apos;a aucun composant pour le moment.
                </div>
              )}
              {Array.from({ length: nbCol }, (_, colIndex) => (
                <Col
                  id="col-grid-component"
                  key={colIndex}
                  className="border p-0 position-relative"
                  style={{ height: "100%" }}
                >
                  {Array.from({ length: numberRowGrid }, (_, rowIndex) => (
                    <div
                      key={`${colIndex}-${rowIndex}`}
                      id={`${colIndex}-${rowIndex}`}
                      className="droppable border"
                      style={{
                        height: heightRow + "px", // Hauteur des lignes
                      }}
                    />
                  ))}
                </Col>
              ))}

              {/* Components */}
              {props.componentsListDashboard &&
                props.componentsListDashboard.map((component) => (
                  <DashboardItem
                    key={component.id}
                    component={component}
                    isSelected={
                      componentSelected
                        ? component.id === componentSelected.id
                        : false
                    }
                    heightRow={heightRow}
                  />
                ))}
            </Row>
          </Container>
        </DndContext>
      ) : (
        <Container fluid>
          {/* Grid Layout */}
          <Row className="position-relative">
            {numberRowGrid === 0 && (
              <div className="alert alert-primary" role="alert">
                Ce dashboard n&apos;a aucun composant pour le moment.
              </div>
            )}
            {Array.from({ length: nbCol }, (_, colIndex) => (
              <Col
                id="col-grid-component"
                key={colIndex}
                className="border p-0 position-relative"
                style={{ height: "100%" }}
              >
                {Array.from({ length: numberRowGrid }, (_, rowIndex) => (
                  <div
                    key={`${colIndex}-${rowIndex}`}
                    id={`${colIndex}-${rowIndex}`}
                    className="droppable border"
                    style={{
                      height: heightRow + "px", // Hauteur des lignes
                    }}
                  />
                ))}
              </Col>
            ))}

            {/* Components */}
            {props.componentsListDashboard &&
              props.componentsListDashboard.length > 0 &&
              props.componentsListDashboard.map((component) => (
                <DashboardItem
                  key={component.id}
                  component={component}
                  isSelected={
                    componentSelected
                      ? component.id === componentSelected.id
                      : false
                  }
                  heightRow={heightRow}
                />
              ))}
          </Row>
        </Container>
      )}
    </>
  );
};

export default ConfigDynamicGrid;

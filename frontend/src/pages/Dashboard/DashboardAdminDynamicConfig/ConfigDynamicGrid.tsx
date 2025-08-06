import { DndContext } from "@dnd-kit/core";
import { useEffect, useState } from "react";
import { Col, Container, Row } from "react-bootstrap";
import { object } from "yup";
import { useGetRun, usePost } from "../../../components/Fetch/useFetch.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import url from "../../../module/fetch.tsx";
import {
  ComponentDashboard,
  DashboardComponentConfig,
  DashboardItemParam,
  formatData,
  QueryData,
} from "../Constants.tsx";
import ConfigFormDashboard from "./ConfigFormDashboard.tsx";
import DashboardItem from "./DashboardItem.tsx";

type ConfigDynamicGridProps = {
  editTabIndex: number | undefined | null;
  componentsListDashboard: ComponentDashboard[];
  setComponentsListDashboard: (arg0: any) => void;
  activeDashboard: DashboardItemParam;
  setActiveDashboard: (arg0: DashboardItemParam) => void;
};

const HEIGHT_ROW = 200;
const NB_COL = 4; // Nombre de colonnes dans la grille

const getInitialValues = (componentSelected: any) => ({
  // Initialise la taille par défaut d'un nouveau composant
  hauteurComponent:
    componentSelected && componentSelected.configPosition
      ? (componentSelected.configPosition.hauteur ?? 1)
      : 1,
  largeurComponent:
    componentSelected && componentSelected.configPosition
      ? (componentSelected.configPosition.largeur ?? 1)
      : 1,
});

const getPrepareVariables = (
  activeDashboard: DashboardItemParam,
  componentsListDashboard: ComponentDashboard[] | null,
  dashboardTitle: string | null,
  dashboardProfil: any | null,
) => {
  // Configure les données à envoyés au service
  return {
    ...(activeDashboard.id && {
      dashboardId: activeDashboard.id,
    }),
    dashboardTitle: dashboardTitle,
    dashboardComponents: componentsListDashboard
      ? componentsListDashboard.map((component: ComponentDashboard) => ({
          componentId: component.id,
          componentConfig: JSON.stringify({
            componentLargeur: component.configPosition?.largeur,
            componentHauteur: component.configPosition?.hauteur,
            componentX: component.configPosition?.x,
            componentY: component.configPosition?.y,
          }),
        }))
      : null,
    dashboardProfilsId: dashboardProfil
      ? dashboardProfil.map(
          (p: { profilUtilisateurId: any }) => p.profilUtilisateurId,
        )
      : null,
  };
};

const ConfigDynamicGrid = ({
  editTabIndex,
  componentsListDashboard,
  setComponentsListDashboard,
  activeDashboard,
  setActiveDashboard,
}: ConfigDynamicGridProps) => {
  const [dashboardProfil, setDashboardProfil] = useState<any | null>();
  const [dashboardTitle, setDashboardtitle] = useState<string | null>(
    "Nouveau Dashboard",
  );

  const [numberRowGrid, setNumberRowGrid] = useState(
    componentsListDashboard ? componentsListDashboard.length * NB_COL : 0,
  ); // Nombre de ligne dans grille

  const [componentSelected, setComponentSelected] =
    useState<ComponentDashboard | null>(); // Composant sélectionner dans la grid

  const handleDragStart = (event: any) => {
    // Set le composant sélectionné
    setComponentSelected(
      componentsListDashboard.find(
        (component: { id: any }) => component.id === event.active.id,
      ),
    );
  };
  const handleDragEnd = (event: {
    delta: { x: number; y: number };
    active: { id: any };
  }) => {
    const tagCol = document.getElementById("col-grid-component"); // Largeur d'une colonne (variable selon taille écran)
    const widthCol = tagCol?.offsetWidth ? tagCol.offsetWidth : 0;
    const newX = Math.round(event.delta.x / widthCol);
    const newY = Math.round(event.delta.y / HEIGHT_ROW);

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
      componentsListDashboard.length > 0 &&
      componentSelected &&
      componentSelected.configPosition &&
      newX + (componentSelected.configPosition.largeur ?? 1) <= NB_COL &&
      newY + (componentSelected.configPosition.hauteur ?? 1) <= NB_COL &&
      posX <= NB_COL &&
      posX >= 0 &&
      posY <= componentsListDashboard.length * NB_COL &&
      posY >= 0
    ) {
      // Set les nouvelles coordonnées dans la grille
      const newComponentConfig = componentsListDashboard.find(
        (component: { id: any }) => component.id === event.active.id,
      );

      if (newComponentConfig && newComponentConfig.configPosition) {
        newComponentConfig.configPosition.x = posX;
        newComponentConfig.configPosition.y = posY;
        setComponentSelected(newComponentConfig);
        const newComponentList = componentsListDashboard.map(
          (component: { id: any }) =>
            component.id === event.active.id ? newComponentConfig : component,
        );
        setComponentsListDashboard(newComponentList);

        // Supprime les lignes en trop si existe
        removeRowUnused(newComponentList);
      }
    }
  };

  const removeRowUnused = (componentList: any[]) => {
    // Supprime les lignes innocupés après le composant le plus bas
    let lowestRow = -1;

    componentList.forEach(
      (component: { configPosition: { y: any; hauteur: any } }) => {
        const { y, hauteur } = component.configPosition;
        const bottomLine = parseInt(y) + parseInt(hauteur); // Ligne la plus basse occupée par ce composant
        if (bottomLine > lowestRow) {
          lowestRow = bottomLine; // Mettre à jour la ligne la plus basse
        }
      },
    );

    lowestRow =
      lowestRow < componentList.length * NB_COL // S'assure qu'on affiche toujours un minimum de ligne égale au nombre de composants
        ? componentList.length * NB_COL
        : lowestRow;

    setNumberRowGrid(lowestRow);
  };

  useEffect(() => {
    setNumberRowGrid(
      componentsListDashboard ? componentsListDashboard.length * NB_COL : 0,
    );
  }, [componentsListDashboard]);

  const urlApiDashboard = url`/api/dashboard/get-dashboard-config/`;

  const { run: runListComponents, data: listComponents } = useGetRun(
    urlApiDashboard + activeDashboard.id,
    {},
  );

  useEffect(() => {
    runListComponents();
  }, [activeDashboard.id, runListComponents]);

  // On initialise la liste des composants du dashbord
  useEffect(() => {
    if (listComponents) {
      const newComponentList: ComponentDashboard[] = [];
      listComponents?.forEach((element: DashboardComponentConfig) => {
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
            largeur: element.componentConfigPosition
              ? element.componentConfigPosition.componentLargeur
              : 2,
            hauteur: element.componentConfigPosition
              ? element.componentConfigPosition.componentHauteur
              : 2,
          },
          queryId: element.componentQueryId,
        });
      });
      setComponentsListDashboard(newComponentList);
    }
  }, [listComponents, setComponentsListDashboard]);

  // Récupère en base les profils liés à un dashboard
  const fetchDashboardProfils = useGetRun(
    url`/api/dashboard/get-dashboard-list-profil/` + activeDashboard.id,
    {},
  );
  useEffect(() => {
    if (fetchDashboardProfils.isResolved) {
      setDashboardProfil(fetchDashboardProfils.data);
    }
  }, [fetchDashboardProfils.data, fetchDashboardProfils.isResolved]);

  // Récupère les dashboard déjà en base
  useEffect(() => {
    if (activeDashboard && !componentsListDashboard) {
      setComponentsListDashboard([]);
      setDashboardProfil(null);
      setDashboardtitle(activeDashboard.title);
      if (activeDashboard.id) {
        fetchDashboardProfils.run();
      }
    }
  }, [
    activeDashboard,
    componentsListDashboard,
    setComponentsListDashboard,
    fetchDashboardProfils,
  ]);

  const urlApiSaveDashboard = url`/api/dashboard/create-dashboard/`;
  const urlApiUpdateDashboard = url`/api/dashboard/update-dashboard/`;

  return (
    <Container fluid>
      <MyFormik
        validationSchema={object({})}
        onSubmit={(dashboard) => {
          setActiveDashboard({
            id: dashboard.dashboardId,
            title: dashboard.dashboardTitle,
          });
        }}
        initialValues={getInitialValues(componentSelected)}
        submitUrl={
          activeDashboard.id ? urlApiUpdateDashboard : urlApiSaveDashboard
        }
        successToastMessage="Le tableau de bord a correctement été enregistré"
        errorToastMessage="Le tableau de bord n'a pas été correctement enregistré"
        prepareVariables={() =>
          getPrepareVariables(
            activeDashboard,
            componentsListDashboard,
            dashboardTitle,
            dashboardProfil,
          )
        }
        isPost={activeDashboard.id ? false : true}
      >
        <ConfigFormDashboard
          editTabIndex={editTabIndex}
          setComponentSelected={setComponentSelected}
          componentsListDashboard={componentsListDashboard}
          setComponentsListDashboard={setComponentsListDashboard}
          componentSelected={componentSelected ?? undefined}
          numberRowGrid={numberRowGrid}
          setNumberRowGrid={setNumberRowGrid}
          removeRowUnused={removeRowUnused}
          dashboardProfilsUtilisateur={dashboardProfil}
          setDashboardProfil={setDashboardProfil}
          dashboardTitle={activeDashboard.title}
          setDashboardTitle={setDashboardtitle}
        />
      </MyFormik>
      {/* Grid Layout */}
      <Row className="position-relative">
        {/* D&D actif seulement en édition */}
        {Array.from({ length: NB_COL }, (_, colIndex) => (
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
                  height: HEIGHT_ROW + "px", // Hauteur des lignes
                }}
              />
            ))}
          </Col>
        ))}
        {editTabIndex !== null ? (
          <DndContext onDragStart={handleDragStart} onDragEnd={handleDragEnd}>
            <ListComponents
              componentsListDashboard={componentsListDashboard}
              setComponentsListDashboard={setComponentsListDashboard}
              componentSelected={componentSelected}
            />
          </DndContext>
        ) : (
          <ListComponents
            componentsListDashboard={componentsListDashboard}
            setComponentsListDashboard={setComponentsListDashboard}
            componentSelected={componentSelected}
          />
        )}
      </Row>
    </Container>
  );
};

export default ConfigDynamicGrid;

/**
 * Permet d'afficher la liste des composants du dashboard
 * Va chercher les données des requêtes associées aux composants
 * @param param0
 * @returns
 */
const ListComponents = ({
  componentsListDashboard,
  setComponentsListDashboard,
  componentSelected,
}: {
  componentsListDashboard: ComponentDashboard[];
  setComponentsListDashboard: React.Dispatch<
    React.SetStateAction<ComponentDashboard[]>
  >;
  componentSelected: ComponentDashboard | null | undefined;
}) => {
  const { data: dataQuerys, run: runDataQuerys } = usePost(
    url`/api/dashboard/get-list-data-query/`,
    {},
  );

  useEffect(() => {
    if (
      componentsListDashboard &&
      componentsListDashboard.length > 0 &&
      componentsListDashboard.some((c) => !("data" in c))
    ) {
      const queryIds = componentsListDashboard.map(
        (component) => component.queryId,
      );
      runDataQuerys({
        dashboardQueryIds: queryIds,
      });
    }
  }, [componentsListDashboard, runDataQuerys]);

  useEffect(() => {
    if (dataQuerys) {
      // On met à jour les données des composants avec les données des requêtes
      setComponentsListDashboard((prev: ComponentDashboard[]) => {
        return prev.map((component: ComponentDashboard): ComponentDashboard => {
          const queryData: QueryData | undefined = dataQuerys?.find(
            (query: QueryData) => query.queryId === component.queryId,
          );
          if (queryData) {
            try {
              const formattedData = formatData(queryData);

              return {
                ...component,
                data: formattedData,
              };
            } catch (error) {
              return component; // Garder le composant sans données en cas d'erreur
            }
          }
          return component;
        });
      });
    }
  }, [dataQuerys, setComponentsListDashboard]);

  return (
    <>
      {/* Components */}
      {componentsListDashboard &&
        componentsListDashboard.map((component) => {
          return (
            <DashboardItem
              key={component.id}
              component={component}
              isSelected={componentSelected?.id === component.id}
              heightRow={HEIGHT_ROW}
            />
          );
        })}
    </>
  );
};

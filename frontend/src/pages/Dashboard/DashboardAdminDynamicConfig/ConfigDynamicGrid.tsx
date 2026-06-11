import { DndContext } from "@dnd-kit/core";
import { useEffect, useRef, useState } from "react";
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
  normalizeComponentConfig,
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
  // Cache partagé depuis le parent
  dashboardCacheRef: React.MutableRefObject<
    Map<string, { components: ComponentDashboard[]; profils: any }>
  >;
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

// Référence stable pour éviter de recréer `run` à chaque render
const EMPTY_OPTIONS = {};

const ConfigDynamicGrid = ({
  editTabIndex,
  componentsListDashboard,
  setComponentsListDashboard,
  activeDashboard,
  setActiveDashboard,
  dashboardCacheRef,
}: ConfigDynamicGridProps) => {
  // Initialiser le profil depuis le cache si disponible
  const [dashboardProfil, setDashboardProfil] = useState<any | null>(() => {
    const cached = activeDashboard.id
      ? dashboardCacheRef.current.get(activeDashboard.id)
      : null;
    return cached?.profils ?? undefined;
  });
  const [dashboardTitle, setDashboardtitle] = useState<string | null>(
    "Nouveau Dashboard",
  );

  const [numberRowGrid, setNumberRowGrid] = useState(
    componentsListDashboard ? componentsListDashboard.length * NB_COL : 0,
  );

  const [componentSelected, setComponentSelected] =
    useState<ComponentDashboard | null>();

  // Refs pour éviter les boucles : on suit le dernier id chargé
  const lastLoadedConfigId = useRef<string | null>(null);
  const lastLoadedProfilId = useRef<string | null>(null);

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
      posX + (componentSelected.configPosition.largeur ?? 1) <= NB_COL &&
      posY + (componentSelected.configPosition.hauteur ?? 1) <= numberRowGrid &&
      posX >= 0 &&
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
    EMPTY_OPTIONS,
  );

  // Ref toujours à jour vers run, mais hors des deps de useEffect
  const runListComponentsRef = useRef(runListComponents);
  runListComponentsRef.current = runListComponents;

  // On initialise la liste des composants du dashboard
  useEffect(() => {
    if (listComponents) {
      // si on a déjà des composants AVEC leurs data
      // on n'écrase pas avec une version "fraîche" sans data
      setComponentsListDashboard((prev: ComponentDashboard[] | null) => {
        if (prev && prev.length > 0 && prev.every((c) => "data" in c)) {
          return prev;
        }

        const newComponentList: ComponentDashboard[] = [];
        listComponents?.forEach((element: DashboardComponentConfig) => {
          newComponentList.push({
            id: element.componentId,
            key: element.componentKey,
            title: element.componentTitle,
            config: normalizeComponentConfig(
              element.componentKey,
              element.componentConfig,
            ),
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
        return newComponentList;
      });
    }
  }, [listComponents, setComponentsListDashboard]);

  // Récupère en base les profils liés à un dashboard
  const fetchDashboardProfils = useGetRun(
    url`/api/dashboard/get-dashboard-list-profil/` + activeDashboard.id,
    EMPTY_OPTIONS,
  );

  // Extraction de valeurs
  const profilsResolved = fetchDashboardProfils.isResolved;
  const profilsData = fetchDashboardProfils.data;
  const runProfilsRef = useRef(fetchDashboardProfils.run);
  runProfilsRef.current = fetchDashboardProfils.run;

  useEffect(() => {
    if (profilsResolved) {
      setDashboardProfil(profilsData);
    }
  }, [profilsData, profilsResolved]);

  useEffect(() => {
    if (activeDashboard && !componentsListDashboard) {
      setComponentsListDashboard([]);
      setDashboardProfil(null);
      setDashboardtitle(activeDashboard.title);
      if (
        activeDashboard.id &&
        lastLoadedProfilId.current !== activeDashboard.id
      ) {
        lastLoadedProfilId.current = activeDashboard.id;
        runProfilsRef.current();
      }
    }
  }, [activeDashboard, componentsListDashboard, setComponentsListDashboard]);

  // Au changement de dashboard : check le cache AVANT de fetch
  useEffect(() => {
    if (!activeDashboard.id) {
      return;
    }
    if (lastLoadedConfigId.current === activeDashboard.id) {
      return;
    }

    const cached = dashboardCacheRef.current.get(activeDashboard.id);
    if (cached) {
      // on bloque tout fetch et on restaure le profil
      lastLoadedConfigId.current = activeDashboard.id;
      lastLoadedProfilId.current = activeDashboard.id;
      setDashboardProfil(cached.profils);
      // Les composants sont déjà setté par le parent (handleDashboardClick)
    } else {
      //  on lance le fetch normalement
      lastLoadedConfigId.current = activeDashboard.id;
      runListComponentsRef.current();
    }
  }, [activeDashboard.id, dashboardCacheRef]);

  // Alimenter le cache dès que les données sont complètes
  useEffect(() => {
    if (
      activeDashboard.id &&
      componentsListDashboard &&
      componentsListDashboard.length > 0 &&
      componentsListDashboard.every((c) => "data" in c)
    ) {
      dashboardCacheRef.current.set(activeDashboard.id, {
        components: componentsListDashboard,
        profils: dashboardProfil,
      });
    }
  }, [
    activeDashboard.id,
    componentsListDashboard,
    dashboardProfil,
    dashboardCacheRef,
  ]);

  // Reset quand on change de dashboard pour autoriser un nouveau fetch
  useEffect(() => {
    return () => {
      // Au démontage
      lastLoadedConfigId.current = null;
      lastLoadedProfilId.current = null;
    };
  }, []);

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
    EMPTY_OPTIONS,
  );

  // Ref vers run pour ne pas le mettre en dépendances
  const runDataQuerysRef = useRef(runDataQuerys);
  runDataQuerysRef.current = runDataQuerys;

  const lastFetchedKey = useRef<string>("");

  useEffect(() => {
    if (
      componentsListDashboard &&
      componentsListDashboard.length > 0 &&
      componentsListDashboard.some((c) => !("data" in c))
    ) {
      const missingQueryIds = componentsListDashboard
        .filter((c) => !("data" in c))
        .map((c) => c.queryId);

      // clé pour éviter des re-fetch inutile
      const key = [...missingQueryIds].sort().join("|");
      if (key && key !== lastFetchedKey.current) {
        lastFetchedKey.current = key;
        runDataQuerysRef.current({ dashboardQueryIds: missingQueryIds });
      }
    }
  }, [componentsListDashboard]);

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
            } catch (_error) {
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

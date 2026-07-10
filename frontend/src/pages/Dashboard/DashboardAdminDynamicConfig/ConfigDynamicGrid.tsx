import { DndContext, DragEndEvent, DragStartEvent } from "@dnd-kit/core";
import {
  Dispatch,
  MutableRefObject,
  SetStateAction,
  useEffect,
  useRef,
  useState,
} from "react";
import { Col, Container, Row } from "react-bootstrap";
import { object } from "yup";
import { useGetRun } from "../../../components/Fetch/useFetch.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import url from "../../../module/fetch.tsx";
import {
  ComponentDashboard,
  DashboardComponentConfig,
  DashboardItemParam,
  normalizeComponentConfig,
} from "../Constants.tsx";
import ConfigFormDashboard from "./ConfigFormDashboard.tsx";
import DashboardItem from "./DashboardItem.tsx";

type ConfigDynamicGridProps = {
  editTabIndex: number | undefined | null;
  componentsListDashboard: ComponentDashboard[] | null;
  setComponentsListDashboard: Dispatch<
    SetStateAction<ComponentDashboard[] | null>
  >;
  activeDashboard: DashboardItemParam;
  setActiveDashboard: (arg0: DashboardItemParam) => void;
  // Cache partagé depuis le parent
  dashboardCacheRef: MutableRefObject<
    Map<
      string,
      { components: ComponentDashboard[]; profils: DashboardProfilApiEntry[] }
    >
  >;
};

const HEIGHT_ROW = 200;
const NB_COL = 4; // Nombre de colonnes dans la grille

type DashboardProfilApiEntry =
  | string
  | { profilUtilisateurId?: string | number; [key: string]: unknown };

const normalizeDashboardProfilIds = (
  dashboardProfil: Array<
    string | { profilUtilisateurId?: string | number }
  > | null,
) => {
  if (!dashboardProfil) {
    return null;
  }

  const normalized = dashboardProfil
    .map((profilEntry) => {
      if (typeof profilEntry === "string") {
        return profilEntry;
      }

      if (profilEntry && profilEntry.profilUtilisateurId !== undefined) {
        return String(profilEntry.profilUtilisateurId);
      }

      return null;
    })
    .filter((profilId): profilId is string => Boolean(profilId));

  return normalized;
};

const getInitialValues = (
  componentSelected: ComponentDashboard | null | undefined,
) => ({
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
  dashboardProfil: Array<
    string | { profilUtilisateurId?: string | number }
  > | null,
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
    dashboardProfilsId: normalizeDashboardProfilIds(dashboardProfil),
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
  const [dashboardProfil, setDashboardProfil] = useState<
    DashboardProfilApiEntry[] | null
  >(() => {
    const cached = activeDashboard.id
      ? dashboardCacheRef.current.get(activeDashboard.id)
      : null;
    return cached?.profils ?? null;
  });
  const [dashboardTitle, setDashboardtitle] = useState<string | null>(
    "Nouveau Dashboard",
  );

  const [numberRowGrid, setNumberRowGrid] = useState(
    componentsListDashboard ? componentsListDashboard.length * NB_COL : 0,
  );

  const [componentSelected, setComponentSelected] =
    useState<ComponentDashboard | null>();

  const loadedConfigIdsRef = useRef<Set<string>>(new Set());
  const configRequestInProgressIdRef = useRef<string | null>(null);
  const profilRequestInProgressIdRef = useRef<string | null>(null);

  const handleDragStart = (event: DragStartEvent) => {
    // Set le composant sélectionné
    setComponentSelected(
      componentsListDashboard?.find(
        (component) => component.id === event.active.id,
      ),
    );
  };
  const handleDragEnd = (event: DragEndEvent) => {
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
      componentsListDashboard &&
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
        (component) => component.id === event.active.id,
      );

      if (newComponentConfig && newComponentConfig.configPosition) {
        newComponentConfig.configPosition.x = posX;
        newComponentConfig.configPosition.y = posY;
        setComponentSelected(newComponentConfig);
        const newComponentList = componentsListDashboard.map((component) =>
          component.id === event.active.id ? newComponentConfig : component,
        );
        setComponentsListDashboard(newComponentList);

        // Supprime les lignes en trop si existe
        removeRowUnused(newComponentList);
      }
    }
  };

  const removeRowUnused = (componentList: ComponentDashboard[]) => {
    // Supprime les lignes innocupés après le composant le plus bas
    let lowestRow = -1;

    componentList.forEach((component) => {
      const y = component.configPosition?.y ?? 0;
      const hauteur = component.configPosition?.hauteur ?? 1;
      const bottomLine = Number(y) + Number(hauteur); // Ligne la plus basse occupée par ce composant
      if (bottomLine > lowestRow) {
        lowestRow = bottomLine; // Mettre à jour la ligne la plus basse
      }
    });

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
        if (activeDashboard.id) {
          loadedConfigIdsRef.current.add(activeDashboard.id);
          if (configRequestInProgressIdRef.current === activeDashboard.id) {
            configRequestInProgressIdRef.current = null;
          }
        }
        return newComponentList;
      });
    }
  }, [listComponents, setComponentsListDashboard, activeDashboard.id]);

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
      const normalizedProfils = Array.isArray(profilsData)
        ? (profilsData as DashboardProfilApiEntry[])
        : null;

      setDashboardProfil(normalizedProfils);
      if (activeDashboard.id) {
        if (profilRequestInProgressIdRef.current === activeDashboard.id) {
          profilRequestInProgressIdRef.current = null;
        }
      }
    }
  }, [profilsData, profilsResolved, activeDashboard.id]);

  useEffect(() => {
    const dashboardId = activeDashboard.id;
    const cached = dashboardId
      ? dashboardCacheRef.current.get(dashboardId)
      : null;

    setDashboardtitle(activeDashboard.title);
    setComponentsListDashboard((prev) => (prev === null ? [] : prev));

    if (cached) {
      setDashboardProfil(cached.profils);
      return;
    }

    setDashboardProfil(null);
  }, [
    activeDashboard.id,
    activeDashboard.title,
    setComponentsListDashboard,
    dashboardCacheRef,
  ]);

  // Au changement de dashboard : check cache et fetch sans doublon
  useEffect(() => {
    const dashboardId = activeDashboard.id;
    if (!dashboardId) {
      return;
    }

    const cached = dashboardCacheRef.current.get(dashboardId);
    if (cached) {
      loadedConfigIdsRef.current.add(dashboardId);
    }

    if (
      !loadedConfigIdsRef.current.has(dashboardId) &&
      configRequestInProgressIdRef.current !== dashboardId
    ) {
      configRequestInProgressIdRef.current = dashboardId;
      runListComponentsRef.current();
    }

    if (profilRequestInProgressIdRef.current !== dashboardId) {
      profilRequestInProgressIdRef.current = dashboardId;
      runProfilsRef.current();
    }
  }, [activeDashboard.id, dashboardCacheRef]);

  // Reset à la fermeture
  useEffect(() => {
    return () => {
      loadedConfigIdsRef.current = new Set();
      configRequestInProgressIdRef.current = null;
      profilRequestInProgressIdRef.current = null;
    };
  }, []);

  const handleDashboardProfilChange = (
    profilsIds: DashboardProfilApiEntry[] | null,
  ) => {
    setDashboardProfil(profilsIds);
  };

  const urlApiSaveDashboard = url`/api/dashboard/create-dashboard/`;
  const urlApiUpdateDashboard = url`/api/dashboard/update-dashboard/`;

  return (
    <Container fluid>
      <MyFormik
        validationSchema={object({})}
        onSubmit={(dashboard) => {
          const savedDashboardId = dashboard.dashboardId ?? activeDashboard.id;
          const savedDashboardTitle =
            dashboard.dashboardTitle ?? dashboardTitle ?? activeDashboard.title;

          if (savedDashboardId) {
            dashboardCacheRef.current.set(savedDashboardId, {
              components: componentsListDashboard ?? [],
              profils: dashboardProfil ?? [],
            });
          }

          setActiveDashboard({
            id: savedDashboardId,
            title: savedDashboardTitle,
            index: activeDashboard.index,
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
          componentsListDashboard={componentsListDashboard ?? []}
          setComponentsListDashboard={setComponentsListDashboard}
          componentSelected={componentSelected ?? undefined}
          numberRowGrid={numberRowGrid}
          setNumberRowGrid={setNumberRowGrid}
          removeRowUnused={removeRowUnused}
          dashboardProfilsUtilisateur={dashboardProfil}
          setDashboardProfil={handleDashboardProfilChange}
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
              componentSelected={componentSelected}
            />
          </DndContext>
        ) : (
          <ListComponents
            componentsListDashboard={componentsListDashboard}
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
  componentSelected,
}: {
  componentsListDashboard: ComponentDashboard[] | null;
  componentSelected: ComponentDashboard | null | undefined;
}) => {
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

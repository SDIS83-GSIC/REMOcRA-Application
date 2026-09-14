import { useDraggable } from "@dnd-kit/core";
import { useEffect, useRef, useState } from "react";
import { Card } from "react-bootstrap";
import Loading from "../../../components/Elements/Loading/Loading.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import url from "../../../module/fetch.tsx";
import {
  COMPONENTS,
  ComponentDashboard,
  formatData,
  QueryData,
} from "../Constants.tsx";

type DashboardItemProps = {
  component: ComponentDashboard;
  isSelected: boolean;
  heightRow: number;
};

type DashboardComponentDataRow = Record<string, unknown>;
type DashboardComponentData = DashboardComponentDataRow[];

const componentDataCache = new Map<string, DashboardComponentData | null>();
const pendingComponentQueryIds = new Set<string>();
const DATA_EMPTY: null = null;
// Subscribers notifiés quand une data queryId arrive (ou est annulée)
const querySubscribers = new Map<string, Set<() => void>>();

const getComponentDataCacheKey = (queryId: string | undefined) =>
  queryId ? `query:${queryId}` : null;

const subscribeToQuery = (
  queryId: string,
  callback: () => void,
): (() => void) => {
  if (!querySubscribers.has(queryId)) {
    querySubscribers.set(queryId, new Set());
  }
  querySubscribers.get(queryId)!.add(callback);
  return () => {
    querySubscribers.get(queryId)?.delete(callback);
  };
};

const notifyQuerySubscribers = (queryId: string) => {
  querySubscribers.get(queryId)?.forEach((cb) => cb());
  querySubscribers.delete(queryId);
};

const DashboardItem = (props: DashboardItemProps) => {
  const queryId: string | undefined = props.component?.queryId;
  const cacheKey = getComponentDataCacheKey(queryId);
  const [formattedData, setFormattedData] = useState<
    DashboardComponentData | null | undefined
  >(() => {
    if (props.component.data !== undefined) {
      return Array.isArray(props.component.data)
        ? (props.component.data as DashboardComponentData)
        : DATA_EMPTY;
    }

    if (!cacheKey) {
      return undefined;
    }

    return componentDataCache.has(cacheKey)
      ? componentDataCache.get(cacheKey)
      : undefined;
  });

  const {
    data: dataQuerys,
    run: runDataQuerys,
    isRejected: isDataQueryRejected,
  } = useGet(url`/api/dashboard/get-list-data-query/` + queryId);
  const runDataQuerysRef = useRef(runDataQuerys);
  runDataQuerysRef.current = runDataQuerys;

  const { attributes, listeners, setNodeRef, transform } = useDraggable({
    id: props.component.id,
  });

  useEffect(() => {
    if (props.component.data !== undefined) {
      const value = Array.isArray(props.component.data)
        ? (props.component.data as DashboardComponentData)
        : DATA_EMPTY;
      setFormattedData(value);
      if (cacheKey) {
        componentDataCache.set(cacheKey, value);
      }
      return;
    }

    if (!cacheKey) {
      setFormattedData(undefined);
      return;
    }

    if (componentDataCache.has(cacheKey)) {
      setFormattedData(componentDataCache.get(cacheKey));
    } else {
      setFormattedData(undefined);
    }
  }, [props.component.data, cacheKey]);

  useEffect(() => {
    if (!queryId || formattedData !== undefined) {
      return;
    }

    if (!cacheKey) {
      return;
    }

    // Vérifie d'abord si la data est arrivée dans le cache entre temps
    if (componentDataCache.has(cacheKey)) {
      setFormattedData(componentDataCache.get(cacheKey));
      return;
    }

    // S'abonne pour être notifié quand la data arrive (via un autre item ou retard)
    const unsubscribe = subscribeToQuery(queryId, () => {
      if (componentDataCache.has(cacheKey)) {
        setFormattedData(componentDataCache.get(cacheKey));
      }
    });

    if (pendingComponentQueryIds.has(queryId)) {
      // Un autre item a déjà lancé le fetch, on attend juste la notification
      return unsubscribe;
    }

    pendingComponentQueryIds.add(queryId);
    runDataQuerysRef.current({ dashboardQueryIds: [queryId] });

    return unsubscribe;
  }, [queryId, cacheKey, formattedData]);

  useEffect(() => {
    if (!dataQuerys || !queryId) {
      return;
    }

    const resolvedQueryData = Array.isArray(dataQuerys)
      ? dataQuerys
      : [dataQuerys];
    const queryData: QueryData | undefined = resolvedQueryData.find(
      (query: QueryData) => query.queryId === queryId,
    );

    if (!queryData) {
      return;
    }

    pendingComponentQueryIds.delete(queryId);

    try {
      const rawFormattedData = formatData(queryData);
      const nextFormattedData = Array.isArray(rawFormattedData)
        ? (rawFormattedData as DashboardComponentData)
        : DATA_EMPTY;
      if (cacheKey) {
        componentDataCache.set(cacheKey, nextFormattedData);
      }
      setFormattedData(nextFormattedData);
      // Notifie tous les autres items qui attendaient cette queryId
      notifyQuerySubscribers(queryId);
    } catch (_error) {
      pendingComponentQueryIds.delete(queryId);
      notifyQuerySubscribers(queryId);
      setFormattedData(DATA_EMPTY);
    }
  }, [dataQuerys, queryId, cacheKey]);

  useEffect(() => {
    if (isDataQueryRejected && queryId) {
      // Libère le verrou pour permettre un retry et notifie les subscribers
      pendingComponentQueryIds.delete(queryId);
      notifyQuerySubscribers(queryId);
    }
  }, [isDataQueryRejected, queryId]);

  // Cleanup : retire cet item du pending s'il est démonté avant la réponse
  useEffect(() => {
    return () => {
      if (queryId) {
        pendingComponentQueryIds.delete(queryId);
      }
    };
  }, [queryId]);

  const componentPosition = props.component.configPosition ?? {
    x: 0,
    y: 0,
    largeur: 1,
    hauteur: 1,
  };

  const style = {
    transform: `translate3d(${transform?.x || 0}px, ${transform?.y || 0}px, 0)`,
    position: "absolute" as const,
    top: `${componentPosition.y * props.heightRow}px`,
    left: `${componentPosition.x * 25}%`,
    width: `${(componentPosition.largeur ?? 1) * 25}%`,
    height: `calc(${(componentPosition.hauteur ?? 1) * props.heightRow}px - 2rem)`,
    zIndex: props.isSelected ? 10 : 1,
  };

  // Set le composant associé à la clé
  const componentEntry =
    COMPONENTS[props.component.key as keyof typeof COMPONENTS];
  const Component = componentEntry?.component;

  return (
    <div
      ref={setNodeRef}
      {...attributes}
      {...listeners}
      style={style}
      className={`dashboard-item ${props.isSelected ? "active" : ""}`}
    >
      <Card
        bg="secondary"
        className="p-2"
        style={{
          border: props.isSelected ? "2px solid blue" : "1px solid grey",
          boxShadow: props.isSelected
            ? "0 0 10px rgba(0, 0, 255, 0.5)"
            : "none",
        }}
      >
        <Card.Body className="p-0">
          <Card.Header>{props.component.title}</Card.Header>
          <div style={{ height: `calc(${style.height} - 2rem)` }}>
            {formattedData === undefined ? (
              <Loading />
            ) : formattedData === DATA_EMPTY ||
              (Array.isArray(formattedData) && formattedData.length === 0) ? (
              <div className="text-center text-muted p-3">
                Aucune donnée disponible
              </div>
            ) : Component ? (
              <Component data={formattedData} config={props.component.config} />
            ) : (
              <div className="text-center text-danger p-3">
                Composant indisponible
              </div>
            )}
          </div>
        </Card.Body>
      </Card>
    </div>
  );
};

export default DashboardItem;

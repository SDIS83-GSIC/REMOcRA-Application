import { GeoJSON } from "ol/format";
import VectorLayer from "ol/layer/Vector";
import { bbox } from "ol/loadingstrategy";
import VectorSource from "ol/source/Vector";
import { Fill, Stroke, Style } from "ol/style";
import CircleStyle from "ol/style/Circle";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import url, { getFetchOptions } from "../../../module/fetch.tsx";
import { useGet } from "../../Fetch/useFetch.tsx";
import { TypeModuleRemocra } from "../../ModuleRemocra/ModuleRemocra.tsx";
import MapComponent, { useMapComponent } from "../Map.tsx";
import { useToolbarContext } from "../MapToolbar.tsx";
import MapToolbarCrise, { useToolbarCriseContext } from "./MapToolbarCrise.tsx";

const MapCrise = ({
  criseId,
  evenementStatutMode,
  variant,
}: {
  criseId: string;
  evenementStatutMode: string;
  variant: string;
}) => {
  const mapElement = useRef<HTMLDivElement>();
  const [evenementIdsFiltres, setEvenementIdsFiltres] = useState<string[]>([]);
  const evenementIdsRef = useRef<string[]>([]);

  const handleEvenementIdsFiltresChange = useCallback(
    (listeEvenementId: string[]) => {
      setEvenementIdsFiltres(listeEvenementId);
      evenementIdsRef.current = listeEvenementId;
    },
    [],
  );

  // Construire les viewParams supplémentaires avec les IDs filtrés - VERSION STABLE (sans dépendances)
  // La ref sera mise à jour mais viewParamsExtras ne changera pas de référence
  const viewParamsExtras = useMemo<Record<string, string>>(() => {
    return {};
  }, []);

  /** Permet d'afficher les géometries évènements */
  const getLayerUrl = useCallback(
    (
      extent: number[],
      projection: { getCode: () => string },
      evenementIdsFiltres: string[],
    ) => {
      const params = new URLSearchParams();
      if (evenementIdsFiltres?.length > 0) {
        params.append("evenementIds", JSON.stringify(evenementIdsFiltres));
      }
      params.append("bbox", extent.join(","));
      params.append("srid", projection.getCode());
      params.append("criseId", criseId);
      params.append("state", evenementStatutMode);
      return `/api/crise/evenement/layer?${params.toString()}`;
    },
    [criseId, evenementStatutMode],
  );

  const listeCouches = useGet(url`/api/crise/${criseId}/get-couches`)?.data;

  const {
    map,
    workingLayer,
    availableLayers,
    addOrRemoveLayer,
    layerListRef,
    projection,
    showOutilI,
    mapToolbarRef,
  } = useMapComponent({
    mapElement: mapElement,
    typeModule: TypeModuleRemocra.CRISE,
    criseId: criseId,
    evenementStatutMode: evenementStatutMode,
    viewParamsExtras: viewParamsExtras,
  });

  const dataEvenementLayer = useMemo(() => {
    if (!map) {
      return;
    }
    // on met en place un style transparent pour que les SDIS le définissent directement dans geoserver (invisible à l'oeil nu mais pas totalement transparent pour que les interactions fonctionnent)
    const style = new Style({
      fill: new Fill({ color: "rgba(0, 0, 0, 0.01)" }),
      stroke: new Stroke({ color: "rgba(0, 0, 0, 0.01)", width: 1 }),
      image: new CircleStyle({
        radius: 5,
        stroke: new Stroke({ color: "rgba(0, 0, 0, 0.01)" }),
        fill: new Fill({ color: "rgba(0, 0, 0, 0.01)" }),
      }),
    });

    const vectorSource = new VectorSource({
      strategy: bbox,
      format: new GeoJSON({
        dataProjection: projection,
        featureProjection: projection,
      }),
    });

    const layer = new VectorLayer({
      source: vectorSource,
      style,
      opacity: 1,
      visible: true,
      minZoom: 8,
      maxResolution: 99999,
      zIndex: 9999,
    });

    map.addLayer(layer);
    return layer;
  }, [map, projection]);

  useEffect(() => {
    if (!map || !dataEvenementLayer) {
      return;
    }
    const source = dataEvenementLayer.getSource()!;
    source?.clear();

    source!.setLoader(
      async (
        extent: number[],
        _: any,
        projection: { getCode: () => string },
        success: (arg0: any) => void,
        failure: () => void,
      ) => {
        try {
          const res = await fetch(
            getLayerUrl(extent, projection, evenementIdsFiltres),
            getFetchOptions({ method: "GET" }),
          );
          const features = source.getFormat().readFeatures(await res.json());
          source.addFeatures(features);
          success(features);
        } catch {
          source.removeLoadedExtent(extent);
          failure();
        }
      },
    );

    // Vider complètement la source et forcer le rechargement
    source.clear();
    // Forcer le rechargement de toutes les extents actuellement visibles
    const view = map.getView();
    const extent = view.calculateExtent(map.getSize());
    source.removeLoadedExtent(extent);
    source.refresh();
  }, [dataEvenementLayer, getLayerUrl, map, evenementIdsFiltres]);

  const {
    tools: extraTools,
    handleCloseEvent,
    showCreateEvent,
    showListEvent,
    showListDocument,
    showPersonalReports,
    setShowPersonalReports,
    setShowListEvent,
    setShowCreateEvent,
    setShowListDocument,
    listeEventId,
    setSousTypeElement,
    geometryElement,
    setGeometryReportCode,
    reportGeometryElement,
    sousTypeElement,
  } = useToolbarCriseContext({
    map,
    workingLayer,
    dataEvenementLayer,
  });

  /**
   * Met à jour la liste des couches actives sur la carte en fonction des couches WMS sélectionnées.
   */
  const listeDesCouches = useMemo(() => {
    if (!listeCouches) {
      return [];
    }
    return availableLayers.map((group: any) => ({
      ...group,
      layers: group.layers.filter((layer: any) => {
        const isActive = listeCouches.some(
          (c: any) =>
            c.code === layer.code && c[evenementStatutMode.toLowerCase()],
        );
        return isActive;
      }),
    }));
  }, [availableLayers, listeCouches, evenementStatutMode]);

  const { toggleTool, activeTool, infoOutilI, handleCloseInfoI } =
    useToolbarContext({
      availableLayers: listeDesCouches,
      map: map,
      workingLayer: workingLayer,
      extraTools: extraTools,
    });

  useEffect(() => {
    if (!map || !listeCouches) {
      return;
    }

    const activeCoucheCodes = new Set(
      listeCouches
        .filter((c: any) => c[evenementStatutMode.toLowerCase()])
        .map((c: any) => c.code),
    );

    availableLayers
      .flatMap((group: any) => group.layers)
      .forEach((layer: any) => {
        const currentLayer = map
          .getLayers()
          .getArray()
          .find((l: any) => l === layer.openlayer);

        if (currentLayer) {
          currentLayer.setVisible(activeCoucheCodes.has(layer.code));
        }
      });
  }, [availableLayers, listeCouches, map, evenementStatutMode]);

  // Rafraîchir les couches WMS/WMTS quand les IDs filtrés changent
  useEffect(() => {
    if (!map) {
      return;
    }

    map.getLayers().forEach((layer) => {
      if (layer.getSource().updateParams) {
        // Récupérer les paramètres actuels
        const currentParams = layer.getSource().getParams();
        const currentViewParams = currentParams?.viewParams || "";

        // Parser les viewParams existants pour extraire les autres paramètres
        const viewParamsMap: Record<string, string> = {};
        if (currentViewParams) {
          currentViewParams.split(";").forEach((param: string) => {
            const [key, value] = param.split(":");
            if (key && value) {
              viewParamsMap[key.trim()] = value;
            }
          });
        }

        // Mettre à jour ou ajouter evenementIds
        if (evenementIdsRef.current.length > 0) {
          viewParamsMap.evenementIds = evenementIdsRef.current.join("|");
        } else {
          delete viewParamsMap.evenementIds;
        }

        // Reconstruire le string viewParams avec tous les paramètres
        const newViewParams = Object.entries(viewParamsMap)
          .map(([key, value]) => `${key}:${value}`)
          .join(";");

        layer.getSource().updateParams({
          time: Date.now(),
          viewParams: newViewParams,
        });
        layer.getSource().refresh();
      }
    });
  }, [map]);

  return (
    <MapComponent
      key={evenementStatutMode}
      map={map}
      showZoomPlace={false}
      mapElement={mapElement}
      availableLayers={listeDesCouches}
      addOrRemoveLayer={addOrRemoveLayer} // les éléments de fonds IGN
      layerListRef={layerListRef} // les éléments de fonds IGN
      mapToolbarRef={mapToolbarRef} // les boutons à modifier / rajouter
      toggleTool={toggleTool}
      outilI={infoOutilI}
      showOutilI={showOutilI}
      handleCloseInfoI={handleCloseInfoI}
      activeTool={activeTool}
      toolbarElement={
        mapToolbarRef.current &&
        dataEvenementLayer && (
          <MapToolbarCrise
            setGeometryReportCode={setGeometryReportCode}
            evenementStatutMode={evenementStatutMode}
            map={map}
            criseId={criseId}
            handleCloseEvent={handleCloseEvent}
            listeEventId={listeEventId}
            toggleTool={toggleTool}
            activeTool={activeTool}
            reportGeometryElement={reportGeometryElement}
            geometryElement={geometryElement}
            workingLayer={workingLayer}
            setSousTypeElement={setSousTypeElement}
            sousTypeElement={sousTypeElement}
            dataCriseLayer={dataEvenementLayer}
            showListEvent={showListEvent}
            showListDocument={showListDocument}
            showCreateEvent={showCreateEvent}
            setShowListEvent={setShowListEvent}
            setShowCreateEvent={setShowCreateEvent}
            setShowListDocument={setShowListDocument}
            setShowPersonalReports={setShowPersonalReports}
            showPersonalReports={showPersonalReports}
            variant={variant}
            onEvenementIdsFiltresChange={handleEvenementIdsFiltresChange}
          />
        )
      }
      variant={variant}
    />
  );
};

export default MapCrise;

import "ol/ol.css";
import { useCallback, useEffect, useMemo, useRef } from "react";
import CircleStyle from "ol/style/Circle";
import { Fill, Stroke, Style } from "ol/style";
import { Vector } from "ol/source";
import { GeoJSON } from "ol/format";
import { bbox } from "ol/loadingstrategy";
import VectorLayer from "ol/layer/Vector";
import { TypeModuleRemocra } from "../../ModuleRemocra/ModuleRemocra.tsx";
import MapComponent, { useMapComponent } from "../Map.tsx";
import { useToolbarContext } from "../MapToolbar.tsx";
import url, { getFetchOptions } from "../../../module/fetch.tsx";
import MapToolbarCrise, { useToolbarCriseContext } from "./MapToolbarCrise.tsx";

const MapCrise = ({ criseId, state }: { criseId: string; state: string }) => {
  const mapElement = useRef<HTMLDivElement>();

  const {
    map,
    workingLayer,
    availableLayers,
    addOrRemoveLayer,
    layerListRef,
    projection,
    mapToolbarRef,
  } = useMapComponent({
    mapElement: mapElement,
    typeModule: TypeModuleRemocra.CRISE,
  });

  /** Permet d'afficher les géometries évènements */
  const getLayerUrl = useCallback(
    (extent: number[], projection: { getCode: () => string }) =>
      `/api/crise/evenement/layer?bbox=${extent.join(",")}&srid=${projection.getCode()}&criseId=${criseId}&state=${state}`,
    [criseId, state],
  );

  const dataEvenementLayer = useMemo(() => {
    if (!map) {
      return;
    }
    const style = new Style({
      fill: new Fill({ color: "rgba(255, 255, 255, 0.2)" }),
      stroke: new Stroke({ color: "rgba(0, 0, 0, 0.5)", width: 1 }),
      image: new CircleStyle({
        radius: 5,
        stroke: new Stroke({ color: "rgb(0, 160, 27)" }),
        fill: new Fill({ color: "rgb(0, 160, 27)" }),
      }),
    });

    const vectorSource = new Vector({
      extent: map.getView().calculateExtent(),
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
      minZoom: 12,
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
            url`${getLayerUrl(extent, projection)}`,
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

    source.refresh();
  }, [dataEvenementLayer, getLayerUrl, map, projection]);

  const {
    tools: extraTools,
    handleCloseEvent,
    showCreateEvent,
    showListEvent,
    showListDocument,
    setShowListEvent,
    setShowCreateEvent,
    setShowListDocument,
    listeEventId,
    setSousTypeElement,
    geometryElement,
    sousTypeElement,
  } = useToolbarCriseContext({
    map,
    workingLayer,
    dataEvenementLayer,
  });

  const { toggleTool, activeTool } = useToolbarContext({
    map: map,
    workingLayer: workingLayer,
    extraTools: extraTools,
  });

  return (
    <MapComponent
      map={map}
      workingLayer={workingLayer}
      mapElement={mapElement}
      availableLayers={availableLayers} // les éléments de fonds IGN
      addOrRemoveLayer={addOrRemoveLayer} // les éléments de fonds IGN
      layerListRef={layerListRef} // les éléments de fonds IGN
      mapToolbarRef={mapToolbarRef} // les boutons à modifier / rajouter
      toggleTool={toggleTool}
      activeTool={activeTool}
      toolbarElement={
        mapToolbarRef.current &&
        dataEvenementLayer && (
          <MapToolbarCrise
            state={state}
            map={map}
            criseId={criseId}
            handleCloseEvent={handleCloseEvent}
            listeEventId={listeEventId}
            toggleTool={toggleTool}
            activeTool={activeTool}
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
          />
        )
      }
    />
  );
};

export default MapCrise;

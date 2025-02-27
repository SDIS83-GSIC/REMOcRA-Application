import React, { useEffect, useMemo, useRef } from "react";
import { useLocation } from "react-router-dom";
import VectorLayer from "ol/layer/Vector";
import { TypeModuleRemocra } from "../../components/ModuleRemocra/ModuleRemocra.tsx";
import MapComponent, { useMapComponent } from "../../components/Map/Map.tsx";
import { createPointLayer } from "../../components/Map/MapUtils.tsx";
import { useToolbarContext } from "../../components/Map/MapToolbar.tsx";
import OldebMapToolbar, { useToolbarOldebContext } from "./OldebMapToolbar.tsx";

const OldebMap = () => {
  const { state } = useLocation();
  const dataOldebLayerRef = useRef<VectorLayer>();

  const mapElement = useRef<HTMLDivElement>();

  const {
    map,
    workingLayer,
    availableLayers,
    addOrRemoveLayer,
    layerListRef,
    mapToolbarRef,
    projection,
  } = useMapComponent({
    mapElement: mapElement,
    typeModule: TypeModuleRemocra.OLDEBS,
    displayPei: false,
  });

  useMemo(() => {
    if (!map) {
      return;
    }

    dataOldebLayerRef.current = createPointLayer(
      map,
      (extent, projection) =>
        `/api/oldeb/layer?bbox=` +
        extent.join(",") +
        "&srid=" +
        projection.getCode(),
      projection,
    );
  }, [map, projection]);

  const { tools: extraTools } = useToolbarOldebContext({
    map,
    workingLayer,
    dataOldebLayerRef,
  });

  const { toggleTool, activeTool } = useToolbarContext({
    map: map,
    workingLayer: workingLayer,
    extraTools: extraTools,
  });

  useEffect(() => {
    if (state?.bbox && map) {
      map?.getView().fit(state.bbox);
      window.history.replaceState(null, "");
    }
  }, [state, map]);

  return (
    <MapComponent
      map={map}
      workingLayer={workingLayer}
      availableLayers={availableLayers}
      addOrRemoveLayer={addOrRemoveLayer}
      layerListRef={layerListRef}
      mapToolbarRef={mapToolbarRef}
      mapElement={mapElement}
      toggleTool={toggleTool}
      activeTool={activeTool}
      toolbarElement={
        mapToolbarRef.current && (
          <OldebMapToolbar toggleTool={toggleTool} activeTool={activeTool} />
        )
      }
    />
  );
};

export default OldebMap;

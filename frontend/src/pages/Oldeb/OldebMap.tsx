import React, { useEffect, useMemo, useRef } from "react";
import { useLocation } from "react-router-dom";
import { transformExtent } from "ol/proj";
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
    showOutilI,
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

  const { toggleTool, activeTool, handleCloseInfoI, infoOutilI } =
    useToolbarContext({
      availableLayers: availableLayers,
      map: map,
      availableLayers: availableLayers,
      workingLayer: workingLayer,
      extraTools: extraTools,
    });

  useEffect(() => {
    if (state?.target && map) {
      map
        .getView()
        .fit(
          transformExtent(
            state.target.extent,
            `EPSG:${state.target.srid}`,
            map.getView().getProjection().getCode(),
          ),
          { maxZoom: 20 },
        );
      window.history.replaceState({ from: state.from }, "");
    }
  }, [state, map]);

  return (
    <MapComponent
      outilI={infoOutilI}
      handleCloseInfoI={handleCloseInfoI}
      showOutilI={showOutilI}
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

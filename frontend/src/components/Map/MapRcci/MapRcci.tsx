import React, { useMemo, useRef } from "react";
import { Circle, Fill, Stroke, Style } from "ol/style";
import VectorLayer from "ol/layer/Vector";
import MapComponent, { useMapComponent } from "../Map.tsx";
import { useToolbarContext } from "../MapToolbar.tsx";
import { createPointLayer } from "../MapUtils.tsx";
import { TypeModuleRemocra } from "../../ModuleRemocra/ModuleRemocra.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { IconRCCI } from "../../../components/Icon/Icon.tsx";
import MapToolbarRcci, { useToolbarRcciContext } from "./MapToolbarRcci.tsx";

const MapRcci = () => {
  const mapElement = useRef<HTMLDivElement>();
  const dataRcciLayerRef = useRef<VectorLayer>();
  const anneeCivileRef = useRef<boolean>(false);
  const now = new Date();

  function displayAnneCivile() {
    anneeCivileRef.current = !anneeCivileRef.current;
    dataRcciLayerRef.current?.changed();
  }

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
    typeModule: TypeModuleRemocra.RCI,
    displayPei: false,
  });

  useMemo(() => {
    if (!map) {
      return;
    }
    dataRcciLayerRef.current = createPointLayer(
      map,
      (extent, projection) =>
        `/api/rcci/layer?bbox=` +
        extent.join(",") +
        "&srid=" +
        projection.getCode(),
      (feature) => {
        const anneeIncendie = feature.getProperties()?.rcciDateIncendie
          ? new Date(feature.getProperties().rcciDateIncendie).getFullYear()
          : null;
        if (!anneeCivileRef.current || anneeIncendie === now.getFullYear()) {
          return new Style({
            image: new Circle({
              radius: 5,
              fill: new Fill({ color: "red" }),
              stroke: new Stroke({
                color: [255, 0, 0],
                width: 2,
              }),
            }),
          });
        }
      },
      projection,
    );
  }, [map, projection]);

  const {
    tools: extraTools,
    editModalRefs,
    deleteModalRefs,
    rcciIdRef,
  } = useToolbarRcciContext({
    map,
    workingLayer,
    dataRcciLayerRef,
  });

  const { toggleTool, activeTool } = useToolbarContext({
    map: map,
    workingLayer: workingLayer,
    extraTools: extraTools,
  });

  return (
    <>
      <PageTitle
        title="Recherche des Causes et des Circonstances d'Incendie (RCCI)"
        icon={<IconRCCI />}
      />
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
            <MapToolbarRcci
              toggleTool={toggleTool}
              activeTool={activeTool}
              dataRcciLayerRef={dataRcciLayerRef}
              editModalRefs={editModalRefs}
              deleteModalRefs={deleteModalRefs}
              rcciIdRef={rcciIdRef}
              anneeCivileRef={{ anneeCivileRef, displayAnneCivile }}
            />
          )
        }
      />
    </>
  );
};

export default MapRcci;

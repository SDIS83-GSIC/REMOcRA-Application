import React, { useMemo, useRef } from "react";
import VectorLayer from "ol/layer/Vector";
import { Fill, Icon, Stroke, Style, Text } from "ol/style";
import { formatDate } from "../../../utils/formatDateUtils.tsx";
import MapComponent, { useMapComponent } from "../Map.tsx";
import { useToolbarContext } from "../MapToolbar.tsx";
import { createPointLayer } from "../MapUtils.tsx";
import { TypeModuleRemocra } from "../../ModuleRemocra/ModuleRemocra.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { IconRCCI } from "../../../components/Icon/Icon.tsx";
import rcciIcon from "../../../img/rci.png";
import rcciBeforeIcon from "../../../img/rci-before.png";
import MapToolbarRcci, { useToolbarRcciContext } from "./MapToolbarRcci.tsx";

const MapRcci = () => {
  const mapElement = useRef<HTMLDivElement>();
  const dataRcciLayerRef = useRef<VectorLayer>();
  const anneeCivileRef = useRef<boolean>(false);

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

  const rcciStyle = (feature): Style | undefined => {
    const date = feature.get("rcciDateIncendie");
    const year = date ? new Date(date).getFullYear() : null;
    const currentYear = new Date().getFullYear();

    if (anneeCivileRef.current && year !== currentYear) {
      return undefined;
    }

    const iconSrc = year === currentYear ? rcciIcon : rcciBeforeIcon;
    const textColor = year === currentYear ? "red" : "orange";

    return new Style({
      image: new Icon({
        src: iconSrc,
        scale: 1,
        anchor: [0.5, 1],
      }),
      text: new Text({
        text: formatDate(date),
        font: "12px Calibri,sans-serif",
        offsetY: 5,
        fill: new Fill({ color: textColor }),
        stroke: new Stroke({ color: "#ffffff", width: 4 }),
      }),
    });
  };

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
      projection,
      rcciStyle as unknown as Style,
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
              map={map}
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

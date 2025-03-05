import "ol/ol.css";
import { useMemo, useRef } from "react";
import CircleStyle from "ol/style/Circle";
import { Fill, Stroke, Style } from "ol/style";
import { TypeModuleRemocra } from "../../ModuleRemocra/ModuleRemocra.tsx";
import MapComponent, { useMapComponent } from "../Map.tsx";
import { useToolbarContext } from "../MapToolbar.tsx";
import { createPointLayer } from "../MapUtils.tsx";
import MapToolbarCrise, { useToolbarCriseContext } from "./MapToolbarCrise.tsx";

const MapCrise = ({ criseId }: { criseId: string }) => {
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

  /** Permet d'afficher les évènements */
  const dataPeiPrescritLayer = useMemo(() => {
    if (!map) {
      return;
    }

    const getLayerUrl = (extent, projection) =>
      `/api/crise/evenement/layer?bbox=${extent.join(",")}&srid=${projection.getCode()}&criseId=${criseId}`;

    const style = new Style({
      fill: new Fill({
        color: "rgba(255, 255, 255, 0.2)",
      }),
      stroke: new Stroke({
        color: "rgba(0, 0, 0, 0.5)",
        width: 1,
      }),
      image: new CircleStyle({
        radius: 5,
        stroke: new Stroke({
          color: "rgb(0, 160, 27)",
        }),
        fill: new Fill({
          color: "rgb(0, 160, 27)",
        }),
      }),
    });

    return createPointLayer(map, getLayerUrl, style, projection);
  }, [map, projection, criseId]);

  const {
    tools: extraTools,
    handleCloseEvent,
    showCreateEvent,
    showListEvent,
    listePeiId,
    listeEventId,
    setSousTypeElement,
    geometryElement,
    sousTypeElement,
  } = useToolbarCriseContext({
    map,
    workingLayer,
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
        mapToolbarRef.current && (
          <MapToolbarCrise
            map={map}
            criseId={criseId}
            handleCloseEvent={handleCloseEvent}
            showCreateEvent={showCreateEvent}
            listePeiId={listePeiId}
            listeEventId={listeEventId}
            toggleTool={toggleTool}
            activeTool={activeTool}
            geometryElement={geometryElement}
            workingLayer={workingLayer}
            setSousTypeElement={setSousTypeElement}
            sousTypeElement={sousTypeElement}
            dataAdresseLayer={dataPeiPrescritLayer}
            showListEvent={showListEvent}
          />
        )
      }
    />
  );
};

export default MapCrise;

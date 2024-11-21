import { Circle, Fill, Stroke, Style } from "ol/style";
import { useMemo, useRef } from "react";
import MapComponent, { useMapComponent } from "../Map.tsx";
import { useToolbarContext } from "../MapToolbar.tsx";
import { createPointLayer } from "../MapUtils.tsx";
import MapToolbarPei, { useToolbarPeiContext } from "./MapToolbarPei.tsx";

const MapPei = () => {
  const mapElement = useRef<HTMLDivElement>();

  const {
    map,
    workingLayer,
    dataPeiLayer,
    availableLayers,
    addOrRemoveLayer,
    layerListRef,
    mapToolbarRef,
    projection,
  } = useMapComponent({ mapElement: mapElement });

  const {
    tools: extraTools,
    createIndispoTemp,
    handleCloseIndispoTemp,
    listePeiId,
    showCreateIndispoTemp,
    listePeiIdTourneePrive,
    listePeiIdTourneePublic,
    createUpdateTournee,
    handleCloseTournee,
    showCreateTournee,
  } = useToolbarPeiContext({
    map,
    workingLayer,
    dataPeiLayer,
  });

  /**
   * Permet d'afficher les PEI en projet
   * @param etudeId l'étude concernée
   * @returns
   */
  const dataDebitSimultaneLayer = useMemo(() => {
    if (!map) {
      return;
    }
    return createPointLayer(
      map,
      (extent, projection) =>
        `/api/debit-simultane/layer?bbox=` +
        extent.join(",") +
        "&srid=" +
        projection.getCode(),
      new Style({
        image: new Circle({
          radius: 5,
          fill: new Fill({ color: "pink" }),
          stroke: new Stroke({
            color: [90, 0, 90],
            width: 1,
          }),
        }),
      }),
      projection,
    );
  }, [map, projection]);

  const { toggleTool, activeTool } = useToolbarContext({
    map: map,
    workingLayer: workingLayer,
    extraTools: extraTools,
  });

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
          <MapToolbarPei
            toggleTool={toggleTool}
            activeTool={activeTool}
            map={map}
            dataPeiLayer={dataPeiLayer}
            showCreateIndispoTemp={showCreateIndispoTemp}
            handleCloseIndispoTemp={handleCloseIndispoTemp}
            listePeiId={listePeiId}
            createIndispoTemp={createIndispoTemp}
            listePeiIdTourneePrive={listePeiIdTourneePrive}
            listePeiIdTourneePublic={listePeiIdTourneePublic}
            createUpdateTournee={createUpdateTournee}
            handleCloseTournee={handleCloseTournee}
            showCreateTournee={showCreateTournee}
            dataDebitSimultaneLayer={dataDebitSimultaneLayer}
          />
        )
      }
    />
  );
};

export default MapPei;

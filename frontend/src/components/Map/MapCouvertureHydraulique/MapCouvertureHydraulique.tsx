import "ol/ol.css";
import { useMemo, useRef } from "react";
import { TypeModuleRemocra } from "../../ModuleRemocra/ModuleRemocra.tsx";
import MapComponent, { useMapComponent } from "../Map.tsx";
import { useToolbarContext } from "../MapToolbar.tsx";
import { createPointLayer } from "../MapUtils.tsx";
import MapToolbarCouvertureHydraulique, {
  useToolbarCouvertureHydrauliqueContext,
} from "./MapToolbarCouvertureHydraulique.tsx";

const MapCouvertureHydraulique = ({
  etudeId,
  disabledEditPeiProjet,
  reseauImporte,
}: {
  etudeId: string;
  disabledEditPeiProjet: boolean;
  reseauImporte: boolean;
}) => {
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
  } = useMapComponent({
    mapElement: mapElement,
    typeModule: TypeModuleRemocra.COUVERTURE_HYDRAULIQUE,
  });

  /**
   * Permet d'afficher les PEI en projet
   * @param etudeId l'étude concernée
   * @returns
   */
  const dataPeiProjetLayer = useMemo(() => {
    if (!map || !etudeId || !projection) {
      return;
    }
    return createPointLayer(
      map,
      (extent, projection) =>
        `/api/couverture-hydraulique/layer?bbox=` +
        extent.join(",") +
        "&srid=" +
        projection.getCode() +
        "&etudeId=" +
        etudeId,
      projection,
    );
  }, [map, etudeId, projection]);

  const {
    tools: extraTools,
    calculCouverture,
    clearCouverture,
    handleClosePeiProjet,
    showCreatePeiProjet,
    pointPeiProjet,
    handleCloseTraceeCouverture,
    showTraceeCouverture,
    listePeiId,
    listePeiProjetId,
    geometrieMove,
    peiProjetIdMove,
    closeMove,
    visibleMove,
  } = useToolbarCouvertureHydrauliqueContext({
    map,
    workingLayer,
    dataPeiLayer,
    dataPeiProjetLayer,
    etudeId,
    reseauImporte,
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
      availableLayers={availableLayers}
      addOrRemoveLayer={addOrRemoveLayer}
      layerListRef={layerListRef}
      mapToolbarRef={mapToolbarRef}
      toggleTool={toggleTool}
      activeTool={activeTool}
      toolbarElement={
        mapToolbarRef.current && (
          <MapToolbarCouvertureHydraulique
            map={map}
            etudeId={etudeId}
            dataPeiProjetLayer={dataPeiProjetLayer}
            disabledEditPeiProjet={disabledEditPeiProjet}
            calculCouverture={calculCouverture}
            clearCouverture={clearCouverture}
            handleClosePeiProjet={handleClosePeiProjet}
            showCreatePeiProjet={showCreatePeiProjet}
            pointPeiProjet={pointPeiProjet}
            handleCloseTraceeCouverture={handleCloseTraceeCouverture}
            showTraceeCouverture={showTraceeCouverture}
            listePeiId={listePeiId}
            listePeiProjetId={listePeiProjetId}
            toggleTool={toggleTool}
            activeTool={activeTool}
            geometrieMove={geometrieMove}
            peiProjetIdMove={peiProjetIdMove}
            closeMove={closeMove}
            visibleMove={visibleMove}
          />
        )
      }
    />
  );
};

export default MapCouvertureHydraulique;

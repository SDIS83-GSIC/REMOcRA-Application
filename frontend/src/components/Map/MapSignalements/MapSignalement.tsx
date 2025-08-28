import { useMemo, useRef } from "react";
import MapComponent, { useMapComponent } from "../Map.tsx";
import { TypeModuleRemocra } from "../../ModuleRemocra/ModuleRemocra.tsx";
import { useToolbarContext } from "../MapToolbar.tsx";
import { createPointLayer, refreshLayerGeoserver } from "../MapUtils.tsx";
import url from "../../../module/fetch.tsx";
import PageTitle from "../../Elements/PageTitle/PageTitle.tsx";
import { IconSignalement } from "../../Icon/Icon.tsx";
import MapToolbarSignalement, {
  useToolbarSignalementContext,
} from "./MapToolbarSignalement.tsx";

const MapSignalement = () => {
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
    displayPei: false,
    typeModule: TypeModuleRemocra.SIGNALEMENTS,
  });

  const dataSignalementLayer = useMemo(() => {
    if (!map) {
      return;
    }

    return createPointLayer(
      map,
      (extent, projection) =>
        url`/api/signalements/layer?bbox=` +
        extent.join(",") +
        "&srid=" +
        projection.getCode(),
      projection,
    );
  }, [map, projection]);

  const {
    tools: extraTools,
    showCreateElement,
    setShowCreateElement,
    handleCloseElement,
    showCreateSignalement,
    setShowCreateSignalement,
    handleCloseSignalement,
    geometrySignalement,
    supprimerFeature,
    selectedFeatures,
    geometryElement,
    setListSignalementElement,
    listSignalementElement,
    setSousTypeElement,
    sousTypeElement,
  } = useToolbarSignalementContext({
    map,
    workingLayer,
    dataSignalementLayer,
  });

  const { toggleTool, activeTool, infoOutilI, handleCloseInfoI } =
    useToolbarContext({
      map: map,
      workingLayer: workingLayer,
      extraTools: extraTools,
    });

  return (
    <>
      <PageTitle title="Carte des signalements" icon={<IconSignalement />} />
      <MapComponent
        map={map}
        availableLayers={availableLayers}
        addOrRemoveLayer={addOrRemoveLayer}
        layerListRef={layerListRef}
        mapToolbarRef={mapToolbarRef}
        generalInfo={infoOutilI}
        handleCloseInfoI={handleCloseInfoI}
        toolbarElement={
          <MapToolbarSignalement
            geometrySignalement={geometrySignalement}
            toggleTool={toggleTool}
            activeTool={activeTool}
            map={map}
            close={() => {
              dataSignalementLayer.getSource().refresh();
              refreshLayerGeoserver(map);
              setListSignalementElement([]);
              workingLayer.getSource().clear();
              setShowCreateSignalement(false);
            }}
            showCreateElement={showCreateElement}
            setShowCreateElement={setShowCreateElement}
            handleCloseElement={handleCloseElement}
            showCreateSignalement={showCreateSignalement}
            setShowCreateSignalement={setShowCreateSignalement}
            handleCloseSignalement={handleCloseSignalement}
            dataSignalementLayer={dataSignalementLayer}
            supprimerFeature={supprimerFeature}
            selectedFeatures={selectedFeatures}
            geometryElement={geometryElement}
            setListSignalementElement={setListSignalementElement}
            listSignalementElement={listSignalementElement}
            setSousTypeElement={setSousTypeElement}
            sousTypeElement={sousTypeElement}
          />
        }
        mapElement={mapElement}
        toggleTool={toggleTool}
        activeTool={activeTool}
      />
    </>
  );
};

export default MapSignalement;

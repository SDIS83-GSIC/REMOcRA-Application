import { useRef } from "react";
import MapComponent, { useMapComponent } from "../Map.tsx";
import { useToolbarContext } from "../MapToolbar.tsx";
import { TypeModuleRemocra } from "../../ModuleRemocra/ModuleRemocra.tsx";
import MapToolbarDFCI from "./MapToolbarDFCI.tsx";

const MapDFCI = () => {
  const mapElement = useRef<HTMLDivElement>();

  const {
    map,
    workingLayer,
    availableLayers,
    addOrRemoveLayer,
    layerListRef,
    mapToolbarRef,
  } = useMapComponent({
    mapElement: mapElement,
    typeModule: TypeModuleRemocra.DFCI,
    displayPei: false,
  });

  const {
    toggleTool,
    activeTool,
    showVoletOutilI,
    generalInfo,
    handleCloseInfoI,
  } = useToolbarContext({
    map: map,
    workingLayer: workingLayer,
    extraTools: {},
  });

  return (
    <MapComponent
      map={map}
      showGeneralInfo={showVoletOutilI}
      generalInfo={generalInfo}
      handleCloseInfoI={handleCloseInfoI}
      availableLayers={availableLayers}
      addOrRemoveLayer={addOrRemoveLayer}
      layerListRef={layerListRef}
      mapToolbarRef={mapToolbarRef}
      mapElement={mapElement}
      toggleTool={toggleTool}
      activeTool={activeTool}
      toolbarElement={mapToolbarRef.current && <MapToolbarDFCI />}
    />
  );
};

export default MapDFCI;

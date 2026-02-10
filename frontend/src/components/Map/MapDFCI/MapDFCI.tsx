import { useRef } from "react";
import MapComponent, { useMapComponent } from "../Map.tsx";
import { useToolbarContext } from "../MapToolbar.tsx";
import { TypeModuleRemocra } from "../../ModuleRemocra/ModuleRemocra.tsx";
import { IconDFCI } from "../../Icon/Icon.tsx";
import PageTitle from "../../Elements/PageTitle/PageTitle.tsx";
import MapToolbarDFCI from "./MapToolbarDFCI.tsx";

const MapDFCI = () => {
  const mapElement = useRef<HTMLDivElement>();

  const {
    map,
    workingLayer,
    availableLayers,
    addOrRemoveLayer,
    layerListRef,
    showOutilI,
    mapToolbarRef,
  } = useMapComponent({
    mapElement: mapElement,
    typeModule: TypeModuleRemocra.DFCI,
    displayPei: false,
  });

  const { toggleTool, activeTool, infoOutilI, handleCloseInfoI } =
    useToolbarContext({
      availableLayers: availableLayers,
      map: map,
      workingLayer: workingLayer,
      availableLayers: availableLayers,
      extraTools: {},
    });

  return (
    <>
      <PageTitle
        title={"Défense de la Forêt Contre les Incendies"}
        icon={<IconDFCI />}
      />

      <MapComponent
        map={map}
        outilI={infoOutilI}
        showOutilI={showOutilI}
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
    </>
  );
};

export default MapDFCI;

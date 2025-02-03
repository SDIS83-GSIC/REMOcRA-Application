import { useRef } from "react";
import SquelettePage from "../../../pages/SquelettePage.tsx";
import PageTitle from "../../Elements/PageTitle/PageTitle.tsx";
import Header from "../../Header/Header.tsx";
import { IconPermis } from "../../Icon/Icon.tsx";
import { TypeModuleRemocra } from "../../ModuleRemocra/ModuleRemocra.tsx";
import MapComponent, { useMapComponent } from "../Map.tsx";
import { useToolbarContext } from "../MapToolbar.tsx";

const MapPermis = () => {
  const mapElement = useRef<HTMLDivElement>();

  const {
    map,
    availableLayers,
    addOrRemoveLayer,
    workingLayer,
    layerListRef,
    mapToolbarRef,
  } = useMapComponent({
    mapElement: mapElement,
    typeModule: TypeModuleRemocra.PERMIS,
    displayPei: false,
  });

  const { toggleTool, activeTool } = useToolbarContext({
    map: map,
    workingLayer: workingLayer,
    extraTools: {},
  });

  return (
    <SquelettePage navbar={<Header />}>
      <PageTitle title="Carte des Permis" icon={<IconPermis />} />
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
      />
    </SquelettePage>
  );
};

export default MapPermis;

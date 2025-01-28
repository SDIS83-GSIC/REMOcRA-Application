import { useRef } from "react";
import MapComponent, { useMapComponent } from "../Map.tsx";
import { useToolbarContext } from "../MapToolbar.tsx";
import SquelettePage from "../../../pages/SquelettePage.tsx";
import Header from "../../Header/Header.tsx";
import PageTitle from "../../Elements/PageTitle/PageTitle.tsx";
import { IconRisque } from "../../Icon/Icon.tsx";
import { TypeModuleRemocra } from "../../ModuleRemocra/ModuleRemocra.tsx";

const MapRisque = () => {
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
    typeModule: TypeModuleRemocra.RISQUES,
    displayPei: true,
  });

  const { toggleTool, activeTool } = useToolbarContext({
    map: map,
    workingLayer: workingLayer,
    extraTools: {},
  });

  return (
    <SquelettePage navbar={<Header />}>
      <PageTitle title="Carte des Risques" icon={<IconRisque />} />
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

export default MapRisque;

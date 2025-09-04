import { useMemo, useRef } from "react";
import MapComponent, { useMapComponent } from "../Map.tsx";
import { useToolbarContext } from "../MapToolbar.tsx";
import { addWktLayer } from "../MapUtils.tsx";
import { TypeModuleRemocra } from "../../ModuleRemocra/ModuleRemocra.tsx";

const MapRapportPersonnalise = ({ wkt }: { wkt: string[] }) => {
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
    typeModule: TypeModuleRemocra.RAPPORT_PERSONNALISE,
    displayPei: false,
  });

  const { toggleTool, activeTool, infoOutilI, handleCloseInfoI } =
    useToolbarContext({
      availableLayers: availableLayers,
      map: map,
      availableLayers: availableLayers,
      workingLayer: workingLayer,
      extraTools: {},
    });

  useMemo(() => {
    if (!map || !projection || !wkt || wkt.length === 0) {
      workingLayer?.getSource()?.clear();
      return;
    }

    const wktString = "GEOMETRYCOLLECTION(" + wkt.join(",") + ")";
    addWktLayer(map, wktString, workingLayer, projection);
  }, [map, projection, wkt, workingLayer]);

  return (
    <MapComponent
      map={map}
      workingLayer={workingLayer}
      outilI={infoOutilI}
      handleCloseInfoI={handleCloseInfoI}
      availableLayers={availableLayers}
      addOrRemoveLayer={addOrRemoveLayer}
      layerListRef={layerListRef}
      mapToolbarRef={mapToolbarRef}
      mapElement={mapElement}
      toggleTool={toggleTool}
      activeTool={activeTool}
    />
  );
};

export default MapRapportPersonnalise;

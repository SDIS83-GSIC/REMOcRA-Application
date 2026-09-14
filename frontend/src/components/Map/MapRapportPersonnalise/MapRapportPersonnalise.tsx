import { useMemo, useRef } from "react";
import { TypeModuleRemocra } from "../../ModuleRemocra/ModuleRemocra.tsx";
import MapComponent, { useMapComponent } from "../Map.tsx";
import { useToolbarContext } from "../MapToolbar.tsx";
import { addWktLayer } from "../MapUtils.tsx";
import "./MapRapportPersonnalise.css";

const MapRapportPersonnalise = ({ wkt }: { wkt: string[] }) => {
  const mapElement = useRef<HTMLDivElement>();

  const {
    map,
    workingLayer,
    availableLayers,
    addOrRemoveLayer,
    showOutilI,
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
    <div className="map-wrapper-rapport">
      <MapComponent
        map={map}
        showOutilI={showOutilI}
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
    </div>
  );
};

export default MapRapportPersonnalise;

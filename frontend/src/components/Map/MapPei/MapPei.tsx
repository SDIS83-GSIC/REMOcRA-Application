import { useRef } from "react";
import MapComponent, { useMapComponent } from "../Map.tsx";
import MapToolbarPei from "./MapToolbarPei.tsx";

const MapPei = () => {
  const mapElement = useRef();
  const {
    map,
    workingLayer,
    dataPeiLayer,
    availableLayers,
    addOrRemoveLayer,
    layerListRef,
    mapToolbarRef,
  } = useMapComponent({ mapElement: mapElement });
  return (
    map && (
      <>
        <MapToolbarPei dataPeiLayer={dataPeiLayer} map={map} />
        <MapComponent
          map={map}
          workingLayer={workingLayer}
          availableLayers={availableLayers}
          addOrRemoveLayer={addOrRemoveLayer}
          layerListRef={layerListRef}
          mapToolbarRef={mapToolbarRef}
          mapElement={mapElement}
        />
      </>
    )
  );
};

export default MapPei;

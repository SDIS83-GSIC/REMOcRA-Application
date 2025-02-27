import { useMemo, useRef } from "react";
import MapComponent, { useMapComponent } from "../Map.tsx";
import { TypeModuleRemocra } from "../../ModuleRemocra/ModuleRemocra.tsx";
import { useToolbarContext } from "../MapToolbar.tsx";
import { createPointLayer } from "../MapUtils.tsx";
import url from "../../../module/fetch.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { IconAdresse } from "../../../components/Icon/Icon.tsx";
import MapToolbarAdresse, {
  useToolbarAdresseContext,
} from "./MapToolbarAdresse.tsx";

const MapAdresse = () => {
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
    typeModule: TypeModuleRemocra.ADRESSES,
  });

  const dataAdresseLayer = useMemo(() => {
    if (!map) {
      return;
    }

    return createPointLayer(
      map,
      (extent, projection) =>
        url`/api/adresses/layer?bbox=` +
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
    showCreateAdresse,
    setShowCreateAdresse,
    handleCloseAdresse,
    geometryAdresse,
    supprimerFeature,
    selectedFeatures,
    geometryElement,
    setListAdresseElement,
    listAdresseElement,
    setSousTypeElement,
    sousTypeElement,
  } = useToolbarAdresseContext({
    map,
    workingLayer,
    dataAdresseLayer,
  });

  const { toggleTool, activeTool } = useToolbarContext({
    map: map,
    workingLayer: workingLayer,
    extraTools: extraTools,
  });

  return (
    <>
      <PageTitle title="Adresse" icon={<IconAdresse />} />
      <MapComponent
        map={map}
        workingLayer={workingLayer}
        availableLayers={availableLayers}
        addOrRemoveLayer={addOrRemoveLayer}
        layerListRef={layerListRef}
        mapToolbarRef={mapToolbarRef}
        toolbarElement={
          <MapToolbarAdresse
            geometryAdresse={geometryAdresse}
            toggleTool={toggleTool}
            activeTool={activeTool}
            map={map}
            showCreateElement={showCreateElement}
            setShowCreateElement={setShowCreateElement}
            handleCloseElement={handleCloseElement}
            showCreateAdresse={showCreateAdresse}
            setShowCreateAdresse={setShowCreateAdresse}
            handleCloseAdresse={handleCloseAdresse}
            dataAdresseLayer={dataAdresseLayer}
            supprimerFeature={supprimerFeature}
            selectedFeatures={selectedFeatures}
            workingLayer={workingLayer}
            geometryElement={geometryElement}
            setListAdresseElement={setListAdresseElement}
            listAdresseElement={listAdresseElement}
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

export default MapAdresse;

import VectorLayer from "ol/layer/Vector";
import VectorSource from "ol/source/Vector";
import { Fill, Stroke, Style } from "ol/style";
import CircleStyle from "ol/style/Circle";
import { useMemo, useRef } from "react";
import url from "../../../module/fetch.tsx";
import PageTitle from "../../Elements/PageTitle/PageTitle.tsx";
import { IconSignalement } from "../../Icon/Icon.tsx";
import { TypeModuleRemocra } from "../../ModuleRemocra/ModuleRemocra.tsx";
import MapComponent, { useMapComponent } from "../Map.tsx";
import { useToolbarContext } from "../MapToolbar.tsx";
import { createPointLayer } from "../MapUtils.tsx";
import MapToolbarSignalement, {
  useToolbarSignalement,
} from "./MapToolbarSignalement.tsx";

const MapSignalement = () => {
  const mapElement = useRef<HTMLDivElement>();

  const {
    map,
    workingLayer,
    availableLayers,
    addOrRemoveLayer,
    layerListRef,
    showOutilI,
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

  const cartographieSignalementLayer = useMemo(() => {
    if (map) {
      const wl = new VectorLayer({
        source: new VectorSource(),
        style: () => {
          return new Style({
            fill: new Fill({
              color: "rgba(5, 176, 255, 1)",
            }),
            stroke: new Stroke({
              color: "rgba(5, 176, 255, 1)",
              width: 2,
            }),
            image: new CircleStyle({
              radius: 7,
              fill: new Fill({
                color: "#00c3ffff",
              }),
            }),
          });
        },
        opacity: 1,
        zIndex: 9000,
      });

      map.addLayer(wl);
      return wl;
    }
  }, [map]);

  const {
    tools: extraTools,
    featureStyle,
    setFeatureStyle,
    selectedFeatures,
    setSousTypeElement,
    supprimerFeature,
    listSignalementElement,
    setShowCreateSignalement,
    handleCloseElement,
    showCreateElement,
    setListSignalementElement,
    sousTypeElement,
    setShowCreateElement,
    geometryElement,
    handleCloseSignalement,
    showCreateSignalement,
  } = useToolbarSignalement({
    map,
    cartographieSignalementLayer,
  });

  const { toggleTool, activeTool, infoOutilI, handleCloseInfoI } =
    useToolbarContext({
      availableLayers: availableLayers,
      map: map,
      workingLayer: workingLayer,
      extraTools: extraTools,
    });

  return (
    <>
      <PageTitle title="Carte des signalements" icon={<IconSignalement />} />
      <MapComponent
        map={map}
        showOutilI={showOutilI}
        availableLayers={availableLayers}
        addOrRemoveLayer={addOrRemoveLayer}
        layerListRef={layerListRef}
        mapToolbarRef={mapToolbarRef}
        outilI={infoOutilI}
        handleCloseInfoI={handleCloseInfoI}
        mapElement={mapElement}
        toggleTool={toggleTool}
        activeTool={activeTool}
        toolbarElement={
          mapToolbarRef.current && (
            <MapToolbarSignalement
              map={map}
              toggleTool={toggleTool}
              activeTool={activeTool!}
              featureStyle={featureStyle}
              setFeatureStyle={setFeatureStyle}
              selectedFeatures={selectedFeatures}
              setSousTypeElement={setSousTypeElement}
              supprimerFeature={supprimerFeature}
              listSignalementElement={listSignalementElement}
              setShowCreateSignalement={setShowCreateSignalement}
              handleCloseElement={handleCloseElement}
              showCreateElement={showCreateElement}
              setListSignalementElement={setListSignalementElement}
              sousTypeElement={sousTypeElement}
              setShowCreateElement={setShowCreateElement}
              geometryElement={geometryElement!}
              dataSignalementLayer={dataSignalementLayer}
              handleCloseSignalement={handleCloseSignalement}
              showCreateSignalement={showCreateSignalement}
              cartographieSignalementLayer={cartographieSignalementLayer}
            />
          )
        }
      />
    </>
  );
};

export default MapSignalement;

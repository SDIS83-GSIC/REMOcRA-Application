import { Circle, Fill, Stroke, Style } from "ol/style";
import { useMemo, useRef } from "react";
import SquelettePage from "../../../pages/SquelettePage.tsx";
import PageTitle from "../../Elements/PageTitle/PageTitle.tsx";
import Header from "../../Header/Header.tsx";
import { IconPermis } from "../../Icon/Icon.tsx";
import { TypeModuleRemocra } from "../../ModuleRemocra/ModuleRemocra.tsx";
import MapComponent, { useMapComponent } from "../Map.tsx";
import { useToolbarContext } from "../MapToolbar.tsx";
import { createPointLayer } from "../MapUtils.tsx";
import MapToolbarPermis, {
  useToolbarPermisContext,
} from "./MapToolbarPermis.tsx";

const MapPermis = () => {
  const mapElement = useRef<HTMLDivElement>();

  const {
    map,
    availableLayers,
    addOrRemoveLayer,
    workingLayer,
    layerListRef,
    mapToolbarRef,
    projection,
  } = useMapComponent({
    mapElement: mapElement,
    typeModule: TypeModuleRemocra.PERMIS,
    displayPei: false,
  });

  /** Permet d'afficher les PEI prescrits */
  const dataPermisLayer = useMemo(() => {
    if (!map) {
      return;
    }
    return createPointLayer(
      map,
      (extent, projection) =>
        `/api/permis/layer?bbox=` +
        extent.join(",") +
        "&srid=" +
        projection.getCode(),
      new Style({
        image: new Circle({
          radius: 5,
          fill: new Fill({ color: "green" }),
          stroke: new Stroke({
            color: [255, 0, 0],
            width: 1,
          }),
        }),
      }),
      projection,
    );
  }, [map, projection]);

  const {
    tools: extraTools,
    showCreatePermis,
    handleClosePermis,
    pointPermis,
  } = useToolbarPermisContext({
    map,
    workingLayer,
    dataPermisLayer,
  });

  const { toggleTool, activeTool } = useToolbarContext({
    map: map,
    workingLayer: workingLayer,
    extraTools: extraTools,
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
        toolbarElement={
          mapToolbarRef.current && (
            <MapToolbarPermis
              map={map}
              dataPermisLayer={dataPermisLayer}
              showCreatePermis={showCreatePermis}
              handleClosePermis={handleClosePermis}
              pointPermis={pointPermis}
              toggleTool={toggleTool}
              activeTool={activeTool}
            />
          )
        }
      />
    </SquelettePage>
  );
};

export default MapPermis;

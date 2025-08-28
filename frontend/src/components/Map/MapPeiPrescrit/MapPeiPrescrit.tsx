import { useMemo, useRef } from "react";
import SquelettePage from "../../../pages/SquelettePage.tsx";
import PageTitle from "../../Elements/PageTitle/PageTitle.tsx";
import Header from "../../Header/Header.tsx";
import { IconPrescrit } from "../../Icon/Icon.tsx";
import MapComponent, { useMapComponent } from "../Map.tsx";
import { useToolbarContext } from "../MapToolbar.tsx";
import { createPointLayer } from "../MapUtils.tsx";
import { TypeModuleRemocra } from "../../ModuleRemocra/ModuleRemocra.tsx";
import MapToolbarPeiPrescrit, {
  useToolbarPeiPrescritContext,
} from "./MapToolbarPeiPrescrit.tsx";

const MapPeiPrescrit = () => {
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
    typeModule: TypeModuleRemocra.PEI_PRESCRIT,
    displayPei: false,
  });

  /** Permet d'afficher les PEI prescrits */
  const dataPeiPrescritLayer = useMemo(() => {
    if (!map) {
      return;
    }
    return createPointLayer(
      map,
      (extent, projection) =>
        `/api/pei-prescrit/layer?bbox=` +
        extent.join(",") +
        "&srid=" +
        projection.getCode(),
      projection,
    );
  }, [map, projection]);

  const {
    tools: extraTools,
    showCreatePeiPrescrit,
    handleClosePeiPrescrit,
    pointPeiPrescrit,
  } = useToolbarPeiPrescritContext({
    map,
    workingLayer,
    dataPeiPrescritLayer,
  });

  const { toggleTool, activeTool, infoOutilI, handleCloseInfoI } =
    useToolbarContext({
      map: map,
      workingLayer: workingLayer,
      availableLayers: availableLayers,
      extraTools: extraTools,
    });

  return (
    <SquelettePage navbar={<Header />}>
      <PageTitle title="PEI Prescrits" icon={<IconPrescrit />} />
      <MapComponent
        map={map}
        workingLayer={workingLayer}
        availableLayers={availableLayers}
        addOrRemoveLayer={addOrRemoveLayer}
        layerListRef={layerListRef}
        generalInfo={infoOutilI}
        handleCloseInfoI={handleCloseInfoI}
        mapToolbarRef={mapToolbarRef}
        mapElement={mapElement}
        toggleTool={toggleTool}
        activeTool={activeTool}
        toolbarElement={
          mapToolbarRef.current && (
            <MapToolbarPeiPrescrit
              map={map}
              dataPeiPrescritLayer={dataPeiPrescritLayer}
              showCreatePeiPrescrit={showCreatePeiPrescrit}
              handleClosePeiPrescrit={handleClosePeiPrescrit}
              pointPeiPrescrit={pointPeiPrescrit}
              toggleTool={toggleTool}
              activeTool={activeTool}
            />
          )
        }
      />
    </SquelettePage>
  );
};

export default MapPeiPrescrit;

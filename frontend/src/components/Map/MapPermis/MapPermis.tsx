import { useEffect, useMemo, useRef } from "react";
import { transformExtent } from "ol/proj";
import { useLocation } from "react-router-dom";
import { isAuthorized } from "../../../droits.tsx";
import UtilisateurEntity from "../../../Entities/UtilisateurEntity.tsx";
import TYPE_DROIT from "../../../enums/DroitEnum.tsx";
import SquelettePage from "../../../pages/SquelettePage.tsx";
import { useAppContext } from "../../App/AppProvider.tsx";
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
  const { state } = useLocation();
  const { user }: { user: UtilisateurEntity } = useAppContext();
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
      projection,
    );
  }, [map, projection]);

  const {
    tools: extraTools,
    showSearchPermis,
    handleCloseSearchPermis,
    showCreatePermis,
    handleClosePermis,
    pointPermis,
    featureState,
    showUpdatePermis,
    handleCloseUpdatePermis,
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

  useEffect(() => {
    if (state?.target && map) {
      map
        .getView()
        .fit(
          transformExtent(
            state.target.extent,
            `EPSG:${state.target.srid}`,
            map.getView().getProjection().getCode(),
          ),
          { maxZoom: 20 },
        );
      window.history.replaceState({ from: state.from }, "");
    }
  }, [state, map]);

  const hasRightToInteract = isAuthorized(user, [TYPE_DROIT.PERMIS_A]);

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
              showSearchPermis={showSearchPermis}
              handleCloseSearchPermis={handleCloseSearchPermis}
              showCreatePermis={showCreatePermis}
              handleClosePermis={handleClosePermis}
              pointPermis={pointPermis}
              featureState={featureState}
              showUpdatePermis={showUpdatePermis}
              handleCloseUpdatePermis={handleCloseUpdatePermis}
              toggleTool={toggleTool}
              activeTool={activeTool}
              hasRightToInteract={hasRightToInteract}
            />
          )
        }
      />
    </SquelettePage>
  );
};

export default MapPermis;

import { isEmpty } from "ol/extent";
import { transformExtent } from "ol/proj";
import { useEffect, useMemo, useRef } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import PageTitle from "../../Elements/PageTitle/PageTitle.tsx";
import { IconPei } from "../../Icon/Icon.tsx";
import { TypeModuleRemocra } from "../../ModuleRemocra/ModuleRemocra.tsx";
import MapComponent, { useMapComponent } from "../Map.tsx";
import { useToolbarContext } from "../MapToolbar.tsx";
import { createPointLayer } from "../MapUtils.tsx";
import MapToolbarPei, { useToolbarPeiContext } from "./MapToolbarPei.tsx";

const MapPei = () => {
  const mapElement = useRef<HTMLDivElement>();
  const { state, search } = useLocation();
  const navigate = useNavigate();

  const {
    map,
    workingLayer,
    dataPeiLayer,
    availableLayers,
    addOrRemoveLayer,
    layerListRef,
    mapToolbarRef,
    projection,
  } = useMapComponent({
    mapElement: mapElement,
    typeModule: TypeModuleRemocra.DECI,
  });

  const {
    tools: extraTools,
    createIndispoTemp,
    handleCloseIndispoTemp,
    listePeiId,
    showCreateIndispoTemp,
    listePeiTourneePrive,
    listePeiTourneePublic,
    createUpdateTournee,
    handleCloseTournee,
    showCreateTournee,
    createDebitSimultane,
    handleCloseDebitSimultane,
    showCreateDebitSimultane,
    listePeiIdDebitSimultane,
    typeReseauId,
    close,
    ref,
    visible,
  } = useToolbarPeiContext({
    map,
    workingLayer,
    dataPeiLayer,
  });

  /**
   * Permet d'afficher les PEI en projet
   * @param etudeId l'étude concernée
   * @returns
   */
  const dataDebitSimultaneLayer = useMemo(() => {
    if (!map) {
      return;
    }
    return createPointLayer(
      map,
      (extent, projection) =>
        `/api/debit-simultane/layer?bbox=` +
        extent.join(",") +
        "&srid=" +
        projection.getCode(),
      projection,
    );
  }, [map, projection]);

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

  useEffect(() => {
    // On met dans l'URL le extent pour simplifier la navigation
    if (map) {
      map.on("moveend", () => {
        const view = map.getView();
        const extent = view.calculateExtent();
        const params = new URLSearchParams();
        params.set("extent", extent.join(","));
        navigate(`?${params.toString()}`, { replace: true, state: state });
      });

      const params = new URLSearchParams(search);

      if (params.get("extent")) {
        const geom = params.get("extent")?.split(",");
        if (!isEmpty(geom)) {
          map.getView().fit(geom, {
            maxZoom: 20,
          });
        }
      }
    }
  }, [state, map, navigate, search]);

  return (
    <>
      <PageTitle title="Carte" icon={<IconPei />} />

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
            <MapToolbarPei
              toggleTool={toggleTool}
              activeTool={activeTool}
              map={map}
              dataPeiLayer={dataPeiLayer}
              showCreateIndispoTemp={showCreateIndispoTemp}
              handleCloseIndispoTemp={handleCloseIndispoTemp}
              listePeiId={listePeiId}
              createIndispoTemp={createIndispoTemp}
              listePeiTourneePrive={listePeiTourneePrive}
              listePeiTourneePublic={listePeiTourneePublic}
              createUpdateTournee={createUpdateTournee}
              handleCloseTournee={handleCloseTournee}
              showCreateTournee={showCreateTournee}
              dataDebitSimultaneLayer={dataDebitSimultaneLayer}
              createDebitSimultane={createDebitSimultane}
              handleCloseDebitSimultane={handleCloseDebitSimultane}
              showCreateDebitSimultane={showCreateDebitSimultane}
              listePeiIdDebitSimultane={listePeiIdDebitSimultane}
              typeReseauId={typeReseauId}
              closeModal={close}
              refModal={ref}
              visibleModal={visible}
            />
          )
        }
      />
    </>
  );
};

export default MapPei;

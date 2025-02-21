import { Circle, Fill, Stroke, Style } from "ol/style";
import { useMemo, useRef } from "react";
import PageTitle from "../../Elements/PageTitle/PageTitle.tsx";
import { IconPei } from "../../Icon/Icon.tsx";
import { TypeModuleRemocra } from "../../ModuleRemocra/ModuleRemocra.tsx";
import MapComponent, { useMapComponent } from "../Map.tsx";
import { useToolbarContext } from "../MapToolbar.tsx";
import { createPointLayer } from "../MapUtils.tsx";
import MapToolbarPei, { useToolbarPeiContext } from "./MapToolbarPei.tsx";

const MapPei = () => {
  const mapElement = useRef<HTMLDivElement>();

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
      new Style({
        image: new Circle({
          radius: 5,
          fill: new Fill({ color: "pink" }),
          stroke: new Stroke({
            color: [90, 0, 90],
            width: 1,
          }),
        }),
      }),
      projection,
    );
  }, [map, projection]);

  const { toggleTool, activeTool } = useToolbarContext({
    map: map,
    workingLayer: workingLayer,
    extraTools: extraTools,
  });

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

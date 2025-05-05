import { Stroke, Style } from "ol/style";
import CircleStyle from "ol/style/Circle";
import { useEffect, useMemo, useRef, useState } from "react";
import { useLocation } from "react-router-dom";
import url from "../../../module/fetch.tsx";
import PageTitle from "../../Elements/PageTitle/PageTitle.tsx";
import { IconPei } from "../../Icon/Icon.tsx";
import { TypeModuleRemocra } from "../../ModuleRemocra/ModuleRemocra.tsx";
import MapComponent, { useMapComponent } from "../Map.tsx";
import { useToolbarContext } from "../MapToolbar.tsx";
import { createPointLayer } from "../MapUtils.tsx";
import MapToolbarPei, { useToolbarPeiContext } from "./MapToolbarPei.tsx";

const MapPei = () => {
  const mapElement = useRef<HTMLDivElement>();

  const location = useLocation();
  const [estSurligne, setEstSurligne] = useState(
    location.state?.listePeiId?.length === 0 ||
      location.state?.listePeiId == null,
  );

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

  const stateListePeiId = location.state?.listePeiId;

  // On construit la couche de surbrillance
  const l = useMemo(() => {
    if (!map || !stateListePeiId) {
      return;
    }

    if (estSurligne) {
      return;
    }

    const l = createPointLayer(
      map,
      (extent, projection) =>
        url`/api/pei/hightlight/layer?bbox=` +
        extent.join(",") +
        "&srid=" +
        projection.getCode() +
        "&listePeiId=" +
        stateListePeiId,
      projection,
      new Style({
        image: new CircleStyle({
          radius: 16,
          stroke: new Stroke({
            color: "rgba(217, 131, 226, 0.7)",
            width: 4,
          }),
        }),
      }),
    );

    return l;
  }, [map, stateListePeiId, estSurligne, projection]);

  useEffect(() => {
    if (!map || !l) {
      return;
    }
    setTimeout(function () {
      map.removeLayer(l);
      setEstSurligne(true);
    }, 10000);
  }, [map, l]);

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

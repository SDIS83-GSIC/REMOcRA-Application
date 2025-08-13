import { Stroke, Style } from "ol/style";
import CircleStyle from "ol/style/Circle";
import { useEffect, useMemo, useRef, useState } from "react";
import { Col, Row } from "react-bootstrap";
import { useLocation } from "react-router-dom";
import PARAMETRE from "../../../enums/ParametreEnum.tsx";
import url from "../../../module/fetch.tsx";
import CreatePei from "../../../pages/Pei/CreatePei.tsx";
import UpdatePei from "../../../pages/Pei/UpdatePei.tsx";
import Visite from "../../../pages/Visite/Visite.tsx";
import PageTitle from "../../Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../Fetch/useFetch.tsx";
import { IconPei } from "../../Icon/Icon.tsx";
import { TypeModuleRemocra } from "../../ModuleRemocra/ModuleRemocra.tsx";
import MapComponent, { useMapComponent } from "../Map.tsx";
import { optimizeVectorLayer } from "../MapPerformanceUtils.tsx";
import { useToolbarContext } from "../MapToolbar.tsx";
import { createPointLayer } from "../MapUtils.tsx";
import MapToolbarPei, { useToolbarPeiContext } from "./MapToolbarPei.tsx";

const MapPei = () => {
  const mapElement = useRef<HTMLDivElement>();

  const [showFormPei, setShowFormPei] = useState(false);
  const [showFormVisite, setShowFormVisite] = useState({
    peiId: null,
    show: false,
  });
  const [coordonneesPeiCreate, setCoordonneesPeiCreate] = useState(null);

  const [peiIdUpdate, setPeiIdUpdate] = useState(null);

  const location = useLocation();
  const [estSurligne, setEstSurligne] = useState(
    location.state?.listePeiId?.length === 0 ||
      location.state?.listePeiId == null,
  );

  const parametres = useGet(
    url`/api/parametres?${{
      listeParametreCode: JSON.stringify([PARAMETRE.PEI_HIGHLIGHT_DUREE]),
    }}`,
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
    visibleMove,
    refMove,
    closeMove,
    peiIdMove,
    geometrieMove,
  } = useToolbarPeiContext({
    map,
    workingLayer,
    dataPeiLayer,
    setShowFormPei,
    setCoordonneesPeiCreate,
  });

  /**
   * Permet d'afficher les débits simultanés
   * @returns
   */
  const dataDebitSimultaneLayer = useMemo(() => {
    if (!map) {
      return;
    }

    const layer = createPointLayer(
      map,
      (extent, projection) =>
        `/api/debit-simultane/layer?bbox=` +
        extent.join(",") +
        "&srid=" +
        projection.getCode(),
      projection,
    );
    if (layer) {
      optimizeVectorLayer(layer);
    }

    return layer;
  }, [map, projection]);

  const { toggleTool, activeTool, disabledTool } = useToolbarContext({
    map: map,
    workingLayer: workingLayer,
    extraTools: extraTools,
  });

  const stateListePeiId = location.state?.listePeiId;

  // On construit la couche de surbrillance optimisée
  const highlightLayer = useMemo(() => {
    if (!map || !stateListePeiId || estSurligne) {
      return;
    }

    // Style optimisé avec cache
    const highlightStyle = new Style({
      image: new CircleStyle({
        radius: 16,
        stroke: new Stroke({
          color: "rgba(217, 131, 226, 0.7)",
          width: 4,
        }),
      }),
    });

    const layer = createPointLayer(
      map,
      (extent, projection) =>
        url`/api/pei/hightlight/layer?bbox=` +
        extent.join(",") +
        "&srid=" +
        projection.getCode() +
        "&listePeiId=" +
        stateListePeiId,
      projection,
      highlightStyle,
    );

    // Application des optimisations de performance
    if (layer) {
      optimizeVectorLayer(layer);
      // Configuration spécifique pour la couche highlight
      layer.setZIndex(8999);
      layer.set("renderBuffer", 50);
    }

    return layer;
  }, [map, stateListePeiId, estSurligne, projection]);

  useEffect(() => {
    if (!map || !highlightLayer || !parametres.data) {
      return;
    }

    setTimeout(function () {
      map.removeLayer(highlightLayer);
      setEstSurligne(true);
    }, parametres.data[PARAMETRE.PEI_HIGHLIGHT_DUREE].parametreValeur * 1000);
  }, [map, highlightLayer, parametres]);

  return (
    <>
      <PageTitle title="Carte" icon={<IconPei />} />
      <Row className={"map-wrapper"}>
        <Col>
          <MapComponent
            map={map}
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
                  setPeiIdUpdate={setPeiIdUpdate}
                  setShowFormPei={setShowFormPei}
                  showFormPei={showFormPei}
                  peiIdUpdate={peiIdUpdate}
                  disabledTool={disabledTool}
                  closeMove={closeMove}
                  refMove={refMove}
                  visibleMove={visibleMove}
                  peiIdMove={peiIdMove}
                  geometrieMove={geometrieMove}
                  setShowFormVisite={setShowFormVisite}
                  showFormVisite={showFormVisite}
                />
              )
            }
          />
        </Col>
        {showFormPei && (
          <Col xs={5} className="border-primary border-start border-3">
            <div className="bg-light p-2 ">
              {coordonneesPeiCreate ? (
                <CreatePei
                  coordonneesPeiCreate={coordonneesPeiCreate}
                  close={() => {
                    workingLayer.getSource().clear();
                    setCoordonneesPeiCreate(null);
                    setShowFormPei(false);
                  }}
                  map={map}
                />
              ) : (
                peiIdUpdate && (
                  <UpdatePei
                    peiIdUpdate={peiIdUpdate}
                    close={() => {
                      setPeiIdUpdate(null);
                      setShowFormPei(false);
                    }}
                    map={map}
                  />
                )
              )}
            </div>
          </Col>
        )}
        {showFormVisite.show && showFormVisite.peiId && (
          <Col xs={5} className="border-primary border-start border-3">
            <div className="bg-light p-2 ">
              <Visite
                peiIdCarte={showFormVisite.peiId}
                closeForm={() =>
                  setShowFormVisite({ show: false, peiId: null })
                }
              />
            </div>
          </Col>
        )}
      </Row>
    </>
  );
};

export default MapPei;

import VectorLayer from "ol/layer/Vector";
import { transformExtent } from "ol/proj";
import { useEffect, useMemo, useRef, useState } from "react";
import { CloseButton, Col, Row } from "react-bootstrap";
import { useLocation } from "react-router-dom";
import MapComponent, { useMapComponent } from "../../components/Map/Map.tsx";
import { useToolbarContext } from "../../components/Map/MapToolbar.tsx";
import { createPointLayer } from "../../components/Map/MapUtils.tsx";
import { TypeModuleRemocra } from "../../components/ModuleRemocra/ModuleRemocra.tsx";
import OldebMapToolbar, { useToolbarOldebContext } from "./OldebMapToolbar.tsx";
import OldebSelectionForUpdate from "./OldebSelectionForUpdate.tsx";
import OldebUpdate from "./OldebUpdate.tsx";

const OldebMap = () => {
  const { state } = useLocation();

  const mapElement = useRef<HTMLDivElement>();

  const {
    map,
    workingLayer,
    availableLayers,
    addOrRemoveLayer,
    showOutilI,
    layerListRef,
    mapToolbarRef,
    projection,
  } = useMapComponent({
    mapElement: mapElement,
    typeModule: TypeModuleRemocra.OLDEBS,
    displayPei: false,
  });

  const dataOldebLayer = useMemo(() => {
    if (!map) {
      return;
    }

    return createPointLayer(
      map,
      (extent, projection) =>
        `/api/oldeb/layer?bbox=` +
        extent.join(",") +
        "&srid=" +
        projection.getCode(),
      projection,
    );
  }, [map, projection]);

  const {
    tools: extraTools,
    editOldebs,
    setEditOldebs,
    closeEdit,
  } = useToolbarOldebContext({
    map,
    workingLayer,
    dataOldebLayer,
  });

  const [oldebIdModifie, setOldebIdModifie] = useState<string | null>(null);
  const [selectedOldebId, setSelectedOldebId] = useState<string | null>(null);

  const { toggleTool, activeTool, handleCloseInfoI, infoOutilI } =
    useToolbarContext({
      availableLayers: availableLayers,
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

  return (
    <Row className="map-wrapper">
      <Col>
        <MapComponent
          outilI={infoOutilI}
          handleCloseInfoI={handleCloseInfoI}
          showOutilI={showOutilI}
          map={map}
          availableLayers={availableLayers}
          addOrRemoveLayer={addOrRemoveLayer}
          layerListRef={layerListRef}
          mapToolbarRef={mapToolbarRef}
          mapElement={mapElement}
          toggleTool={toggleTool}
          activeTool={activeTool || ""}
          toolbarElement={
            mapToolbarRef.current &&
            map &&
            dataOldebLayer && (
              <OldebMapToolbar
                toggleTool={toggleTool}
                activeTool={activeTool || ""}
                oldebIdModifie={oldebIdModifie}
                setOldebIdModifie={setOldebIdModifie}
                selectedOldebId={selectedOldebId}
                setSelectedOldebId={setSelectedOldebId}
                closeEdit={() => {
                  closeEdit();
                  setOldebIdModifie(null);
                  setSelectedOldebId(null);
                }}
                dataOldebLayer={dataOldebLayer as VectorLayer}
              />
            )
          }
        />
      </Col>
      {editOldebs && !oldebIdModifie && (
        <Col
          xs={3}
          className="border-primary border-start border-3 position-static"
          style={{ maxHeight: "85vh" }}
        >
          <OldebSelectionForUpdate
            editOldebs={editOldebs}
            onClose={() => setEditOldebs(null)}
            onEdit={(id) => {
              setOldebIdModifie(id);
              setSelectedOldebId(id);
            }}
            selectedOldebId={selectedOldebId}
            setSelectedOldebId={setSelectedOldebId}
            dataOldebLayer={dataOldebLayer}
            map={map}
            closeEdit={closeEdit}
          />
        </Col>
      )}
      {oldebIdModifie && (
        <Col
          xs={5}
          className="border-primary border-start border-3 overflow-y-scroll position-static"
          style={{ maxHeight: "85vh" }}
        >
          <div className="bg-light p-2">
            <div className="d-flex justify-content-end mb-2">
              <CloseButton
                aria-label="Fermer"
                onClick={() => setOldebIdModifie(null)}
              />
            </div>
            <OldebUpdate
              oldebIdCarte={oldebIdModifie}
              onClose={() => {
                closeEdit();
                dataOldebLayer?.getSource()?.refresh();
                setOldebIdModifie(null);
              }}
            />
          </div>
        </Col>
      )}
    </Row>
  );
};

export default OldebMap;

import React, { useRef } from "react";
import { Button } from "react-bootstrap";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import PageTitle from "../../Elements/PageTitle/PageTitle.tsx";
import { formatDateHeure } from "../../../utils/formatDateUtils.tsx";
import { IconCarte, IconPrint } from "../../Icon/Icon.tsx";
import MapComponent, { useMapComponent } from "../Map.tsx";
import { useToolbarContext } from "../MapToolbar.tsx";
import { TypeModuleRemocra } from "../../ModuleRemocra/ModuleRemocra.tsx";
import "./MapPerso.css";
import SquelettePage from "../../../pages/SquelettePage.tsx";
import Header from "../../Header/Header.tsx";
import MapToolbarPerso, { useToolbarPersoContext } from "./MapToolbarPerso.tsx";

const MapPerso = () => {
  const now = new Date();
  const mapElement = useRef<HTMLDivElement>();

  const {
    map,
    workingLayer,
    availableLayers,
    addOrRemoveLayer,
    layerListRef,
    mapToolbarRef,
  } = useMapComponent({
    mapElement: mapElement,
    typeModule: TypeModuleRemocra.CARTOGRAPHIE_PERSONNALISEE,
  });

  const {
    tools: extraTools,
    featureStyle,
    setFeatureStyle,
    selectedFeatures,
  } = useToolbarPersoContext({
    map,
    workingLayer,
  });

  const { toggleTool, activeTool, outilI, handleCloseInfoI } =
    useToolbarContext({
      map: map,
      workingLayer: workingLayer,
      extraTools: extraTools,
    });

  return (
    <SquelettePage navbar={<Header />}>
      <PageTitle
        title="Cartographie personnalisée"
        icon={<IconCarte />}
        right={
          <Button
            variant="primary"
            onClick={() => {
              window.print();
            }}
          >
            <IconPrint /> Imprimer
          </Button>
        }
      />
      <div id={"papersheet"} className={"printable-no-margin mx-auto"}>
        <h1 contentEditable={"true"}>✎ Titre de la carte</h1>
        <MapComponent
          map={map}
          outilI={outilI}
          handleCloseInfoI={handleCloseInfoI}
          availableLayers={availableLayers}
          addOrRemoveLayer={addOrRemoveLayer}
          layerListRef={layerListRef}
          mapToolbarRef={mapToolbarRef}
          mapElement={mapElement}
          toggleTool={toggleTool}
          activeTool={activeTool}
          toolbarElement={
            mapToolbarRef.current && (
              <MapToolbarPerso
                toggleTool={toggleTool}
                activeTool={activeTool}
                featureStyle={featureStyle}
                setFeatureStyle={setFeatureStyle}
                selectedFeatures={selectedFeatures}
                workingLayer={workingLayer}
              />
            )
          }
        />
        <Row>
          <Col className={"h-100 text-italic col-sm-auto"}>
            <p
              className={"text-nowrap"}
            >{`Édition réalisée le ${formatDateHeure(now)} à partir de ${window.location.origin + window.location.pathname}`}</p>
          </Col>
          <Col>
            <p className={"text-end text-nowrap"}>Copyright © 2015 SDIS 83</p>
          </Col>
        </Row>
      </div>
    </SquelettePage>
  );
};

export default MapPerso;

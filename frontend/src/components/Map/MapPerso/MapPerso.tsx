import React, { useRef } from "react";
import { useNavigate } from "react-router-dom";
import Container from "react-bootstrap/Container";
import { Button, ButtonGroup } from "react-bootstrap";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import PageTitle from "../../Elements/PageTitle/PageTitle.tsx";
import { formatDateHeure } from "../../../utils/formatDateUtils.tsx";
import { IconCarte, IconPrint, IconReturn } from "../../Icon/Icon.tsx";
import MapComponent, { useMapComponent } from "../Map.tsx";
import { useToolbarContext } from "../MapToolbar.tsx";
import { URLS } from "../../../routes.tsx";
import MapToolbarPerso, { useToolbarPersoContext } from "./MapToolbarPerso.tsx";
import "./MapPerso.css";

const MapPerso = () => {
  const navigate = useNavigate();
  const now = new Date();
  const mapElement = useRef<HTMLDivElement>();

  const {
    map,
    workingLayer,
    availableLayers,
    addOrRemoveLayer,
    layerListRef,
    mapToolbarRef,
  } = useMapComponent({ mapElement: mapElement });

  const {
    tools: extraTools,
    featureStyle,
    setFeatureStyle,
    selectedFeatures,
  } = useToolbarPersoContext({
    map,
    workingLayer,
  });

  const { toggleTool, activeTool } = useToolbarContext({
    map: map,
    workingLayer: workingLayer,
    extraTools: extraTools,
  });

  return (
    <Container className={"printable-no-margin"}>
      <PageTitle
        title="Cartographie personnalisée"
        icon={<IconCarte />}
        right={
          <ButtonGroup>
            <Button
              variant="primary"
              onClick={() => {
                navigate(URLS.ACCUEIL);
              }}
            >
              <IconReturn /> Retour
            </Button>
            <Button
              variant="primary"
              onClick={() => {
                window.print();
              }}
            >
              <IconPrint /> Imprimer
            </Button>
          </ButtonGroup>
        }
      />
      <div id={"papersheet"}>
        <h1 contentEditable={"true"}>✎ Titre de la carte</h1>
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
    </Container>
  );
};

export default MapPerso;

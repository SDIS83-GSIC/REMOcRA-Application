import VectorLayer from "ol/layer/Vector";
import VectorSource from "ol/source/Vector";
import { Fill, Stroke, Style } from "ol/style";
import CircleStyle from "ol/style/Circle";
import { useMemo, useRef } from "react";
import { Button } from "react-bootstrap";
import Col from "react-bootstrap/Col";
import Row from "react-bootstrap/Row";
import SquelettePage from "../../../pages/SquelettePage.tsx";
import { formatDateHeure } from "../../../utils/formatDateUtils.tsx";
import PageTitle from "../../Elements/PageTitle/PageTitle.tsx";
import Header from "../../Header/Header.tsx";
import { IconCarte, IconPrint } from "../../Icon/Icon.tsx";
import { TypeModuleRemocra } from "../../ModuleRemocra/ModuleRemocra.tsx";
import MapComponent, { useMapComponent } from "../Map.tsx";
import { useToolbarContext } from "../MapToolbar.tsx";
import "./MapPerso.css";
import MapToolbarPerso, { useToolbarPersoContext } from "./MapToolbarPerso.tsx";

const MapPerso = () => {
  const now = new Date();
  const mapElement = useRef<HTMLDivElement>();

  const {
    map,
    workingLayer,
    availableLayers,
    addOrRemoveLayer,
    showOutilI,
    layerListRef,
    mapToolbarRef,
  } = useMapComponent({
    mapElement: mapElement,
    typeModule: TypeModuleRemocra.CARTOGRAPHIE_PERSONNALISEE,
  });

  // Couche dédiée à la cartographie personnalisée. Toutes les formes seront ajoutées à cette couche.
  const cartographiePersoLayer = useMemo(() => {
    if (map) {
      const wl = new VectorLayer({
        source: new VectorSource(),
        style: () => {
          return new Style({
            fill: new Fill({
              color: "rgba(5, 176, 255, 1)",
            }),
            stroke: new Stroke({
              color: "rgba(5, 176, 255, 1)",
              width: 2,
            }),
            image: new CircleStyle({
              radius: 7,
              fill: new Fill({
                color: "#00c3ffff",
              }),
            }),
          });
        },
        opacity: 1,
        zIndex: 9000,
      });

      map.addLayer(wl);
      return wl;
    }
  }, [map]);

  const {
    tools: extraTools,
    featureStyle,
    setFeatureStyle,
    selectedFeatures,
  } = useToolbarPersoContext({
    map,
    cartographiePersoLayer,
  });

  const { toggleTool, activeTool, infoOutilI, handleCloseInfoI } =
    useToolbarContext({
      availableLayers: availableLayers,
      map: map,
      workingLayer: workingLayer,
      extraTools: extraTools,
    });

  return (
    <SquelettePage navbar={<Header />}>
      <div style={{ marginBottom: "100px" }} className="noprint">
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
      </div>
      <div id={"papersheet"} className={"printable-no-margin mx-auto"}>
        <h1 contentEditable={"true"}>✎ Titre de la carte</h1>
        <MapComponent
          map={map}
          printable={true}
          outilI={infoOutilI}
          showOutilI={showOutilI}
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
                cartographiePersoLayer={cartographiePersoLayer}
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

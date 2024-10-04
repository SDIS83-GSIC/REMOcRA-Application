import { forwardRef, useEffect, useImperativeHandle, useState } from "react";
import {
  Button,
  ButtonGroup,
  ButtonToolbar,
  ToggleButton,
} from "react-bootstrap";
import Map from "ol/Map";
import { DragBox, DragPan, Draw, Select } from "ol/interaction";
import { Fill, Stroke, Style } from "ol/style";
import CircleStyle from "ol/style/Circle";
import { LineString, Polygon } from "ol/geom";
import { getArea, getLength } from "ol/sphere";
import { Overlay } from "ol";
import { unByKey } from "ol/Observable";
import { shiftKeyOnly } from "ol/events/condition";
import Row from "react-bootstrap/Row";
import AdresseTypeahead from "./AdresseTypeahead.tsx";

const MapToolbar = forwardRef(
  (
    {
      map,
      dataPeiLayer,
      dataPeiProjetLayer,
      workingLayer,
    }: {
      map: Map;
      dataPeiLayer: any;
      dataPeiProjetLayer: any;
      workingLayer: any;
    },
    ref,
  ) => {
    const [activeTool, setActiveTool] = useState<string>();
    const [zoom, setZoom] = useState<number>();
    const measureStyle = new Style({
      fill: new Fill({
        color: "rgba(255, 255, 255, 0.2)",
      }),
      stroke: new Stroke({
        color: "rgba(0, 0, 0, 0.5)",
        lineDash: [10, 10],
        width: 2,
      }),
      image: new CircleStyle({
        radius: 5,
        stroke: new Stroke({
          color: "rgba(0, 0, 0, 0.7)",
        }),
        fill: new Fill({
          color: "rgba(255, 255, 255, 0.2)",
        }),
      }),
    });

    useEffect(() => {
      setActiveTool("move");
      setZoom(Math.floor(map.getView().getZoom()));
    }, []);

    map.getView().on("change:resolution", () => {
      if (map.getView().getZoom() % 1 === 0) {
        setZoom(Math.floor(map.getView().getZoom()));
      }
    });

    function toggleMove(active = false) {
      const dragPanCtrl = map
        ?.getInteractions()
        .getArray()
        .filter((c) => c instanceof DragPan)[0];
      if (active) {
        if (!dragPanCtrl) {
          map.addInteraction(new DragPan());
        }
      } else {
        if (dragPanCtrl) {
          map.removeInteraction(dragPanCtrl);
        }
      }
    }

    function toggleSelect(active = false) {
      const selectCtrl = map
        ?.getInteractions()
        .getArray()
        .filter((c) => c instanceof Select)[0];
      const dragBoxCtrl = map
        ?.getInteractions()
        .getArray()
        .filter((c) => c instanceof DragBox)[0];
      if (active) {
        if (!selectCtrl) {
          const select = new Select({});
          const dragBox = new DragBox({
            style: new Style({
              stroke: new Stroke({
                color: [0, 0, 255, 1],
              }),
            }),
            minArea: 25,
          });
          dragBox.on("boxend", function (e) {
            if (!shiftKeyOnly(e.mapBrowserEvent)) {
              select.getFeatures().clear();
            }
            const boxExtent = dragBox.getGeometry().getExtent();
            const boxFeatures = dataPeiLayer
              .getSource()
              .getFeaturesInExtent(boxExtent);
            select.getFeatures().extend(boxFeatures);

            const boxFeaturesPeiProjet = dataPeiProjetLayer
              .getSource()
              .getFeaturesInExtent(boxExtent);

            select.getFeatures().extend(boxFeaturesPeiProjet);
          });

          map.addInteraction(select);
          map.addInteraction(dragBox);
        }
      } else {
        if (selectCtrl) {
          map.removeInteraction(selectCtrl);
        }
        if (dragBoxCtrl) {
          map.removeInteraction(dragBoxCtrl);
        }
      }
    }

    let measureTooltipElement, measureTooltip;

    function createMeasureTooltip() {
      if (measureTooltipElement) {
        measureTooltipElement.remove();
      }
      measureTooltipElement = document.createElement("div");
      measureTooltipElement.className = "ol-tooltip ol-tooltip-measure";
      measureTooltip = new Overlay({
        element: measureTooltipElement,
        offset: [0, -15],
        positioning: "bottom-center",
        stopEvent: false,
        insertFirst: false,
      });
      map.addOverlay(measureTooltip);
    }

    const formatLength = function (line) {
      const length = getLength(line);
      let output;
      if (length > 100) {
        output = Math.round((length / 1000) * 100) / 100 + " " + "km";
      } else {
        output = Math.round(length * 100) / 100 + " " + "m";
      }
      return output;
    };

    const formatArea = function (polygon) {
      const area = getArea(polygon);
      let output;
      if (area > 10000) {
        output =
          Math.round((area / 1000000) * 100) / 100 + " " + "km<sup>2</sup>";
      } else {
        output = Math.round(area * 100) / 100 + " " + "m<sup>2</sup>";
      }
      return output;
    };

    function toggleMeasureLength(active = false) {
      toggleMeasure(active, "LineString");
    }

    function toggleMeasureArea(active = false) {
      toggleMeasure(active, "Polygon");
    }

    function toggleMeasure(active = false, type) {
      const measureCtrl = map
        ?.getInteractions()
        .getArray()
        .filter((c) => c instanceof Draw)[0];
      if (active) {
        if (!measureCtrl) {
          const draw = new Draw({
            source: workingLayer.getSource(),
            type: type,
            style: (feature) => {
              const geometryType = feature.getGeometry().getType();
              if (geometryType === type || geometryType === "Point") {
                return measureStyle;
              }
            },
          });
          map.addInteraction(draw);

          createMeasureTooltip();

          let listener;
          draw.on("drawstart", function (evt) {
            const sketch = evt.feature;
            let tooltipCoord = evt.coordinate;
            listener = sketch.getGeometry().on("change", function (evt) {
              const geom = evt.target;
              let output;
              if (geom instanceof Polygon) {
                output = formatArea(geom);
                tooltipCoord = geom.getInteriorPoint().getCoordinates();
              } else if (geom instanceof LineString) {
                output = formatLength(geom);
                tooltipCoord = geom.getLastCoordinate();
              }
              measureTooltipElement.innerHTML = output;
              measureTooltip.setPosition(tooltipCoord);
            });
          });

          draw.on("drawend", function () {
            measureTooltipElement.className = "ol-tooltip ol-tooltip-static";
            measureTooltip.setOffset([0, -7]);
            // unset sketch
            // unset tooltip so that a new one can be created
            measureTooltipElement = null;
            createMeasureTooltip();
            unByKey(listener);
          });
        }
      } else {
        if (measureCtrl && measureCtrl.mode_ === type) {
          map.removeInteraction(measureCtrl);
        }
      }
    }

    function zoomIn() {
      if (map.getView().getZoom() < map.getView().getMaxZoom()) {
        map.getView().setZoom(Math.floor(map.getView().getZoom() + 1));
      }
    }

    function zoomOut() {
      if (map.getView().getZoom() > map.getView().getMinZoom()) {
        map.getView().setZoom(Math.floor(map.getView().getZoom() - 1));
      }
    }

    const tools = {
      move: {
        action: toggleMove,
      },
      select: {
        action: toggleSelect,
      },
      "measure-length": {
        action: toggleMeasureLength,
      },
      "measure-area": {
        action: toggleMeasureArea,
      },
    };

    function toggleTool(toolId) {
      let newTool = null;
      if (activeTool === toolId) {
        setActiveTool(null);
      } else {
        setActiveTool(toolId);
        newTool = toolId;
      }
      for (const property in tools) {
        tools[property].action(property === newTool);
      }
    }

    // Pour accéder aux méthodes de la bar en dehors
    useImperativeHandle(ref, () => ({}));

    return (
      <Row>
        <ButtonToolbar>
          <ButtonGroup>
            <Button
              onClick={zoomIn}
              disabled={zoom >= map?.getView().getMaxZoom()}
            >
              Zoom +
            </Button>
            <Button
              onClick={zoomOut}
              disabled={zoom <= map?.getView().getMinZoom()}
            >
              Zoom -
            </Button>
          </ButtonGroup>
          <ButtonGroup>
            <ToggleButton
              name={"tool"}
              onClick={() => toggleTool("move")}
              id={"move"}
              value={"move"}
              type={"radio"}
              variant={"outline-primary"}
              checked={activeTool === "move"}
            >
              Déplacer
            </ToggleButton>
            <ToggleButton
              name={"tool"}
              onClick={() => toggleTool("select")}
              id={"select"}
              value={"select"}
              type={"radio"}
              variant={"outline-primary"}
              checked={activeTool === "select"}
            >
              Sélectionner
            </ToggleButton>
          </ButtonGroup>
          <AdresseTypeahead map={map} />
          <ButtonGroup>
            <ToggleButton
              name={"tool"}
              onClick={() => toggleTool("measure-length")}
              id={"measure-length"}
              value={"measure-length"}
              type={"radio"}
              variant={"outline-primary"}
              checked={activeTool === "measure-length"}
            >
              Distance
            </ToggleButton>
            <ToggleButton
              name={"tool"}
              onClick={() => toggleTool("measure-area")}
              id={"measure-area"}
              value={"measure-area"}
              type={"radio"}
              variant={"outline-primary"}
              checked={activeTool === "measure-area"}
            >
              Surface
            </ToggleButton>
          </ButtonGroup>
        </ButtonToolbar>
      </Row>
    );
  },
);

MapToolbar.displayName = "MapToolbar";

export default MapToolbar;

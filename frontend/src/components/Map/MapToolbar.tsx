import { Overlay } from "ol";
import Map from "ol/Map";
import { unByKey } from "ol/Observable";
import { LineString, Polygon } from "ol/geom";
import { DragPan, Draw } from "ol/interaction";
import { getArea, getLength } from "ol/sphere";
import { Fill, Stroke, Style } from "ol/style";
import CircleStyle from "ol/style/Circle";
import {
  forwardRef,
  useEffect,
  useImperativeHandle,
  useMemo,
  useState,
} from "react";
import { Button, ButtonGroup, ButtonToolbar } from "react-bootstrap";
import Row from "react-bootstrap/Row";
import {
  IconDistance,
  IconMoveCarte,
  IconSurface,
  IconZoomIn,
  IconZoomOut,
} from "../Icon/Icon.tsx";
import AdresseTypeahead from "./AdresseTypeahead.tsx";
import ToolbarButton from "./ToolbarButton.tsx";

const measureStyle = new Style({
  fill: new Fill({
    color: "rgba(255, 255, 255, 0.2)",
  }),
  stroke: new Stroke({
    color: "rgba(255, 0, 0, 0.7)",
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

const formatLength = function (line) {
  const length = getLength(line);
  let output;
  if (length > 1000) {
    output = Math.round((length / 1000) * 100) / 100 + " " + "km";
  } else {
    output = Math.round(length * 100) / 100 + " " + "m";
  }
  return output;
};

const formatArea = function (polygon) {
  const area = getArea(polygon);
  let output;
  // Au dessus de 10km², on passe en hectares ; 1km² == 100ha
  if (area > 10000000) {
    output = Math.round((area / 10000) * 100) / 100 + " ha";
  } else if (area > 10000) {
    output = Math.round((area / 1000000) * 100) / 100 + " " + "km<sup>2</sup>";
  } else {
    output = Math.round(area * 100) / 100 + " " + "m<sup>2</sup>";
  }
  return output;
};

export const useToolbarContext = ({ map, workingLayer, extraTools = {} }) => {
  const [activeTool, setActiveTool] = useState<string>("");
  const measureOverlayArray = [];
  const geometryOverlayArray = [];

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
    measureOverlayArray.push(measureTooltip);
    map.addOverlay(measureTooltip);
  }

  const tools = useMemo(() => {
    if (!map) {
      return {};
    }

    const dragPanCtrl = new DragPan();

    function toggleMove(active = false) {
      const idx = map.getInteractions().getArray().indexOf(dragPanCtrl);
      if (active) {
        if (idx === -1) {
          map.addInteraction(dragPanCtrl);
        }
      } else {
        map.removeInteraction(dragPanCtrl);
      }
    }

    function toggleMeasureLength(active = false) {
      toggleMeasure(active, "LineString");
    }

    function toggleMeasureArea(active = false) {
      toggleMeasure(active, "Polygon");
    }

    let listener;
    const drawstartCallback = (evt) => {
      const sketch = evt.feature;
      let tooltipCoord = evt.coordinate;
      listener = sketch.getGeometry().on("change", function (evt) {
        createMeasureTooltip();
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
    };

    const drawendCallback = (evt) => {
      geometryOverlayArray.push(evt.feature);
      measureTooltipElement.className = "ol-tooltip ol-tooltip-static";
      measureTooltip.setOffset([0, -7]);
      // unset sketch
      // unset tooltip so that a new one can be created
      measureTooltipElement = null;
      unByKey(listener);
    };

    const measureLengthCtrl = new Draw({
      source: workingLayer.getSource(),
      type: "LineString",
      style: (feature) => {
        const geometryType = feature.getGeometry().getType();
        if (geometryType === "LineString" || geometryType === "Point") {
          return measureStyle;
        }
      },
    });

    measureLengthCtrl.on("drawstart", drawstartCallback);
    measureLengthCtrl.on("drawend", drawendCallback);
    const measureAreaCtrl = new Draw({
      source: workingLayer.getSource(),
      type: "Polygon",
      style: (feature) => {
        const geometryType = feature.getGeometry().getType();
        if (geometryType === "Polygon" || geometryType === "Point") {
          return measureStyle;
        }
      },
    });
    measureAreaCtrl.on("drawstart", drawstartCallback);
    measureAreaCtrl.on("drawend", drawendCallback);

    function toggleMeasure(active = false, type) {
      const ctrl = type === "LineString" ? measureLengthCtrl : measureAreaCtrl;
      const idx = map.getInteractions().getArray().indexOf(ctrl);
      if (active) {
        if (idx === -1) {
          map.addInteraction(ctrl);
        }
      } else {
        map.removeInteraction(ctrl);
        measureOverlayArray.forEach((o) => map.removeOverlay(o));
        measureOverlayArray.splice(0, measureOverlayArray.length);
        workingLayer.getSource().removeFeatures(geometryOverlayArray);
        geometryOverlayArray.splice(0, geometryOverlayArray.length);
      }
    }

    return {
      "move-view": {
        action: toggleMove,
      },
      "measure-length": {
        action: toggleMeasureLength,
      },
      "measure-area": {
        action: toggleMeasureArea,
      },
      ...extraTools,
    };
  }, [map]);

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

  return {
    activeTool,
    toggleTool,
  };
};

const MapToolbar = forwardRef(
  (
    {
      map,
      toggleTool,
      activeTool,
    }: {
      map: Map;
      toggleTool: (toolId: string) => void;
      activeTool: string;
    },
    ref,
  ) => {
    const [zoom, setZoom] = useState<number>();

    useEffect(() => {
      // setActiveTool("move");
      setZoom(Math.floor(map.getView().getZoom()));
    }, []);

    map.getView().on("change:resolution", () => {
      if (map.getView().getZoom() % 1 === 0) {
        setZoom(Math.floor(map.getView().getZoom()));
      }
    });

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

    // Pour accéder aux méthodes de la bar en dehors
    useImperativeHandle(ref, () => ({}));

    return (
      <Row>
        <ButtonToolbar>
          <ButtonGroup>
            <Button
              className="m-2 rounded"
              onClick={zoomIn}
              disabled={zoom >= map.getView().getMaxZoom()}
            >
              <IconZoomIn />
            </Button>
            <Button
              className="m-2 rounded"
              onClick={zoomOut}
              disabled={zoom <= map.getView().getMinZoom()}
            >
              <IconZoomOut />
            </Button>
          </ButtonGroup>
          <ButtonGroup>
            <ToolbarButton
              toolName={"move-view"}
              toolIcon={<IconMoveCarte />}
              toolLabelTooltip={"Se déplacer sur la carte"}
              toggleTool={toggleTool}
              activeTool={activeTool}
            />
          </ButtonGroup>
          <AdresseTypeahead map={map} />
          <ButtonGroup>
            <ToolbarButton
              toolName={"measure-length"}
              toolIcon={<IconDistance />}
              toolLabelTooltip={"Mesurer une distance"}
              toggleTool={toggleTool}
              activeTool={activeTool}
            />
            <ToolbarButton
              toolName={"measure-area"}
              toolIcon={<IconSurface />}
              toolLabelTooltip={"Mesurer une superficie"}
              toggleTool={toggleTool}
              activeTool={activeTool}
            />
          </ButtonGroup>
        </ButtonToolbar>
      </Row>
    );
  },
);

MapToolbar.displayName = "MapToolbar";

export default MapToolbar;

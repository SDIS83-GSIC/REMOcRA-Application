import { Overlay } from "ol";
import Map from "ol/Map";
import { unByKey } from "ol/Observable";
import { LineString, Polygon } from "ol/geom";
import { DragPan, Draw, Interaction } from "ol/interaction";
import { getArea, getLength } from "ol/sphere";
import { Fill, Stroke, Style } from "ol/style";
import CircleStyle from "ol/style/Circle";
import TileLayer from "ol/layer/Tile";
import { TileWMS } from "ol/source";
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
  IconInfo,
  IconSurface,
  IconZoomIn,
  IconZoomOut,
} from "../Icon/Icon.tsx";
import Volet from "../Volet/Volet.tsx";
import AdresseTypeahead from "./AdresseTypeahead.tsx";
import ToolbarButton from "./ToolbarButton.tsx";
import OutilIVolet from "./MapOutilI/ShowInfoVolet.tsx";

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

const formatLength = function (line: LineString): string {
  const length = getLength(line); // m
  let output: string;
  if (length > 1000) {
    output = Math.round((length / 1000) * 100) / 100 + " " + "km";
  } else {
    output = Math.round(length * 100) / 100 + " " + "m";
  }
  return output;
};

const formatArea = function (polygon: Polygon): string {
  const area = getArea(polygon); // m²
  let output: string;
  // Au dessus de 10 000 m², on passe en hectares ; 1ha == 10 000 m² == 100 m * 100 m
  if (area > 10000) {
    output = Math.round((area / 10000) * 100) / 100 + " ha";
  } else {
    output = Math.round(area * 100) / 100 + " " + "m<sup>2</sup>";
  }
  return output;
};

export const useToolbarContext = ({
  map,
  workingLayer,
  extraTools = {},
}: {
  map?: Map;
  workingLayer: any;
  extraTools?: any;
}) => {
  const [activeTool, setActiveTool] = useState<string | null>("");
  // const [showVoletOutilI, setShowVoletOutilI] = useState(false);
  const [infoOutilI, setInfoOutilI] = useState<{
    show: boolean;
    data: any[];
  }>({ show: false, data: [] });

  const measureOverlayArray: Overlay[] = [];
  const geometryOverlayArray: Overlay[] = [];

  const handleCloseInfoI = () => {
    workingLayer.getSource().clear();
    setInfoOutilI({ show: false, data: [] });
  };

  let measureTooltipElement: HTMLDivElement | null,
    measureTooltip: Overlay | undefined;

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
    map!.addOverlay(measureTooltip);
  }

  const tools = useMemo(() => {
    if (!map) {
      return {};
    }

    const dragPanCtrl = new DragPan();

    function toggleMove(active = false) {
      const idx = map!.getInteractions().getArray().indexOf(dragPanCtrl);
      if (active) {
        if (idx === -1) {
          map!.addInteraction(dragPanCtrl);
        }
        dragPanCtrl.setActive(true);
      } else {
        dragPanCtrl.setActive(false);
      }
    }

    function toggleMeasureLength(active = false) {
      toggleMeasure(active, "LineString");
    }

    function toggleMeasureArea(active = false) {
      toggleMeasure(active, "Polygon");
    }

    function toggleInfoArea(active = false) {
      const draw = drawCtrlInfo;
      if (active) {
        if (map?.getInteractions().getArray().indexOf(draw) === -1) {
          map?.addInteraction(draw);
        }
      } else {
        map?.removeInteraction(draw);
        handleCloseInfoI();
      }
    }

    let listener: ReturnType<typeof unByKey> | undefined;

    interface GeometryChangeEvent {
      target: Polygon | LineString;
    }

    const drawstartCallback = (evt: any) => {
      const sketch = evt.feature;
      let tooltipCoord: number[] = evt.coordinate;
      listener = sketch
        .getGeometry()
        .on("change", function (evt: GeometryChangeEvent) {
          createMeasureTooltip();
          const geom = evt.target;
          let output: string = "";
          if (geom instanceof Polygon) {
            output = formatArea(geom);
            tooltipCoord = geom.getInteriorPoint().getCoordinates();
          } else if (geom instanceof LineString) {
            output = formatLength(geom);
            tooltipCoord = geom.getLastCoordinate();
          }
          if (measureTooltipElement && output) {
            measureTooltipElement.innerHTML = output;
          }
          if (measureTooltip) {
            measureTooltip.setPosition(tooltipCoord);
          }
        });
    };

    const drawendCallback = (evt: any) => {
      geometryOverlayArray.push(evt.feature);
      if (measureTooltipElement) {
        measureTooltipElement.className = "ol-tooltip ol-tooltip-static";
      }
      if (measureTooltip) {
        measureTooltip.setOffset([0, -7]);
      }
      // unset sketch
      // unset tooltip so that a new one can be created
      measureTooltipElement = null;
      if (listener) {
        unByKey(listener);
      }
    };

    // draw pour l'élément "boutonI"
    const drawCtrlInfo = new Draw({
      source: workingLayer.getSource(),
      type: "Point",
    });

    drawCtrlInfo.on("drawend", async (event: { feature: any }) => {
      const feature = event.feature;
      const coords = feature.getGeometry().getCoordinates();
      setInfoOutilI({ show: false, data: [] });

      map!
        .getLayers()
        .getArray()
        .filter(
          (l) => l instanceof TileLayer && l.getSource() instanceof TileWMS,
        )
        .forEach((wmsLayer) => {
          const view = map!.getView();
          const url = wmsLayer
            .getSource()
            ?.getFeatureInfoUrl(
              coords,
              view.getResolution()!,
              view.getProjection(),
              {
                INFO_FORMAT: "application/json",
                FEATURE_COUNT: 5,
              },
            );

          if (url) {
            fetch(url)
              .then((r) => {
                if (!r.ok) {
                  throw new Error("Mauvaise réponse internet");
                }
                return r.json();
              })
              .then((data) => {
                setInfoOutilI((e) => ({
                  show: true,
                  data: [...e.data, { ...data }],
                }));
              })
              .catch(() => {
                // plan sans documentation => ne rien faire
              });
          }
        });
    });

    drawCtrlInfo.on("drawstart", async () => {
      workingLayer.getSource().clear();
    });

    const measureLengthCtrl = new Draw({
      source: workingLayer.getSource(),
      type: "LineString",
      style: (feature) => {
        const geometry = feature.getGeometry();
        if (!geometry) {
          return;
        }
        const geometryType = geometry.getType();
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
        const geometry = feature.getGeometry();
        if (!geometry) {
          return;
        }
        const geometryType = geometry.getType();
        if (geometryType === "Polygon" || geometryType === "Point") {
          return measureStyle;
        }
      },
    });
    measureAreaCtrl.on("drawstart", drawstartCallback);
    measureAreaCtrl.on("drawend", drawendCallback);

    function toggleMeasure(active = false, type: string) {
      const ctrl = type === "LineString" ? measureLengthCtrl : measureAreaCtrl;
      const idx = map!.getInteractions().getArray().indexOf(ctrl);
      if (active) {
        if (idx === -1) {
          map!.addInteraction(ctrl);
        }
      } else {
        map!.removeInteraction(ctrl);
        measureOverlayArray.forEach((o) => map!.removeOverlay(o));
        measureOverlayArray.splice(0, measureOverlayArray.length);
        workingLayer.getSource().removeFeatures(geometryOverlayArray);
        geometryOverlayArray.splice(0, geometryOverlayArray.length);
      }
    }

    return {
      "move-view": {
        action: toggleMove,
        actionPossibleEnDeplacement: true,
      },
      "measure-length": {
        action: toggleMeasureLength,
        actionPossibleEnDeplacement: true,
      },
      "measure-area": {
        action: toggleMeasureArea,
        actionPossibleEnDeplacement: true,
      },
      "info-outil-i": {
        action: toggleInfoArea,
        actionPossibleEnDeplacement: true,
      },
      ...extraTools,
    };
  }, [map]);

  function disabledTool(toolId: string) {
    if (activeTool === toolId) {
      setActiveTool(null);
      tools[toolId]?.action(false);
    }
  }

  function toggleTool(toolId: string) {
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

    // on autorise le déplacement que si on n'est pas dans une situation qui l'interdit (sélection)
    tools["move-view"].action(
      tools[toolId]?.actionPossibleEnDeplacement !== false ||
        newTool == null ||
        map!
          .getInteractions()
          .getArray()
          .find((interaction: Interaction) => interaction instanceof DragPan)
          ?.getActive() !== true,
    );
  }

  return {
    activeTool,
    toggleTool,
    disabledTool,
    infoOutilI,
    handleCloseInfoI,
  };
};

const MapToolbar = forwardRef(
  (
    {
      map,
      toggleTool,
      activeTool,
      variant = "primary",
      generalInfo,
      handleCloseInfoI,
    }: {
      map: Map;
      toggleTool: (toolId: string) => void;
      activeTool: string;
      variant: string;
      generalInfo: any;
      handleCloseInfoI: () => void;
    },
    ref,
  ) => {
    const [zoom, setZoom] = useState<number>(
      Math.floor(map.getView().getZoom() ?? 0),
    );

    useEffect(() => {
      // setActiveTool("move");
      setZoom(Math.floor(map.getView().getZoom() ?? 0));
    }, [setZoom, map]);

    map.getView().on("change:resolution", () => {
      if ((map.getView().getZoom() ?? 0) % 1 === 0) {
        setZoom(Math.floor(map.getView().getZoom() ?? 0));
      }
    });

    function zoomIn() {
      if ((map.getView().getZoom() ?? 0) < map.getView().getMaxZoom()) {
        map.getView().setZoom(Math.floor((map.getView().getZoom() ?? 0) + 1));
      }
    }

    function zoomOut() {
      if ((map.getView().getZoom() ?? 0) > map.getView().getMinZoom()) {
        map.getView().setZoom(Math.floor((map.getView().getZoom() ?? 0) - 1));
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
              variant={variant}
            >
              <IconZoomIn />
            </Button>
            <Button
              className="m-2 rounded"
              onClick={zoomOut}
              disabled={zoom <= map.getView().getMinZoom()}
              variant={variant}
            >
              <IconZoomOut />
            </Button>
          </ButtonGroup>
          <AdresseTypeahead map={map} />
          <ButtonGroup>
            <ToolbarButton
              toolName={"measure-length"}
              toolIcon={<IconDistance />}
              toolLabelTooltip={"Mesurer une distance"}
              toggleTool={toggleTool}
              activeTool={activeTool}
              variant={variant}
            />
            <ToolbarButton
              toolName={"measure-area"}
              toolIcon={<IconSurface />}
              toolLabelTooltip={"Mesurer une superficie"}
              toggleTool={toggleTool}
              activeTool={activeTool}
              variant={variant}
            />
            <ToolbarButton
              toolName={"info-outil-i"}
              toolIcon={<IconInfo />}
              toolLabelTooltip={
                "Obtenir des informations sur un point de la carte"
              }
              toggleTool={toggleTool}
              activeTool={activeTool}
              variant={variant}
            />
          </ButtonGroup>
        </ButtonToolbar>

        <Volet
          handleClose={handleCloseInfoI}
          show={generalInfo.show}
          className="w-auto"
        >
          <OutilIVolet generalsInfos={generalInfo.data} />
        </Volet>
      </Row>
    );
  },
);

MapToolbar.displayName = "MapToolbar";

export default MapToolbar;

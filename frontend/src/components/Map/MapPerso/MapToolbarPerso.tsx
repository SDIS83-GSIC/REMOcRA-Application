import { Feature } from "ol";
import { asArray, asString } from "ol/color";
import { never, platformModifierKeyOnly } from "ol/events/condition";
import { getCenter, getHeight, getWidth } from "ol/extent";
import { LineString, Point, Polygon } from "ol/geom";
import { DragBox, Draw, Modify, Select, Translate } from "ol/interaction";
import VectorLayer from "ol/layer/Vector";
import Map from "ol/Map";
import VectorSource from "ol/source/Vector";
import { Fill, Stroke, Style } from "ol/style";
import CircleStyle from "ol/style/Circle";
import View from "ol/View";
import { useLayoutEffect, useMemo, useState } from "react";
import { Button, ButtonGroup, Form, ToggleButton } from "react-bootstrap";
import FormRange from "react-bootstrap/FormRange";
import AccordionCustom, {
  useAccordionState,
} from "../../Accordion/Accordion.tsx";
import {
  IconDelete,
  IconEdit,
  IconLine,
  IconMoveCarte,
  IconPoint,
  IconPolygon,
  IconRotate,
  IconSelect,
  IconStyle,
} from "../../Icon/Icon.tsx";
import TooltipCustom from "../../Tooltip/Tooltip.tsx";
import Volet from "../../Volet/Volet.tsx";
import ToolbarButton from "../ToolbarButton.tsx";

const defaultStyle = new Style({
  fill: new Fill({
    color: "#64C0FFFF",
  }),
  stroke: new Stroke({
    color: "#64C0FFFF",
    width: 2,
  }),
  image: new CircleStyle({
    radius: 5,
    stroke: new Stroke({
      color: "#64C0FFFF",
    }),
    fill: new Fill({
      color: "#64C0FFFF",
    }),
  }),
});

const FORMATS = [
  {
    code: "a4landscape",
    label: "A4 (Paysage)",
    value: { width: "297mm", height: "210mm" },
  },
  {
    code: "a4portrait",
    label: "A4 (Portrait)",
    value: { width: "210mm", height: "297mm" },
  },
  {
    code: "a3landscape",
    label: "A3 (Paysage)",
    value: { width: "594mm", height: "420mm" },
  },
  {
    code: "a3portrait",
    label: "A3 (Portrait)",
    value: { width: "420mm", height: "594mm" },
  },
];

const LINE_CAPS = [
  ["round", "Arrondies"],
  ["square", "Carrées"],
  ["butt", "Droites"],
];

const LINE_DASHES = [
  ["solid", "Continu"],
  ["dot", "Pointillés"],
  ["dash", "Tirets"],
  ["dashdot", "Tirets / Pointillés"],
  ["longdash", "Tirets longs"],
  ["longdashdot", "Tirets longs / Pointillés"],
];

export const useToolbarPersoContext = ({ map, workingLayer }) => {
  const [featureStyle, setFeatureStyle] = useState(defaultStyle);
  const [selectedFeatures, setSelectedFeatures] = useState([]);

  const tools = useMemo(() => {
    if (!map) {
      return {};
    }

    const drawPointCtrl = new Draw({
      source: workingLayer.getSource(),
      type: "Point",
    });

    const drawLineCtrl = new Draw({
      source: workingLayer.getSource(),
      type: "LineString",
    });

    const drawShapeCtrl = new Draw({
      source: workingLayer.getSource(),
      type: "Polygon",
    });

    drawPointCtrl.on("drawend", (event) => {
      event.feature.setStyle(featureStyle.clone());
    });

    drawLineCtrl.on("drawend", (event) => {
      event.feature.setStyle(featureStyle.clone());
    });

    drawShapeCtrl.on("drawend", (event) => {
      event.feature.setStyle(featureStyle.clone());
    });

    const selectCtrl = new Select({
      layers: [workingLayer],
      toggleCondition: platformModifierKeyOnly,
    });
    const dragBoxCtrl = new DragBox({
      style: new Style({
        stroke: new Stroke({
          color: [0, 0, 255, 1],
        }),
      }),
      minArea: 25,
    });
    dragBoxCtrl.on("boxend", function (e) {
      if (!platformModifierKeyOnly(e.mapBrowserEvent)) {
        selectCtrl.getFeatures().clear();
      }
      const boxExtent = dragBoxCtrl.getGeometry().getExtent();
      const boxFeatures = workingLayer
        .getSource()
        .getFeaturesInExtent(boxExtent);

      selectCtrl.getFeatures().extend(boxFeatures);

      setSelectedFeatures(selectCtrl.getFeatures().getArray());
    });

    function toggleSelectDraw(active = false) {
      const idx1 = map?.getInteractions().getArray().indexOf(selectCtrl);
      const idx2 = map?.getInteractions().getArray().indexOf(dragBoxCtrl);
      if (active) {
        if (idx1 === -1 && idx2 === -1) {
          map.addInteraction(selectCtrl);
          map.addInteraction(dragBoxCtrl);
        }
      } else {
        map.removeInteraction(selectCtrl);
        map.removeInteraction(dragBoxCtrl);
      }
    }

    function toggleCtrl(active = false, ctrl) {
      const idx = map?.getInteractions().getArray().indexOf(ctrl);
      if (active) {
        if (idx === -1) {
          map.addInteraction(ctrl);
        }
      } else {
        map.removeInteraction(ctrl);
      }
    }

    function toggleDrawPoint(active = false) {
      toggleCtrl(active, drawPointCtrl);
    }

    function toggleDrawLine(active = false) {
      toggleCtrl(active, drawLineCtrl);
    }

    function toggleDrawShape(active = false) {
      toggleCtrl(active, drawShapeCtrl);
    }

    const modifyShapeCtrl = new Modify({
      source: workingLayer.getSource(),
    });

    const calculateCenter = (geometry) => {
      let center, coordinates, minRadius;
      const type = geometry.getType();
      if (type === "Polygon") {
        let x = 0;
        let y = 0;
        let i = 0;
        coordinates = geometry.getCoordinates()[0].slice(1);
        coordinates.forEach(function (coordinate) {
          x += coordinate[0];
          y += coordinate[1];
          i++;
        });
        center = [x / i, y / i];
      } else if (type === "LineString") {
        center = geometry.getCoordinateAt(0.5);
        coordinates = geometry.getCoordinates();
      } else {
        center = getCenter(geometry.getExtent());
      }
      let sqDistances;
      if (coordinates) {
        sqDistances = coordinates.map(function (coordinate) {
          const dx = coordinate[0] - center[0];
          const dy = coordinate[1] - center[1];
          return dx * dx + dy * dy;
        });
        minRadius = Math.sqrt(Math.max(...sqDistances)) / 3;
      } else {
        minRadius =
          Math.max(
            getWidth(geometry.getExtent()),
            getHeight(geometry.getExtent()),
          ) / 3;
      }
      return {
        center: center,
        coordinates: coordinates,
        minRadius: minRadius,
        sqDistances: sqDistances,
      };
    };

    const defaultStyle = new Modify({ source: workingLayer.getSource() })
      .getOverlay()
      .getStyleFunction();

    // FIXME faire en sorte d'afficher une géométrie dédiée lors de la modification, actuellement seul le point sélectionné est visuellement déplacé
    const modifyScaleCtrl = new Modify({
      source: workingLayer.getSource(),
      deleteCondition: never,
      insertVertexCondition: never,
      style: function (feature) {
        feature.get("features").forEach(function (modifyFeature) {
          const modifyGeometry = modifyFeature.get("modifyGeometry");
          if (modifyGeometry) {
            const point = feature.getGeometry().getCoordinates();
            let modifyPoint = modifyGeometry.point;
            if (!modifyPoint) {
              // save the initial geometry and vertex position
              modifyPoint = point;
              modifyGeometry.point = modifyPoint;
              modifyGeometry.geometry0 = modifyGeometry.geometry;
              // get anchor and minimum radius of vertices to be used
              const result = calculateCenter(modifyGeometry.geometry0);
              modifyGeometry.center = result.center;
              modifyGeometry.minRadius = result.minRadius;
            }

            const center = modifyGeometry.center;
            const minRadius = modifyGeometry.minRadius;
            let dx, dy;
            dx = modifyPoint[0] - center[0];
            dy = modifyPoint[1] - center[1];
            const initialRadius = Math.sqrt(dx * dx + dy * dy);
            if (initialRadius > minRadius) {
              const initialAngle = Math.atan2(dy, dx);
              dx = point[0] - center[0];
              dy = point[1] - center[1];
              const currentRadius = Math.sqrt(dx * dx + dy * dy);
              if (currentRadius > 0) {
                const currentAngle = Math.atan2(dy, dx);
                const geometry = modifyGeometry.geometry0.clone();
                geometry.scale(
                  currentRadius / initialRadius,
                  undefined,
                  center,
                );
                geometry.rotate(currentAngle - initialAngle, center);
                modifyGeometry.geometry = geometry;
              }
            }
          }
        });

        return defaultStyle(feature);
      },
    });

    modifyScaleCtrl.on("modifystart", function (event) {
      event.features.forEach(function (feature) {
        feature.set(
          "modifyGeometry",
          { geometry: feature.getGeometry().clone() },
          true,
        );
      });
    });

    modifyScaleCtrl.on("modifyend", function (event) {
      event.features.forEach(function (feature) {
        const modifyGeometry = feature.get("modifyGeometry");
        if (modifyGeometry) {
          feature.setGeometry(modifyGeometry.geometry);
          feature.unset("modifyGeometry", true);
        }
      });
    });

    function toggleModifyShape(active = false) {
      toggleCtrl(active, modifyShapeCtrl);
    }

    function toggleModifyScale(active = false) {
      toggleCtrl(active, modifyScaleCtrl);
    }

    const translateSelectCtrl = new Select({
      layers: [workingLayer],
    });
    const translateCtrl = new Translate({
      features: translateSelectCtrl.getFeatures(),
    });

    function toggleTranslate(active = false) {
      const idx1 = map
        ?.getInteractions()
        .getArray()
        .indexOf(translateSelectCtrl);
      const idx2 = map?.getInteractions().getArray().indexOf(translateCtrl);
      if (active) {
        if (idx1 === -1 && idx2 === -1) {
          map.addInteraction(translateSelectCtrl);
          map.addInteraction(translateCtrl);
        }
      } else {
        map.removeInteraction(translateSelectCtrl);
        map.removeInteraction(translateCtrl);
      }
    }

    const tools = {
      "select-draw": {
        action: toggleSelectDraw,
        actionPossibleEnDeplacement: false,
      },
      "draw-point": {
        action: toggleDrawPoint,
      },
      "draw-line": {
        action: toggleDrawLine,
      },
      "draw-polygon": {
        action: toggleDrawShape,
      },
      "edit-shape": {
        action: toggleModifyShape,
      },
      "edit-scale-rotate": {
        action: toggleModifyScale,
      },
      "edit-translate": {
        action: toggleTranslate,
      },
    };

    return tools;
  }, [map]);

  return {
    tools,
    featureStyle,
    setFeatureStyle,
    selectedFeatures,
  };
};

const MapToolbarPerso = ({
  toggleTool: toggleToolCallback,
  activeTool,
  featureStyle,
  setFeatureStyle,
  selectedFeatures,
  workingLayer,
}: {
  toggleTool: (toolId: string) => void;
  activeTool: string;
  featureStyle: Style;
  setFeatureStyle: (style: Style) => void;
  selectedFeatures: Feature[];
  workingLayer: VectorLayer;
}) => {
  const [previewRef, setPreviewRef] = useState<HTMLDivElement>(null);
  const [showPanel, setShowPanel] = useState(false);
  const [previewStyle] = useState(featureStyle);

  const { activesKeys, handleShowClose } = useAccordionState(
    Array(3).fill(true),
  );

  const previewMap = useMemo(() => {
    const initialMap = new Map({
      controls: [],
      interactions: [],
      allOverlays: true,
      target: previewRef,
      layers: [],
      theme: false,
      view: new View({
        extent: [-25, -20, 30, 20],
        center: [0, 0],
        zoom: 2,
      }),
    });
    return initialMap;
  }, []);

  const previewLayer = useMemo(() => {
    // Alimentation des géométries de prévisualisation
    const pointFeature = new Point([-4, 8]);
    const lineFeature = new LineString([
      [-9, -12],
      [-4, -15],
      [1, -10],
    ]);
    const polygonFeature = new Polygon([
      [
        [5, 0],
        [10, -3],
        [15, 6],
        [9, 5],
        [5, 0],
      ],
    ]);

    const vectorLayer = new VectorLayer({
      source: new VectorSource({
        features: [pointFeature, lineFeature, polygonFeature].map(
          (p) => new Feature(p),
        ),
      }),
      style: featureStyle,
      extent: [-25, -20, 30, 20],
    });

    previewMap.addLayer(vectorLayer);

    return vectorLayer;
  }, []);

  useLayoutEffect(() => {
    if (previewRef) {
      previewMap.setTarget(previewRef);
    }
  }, [previewRef]);

  function updatePreview() {
    if (!previewLayer.getSource()) {
      return;
    }
    previewLayer
      .getSource()
      .getFeatures()
      .forEach((f) => {
        f.setStyle(previewStyle);
      });
  }

  function updateFeatureStyle() {
    if (selectedFeatures.length) {
      selectedFeatures.forEach((f) => {
        // On retire l'élément sinon il conservera le style de la couche
        const copy = f.clone();
        workingLayer.getSource().removeFeature(f);
        // On copie le style pour empêcher l'accès à la référence
        copy.setStyle(previewStyle.clone());
        workingLayer.getSource().addFeature(copy);
      });
    } else {
      setFeatureStyle(previewStyle);
    }
  }

  function deleteFeature() {
    selectedFeatures.forEach((f) => {
      workingLayer.getSource().removeFeature(f);
    });
  }

  return (
    <>
      <>
        <ButtonGroup>
          <ToolbarButton
            toolName={"select-draw"}
            toolIcon={<IconSelect />}
            toolLabelTooltip={"Sélectionner des éléments"}
            toggleTool={toggleToolCallback}
            activeTool={activeTool}
          />
        </ButtonGroup>
        <ButtonGroup>
          <ToolbarButton
            toolName={"draw-point"}
            toolIcon={<IconPoint />}
            toolLabelTooltip={"Dessiner un point"}
            toggleTool={toggleToolCallback}
            activeTool={activeTool}
          />
          <ToolbarButton
            toolName={"draw-line"}
            toolIcon={<IconLine />}
            toolLabelTooltip={"Dessiner une ligne"}
            toggleTool={toggleToolCallback}
            activeTool={activeTool}
          />
          <ToolbarButton
            toolName={"draw-polygon"}
            toolIcon={<IconPolygon />}
            toolLabelTooltip={"Dessiner un polygone"}
            toggleTool={toggleToolCallback}
            activeTool={activeTool}
          />
        </ButtonGroup>
        <ButtonGroup>
          <TooltipCustom
            tooltipText={"Modifier le style"}
            tooltipId={"pick-style"}
          >
            <ToggleButton
              name={"pick-style"}
              onClick={() => setShowPanel(!showPanel)}
              id={"pick-style"}
              value={"pick-style"}
              type={"radio"}
              variant={"outline-primary"}
              checked={showPanel}
              className="m-2"
            >
              <IconStyle />
            </ToggleButton>
          </TooltipCustom>
          <ToolbarButton
            toolName={"edit-shape"}
            toolIcon={<IconEdit />}
            toolLabelTooltip={"Modifier un élément"}
            toggleTool={toggleToolCallback}
            activeTool={activeTool}
          />
          <ToolbarButton
            toolName={"edit-scale-rotate"}
            toolIcon={<IconRotate />}
            toolLabelTooltip={
              "Mettre à l'échelle / effectuer une rotation sur un élément"
            }
            toggleTool={toggleToolCallback}
            activeTool={activeTool}
          />
          <ToolbarButton
            toolName={"edit-translate"}
            toolIcon={<IconMoveCarte />}
            toolLabelTooltip={"Déplacer un élément"}
            toggleTool={toggleToolCallback}
            activeTool={activeTool}
          />
          <TooltipCustom
            tooltipText={"Supprimer un élément"}
            tooltipId={"delete-features"}
          >
            <ToggleButton
              name={"delete-features"}
              onClick={deleteFeature}
              id={"delete-features"}
              value={"delete-features"}
              type={"radio"}
              variant={"outline-primary"}
              className="m-2"
            >
              <IconDelete />
            </ToggleButton>
          </TooltipCustom>
        </ButtonGroup>
        <ButtonGroup>
          <Form.Group>
            <Form.Group>
              <Form.Select
                onChange={(evt) => {
                  const format = FORMATS.filter(
                    (f) => f.code === evt.target.value,
                  )[0];
                  document.getElementById("papersheet").style.width =
                    format.value.width;
                  document.getElementById("papersheet").style.height =
                    format.value.height;
                }}
              >
                {FORMATS.map((v, k) => (
                  <option key={k} value={v.code}>
                    {v.label}
                  </option>
                ))}
              </Form.Select>
            </Form.Group>
          </Form.Group>
        </ButtonGroup>
      </>
      <Volet
        show={showPanel}
        handleClose={() => setShowPanel(false)}
        title={"Modifier le style"}
      >
        <AccordionCustom
          activesKeys={activesKeys}
          list={[
            {
              header: "Point",
              content: (
                <>
                  <Form.Group>
                    <Form.Label>Rayon</Form.Label>
                    <FormRange
                      min={1}
                      max={200}
                      step={1}
                      defaultValue={previewStyle.getImage().getRadius()}
                      onChange={(evt) => {
                        previewStyle
                          .getImage()
                          .setRadius(parseInt(evt.target.value));
                        updatePreview();
                      }}
                    />
                  </Form.Group>
                  <Form.Group>
                    <Form.Label>Rotation</Form.Label>
                    <FormRange
                      min={0}
                      max={359}
                      step={1}
                      defaultValue={previewStyle.getImage().getRotation()}
                      onChange={(evt) => {
                        previewStyle
                          .getImage()
                          .setRotation(parseInt(evt.target.value));
                        updatePreview();
                      }}
                    />
                  </Form.Group>
                </>
              ),
            },

            {
              header: "Ligne",
              content: (
                <>
                  <Form.Group>
                    <Form.Label>Couleur</Form.Label>
                    <Form.Control
                      type="color"
                      defaultValue={previewStyle
                        .getStroke()
                        .getColor()
                        .substring(0, 7)}
                      onChange={(evt) => {
                        const newColor = asString(
                          asArray(evt.target.value).concat(
                            asArray(previewStyle.getFill().getColor())[3] ?? 1,
                          ),
                        );
                        previewStyle.getStroke().setColor(newColor);
                        previewStyle
                          .getImage()
                          .getStroke(previewStyle.getStroke());
                        updatePreview();
                      }}
                    />
                  </Form.Group>
                  <Form.Group>
                    <Form.Label>Opacité</Form.Label>
                    <FormRange
                      min={0}
                      max={1}
                      step={0.01}
                      defaultValue={
                        asArray(previewStyle.getStroke().getColor())[3] ?? 1
                      }
                      onChange={(evt) => {
                        const newColor = asString(
                          asArray(previewStyle.getStroke().getColor())
                            .slice(0, 3)
                            .concat(parseFloat(evt.target.value)),
                        );
                        previewStyle.getStroke().setColor(newColor);
                        previewStyle
                          .getImage()
                          .setStroke(previewStyle.getStroke());
                        updatePreview();
                      }}
                    />
                  </Form.Group>
                  <Form.Group>
                    <Form.Label>Epaisseur</Form.Label>
                    <FormRange
                      min={0}
                      max={100}
                      step={1}
                      defaultValue={previewStyle.getStroke().getWidth()}
                      onChange={(evt) => {
                        previewStyle
                          .getStroke()
                          .setWidth(parseInt(evt.target.value));
                        previewStyle
                          .getImage()
                          .setStroke(previewStyle.getStroke());
                        updatePreview();
                      }}
                    />
                  </Form.Group>
                  <Form.Group>
                    <Form.Label>Fins de ligne</Form.Label>
                    <Form.Select
                      onChange={(evt) => {
                        previewStyle.getStroke().setLineCap(evt.target.value);
                        previewStyle
                          .getImage()
                          .setStroke(previewStyle.getStroke());
                        updatePreview();
                      }}
                    >
                      {LINE_CAPS.map((v, k) => (
                        <option key={k} value={v[0]}>
                          {v[1]}
                        </option>
                      ))}
                    </Form.Select>
                  </Form.Group>
                  <Form.Group>
                    <Form.Label>Style de ligne</Form.Label>
                    <Form.Select
                      onChange={(evt) => {
                        let dash = [1];
                        switch (evt.target.value) {
                          case "dot":
                            dash = [0, 10];
                            break;
                          case "dash":
                            dash = [10];
                            break;
                          case "dashdot":
                            dash = [10, 0, 10];
                            break;
                          case "longdash":
                            dash = [30];
                            break;
                          case "longdashdot":
                            dash = [30, 0, 30];
                            break;
                          default:
                            dash = [1];
                        }
                        previewStyle.getStroke().setLineDash(dash);
                        previewStyle
                          .getImage()
                          .setStroke(previewStyle.getStroke());
                        updatePreview();
                      }}
                    >
                      {LINE_DASHES.map((v, k) => (
                        <option key={k} value={v[0]}>
                          {v[1]}
                        </option>
                      ))}
                    </Form.Select>
                  </Form.Group>
                </>
              ),
            },

            {
              header: "Remplissage",
              content: (
                <>
                  <Form.Group>
                    <Form.Label>Couleur</Form.Label>
                    <Form.Control
                      type="color"
                      defaultValue={previewStyle
                        .getFill()
                        .getColor()
                        .substring(0, 7)}
                      onChange={(evt) => {
                        const newColor = asString(
                          asArray(evt.target.value).concat(
                            asArray(previewStyle.getFill().getColor())[3] ?? 1,
                          ),
                        );
                        previewStyle.getFill().setColor(newColor);
                        previewStyle.getImage().setFill(previewStyle.getFill());
                        updatePreview();
                      }}
                    />
                  </Form.Group>
                  <Form.Group>
                    <Form.Label>Opacité</Form.Label>
                    <FormRange
                      min={0}
                      max={1}
                      step={0.01}
                      defaultValue={
                        asArray(previewStyle.getFill().getColor())[3] ?? 1
                      }
                      onChange={(evt) => {
                        const newColor = asString(
                          asArray(previewStyle.getFill().getColor())
                            .slice(0, 3)
                            .concat(parseFloat(evt.target.value)),
                        );
                        previewStyle.getFill().setColor(newColor);
                        previewStyle.getImage().setFill(previewStyle.getFill());
                        updatePreview();
                      }}
                    />
                  </Form.Group>
                </>
              ),
            },
          ]}
          handleShowClose={handleShowClose}
        />

        <Button onClick={updateFeatureStyle}>Appliquer</Button>
        <div
          ref={setPreviewRef}
          style={{
            width: "400px",
            height: "400px",
          }}
        />
      </Volet>
    </>
  );
};

MapToolbarPerso.displayName = "MapToolbarPerso";

export default MapToolbarPerso;

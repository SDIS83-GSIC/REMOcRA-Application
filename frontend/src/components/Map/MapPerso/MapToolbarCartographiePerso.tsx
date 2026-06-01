import { Feature, MapBrowserEvent } from "ol";
import { asArray, asString } from "ol/color";
import { EventsKey } from "ol/events";
import { never, platformModifierKeyOnly } from "ol/events/condition";
import { getCenter } from "ol/extent";
import { LineString, Point, Polygon } from "ol/geom";
import {
  DragBox,
  Draw,
  Interaction,
  Modify,
  Select,
  Translate,
} from "ol/interaction";
import { ModifyEvent } from "ol/interaction/Modify";
import VectorLayer from "ol/layer/Vector";
import OLMap from "ol/Map";
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

type ToolbarPersoContextProps = {
  map: OLMap | undefined;
  cartographiePersoLayer: VectorLayer | undefined;
};

export const useToolbarPersoContext = ({
  map,
  cartographiePersoLayer,
}: ToolbarPersoContextProps) => {
  const [featureStyle, setFeatureStyle] = useState(defaultStyle);
  const [selectedFeatures, setSelectedFeatures] = useState<Feature[]>([]);

  const tools = useMemo(() => {
    if (!map || !cartographiePersoLayer) {
      return {};
    }

    const drawPointCtrl = new Draw({
      source: cartographiePersoLayer.getSource() ?? undefined,
      type: "Point",
    });

    const drawLineCtrl = new Draw({
      source: cartographiePersoLayer.getSource() ?? undefined,
      type: "LineString",
    });

    const drawShapeCtrl = new Draw({
      source: cartographiePersoLayer.getSource() ?? undefined,
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
      layers: [cartographiePersoLayer],
      toggleCondition: platformModifierKeyOnly,
    });
    selectCtrl.on("select", () => {
      setSelectedFeatures(selectCtrl.getFeatures().getArray());
    });
    const dragBoxCtrl = new DragBox({
      minArea: 25,
    });
    dragBoxCtrl.on("boxend", function (e) {
      if (!platformModifierKeyOnly(e.mapBrowserEvent)) {
        selectCtrl.getFeatures().clear();
      }
      const boxExtent = dragBoxCtrl.getGeometry().getExtent();
      const boxFeatures =
        cartographiePersoLayer.getSource()?.getFeaturesInExtent(boxExtent) ??
        [];

      selectCtrl.getFeatures().extend(boxFeatures);

      setSelectedFeatures(selectCtrl.getFeatures().getArray());
    });

    function toggleSelectDraw(active = false) {
      const idx1 = map?.getInteractions().getArray().indexOf(selectCtrl);
      const idx2 = map?.getInteractions().getArray().indexOf(dragBoxCtrl);
      if (active) {
        if (idx1 === -1 && idx2 === -1) {
          map?.addInteraction(selectCtrl);
          map?.addInteraction(dragBoxCtrl);
        }
      } else {
        map?.removeInteraction(selectCtrl);
        map?.removeInteraction(dragBoxCtrl);
      }
    }

    function toggleCtrl(active: boolean, ctrl: Interaction) {
      const idx = map?.getInteractions().getArray().indexOf(ctrl);
      if (active) {
        if (idx === -1) {
          map?.addInteraction(ctrl);
        }
      } else {
        map?.removeInteraction(ctrl);
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
      source: cartographiePersoLayer.getSource() ?? undefined,
    });

    const defaultStyle = new Modify({
      source: cartographiePersoLayer.getSource() ?? undefined,
    })
      .getOverlay()
      .getStyleFunction();

    const scaleRotatePreviewSource = new VectorSource();
    const scaleRotatePreviewLayer = new VectorLayer({
      source: scaleRotatePreviewSource,
      style: (feature) => {
        const originalFeature = feature.get("originalFeature");
        return (
          originalFeature?.getStyle?.() ?? cartographiePersoLayer.getStyle()
        );
      },
    });
    scaleRotatePreviewLayer.setZIndex(
      (cartographiePersoLayer.getZIndex() ?? 0) + 1,
    );

    const modifyScaleCtrl = new Modify({
      source: cartographiePersoLayer.getSource() ?? undefined,
      deleteCondition: never,
      insertVertexCondition: never,
    });

    // Style invisible pour masquer la feature originale
    const invisibleStyle = new Style({
      fill: new Fill({ color: "rgba(0,0,0,0.0)" }),
      stroke: new Stroke({ color: "rgba(0,0,0,0)", width: 0 }),
      image: new CircleStyle({ radius: 0 }),
    });

    modifyScaleCtrl.on("modifystart", function (event: ModifyEvent) {
      event.features.forEach(function (feature: Feature) {
        const geometry = feature.getGeometry();
        if (!geometry) {
          return;
        }
        // Récupère le style d'origine (objet ou fonction)
        let originalStyle = feature.getStyle?.();
        if (typeof originalStyle === "function") {
          originalStyle = originalStyle(feature, 1) || defaultStyle;
        }
        // Crée la preview avec le style d'origine cloné si possible
        const previewFeature = new Feature(geometry.clone());
        let previewStyle: Style =
          defaultStyle instanceof Style ? defaultStyle : new Style();
        // Vérifie que le style est bien une instance de Style (et non une fonction)
        if (originalStyle instanceof Style) {
          previewStyle = originalStyle.clone();
        } else if (defaultStyle instanceof Style) {
          previewStyle = defaultStyle.clone();
        }
        previewFeature.setStyle(previewStyle);
        scaleRotatePreviewSource.addFeature(previewFeature);
        // Stocke la géométrie et le style d'origine
        feature.set("_geometry0", geometry.clone());
        feature.set("_previewFeature", previewFeature);
        // Si la feature n'a pas de style propre, stocke null (pour restaurer le style du layer)
        feature.set(
          "_originalStyle",
          feature.getStyle ? (feature.getStyle() ?? null) : null,
        );
        // Masque la feature originale
        feature.setStyle(invisibleStyle);
      });
    });

    // Gestion du drag pour la preview (écouteur pointerdrag sur la map)
    let pointerDragListener: EventsKey | null = null;

    modifyScaleCtrl.on("modifystart", function (event: ModifyEvent) {
      const map =
        event.mapBrowserEvent?.map ||
        (event.target && event.target.getMap && event.target.getMap());
      if (map) {
        // Toujours retirer l'ancien listener si présent
        if (pointerDragListener) {
          map.un("pointerdrag", pointerDragListener.listener);
          pointerDragListener = null;
        }
        pointerDragListener = map.on(
          "pointerdrag",
          function (evt: MapBrowserEvent<PointerEvent>) {
            event.features.forEach(function (feature: Feature) {
              const previewFeature = feature.get("_previewFeature");
              const geometry0 = feature.get("_geometry0");
              if (!previewFeature || !geometry0) {
                return;
              }
              const coordinate = evt.coordinate;
              let point0 = feature.get("_point0");
              let center0 = feature.get("_center0");
              let radius0 = feature.get("_radius0");
              let angle0 = feature.get("_angle0");
              if (!point0) {
                point0 = coordinate;
                feature.set("_point0", point0);
                let center;
                const type = geometry0.getType();
                if (type === "Polygon") {
                  let x = 0,
                    y = 0,
                    i = 0;
                  const coords = geometry0.getCoordinates()[0].slice(1);
                  coords.forEach((c: number[]) => {
                    x += c[0];
                    y += c[1];
                    i++;
                  });
                  center = [x / i, y / i];
                } else if (type === "LineString") {
                  center = geometry0.getCoordinateAt(0.5);
                } else {
                  center = getCenter(geometry0.getExtent());
                }
                center0 = center;
                feature.set("_center0", center0);
                radius0 = Math.sqrt(
                  Math.pow(point0[0] - center0[0], 2) +
                    Math.pow(point0[1] - center0[1], 2),
                );
                feature.set("_radius0", radius0);
                angle0 = Math.atan2(
                  point0[1] - center0[1],
                  point0[0] - center0[0],
                );
                feature.set("_angle0", angle0);
              }
              const currentRadius = Math.sqrt(
                Math.pow(coordinate[0] - center0[0], 2) +
                  Math.pow(coordinate[1] - center0[1], 2),
              );
              const currentAngle = Math.atan2(
                coordinate[1] - center0[1],
                coordinate[0] - center0[0],
              );
              const scale = currentRadius / radius0;
              const rotate = currentAngle - angle0;
              const newGeometry = geometry0.clone();
              newGeometry.scale(scale, undefined, center0);
              newGeometry.rotate(rotate, center0);
              previewFeature.setGeometry(newGeometry);
            });
          },
        );
      }
    });

    modifyScaleCtrl.on("modifyend", function (event: ModifyEvent) {
      // Retire l'écouteur pointerdrag
      const map =
        event.mapBrowserEvent?.map ||
        (event.target && event.target.getMap && event.target.getMap());
      if (map && pointerDragListener) {
        map.un("pointerdrag", pointerDragListener.listener);
        pointerDragListener = null;
      }
      event.features.forEach(function (feature: Feature) {
        const previewFeature = feature.get("_previewFeature");
        if (previewFeature) {
          const finalGeometry = previewFeature.getGeometry();
          if (finalGeometry) {
            feature.setGeometry(finalGeometry.clone());
          }
          scaleRotatePreviewSource.removeFeature(previewFeature);
          feature.unset("_previewFeature");
        }
        // Restaure le style d'origine (null ou undefined => style du layer)
        const originalStyle = feature.get("_originalStyle");
        if (originalStyle !== undefined && originalStyle !== null) {
          feature.setStyle(originalStyle);
        } else {
          feature.setStyle(undefined);
        }
        feature.unset("_originalStyle");
        feature.unset("_geometry0");
        feature.unset("_point0");
        feature.unset("_center0");
        feature.unset("_radius0");
        feature.unset("_angle0");
      });
    });

    function toggleModifyShape(active = false) {
      toggleCtrl(active, modifyShapeCtrl);
    }

    function toggleModifyScale(active = false) {
      toggleCtrl(active, modifyScaleCtrl);
      if (active) {
        if (!map?.getLayers().getArray().includes(scaleRotatePreviewLayer)) {
          map?.addLayer(scaleRotatePreviewLayer);
        }
      } else {
        const source = cartographiePersoLayer?.getSource();
        source?.getFeatures().forEach((feature) => {
          const modifyGeometry = feature.get("modifyGeometry");
          if (modifyGeometry?.previewFeature) {
            scaleRotatePreviewSource.removeFeature(
              modifyGeometry.previewFeature,
            );
          }

          feature.unset("modifyGeometry", true);
        });

        scaleRotatePreviewSource.clear();
        map?.removeLayer(scaleRotatePreviewLayer);
      }
    }

    const translateSelectCtrl = new Select({
      layers: [cartographiePersoLayer],
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
          map?.addInteraction(translateSelectCtrl);
          map?.addInteraction(translateCtrl);
        }
      } else {
        map?.removeInteraction(translateSelectCtrl);
        map?.removeInteraction(translateCtrl);
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
  }, [map, cartographiePersoLayer, featureStyle.clone]);

  return {
    tools,
    featureStyle,
    setFeatureStyle,
    selectedFeatures,
  };
};

const MapToolbarCartographiePerso = ({
  toggleTool: toggleToolCallback,
  activeTool,
  featureStyle,
  setFeatureStyle,
  selectedFeatures,
  cartographiePersoLayer,
}: {
  toggleTool: (toolId: string) => void;
  activeTool: string | undefined;
  featureStyle: Style;
  setFeatureStyle: (style: Style) => void;
  selectedFeatures: Feature[];
  cartographiePersoLayer: VectorLayer;
}) => {
  const [previewRef, setPreviewRef] = useState<HTMLDivElement | null>(null);
  const [showPanel, setShowPanel] = useState(false);
  const [previewStyle] = useState(featureStyle);

  const { activesKeys, handleShowClose } = useAccordionState(
    Array(3).fill(true),
  );

  const previewMap = useMemo(() => {
    const initialMap = new OLMap({
      controls: [],
      interactions: [],
      target: previewRef || undefined,
      layers: [],
      view: new View({
        extent: [-25, -20, 30, 20],
        center: [0, 0],
        zoom: 2,
      }),
    });
    return initialMap;
  }, [previewRef]);

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
  }, [featureStyle, previewMap.addLayer]);

  useLayoutEffect(() => {
    if (previewRef) {
      previewMap.setTarget(previewRef);
    }
  }, [previewRef, previewMap.setTarget]);

  function updatePreview() {
    if (!previewLayer.getSource()) {
      return;
    }
    previewLayer
      ?.getSource()
      ?.getFeatures()
      .forEach((f) => {
        f.setStyle(previewStyle);
      });
  }

  function updateFeatureStyle() {
    if (selectedFeatures.length) {
      selectedFeatures.forEach((f) => {
        f.setStyle(previewStyle.clone());
      });
    } else {
      setFeatureStyle(previewStyle);
    }
  }

  function deleteFeature() {
    selectedFeatures.forEach((f) => {
      cartographiePersoLayer?.getSource()?.removeFeature(f);
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
                  const papersheet = document.getElementById("papersheet");
                  if (papersheet) {
                    papersheet.style.width = format.value.width;
                    papersheet.style.height = format.value.height;
                  }
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
                      defaultValue={(
                        previewStyle?.getImage() as CircleStyle
                      )?.getRadius()}
                      onChange={(evt) => {
                        (previewStyle?.getImage() as CircleStyle)?.setRadius(
                          parseInt(evt.target.value),
                        );
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
                      defaultValue={(
                        previewStyle?.getImage() as CircleStyle
                      )?.getRotation()}
                      onChange={(evt) => {
                        (previewStyle?.getImage() as CircleStyle)?.setRotation(
                          parseInt(evt.target.value),
                        );
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
                      defaultValue={(() => {
                        const color =
                          previewStyle?.getStroke()?.getColor() ?? "#000000";
                        if (typeof color === "string") {
                          return color.substring(0, 7);
                        }
                        if (Array.isArray(color)) {
                          return asString(color).substring(0, 7);
                        }
                        // fallback for CanvasPattern/CanvasGradient or unknown
                        return "#000000";
                      })()}
                      onChange={(evt) => {
                        const newColor = asString(
                          asArray(evt.target.value).concat(
                            (() => {
                              const fillColor = previewStyle
                                ?.getFill()
                                ?.getColor();
                              if (
                                typeof fillColor === "string" ||
                                Array.isArray(fillColor)
                              ) {
                                return asArray(fillColor)[3] ?? 1;
                              }
                              // fallback for CanvasPattern/CanvasGradient or unknown
                              return 1;
                            })(),
                          ),
                        );
                        previewStyle?.getStroke()?.setColor(newColor);
                        (previewStyle?.getImage() as CircleStyle)?.setStroke(
                          previewStyle?.getStroke(),
                        );
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
                      defaultValue={(() => {
                        const color = previewStyle?.getStroke()?.getColor();
                        if (typeof color === "string" || Array.isArray(color)) {
                          return asArray(color)[3] ?? 1;
                        }
                        // fallback for CanvasPattern/CanvasGradient or unknown
                        return 1;
                      })()}
                      onChange={(evt) => {
                        const newColor = asString(
                          (() => {
                            const color =
                              previewStyle?.getStroke()?.getColor() ??
                              "#000000";
                            if (
                              typeof color === "string" ||
                              Array.isArray(color)
                            ) {
                              return asArray(color)
                                .slice(0, 3)
                                .concat(parseFloat(evt.target.value));
                            }
                            // fallback for CanvasPattern/CanvasGradient or unknown
                            return asArray("#000000")
                              .slice(0, 3)
                              .concat(parseFloat(evt.target.value));
                          })(),
                        );
                        previewStyle?.getStroke()?.setColor(newColor);
                        (previewStyle?.getImage() as CircleStyle)?.setStroke(
                          previewStyle?.getStroke(),
                        );
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
                      defaultValue={previewStyle?.getStroke()?.getWidth()}
                      onChange={(evt) => {
                        previewStyle
                          ?.getStroke()
                          ?.setWidth(parseInt(evt.target.value));
                        (previewStyle?.getImage() as CircleStyle)?.setStroke(
                          previewStyle?.getStroke(),
                        );
                        updatePreview();
                      }}
                    />
                  </Form.Group>
                  <Form.Group>
                    <Form.Label>Fins de ligne</Form.Label>
                    <Form.Select
                      onChange={(evt) => {
                        previewStyle
                          ?.getStroke()
                          ?.setLineCap(evt.target.value as CanvasLineCap);
                        (previewStyle?.getImage() as CircleStyle)?.setStroke(
                          previewStyle?.getStroke(),
                        );
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
                        previewStyle?.getStroke()?.setLineDash(dash);
                        (previewStyle?.getImage() as CircleStyle)?.setStroke(
                          previewStyle?.getStroke(),
                        );
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
                      defaultValue={(() => {
                        const color = previewStyle?.getFill()?.getColor();
                        if (typeof color === "string") {
                          return color.substring(0, 7);
                        }
                        if (Array.isArray(color)) {
                          return asString(color).substring(0, 7);
                        }
                        // fallback for CanvasPattern/CanvasGradient or unknown
                        return "#000000";
                      })()}
                      onChange={(evt) => {
                        const newColor = asString(
                          asArray(evt.target.value).concat(
                            (() => {
                              const fillColor = previewStyle
                                ?.getFill()
                                ?.getColor();
                              if (
                                typeof fillColor === "string" ||
                                Array.isArray(fillColor)
                              ) {
                                return asArray(fillColor)[3] ?? 1;
                              }
                              // fallback for CanvasPattern/CanvasGradient or unknown
                              return 1;
                            })(),
                          ),
                        );
                        previewStyle?.getFill()?.setColor(newColor);
                        (previewStyle?.getImage() as CircleStyle)?.setFill(
                          previewStyle?.getFill(),
                        );
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
                      defaultValue={(() => {
                        const fillColor = previewStyle?.getFill()?.getColor();
                        if (
                          typeof fillColor === "string" ||
                          Array.isArray(fillColor)
                        ) {
                          return asArray(fillColor)[3] ?? 1;
                        }
                        // fallback for CanvasPattern/CanvasGradient or unknown
                        return 1;
                      })()}
                      onChange={(evt) => {
                        const newColor = asString(
                          (() => {
                            const fillColor = previewStyle
                              ?.getFill()
                              ?.getColor();
                            if (
                              typeof fillColor === "string" ||
                              Array.isArray(fillColor)
                            ) {
                              return asArray(fillColor ?? "#000000");
                            }
                            // fallback for CanvasPattern/CanvasGradient or unknown
                            return asArray("#000000");
                          })()
                            .slice(0, 3)
                            .concat(parseFloat(evt.target.value)),
                        );
                        previewStyle?.getFill()?.setColor(newColor);
                        (previewStyle?.getImage() as CircleStyle)?.setFill(
                          previewStyle?.getFill(),
                        );
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

MapToolbarCartographiePerso.displayName = "MapToolbarPerso";

export default MapToolbarCartographiePerso;

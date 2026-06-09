import { Feature, MapBrowserEvent } from "ol";
import { asArray, asString, Color } from "ol/color";
import { ColorLike, PatternDescriptor } from "ol/colorlike";
import type { EventsKey } from "ol/events";
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
import { useEffect, useLayoutEffect, useMemo, useRef, useState } from "react";
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
  featureStyle: Style;
  SetFeatureStyle: (style: Style) => void;
};

export const useToolbarPersoContext = ({
  map,
  cartographiePersoLayer,
  featureStyle,
  SetFeatureStyle,
}: ToolbarPersoContextProps) => {
  const [selectedFeatures, setSelectedFeatures] = useState<Feature[]>([]);
  // Ajout pour gestion du select
  const selectCtrlRef = useRef<Select | null>(null);
  // Ref pour le style courant
  const featureStyleRef = useRef(featureStyle);
  // Sync ref à chaque changement de style
  useEffect(() => {
    featureStyleRef.current = featureStyle;
  }, [featureStyle]);

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
      const s = featureStyleRef.current.clone();
      event.feature.setStyle(s);
      event.feature.set("_userStyle", s);
    });

    drawLineCtrl.on("drawend", (event) => {
      const s = featureStyleRef.current.clone();
      event.feature.setStyle(s);
      event.feature.set("_userStyle", s);
    });

    drawShapeCtrl.on("drawend", (event) => {
      const s = featureStyleRef.current.clone();
      event.feature.setStyle(s);
      event.feature.set("_userStyle", s);
    });

    const selectCtrl = new Select({
      layers: [cartographiePersoLayer],
      toggleCondition: platformModifierKeyOnly,
    });
    selectCtrlRef.current = selectCtrl;
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

    // Désactive select sans clear la sélection
    function toggleSelectDraw(active = false, keepSelection = false) {
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
        if (!keepSelection) {
          selectCtrl.getFeatures().clear();
          setSelectedFeatures([]);
        }
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
  }, [map, cartographiePersoLayer]);

  return {
    tools,
    featureStyle,
    SetFeatureStyle,
    selectedFeatures,
  };
};

// Utilitaire pour convertir n'importe quel format OL en hexadécimal #RRGGBB
function colorToHex(
  color: Color | ColorLike | PatternDescriptor | null | undefined,
) {
  if (!color) {
    return "#000000";
  }
  let arr: Color | ColorLike | PatternDescriptor = color;
  if (typeof color === "string") {
    if (color.startsWith("#")) {
      return color.substring(0, 7);
    }
    if (color.startsWith("rgba") || color.startsWith("rgb")) {
      arr = asArray(color);
    } else {
      arr = asArray(color);
    }
  } else if (Array.isArray(color)) {
    arr = color;
  } else {
    // CanvasPattern, CanvasGradient, PatternDescriptor, etc.
    return "#000000";
  }
  return (
    "#" +
    asArray(arr)
      .slice(0, 3)
      .map((x: number) => {
        const hex = Math.round(x).toString(16);
        return hex.length === 1 ? "0" + hex : hex;
      })
      .join("")
  );
}

const MapToolbarCartographiePerso = ({
  toggleTool: toggleToolCallback,
  activeTool,
  featureStyle,
  setFeatureStyle,
  selectedFeatures,
  cartographiePersoLayer,
  tools,
}: {
  toggleTool: (toolId: string) => void;
  activeTool: string | undefined;
  featureStyle: Style;
  setFeatureStyle: (style: Style) => void;
  selectedFeatures: Feature[];
  cartographiePersoLayer: VectorLayer;
  tools: Record<
    string,
    {
      action: (active: boolean, keepSelection?: boolean) => void;
      actionPossibleEnDeplacement?: boolean;
      keepSelectionOnDeplacement?: boolean;
    }
  >;
}) => {
  // --- STYLE DE FORMULAIRE ---
  const [formStyle, setFormStyle] = useState<Style>(() => featureStyle.clone());
  const previewRef = useRef<HTMLDivElement>(null);
  const [showPanel, setShowPanel] = useState(false);
  // Pour stocker les styles d'origine
  const originalStylesRef = useRef<Map<Feature, Style | undefined>>(new Map());

  // --- SYNCHRONISATION DU FORMULAIRE AVEC LA SÉLECTION ---
  useLayoutEffect(() => {
    if (!showPanel) {
      return;
    }
    if (selectedFeatures.length === 1) {
      const style = selectedFeatures[0].getStyle?.();
      if (style instanceof Style) {
        setFormStyle(style.clone());
      } else {
        setFormStyle(featureStyle.clone());
      }
    } else {
      setFormStyle(featureStyle.clone());
    }
  }, [selectedFeatures, featureStyle, showPanel]);

  const previewMap = useMemo(
    () =>
      new OLMap({
        controls: [],
        interactions: [],
        layers: [],
        view: new View({
          extent: [-25, -20, 30, 20],
          center: [0, 0],
          zoom: 2,
        }),
      }),
    [],
  );

  useEffect(() => {
    if (previewRef.current) {
      previewMap.setTarget(previewRef.current);
    }
    return () => {
      previewMap.setTarget(undefined);
    };
  }, [previewMap]);

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

  function updatePreview() {
    if (!previewLayer.getSource()) {
      return;
    }
    previewLayer
      ?.getSource()
      ?.getFeatures()
      .forEach((f) => {
        f.setStyle(formStyle);
      });
    // Appliquer le style de preview sur les features sélectionnées uniquement pour la preview (pas de persistance)
    if (selectedFeatures.length) {
      selectedFeatures.forEach((f) => {
        // Stocker le style d'origine si pas déjà fait
        if (!originalStylesRef.current.has(f)) {
          const style = f.getStyle?.();
          if (style === undefined || style instanceof Style) {
            originalStylesRef.current.set(f, style);
          } else {
            originalStylesRef.current.set(f, undefined);
          }
        }
        f.setStyle(formStyle.clone());
      });
    } else {
      setFeatureStyle(formStyle.clone());
    }
    // Clone pour créer une nouvelle référence et déclencher le re-render React
    setFormStyle(formStyle.clone());
  }

  function updateFeatureStyle() {
    if (selectedFeatures.length) {
      selectedFeatures.forEach((f) => {
        const s = formStyle.clone();
        f.setStyle(s);
        f.set("_userStyle", s);
        originalStylesRef.current.delete(f);
      });
      originalStylesRef.current.clear();
      // Désactive visuellement le bouton select-draw
      toggleToolCallback("select-draw");
    } else {
      setFeatureStyle(formStyle.clone());
    }
    setShowPanel(false);
  }

  function deleteFeature() {
    selectedFeatures.forEach((f) => {
      cartographiePersoLayer?.getSource()?.removeFeature(f);
    });
  }

  function handleOpenPanel() {
    // Calcule le style à afficher dans le formulaire (et en preview)
    let initialStyle: Style;
    if (selectedFeatures.length === 1) {
      const userStyle = selectedFeatures[0].get("_userStyle");
      initialStyle =
        userStyle instanceof Style ? userStyle.clone() : featureStyle.clone();
    } else {
      initialStyle = featureStyle.clone();
    }
    setFormStyle(initialStyle);

    if (selectedFeatures.length) {
      // Désactive l'outil select-draw dans la logique métier mais garde la sélection
      if (
        tools?.["select-draw"] &&
        typeof tools["select-draw"].action === "function"
      ) {
        tools["select-draw"].action(false, true);
      }
      // Appliquer le style de preview uniquement pour la preview (pas de persistance)
      selectedFeatures.forEach((f) => {
        if (!originalStylesRef.current.has(f)) {
          const style = f.getStyle?.();
          if (style === undefined || style instanceof Style) {
            originalStylesRef.current.set(f, style);
          } else {
            originalStylesRef.current.set(f, undefined);
          }
        }
        f.setStyle(initialStyle.clone());
      });
    }
    setShowPanel(true);
  }

  function handleClosePanel() {
    // Restaure le style d'origine si on ferme sans appliquer
    if (selectedFeatures.length) {
      selectedFeatures.forEach((f) => {
        if (originalStylesRef.current.has(f)) {
          const orig = originalStylesRef.current.get(f);
          f.setStyle(orig);
        }
      });
      originalStylesRef.current.clear();
      // Désactive visuellement le bouton select-draw
      toggleToolCallback("select-draw");
    }
    setShowPanel(false);
  }

  const { activesKeys, handleShowClose } = useAccordionState(
    Array(3).fill(true),
  );

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
            disabled={showPanel} // Désactive le bouton quand le volet de style est ouvert
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
              onClick={() =>
                showPanel ? handleClosePanel() : handleOpenPanel()
              }
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
        handleClose={handleClosePanel}
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
                        formStyle?.getImage() as CircleStyle | undefined
                      )?.getRadius()}
                      onChange={(evt) => {
                        (
                          formStyle?.getImage() as CircleStyle | undefined
                        )?.setRadius(parseInt(evt.target.value));
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
                      defaultValue={formStyle?.getImage()?.getRotation()}
                      onChange={(evt) => {
                        formStyle
                          ?.getImage()
                          ?.setRotation(parseInt(evt.target.value));
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
                      defaultValue={colorToHex(
                        formStyle?.getStroke()?.getColor(),
                      )}
                      onChange={(evt) => {
                        const newColor = asString(
                          asArray(evt.target.value).concat(
                            (() => {
                              const fillColor =
                                formStyle?.getFill()?.getColor() ?? "#000000";
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
                        formStyle?.getStroke()?.setColor(newColor);
                        (formStyle?.getImage() as CircleStyle)?.setStroke(
                          formStyle?.getStroke(),
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
                        const color = formStyle?.getStroke()?.getColor();
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
                              formStyle?.getStroke()?.getColor() ?? "#000000";
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
                        formStyle?.getStroke()?.setColor(newColor);
                        (formStyle?.getImage() as CircleStyle)?.setStroke(
                          formStyle?.getStroke(),
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
                      defaultValue={formStyle?.getStroke()?.getWidth()}
                      onChange={(evt) => {
                        formStyle
                          ?.getStroke()
                          ?.setWidth(parseInt(evt.target.value));
                        (formStyle?.getImage() as CircleStyle)?.setStroke(
                          formStyle?.getStroke(),
                        );
                        updatePreview();
                      }}
                    />
                  </Form.Group>
                  <Form.Group>
                    <Form.Label>Fins de ligne</Form.Label>
                    <Form.Select
                      value={(() => {
                        const stroke = formStyle.getStroke();
                        if (stroke && stroke.getLineCap) {
                          return stroke.getLineCap?.() || "round";
                        }
                        return "round";
                      })()}
                      onChange={(evt) => {
                        const stroke = formStyle.getStroke();
                        if (stroke && stroke.setLineCap) {
                          stroke.setLineCap(evt.target.value as CanvasLineCap);
                          const img = formStyle.getImage();
                          if (img && (img as CircleStyle).setStroke) {
                            (img as CircleStyle).setStroke(stroke);
                          }
                          updatePreview();
                        }
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
                      value={(() => {
                        const stroke = formStyle.getStroke();
                        if (!stroke) {
                          return "solid";
                        }
                        const dash = stroke.getLineDash?.() || [1];
                        if (
                          dash.length === 2 &&
                          dash[0] === 0 &&
                          dash[1] === 10
                        ) {
                          return "dot";
                        }
                        if (dash.length === 1 && dash[0] === 10) {
                          return "dash";
                        }
                        if (
                          dash.length === 3 &&
                          dash[0] === 10 &&
                          dash[1] === 0 &&
                          dash[2] === 10
                        ) {
                          return "dashdot";
                        }
                        if (dash.length === 1 && dash[0] === 30) {
                          return "longdash";
                        }
                        if (
                          dash.length === 3 &&
                          dash[0] === 30 &&
                          dash[1] === 0 &&
                          dash[2] === 30
                        ) {
                          return "longdashdot";
                        }
                        return "solid";
                      })()}
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
                        const stroke = formStyle.getStroke();
                        if (stroke && stroke.setLineDash) {
                          stroke.setLineDash(dash);
                          const img = formStyle.getImage();
                          if (img && (img as CircleStyle).setStroke) {
                            (img as CircleStyle).setStroke(stroke);
                          }
                          updatePreview();
                        }
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
                      defaultValue={colorToHex(
                        formStyle?.getFill()?.getColor(),
                      )}
                      onChange={(evt) => {
                        const fillColor = formStyle?.getFill()?.getColor();
                        let alpha = 1;
                        if (
                          typeof fillColor === "string" ||
                          Array.isArray(fillColor)
                        ) {
                          alpha = asArray(fillColor)[3] ?? 1;
                        }
                        const newColor = asString(
                          asArray(evt.target.value).concat(alpha),
                        );
                        formStyle?.getFill()?.setColor(newColor);
                        (formStyle?.getImage() as CircleStyle)?.setFill(
                          formStyle?.getFill(),
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
                        const fillColor = formStyle?.getFill()?.getColor();
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
                            const fillColor = formStyle?.getFill()?.getColor();
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
                        formStyle?.getFill()?.setColor(newColor);
                        (formStyle?.getImage() as CircleStyle)?.setFill(
                          formStyle?.getFill(),
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
          ref={previewRef}
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

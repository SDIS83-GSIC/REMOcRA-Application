import { shiftKeyOnly } from "ol/events/condition";
import { Point } from "ol/geom";
import { DragBox, Draw, Select } from "ol/interaction";
import Map from "ol/Map";
import { Fill, Stroke, Style } from "ol/style";
import CircleStyle from "ol/style/Circle";
import { forwardRef, useState } from "react";
import { ToggleButton } from "react-bootstrap";
import CreatePeiProjet from "../../../pages/CouvertureHydraulique/PeiProjet/CreatePeiProjet.tsx";
import Volet from "../../Volet/Volet.tsx";
import ToolbarButton from "../ToolbarButton.tsx";

const MapToolbarCouvertureHydraulique = forwardRef(
  ({
    map,
    dataPeiLayer,
    dataPeiProjetLayer,
    workingLayer,
    etudeId,
    disabledEditPeiProjet,
  }: {
    map: Map;
    dataPeiLayer: any;
    dataPeiProjetLayer: any;
    workingLayer: any;
    etudeId: string;
    disabledEditPeiProjet: boolean;
  }) => {
    const [show, setShow] = useState(false);
    const [pointPeiProjet, setPointPeiProjet] = useState<Point | null>(null);
    const [activeTool, setActiveTool] = useState<string>();

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

    /**
     * Permet de dessiner un point pour la création des PEI en projet
     */
    function toggleCreatePeiProjet(active = false) {
      const createCtrl = map
        ?.getInteractions()
        .getArray()
        .filter((c) => c instanceof Draw)[0];
      if (active) {
        if (!createCtrl) {
          const draw = new Draw({
            source: workingLayer.getSource(),
            type: "Point",
            style: (feature) => {
              const geometryType = feature.getGeometry().getType();
              if (geometryType === "Point") {
                return measureStyle;
              }
            },
          });
          draw.on("drawstart", async () => {
            // Avant de redessiner un point, on supprime les autres points, le but est d'avoir juste un seul point à la fois.
            workingLayer.getSource().clear();
          });
          draw.on("drawend", async (event) => {
            const geometry = event.feature.getGeometry();
            setPointPeiProjet(geometry);
            setShow(true);
          });
          map.addInteraction(draw);
        }
      } else {
        if (createCtrl) {
          map.removeInteraction(createCtrl);
        }
      }
    }

    const handleClose = () => setShow(false);
    const tools = {
      create: {
        action: toggleCreatePeiProjet,
      },
      select: {
        action: toggleSelect,
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

    return (
      <>
        {/**Pour la couverture hydraulique */}
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
        <ToolbarButton
          toolName={"create"}
          toolLabel={"Créer un PEI en projet"}
          toggleTool={toggleTool}
          activeTool={activeTool}
          disabled={disabledEditPeiProjet}
        />
        <Volet handleClose={handleClose} show={show} className="w-auto">
          <CreatePeiProjet
            coordonneeX={pointPeiProjet?.getFlatCoordinates()[0]}
            coordonneeY={pointPeiProjet?.getFlatCoordinates()[1]}
            srid={map.getView().getProjection().getCode().split(":").pop()}
            etudeId={etudeId}
            onSubmit={() => {
              dataPeiProjetLayer.getSource().refresh();
              handleClose();
            }}
          />
        </Volet>
      </>
    );
  },
);

MapToolbarCouvertureHydraulique.displayName = "MapToolbarCouvertureHydraulique";

export default MapToolbarCouvertureHydraulique;

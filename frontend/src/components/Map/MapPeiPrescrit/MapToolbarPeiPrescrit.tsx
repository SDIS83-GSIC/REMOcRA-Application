import { Map } from "ol";
import { Point } from "ol/geom";
import { Draw } from "ol/interaction";
import { Circle, Fill, Stroke, Style } from "ol/style";
import { forwardRef, useMemo, useState } from "react";
import { ButtonGroup } from "react-bootstrap";
import CreatePeiPrescrit from "../../../pages/PeiPrescrit/CreatePeiPrescrit.tsx";
import { IconCreate } from "../../Icon/Icon.tsx";
import Volet from "../../Volet/Volet.tsx";
import ToolbarButton from "../ToolbarButton.tsx";

const defaultStyle = new Style({
  image: new Circle({
    radius: 5,
    fill: new Fill({ color: "green" }),
    stroke: new Stroke({
      color: [255, 0, 0],
      width: 1,
    }),
  }),
});

export const useToolbarPeiPrescritContext = ({ map, workingLayer }) => {
  const [featureStyle] = useState(defaultStyle);
  const [showCreatePeiPrescrit, setShowPeiPrescrit] = useState(false);
  const handleClosePeiPrescrit = () => setShowPeiPrescrit(false);
  const [pointPeiPrescrit, setPointPeiPrescrit] = useState<Point | null>(null);

  const tools = useMemo(() => {
    if (!map) {
      return {};
    }

    const createPeiPrescrit = new Draw({
      source: workingLayer.getSource(),
      type: "Point",
      style: (feature) => {
        const geometryType = feature.getGeometry().getType();
        if (geometryType === "Point") {
          return defaultStyle;
        }
      },
    });
    createPeiPrescrit.on("drawstart", async () => {
      // Avant de redessiner un point, on supprime les autres points, le but est d'avoir juste un seul point à la fois.
      workingLayer.getSource().clear();
    });
    createPeiPrescrit.on("drawend", (event) => {
      event.feature.setStyle(featureStyle.clone());
      const geometry = event.feature.getGeometry();
      setPointPeiPrescrit(geometry);
      setShowPeiPrescrit(true);
    });

    function toggleCreatePeiPrescrit(active = false) {
      const idx = map?.getInteractions().getArray().indexOf(createPeiPrescrit);
      if (active) {
        if (idx === -1) {
          map.addInteraction(createPeiPrescrit);
        }
      } else {
        map.removeInteraction(createPeiPrescrit);
      }
    }

    const tools = {
      "create-pei-prescrits": {
        action: toggleCreatePeiPrescrit,
      },
    };

    return tools;
  }, [map, featureStyle, workingLayer]);

  return {
    tools,
    showCreatePeiPrescrit,
    handleClosePeiPrescrit,
    pointPeiPrescrit,
  };
};

const MapToolbarPeiPrescrit = forwardRef(
  ({
    map,
    dataPeiPrescritLayer,
    showCreatePeiPrescrit,
    handleClosePeiPrescrit,
    pointPeiPrescrit,
    toggleTool: toggleToolCallback,
    activeTool,
  }: {
    map?: Map;
    dataPeiPrescritLayer: any;
    showCreatePeiPrescrit: boolean;
    handleClosePeiPrescrit: () => void;
    pointPeiPrescrit: string[];
    toggleTool: (toolId: string) => void;
    activeTool: string;
  }) => {
    return (
      <>
        <ButtonGroup>
          <ToolbarButton
            toolName={"create-pei-prescrits"}
            toolIcon={<IconCreate />}
            toolLabelTooltip={"Prescrire des points d'eau"}
            toggleTool={toggleToolCallback}
            activeTool={activeTool}
          />
        </ButtonGroup>
        <Volet
          handleClose={handleClosePeiPrescrit}
          show={showCreatePeiPrescrit}
          className="w-auto"
        >
          <CreatePeiPrescrit
            coordonneeX={pointPeiPrescrit?.getFlatCoordinates()[0]}
            coordonneeY={pointPeiPrescrit?.getFlatCoordinates()[1]}
            srid={map.getView().getProjection().getCode().split(":").pop()}
            onSubmit={() => {
              dataPeiPrescritLayer.getSource().refresh();
              handleClosePeiPrescrit();
            }}
          />
        </Volet>
      </>
    );
  },
);

MapToolbarPeiPrescrit.displayName = "MapToolbarPeiPrescrit";

export default MapToolbarPeiPrescrit;

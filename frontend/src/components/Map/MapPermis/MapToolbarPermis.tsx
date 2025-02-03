import { Map } from "ol";
import { WKT } from "ol/format";
import { Point } from "ol/geom";
import { Draw } from "ol/interaction";
import { Circle, Fill, Stroke, Style } from "ol/style";
import { forwardRef, useMemo, useState } from "react";
import { ButtonGroup } from "react-bootstrap";
import url, { getFetchOptions } from "../../../module/fetch.tsx";
import { useToastContext } from "../../../module/Toast/ToastProvider.tsx";
import CreatePermis from "../../../pages/Permis/CreatePermis.tsx";
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
export const useToolbarPermisContext = ({ map, workingLayer }) => {
  const [featureStyle] = useState(defaultStyle);
  const { error: errorToast } = useToastContext();
  const [showCreatePermis, setShowPermis] = useState(false);
  const handleClosePermis = () => {
    setShowPermis(false);
    workingLayer.getSource().refresh();
  };
  const [pointPermis, setPointPermis] = useState<Point | null>(null);
  const tools = useMemo(() => {
    if (!map) {
      return {};
    }
    const createPermis = new Draw({
      source: workingLayer.getSource(),
      type: "Point",
      style: (feature) => {
        const geometryType = feature.getGeometry().getType();
        if (geometryType === "Point") {
          return defaultStyle;
        }
      },
    });
    createPermis.on("drawstart", async () => {
      // Avant de redessiner un point, on supprime les autres points, le but est d'avoir juste un seul point à la fois.
      workingLayer.getSource().clear();
    });

    createPermis.on("drawend", async (event) => {
      (
        await fetch(
          url`/api/zone-integration/check`,
          getFetchOptions({
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
              wkt: new WKT().writeFeature(event.feature),
              srid: map.getView().getProjection().getCode().split(":").pop(),
            }),
          }),
        )
      )
        .text()
        .then((text) => {
          if (text === "true") {
            event.feature.setStyle(featureStyle.clone());
            const geometry = event.feature.getGeometry();
            setPointPermis(geometry);
            setShowPermis(true);
          } else {
            workingLayer.getSource().removeFeature(event.feature);
            errorToast(text);
          }
        })
        .catch((reason) => {
          workingLayer.getSource().removeFeature(event.feature);
          errorToast(reason);
        });
    });

    function toggleCreatePermis(active = false) {
      const idx = map?.getInteractions().getArray().indexOf(createPermis);
      if (active) {
        if (idx === -1) {
          map.addInteraction(createPermis);
        }
      } else {
        map.removeInteraction(createPermis);
      }
    }
    const tools = {
      "create-permis": {
        action: toggleCreatePermis,
      },
    };
    return tools;
  }, [map, featureStyle, workingLayer, errorToast]);
  return {
    tools,
    showCreatePermis,
    handleClosePermis,
    pointPermis,
  };
};
const MapToolbarPermis = forwardRef(
  ({
    map,
    dataPermisLayer,
    showCreatePermis,
    handleClosePermis,
    pointPermis,
    toggleTool: toggleToolCallback,
    activeTool,
  }: {
    map?: Map;
    dataPermisLayer: any;
    showCreatePermis: boolean;
    handleClosePermis: () => void;
    pointPermis: string[];
    toggleTool: (toolId: string) => void;
    activeTool: string;
  }) => {
    return (
      <>
        <ButtonGroup>
          <ToolbarButton
            toolName={"create-permis"}
            toolIcon={<IconCreate />}
            toolLabelTooltip={"Créer un permis"}
            toggleTool={toggleToolCallback}
            activeTool={activeTool}
          />
        </ButtonGroup>
        <Volet
          handleClose={handleClosePermis}
          show={showCreatePermis}
          className="w-auto"
        >
          <CreatePermis
            coordonneeX={pointPermis?.getFlatCoordinates()[0]}
            coordonneeY={pointPermis?.getFlatCoordinates()[1]}
            srid={map.getView().getProjection().getCode().split(":").pop()}
            onSubmit={() => {
              dataPermisLayer.getSource().refresh();
              handleClosePermis();
            }}
          />
        </Volet>
      </>
    );
  },
);
MapToolbarPermis.displayName = "MapToolbarPermis";
export default MapToolbarPermis;

import { Map } from "ol";
import { WKT } from "ol/format";
import { Point } from "ol/geom";
import { Draw, Modify } from "ol/interaction";
import { ModifyEvent } from "ol/interaction/Modify";
import { Circle, Fill, Stroke, Style } from "ol/style";
import { useMemo, useState } from "react";
import { ButtonGroup } from "react-bootstrap";
import url, { getFetchOptions } from "../../../module/fetch.tsx";
import { useToastContext } from "../../../module/Toast/ToastProvider.tsx";
import CreatePermis from "../../../pages/Permis/CreatePermis.tsx";
import SearchPermis from "../../../pages/Permis/SearchPermis.tsx";
import UpdatePermis from "../../../pages/Permis/UpdatePermis.tsx";
import { IconCreate, IconMoveObjet, IconSearch } from "../../Icon/Icon.tsx";
import Volet from "../../Volet/Volet.tsx";
import ToolbarButton from "../ToolbarButton.tsx";
import { TooltipMapEditPermis } from "../TooltipsMap.tsx";
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
export const useToolbarPermisContext = ({
  map,
  workingLayer,
  dataPermisLayer,
}) => {
  const [featureStyle] = useState(defaultStyle);
  const { error: errorToast } = useToastContext();

  const [showSearchPermis, setShowSearchPermis] = useState(false);
  const handleCloseSearchPermis = () => setShowSearchPermis(false);

  const [showCreatePermis, setShowPermis] = useState(false);
  const handleClosePermis = () => {
    setShowPermis(false);
    workingLayer.getSource().refresh();
  };
  const [featureState, setFeatureState] = useState<any>(null);
  const [showUpdatePermis, setShowUpdatePermis] = useState(false);
  const handleCloseUpdatePermis = () => setShowUpdatePermis(false);

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

    const movePermisCtrl = new Modify({
      source: dataPermisLayer.getSource(),
    });
    movePermisCtrl.on("modifyend", async (event: ModifyEvent) => {
      if (!event.features || event.features.getLength() !== 1) {
        return;
      }
      (
        await fetch(
          url`/api/zone-integration/check`,
          getFetchOptions({
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
              wkt: new WKT().writeFeature(event.features.getArray()[0]),
              srid: map.getView().getProjection().getCode().split(":").pop(),
            }),
          }),
        )
      )
        .text()
        .then((text) => {
          if (text === "true") {
            setFeatureState(event.features.getArray()[0].getProperties());
            setShowUpdatePermis(true);
          } else {
            dataPermisLayer.getSource().refresh();
            errorToast(text);
          }
        })
        .catch((reason) => {
          dataPermisLayer.getSource().refresh();
          errorToast(reason);
        });
    });
    function toggleDeplacerPermis(active = false) {
      const idx = map?.getInteractions().getArray().indexOf(movePermisCtrl);
      if (active) {
        if (idx === -1) {
          map.addInteraction(movePermisCtrl);
        }
      } else {
        map.removeInteraction(movePermisCtrl);
      }
    }

    function toggleSearchBarPermis(active = false) {
      if (active) {
        setShowSearchPermis(true);
      }
    }

    const tools = {
      "search-permis": {
        action: toggleSearchBarPermis,
      },
      "create-permis": {
        action: toggleCreatePermis,
      },
      "deplacer-permis": {
        action: toggleDeplacerPermis,
      },
    };
    return tools;
  }, [map, featureStyle, workingLayer, errorToast, dataPermisLayer]);
  return {
    tools,

    showSearchPermis,
    handleCloseSearchPermis,

    showCreatePermis,
    handleClosePermis,
    showUpdatePermis,
    handleCloseUpdatePermis,
    pointPermis,
    featureState,
    setShowUpdatePermis,
    setFeatureState,
  };
};
const MapToolbarPermis = ({
  map,
  dataPermisLayer,

  showSearchPermis,
  handleCloseSearchPermis,

  showCreatePermis,
  handleClosePermis,

  showUpdatePermis,
  handleCloseUpdatePermis,
  featureState,

  pointPermis,
  toggleTool: toggleToolCallback,
  activeTool,
  hasRightToInteract = false,
}: {
  map?: Map;
  dataPermisLayer: any;

  showSearchPermis: boolean;
  handleCloseSearchPermis: () => void;

  showCreatePermis: boolean;
  handleClosePermis: () => void;

  showUpdatePermis: boolean;
  handleCloseUpdatePermis: () => void;
  featureState: any;

  pointPermis: string[];
  toggleTool: (toolId: string) => void;
  activeTool: string;

  hasRightToInteract: boolean;
}) => {
  return (
    <>
      <ButtonGroup>
        <ToolbarButton
          toolName={"search-permis"}
          toolIcon={<IconSearch />}
          toolLabelTooltip={"Rechercher un permis"}
          toggleTool={toggleToolCallback}
          activeTool={activeTool}
        />
        {hasRightToInteract && (
          <>
            <ToolbarButton
              toolName={"create-permis"}
              toolIcon={<IconCreate />}
              toolLabelTooltip={"Créer un permis"}
              toggleTool={toggleToolCallback}
              activeTool={activeTool}
            />
            <ToolbarButton
              toolName={"deplacer-permis"}
              toolIcon={<IconMoveObjet />}
              toolLabelTooltip={"Déplacer un permis"}
              toggleTool={toggleToolCallback}
              activeTool={activeTool}
            />
          </>
        )}
      </ButtonGroup>
      {/* Volet de Recherche */}
      <Volet
        handleClose={handleCloseSearchPermis}
        show={showSearchPermis}
        className="w-auto"
      >
        <SearchPermis />
      </Volet>
      {/* Volet de Création*/}
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
      {/* ToolTip d'Update et Delete */}
      <TooltipMapEditPermis
        map={map}
        disabledEditPermis={false}
        dataPermisLayer={dataPermisLayer}
        disabled={false}
        hasRightToInteract={hasRightToInteract}
      />
      {/* Volet d'Update suite à un déplacement */}
      <Volet
        handleClose={() => {
          handleCloseUpdatePermis();
          dataPermisLayer.getSource().refresh();
        }}
        show={showUpdatePermis}
        className="w-auto"
      >
        <UpdatePermis
          permisId={featureState?.elementId}
          coordonneeX={featureState?.geometry.getFlatCoordinates()[0]}
          coordonneeY={featureState?.geometry.getFlatCoordinates()[1]}
          srid={map.getView().getProjection().getCode().split(":")[1]}
          onSubmit={() => {
            handleCloseUpdatePermis();
          }}
        />
      </Volet>
    </>
  );
};
MapToolbarPermis.displayName = "MapToolbarPermis";
export default MapToolbarPermis;

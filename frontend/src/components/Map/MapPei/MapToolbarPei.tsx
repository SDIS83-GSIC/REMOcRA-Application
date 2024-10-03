import { useNavigate } from "react-router-dom";
import { shiftKeyOnly } from "ol/events/condition";
import { DragBox, Draw, Select } from "ol/interaction";
import { Fill, Stroke, Style } from "ol/style";
import { forwardRef, useMemo } from "react";
import CircleStyle from "ol/style/Circle";
import { ButtonGroup } from "react-bootstrap";
import url, { getFetchOptions } from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import { useAppContext } from "../../App/AppProvider.tsx";
import ToolbarButton from "../ToolbarButton.tsx";
import { useToastContext } from "../../../module/Toast/ToastProvider.tsx";
import { TYPE_DROIT } from "../../../Entities/UtilisateurEntity.tsx";
import { hasDroit } from "../../../droits.tsx";

export const useToolbarPeiContext = ({ map, workingLayer, dataPeiLayer }) => {
  const navigate = useNavigate();
  const { error: errorToast } = useToastContext();

  const tools = useMemo(() => {
    if (!map) {
      return {};
    }

    const createCtrl = new Draw({
      source: workingLayer.getSource(),
      type: "Point",
      style: (feature) => {
        const geometryType = feature.getGeometry().getType();
        if (geometryType === "Point") {
          return new Style({
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
        }
      },
    });
    createCtrl.on("drawend", async (event) => {
      const geometry = event.feature.getGeometry();
      (
        await fetch(
          url`/api/zone-integration/check`,
          getFetchOptions({
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
              wkt: `POINT(${geometry.getFlatCoordinates()[0]} ${geometry.getFlatCoordinates()[1]})`,
              srid: map.getView().getProjection().getCode(),
            }),
          }),
        )
      )
        .text()
        .then((text) => {
          if (text === "true") {
            navigate(URLS.CREATE_PEI, {
              state: {
                coordonneeX: geometry.getFlatCoordinates()[0],
                coordonneeY: geometry.getFlatCoordinates()[1],
                srid: map.getView().getProjection().getCode().split(":").pop(),
              },
            });
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

    const selectCtrl = new Select({});
    const dragBoxCtrl = new DragBox({
      style: new Style({
        stroke: new Stroke({
          color: [0, 0, 255, 1],
        }),
      }),
      minArea: 25,
    });
    dragBoxCtrl.on("boxend", function (e) {
      if (!shiftKeyOnly(e.mapBrowserEvent)) {
        selectCtrl.getFeatures().clear();
      }
      const boxExtent = dragBoxCtrl.getGeometry().getExtent();
      const boxFeatures = dataPeiLayer
        .getSource()
        .getFeaturesInExtent(boxExtent);

      selectCtrl.getFeatures().extend(boxFeatures);
    });

    function toggleSelect(active = false) {
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

    function toggleCreate(active = false) {
      const idx = map?.getInteractions().getArray().indexOf(createCtrl);
      if (active) {
        if (idx === -1) {
          map.addInteraction(createCtrl);
        }
      } else {
        map.removeInteraction(createCtrl);
      }
    }

    const tools = {
      "select-pei": {
        action: toggleSelect,
      },
      "create-pei": {
        action: toggleCreate,
      },
    };

    return tools;
  }, [map]);

  return {
    tools,
  };
};

const MapToolbarPei = forwardRef(
  ({
    toggleTool: toggleToolCallback,
    activeTool,
  }: {
    toggleTool: (toolId: string) => void;
    activeTool: string;
  }) => {
    const { user } = useAppContext();

    return (
      <ButtonGroup>
        <ToolbarButton
          toolName={"select-pei"}
          toolLabel={"Sélectionner"}
          toggleTool={toggleToolCallback}
          activeTool={activeTool}
        />
        {hasDroit(user, TYPE_DROIT.PEI_C) && (
          <ToolbarButton
            toolName={"create-pei"}
            toolLabel={"Créer"}
            toggleTool={toggleToolCallback}
            activeTool={activeTool}
          />
        )}
      </ButtonGroup>
    );
  },
);

MapToolbarPei.displayName = "MapToolbarPei";

export default MapToolbarPei;

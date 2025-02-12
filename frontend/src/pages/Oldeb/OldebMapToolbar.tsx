import { Ref, useMemo } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { ButtonGroup } from "react-bootstrap";
import VectorLayer from "ol/layer/Vector";
import { Stroke, Style } from "ol/style";
import { DragBox, Draw, Modify, Select } from "ol/interaction";
import { shiftKeyOnly } from "ol/events/condition";
import { WKT } from "ol/format";
import { Map } from "ol";
import TYPE_DROIT from "../../enums/DroitEnum.tsx";
import {
  IconCreate,
  IconEdit,
  IconSelect,
} from "../../components/Icon/Icon.tsx";
import { hasDroit } from "../../droits.tsx";
import url, { getFetchOptions } from "../../module/fetch.tsx";
import { URLS } from "../../routes.tsx";
import { useToastContext } from "../../module/Toast/ToastProvider.tsx";
import ToolbarButton from "../../components/Map/ToolbarButton.tsx";
import { useAppContext } from "../../components/App/AppProvider.tsx";

export const useToolbarOldebContext = ({
  map,
  workingLayer,
  dataOldebLayerRef,
}: {
  map: Map;
  workingLayer: VectorLayer;
  dataOldebLayerRef: Ref<VectorLayer>;
}) => {
  const navigate = useNavigate();
  const location = useLocation();
  const { success: successToast } = useToastContext();
  const { error: errorToast } = useToastContext();

  const tools = useMemo(() => {
    if (!map) {
      return {};
    }

    const createCtrl = new Draw({
      source: workingLayer.getSource(),
      type: "Polygon",
    });
    createCtrl.on("drawend", async (event) => {
      (
        await fetch(
          url`/api/zone-integration/check`,
          getFetchOptions({
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
              wkt: new WKT().writeFeature(event.feature),
              srid: map.getView().getProjection().getCode(),
            }),
          }),
        )
      )
        .text()
        .then((text) => {
          if (text === "true") {
            navigate(URLS.OLDEB_CREATE, {
              state: {
                ...location.state,
                wkt: new WKT().writeFeature(event.feature),
                epsg: map.getView().getProjection().getCode(),
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

    dragBoxCtrl.on("boxend", (e) => {
      if (!shiftKeyOnly(e.mapBrowserEvent)) {
        selectCtrl.getFeatures().clear();
      }
      const boxExtent = dragBoxCtrl.getGeometry().getExtent();

      const boxFeatures = dataOldebLayerRef.current
        ?.getSource()
        .getFeaturesInExtent(boxExtent);

      selectCtrl.getFeatures().extend(boxFeatures);
    });

    const editCtrl = new Modify({
      source: dataOldebLayerRef.current?.getSource(),
    });
    editCtrl.on("modifyend", (event) => {
      if (!event.features || event.features.getLength() !== 1) {
        return;
      }
      event.features.forEach(async (feature) => {
        (
          await fetch(
            url`/api/zone-integration/check`,
            getFetchOptions({
              method: "POST",
              headers: { "Content-Type": "application/json" },
              body: JSON.stringify({
                wkt: new WKT().writeFeature(feature),
                srid: map.getView().getProjection().getCode(),
              }),
            }),
          )
        )
          .text()
          .then((text) => {
            if (text === "true") {
              fetch(
                url`/api/oldeb/${feature.getProperties().pointId}/geometry`,
                getFetchOptions({
                  method: "PATCH",
                  headers: { "Content-Type": "application/json" },
                  body: JSON.stringify({
                    oldebId: feature.getProperties().pointId,
                    oldebGeometrie: `SRID=${map.getView().getProjection().getCode().split(":").pop()};${new WKT().writeFeature(feature)}`,
                  }),
                }),
              ).then((res) => {
                if (res.status === 200) {
                  successToast("Géométrie modifiée");
                }
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

    function toggleEdit(active = false) {
      const idx = map?.getInteractions().getArray().indexOf(editCtrl);
      if (active) {
        if (idx === -1) {
          map.addInteraction(editCtrl);
        }
      } else {
        map.removeInteraction(editCtrl);
      }
    }

    const tools = {
      "select-oldeb": {
        action: toggleSelect,
      },
      "edit-oldeb": {
        action: toggleEdit,
      },
      "create-oldeb": {
        action: toggleCreate,
      },
    };

    return tools;
  }, [map]);

  return {
    tools,
  };
};

const OldebMapToolbar = ({
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
        toolName={"select-oldeb"}
        toolIcon={<IconSelect />}
        toolLabelTooltip={"Sélectionner"}
        toggleTool={toggleToolCallback}
        activeTool={activeTool}
      />
      {hasDroit(user, TYPE_DROIT.OLDEB_C) && (
        <ToolbarButton
          toolName={"create-oldeb"}
          toolIcon={<IconCreate />}
          toolLabelTooltip={"Créer une OLD"}
          toggleTool={toggleToolCallback}
          activeTool={activeTool}
        />
      )}
      {hasDroit(user, TYPE_DROIT.OLDEB_U) && (
        <ToolbarButton
          toolName={"edit-oldeb"}
          toolIcon={<IconEdit />}
          toolLabelTooltip={"Modifier une OLD"}
          toggleTool={toggleToolCallback}
          activeTool={activeTool}
        />
      )}
    </ButtonGroup>
  );
};

OldebMapToolbar.displayName = "OldebMapToolbar";

export default OldebMapToolbar;

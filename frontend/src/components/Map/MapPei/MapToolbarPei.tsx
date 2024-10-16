import { shiftKeyOnly } from "ol/events/condition";
import { DragBox, Select } from "ol/interaction";
import Map from "ol/Map";
import { Stroke, Style } from "ol/style";
import { forwardRef, useState } from "react";
import { ToggleButton } from "react-bootstrap";

const MapToolbarPei = forwardRef(
  ({ map, dataPeiLayer }: { map: Map; dataPeiLayer: any }) => {
    const [activeTool, setActiveTool] = useState<string>();

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

    const tools = {
      select: {
        action: toggleSelect,
      },
    };

    return (
      <>
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
      </>
    );
  },
);

MapToolbarPei.displayName = "MapToolbarPei";

export default MapToolbarPei;

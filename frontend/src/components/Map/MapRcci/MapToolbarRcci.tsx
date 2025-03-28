import { Stroke, Style } from "ol/style";
import VectorLayer from "ol/layer/Vector";
import { DragBox, Draw, Modify, Select } from "ol/interaction";
import { shiftKeyOnly } from "ol/events/condition";
import { WKT } from "ol/format";
import CircleStyle from "ol/style/Circle";
import { MutableRefObject, useMemo, useRef } from "react";
import { Button, ButtonGroup } from "react-bootstrap";
import ToolbarButton from "../ToolbarButton.tsx";
import {
  IconCreate,
  IconCursorAdd,
  IconDelete,
  IconEdit,
  IconHide,
  IconMoveObjet,
  IconSelect,
} from "../../Icon/Icon.tsx";
import { hasDroit } from "../../../droits.tsx";
import TYPE_DROIT from "../../../enums/DroitEnum.tsx";
import { useAppContext } from "../../App/AppProvider.tsx";
import url, { getFetchOptions } from "../../../module/fetch.tsx";
import { useToastContext } from "../../../module/Toast/ToastProvider.tsx";
import TooltipCustom from "../../Tooltip/Tooltip.tsx";
import DeleteModal from "../../Modal/DeleteModal.tsx";
import useModal from "../../Modal/ModalUtils.tsx";
import EditModal from "../../Modal/EditModal.tsx";
import RcciForm, {
  getInitialValues,
  prepareValues,
  validationSchema,
} from "../../../pages/Admin/rcci/RcciForm.tsx";
import { refreshLayerGeoserver } from "../MapUtils.tsx";

export const useToolbarRcciContext = ({
  map,
  workingLayer,
  dataRcciLayerRef,
}) => {
  const { success: successToast, error: errorToast } = useToastContext();
  const {
    value: editValue,
    visible: editVisible,
    show: editShow,
    close: editClose,
    ref: editRef,
  } = useModal();
  const {
    value: deleteValue,
    visible: deleteVisible,
    show: deleteShow,
    close: deleteClose,
    ref: deleteRef,
  } = useModal();
  const rcciIdRef = useRef();

  const tools = useMemo(() => {
    if (!map) {
      return {};
    }

    const selectCtrl = new Select({
      style: new Style({
        image: new CircleStyle({
          radius: 16,
          stroke: new Stroke({
            color: "rgba(255, 0, 0, 0.7)",
            width: 4,
          }),
        }),
      }),
      hitTolerance: 4,
    });
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

      const boxFeatures = dataRcciLayerRef.current
        ?.getSource()
        .getFeaturesInExtent(boxExtent);

      selectCtrl.getFeatures().extend(boxFeatures);
    });

    const createCtrl = new Draw({
      source: workingLayer.getSource(),
      type: "Point",
    });
    createCtrl.on("drawend", async (event) => {
      (
        await fetch(
          url`/api/zone-integration/check`,
          getFetchOptions({
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
              geometry:
                "SRID=" +
                map.getView().getProjection().getCode().split(":").pop() +
                ";" +
                new WKT().writeFeature(event.feature),
            }),
          }),
        )
      )
        .text()
        .then((text) => {
          if (text === "true") {
            const geometry =
              "SRID=" +
              map.getView().getProjection().getCode().split(":").pop() +
              ";POINT(" +
              event.feature.getGeometry().getCoordinates()[0] +
              " " +
              event.feature.getGeometry().getCoordinates()[1] +
              ")";
            editShow({
              rcci: {
                rcciGeometrie: geometry,
              },
            });
            workingLayer.getSource().removeFeature(event.feature);
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

    const moveCtrl = new Modify({
      source: dataRcciLayerRef.current?.getSource(),
    });
    moveCtrl.on("modifyend", (event) => {
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
                geometry:
                  "SRID=" +
                  map.getView().getProjection().getCode().split(":").pop() +
                  ";" +
                  new WKT().writeFeature(feature),
              }),
            }),
          )
        )
          .text()
          .then((text) => {
            if (text === "true") {
              fetch(
                url`/api/rcci/${feature.getProperties().elementId}/geometry`,
                getFetchOptions({
                  method: "PATCH",
                  headers: { "Content-Type": "application/json" },
                  body: JSON.stringify({
                    rcciId: feature.getProperties().elementId,
                    rcciGeometrie: `SRID=${map.getView().getProjection().getCode().split(":").pop()};${new WKT().writeFeature(feature)}`,
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

    const deleteCtrl = new Select({
      layers: [dataRcciLayerRef.current],
    });
    deleteCtrl.on("select", function (evt) {
      if (!evt.selected || evt.selected.length !== 1) {
        return;
      }
      evt.selected.forEach(async function (feature) {
        deleteShow(feature.getProperties().elementId);
      });
    });

    const editCtrl = new Select({
      layers: [dataRcciLayerRef.current],
    });
    editCtrl.on("select", function (evt) {
      if (!evt.selected || evt.selected.length !== 1) {
        return;
      }
      evt.selected.forEach(async function (feature) {
        rcciIdRef.current = feature.getProperties().elementId;
        editShow();
      });
    });

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
    function toggleDelete(active = false) {
      const idx = map?.getInteractions().getArray().indexOf(deleteCtrl);
      if (active) {
        if (idx === -1) {
          map.addInteraction(deleteCtrl);
        }
      } else {
        map.removeInteraction(deleteCtrl);
      }
    }
    function toggleMove(active = false) {
      const idx = map?.getInteractions().getArray().indexOf(moveCtrl);
      if (active) {
        if (idx === -1) {
          map.addInteraction(moveCtrl);
        }
      } else {
        map.removeInteraction(moveCtrl);
      }
    }

    const tools = {
      "select-rcci": {
        action: toggleSelect,
      },
      "create-rcci": {
        action: toggleCreate,
      },
      "edit-rcci": {
        action: toggleEdit,
      },
      "delete-rcci": {
        action: toggleDelete,
      },
      "move-rcci": {
        action: toggleMove,
      },
    };

    return tools;
  }, [map]);

  return {
    tools,
    editModalRefs: {
      visible: editVisible,
      value: editValue,
      show: editShow,
      close: editClose,
      ref: editRef,
    },
    deleteModalRefs: {
      visible: deleteVisible,
      value: deleteValue,
      show: deleteShow,
      close: deleteClose,
      ref: deleteRef,
    },
    rcciIdRef,
  };
};

const MapToolbarRcci = ({
  toggleTool: toggleToolCallback,
  activeTool,
  dataRcciLayerRef,
  editModalRefs,
  deleteModalRefs,
  rcciIdRef,
  anneeCivileRef,
}: {
  toggleTool: (toolId: string) => void;
  activeTool: string;
  dataRcciLayerRef: MutableRefObject<VectorLayer | undefined>;
  rcciIdRef: MutableRefObject<string | undefined>;
  editModalRefs: {
    visible: boolean;
    show: () => void;
    close: () => void;
    ref: MutableRefObject<HTMLDivElement>;
    value: any;
  };
  deleteModalRefs: {
    visible: boolean;
    show: () => void;
    close: () => void;
    ref: MutableRefObject<HTMLDivElement>;
    value: any;
  };
  anneeCivileRef: {
    anneeCivileRef: MutableRefObject<boolean>;
    displayAnneCivile: () => void;
  };
}) => {
  const { user } = useAppContext();

  return (
    <>
      <ButtonGroup>
        <ToolbarButton
          toolName={"select-rcci"}
          toolIcon={<IconSelect />}
          toolLabelTooltip={"Sélectionner"}
          toggleTool={toggleToolCallback}
          activeTool={activeTool}
        />
      </ButtonGroup>
      {hasDroit(user, TYPE_DROIT.RCCI_A) && (
        <ButtonGroup>
          <ToolbarButton
            toolName={"create-rcci"}
            toolIcon={<IconCursorAdd />}
            toolLabelTooltip={"Créer une RCCI"}
            toggleTool={toggleToolCallback}
            activeTool={activeTool}
          />
          <TooltipCustom
            tooltipText={"Créer une RCCI (saisie X/Y)"}
            tooltipId={"create-rcci-xy"}
          >
            <Button
              name={"tool"}
              onClick={() => editModalRefs.show()}
              id={"create-rcci-xy"}
              value={"create-rcci-xy"}
              variant={"outline-primary"}
              className="m-1"
            >
              {<IconCreate />}
            </Button>
          </TooltipCustom>
          <ToolbarButton
            toolName={"edit-rcci"}
            toolIcon={<IconEdit />}
            toolLabelTooltip={"Modifier une RCCI"}
            toggleTool={toggleToolCallback}
            activeTool={activeTool}
          />
          <ToolbarButton
            toolName={"delete-rcci"}
            toolIcon={<IconDelete />}
            toolLabelTooltip={"Supprimer une RCCI"}
            toggleTool={toggleToolCallback}
            activeTool={activeTool}
          />
          <ToolbarButton
            toolName={"move-rcci"}
            toolIcon={<IconMoveObjet />}
            toolLabelTooltip={"Déplacer une RCCI"}
            toggleTool={toggleToolCallback}
            activeTool={activeTool}
          />
        </ButtonGroup>
      )}
      <ButtonGroup>
        <TooltipCustom
          tooltipText={"Masquer les départs antérieurs à l'année civile"}
          tooltipId={"hide-rcci"}
        >
          <Button
            name={"tool"}
            onClick={(e) => {
              anneeCivileRef.displayAnneCivile();
              e.target.active = anneeCivileRef.anneeCivileRef.current;
            }}
            toolName={"edit-rcci"}
            value={"hide-rcci"}
            variant={"outline-primary"}
            className="m-1"
            active={anneeCivileRef.anneeCivileRef.current}
          >
            {<IconHide />}
          </Button>
        </TooltipCustom>
      </ButtonGroup>
      {editModalRefs.visible && (
        <EditModal
          visible={editModalRefs.visible}
          closeModal={editModalRefs.close}
          query={
            url`/api/rcci/` +
            (!rcciIdRef.current ? "create" : rcciIdRef.current)
          }
          ref={editModalRefs.ref}
          validationSchema={validationSchema}
          getInitialValues={(values) =>
            getInitialValues(values, user.utilisateurId)
          }
          prepareVariables={prepareValues}
          canModify={true}
          header={""}
          value={editModalRefs.value}
          id={rcciIdRef.current}
          isMultipartFormData={true}
          onSubmit={() => {
            dataRcciLayerRef.current?.getSource().refresh();

            refreshLayerGeoserver(map);
          }}
        >
          <RcciForm />
        </EditModal>
      )}
      {deleteModalRefs.visible && (
        <DeleteModal
          visible={deleteModalRefs.visible}
          closeModal={deleteModalRefs.close}
          query={url`/api/rcci`}
          ref={deleteModalRefs.ref}
          id={deleteModalRefs.value}
          onDelete={() => {
            dataRcciLayerRef.current?.getSource().refresh();

            refreshLayerGeoserver(map);
          }}
        />
      )}
    </>
  );
};

MapToolbarRcci.displayName = "MapToolbarRcci";

export default MapToolbarRcci;

import { Map } from "ol";
import { WKT } from "ol/format";
import { Draw, Modify, Select } from "ol/interaction";
import VectorLayer from "ol/layer/Vector";
import { MutableRefObject, useMemo, useReducer, useState } from "react";
import { Button, ButtonGroup } from "react-bootstrap";
import { hasDroit } from "../../../droits.tsx";
import TYPE_DROIT from "../../../enums/DroitEnum.tsx";
import THEMATIQUE from "../../../enums/ThematiqueEnum.tsx";
import url, { getFetchOptions } from "../../../module/fetch.tsx";
import { useToastContext } from "../../../module/Toast/ToastProvider.tsx";
import CreateRcci from "../../../pages/Admin/rcci/CreateRcci.tsx";
import UpdateRcci from "../../../pages/Admin/rcci/UpdateRcci.tsx";
import { useAppContext } from "../../App/AppProvider.tsx";
import {
  IconCreate,
  IconCursorAdd,
  IconDelete,
  IconEdit,
  IconHide,
  IconMoveObjet,
} from "../../Icon/Icon.tsx";
import VoletButtonListeDocumentThematique from "../../ListeDocumentThematique/VoletButtonListeDocumentThematique.tsx";
import DeleteModal from "../../Modal/DeleteModal.tsx";
import useModal from "../../Modal/ModalUtils.tsx";
import TooltipCustom from "../../Tooltip/Tooltip.tsx";
import Volet from "../../Volet/Volet.tsx";
import { refreshLayerGeoserver } from "../MapUtils.tsx";
import ToolbarButton from "../ToolbarButton.tsx";

export const useToolbarRcciContext = ({
  map,
  workingLayer,
  dataRcciLayerRef,
}) => {
  const { success: successToast, error: errorToast } = useToastContext();
  const [creationRcciGeometrie, setCreationRcciGeometrie] = useState<{
    rcci: { rcciGeometrie: string };
  } | null>(null);

  const handleCloseCreationRcci = () => setCreationRcciGeometrie(null);

  const {
    value: deleteValue,
    visible: deleteVisible,
    show: deleteShow,
    close: deleteClose,
    ref: deleteRef,
  } = useModal();

  const [rcciModifieId, setRcciModifieId] = useState<string | null>(null);

  const tools = useMemo(() => {
    if (!map) {
      return {};
    }

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

            setCreationRcciGeometrie({
              rcci: {
                rcciGeometrie: geometry,
              },
            });

            refreshLayerGeoserver(map);
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

    let moveCtrl = dataRcciLayerRef.current
      ? new Modify({
          source: dataRcciLayerRef.current.getSource(),
        })
      : undefined;

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

    let deleteCtrl = dataRcciLayerRef.current
      ? new Select({
          layers: [dataRcciLayerRef.current],
        })
      : undefined;

    let editCtrl = dataRcciLayerRef.current
      ? new Select({
          layers: [dataRcciLayerRef.current],
        })
      : undefined;

    function toggleEdit(active = false) {
      if (!editCtrl) {
        editCtrl = dataRcciLayerRef.current
          ? new Select({
              layers: [dataRcciLayerRef.current],
            })
          : undefined;
      }

      if (!editCtrl || !map) {
        return;
      }

      const hasSelectListener =
        (editCtrl?.getListeners("select")?.length ?? 0) > 0;

      if (!hasSelectListener) {
        editCtrl?.on("select", function (evt) {
          if (!evt.selected || evt.selected.length !== 1) {
            return;
          }
          evt.selected.forEach(async function (feature) {
            setRcciModifieId(feature.getProperties().elementId);
          });
          // Supprimer la sélection après avoir sélectionné l'élément
          if (editCtrl) {
            editCtrl.getFeatures().clear();
          }
        });
      }

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
      if (!deleteCtrl) {
        deleteCtrl = dataRcciLayerRef.current
          ? new Select({
              layers: [dataRcciLayerRef.current],
            })
          : undefined;
      }
      if (!deleteCtrl || !map) {
        return;
      }

      const hasSelectListener =
        (deleteCtrl?.getListeners("select")?.length ?? 0) > 0;

      if (!hasSelectListener) {
        deleteCtrl?.on("select", function (evt) {
          if (!evt.selected || evt.selected.length !== 1) {
            return;
          }
          evt.selected.forEach(async function (feature) {
            deleteShow(feature.getProperties().elementId);
          });
        });
      }

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
      if (!moveCtrl) {
        moveCtrl = dataRcciLayerRef.current
          ? new Modify({
              source: dataRcciLayerRef.current.getSource(),
            })
          : undefined;
      }

      if (!moveCtrl || !map) {
        return;
      }

      const hasModifyEndListener =
        (moveCtrl?.getListeners("modifyend")?.length ?? 0) > 0;

      if (!hasModifyEndListener) {
        moveCtrl?.on("modifyend", (event) => {
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
                      refreshLayerGeoserver(map);
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
      }
      const idx = map.getInteractions().getArray().indexOf(moveCtrl);
      if (active) {
        if (idx === -1) {
          moveCtrl.setActive(true);
          map.addInteraction(moveCtrl);
        }
      } else {
        moveCtrl.setActive(false);
        map.removeInteraction(moveCtrl);
      }
    }

    const tools = {
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
        actionPossibleEnDeplacement: false,
      },
    };

    return tools;
  }, [map, dataRcciLayerRef.current, workingLayer]);

  return {
    tools,
    creationRcciGeometrie,
    handleCloseCreationRcci,
    deleteModalRefs: {
      visible: deleteVisible,
      value: deleteValue,
      show: deleteShow,
      close: deleteClose,
      ref: deleteRef,
    },
    rcciModifieId,
    setRcciModifieId,
  };
};

const MapToolbarRcci = ({
  map,
  toggleTool: toggleToolCallback,
  activeTool,
  dataRcciLayerRef,
  creationRcciGeometrie,
  handleCloseCreationRcci,
  deleteModalRefs,
  anneeCivileRef,
  rcciModifieId,
  setRcciModifieId,
}: {
  toggleTool: (toolId: string) => void;
  map: Map;
  activeTool: string;
  dataRcciLayerRef: MutableRefObject<VectorLayer | undefined>;
  creationRcciGeometrie: { rcci: { rcciGeometrie: string } } | null;
  handleCloseCreationRcci: () => void;
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
  rcciModifieId: string | undefined;
  setRcciModifieId: (id: string | null) => void;
}) => {
  const { user } = useAppContext();
  const [, forceUpdate] = useReducer((x) => x + 1, 0);
  const [creationSansGeom, setCreationSansGeom] = useState(false);
  return (
    <>
      <ButtonGroup>
        <VoletButtonListeDocumentThematique
          codeThematique={THEMATIQUE.RCI}
          titreVolet="Liste des documents liés aux RCCI"
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
              onClick={() => setCreationSansGeom(true)}
              id={"create-rcci-xy"}
              value={"create-rcci-xy"}
              variant={"outline-primary"}
              className="m-2"
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
            onClick={() => {
              anneeCivileRef.displayAnneCivile();
              forceUpdate(); // forcer rerender pour relire le ref
            }}
            toolName={"edit-rcci"}
            value={"hide-rcci"}
            variant={"outline-primary"}
            className="m-2"
            active={anneeCivileRef.anneeCivileRef.current}
          >
            {<IconHide />}
          </Button>
        </TooltipCustom>
      </ButtonGroup>

      <Volet
        handleClose={() => {
          handleCloseCreationRcci();
          setCreationSansGeom(false);
        }}
        show={creationRcciGeometrie !== null || creationSansGeom}
        className="w-auto"
      >
        <CreateRcci
          creationRcciGeometrie={creationRcciGeometrie}
          onSubmit={() => {
            dataRcciLayerRef.current?.getSource().refresh();
            refreshLayerGeoserver(map);
            handleCloseCreationRcci();
            setCreationSansGeom(false);
          }}
        />
      </Volet>
      <Volet
        handleClose={() => {
          setRcciModifieId(null);
        }}
        show={rcciModifieId !== null}
        className="w-auto"
        backdrop={true}
      >
        <UpdateRcci
          rcciId={rcciModifieId!}
          onSubmit={() => {
            dataRcciLayerRef.current?.getSource().refresh();
            -refreshLayerGeoserver(map);
            setRcciModifieId(null);
          }}
        />
      </Volet>

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

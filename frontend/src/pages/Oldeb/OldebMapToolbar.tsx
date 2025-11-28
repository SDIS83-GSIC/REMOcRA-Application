import { Map } from "ol";
import { platformModifierKeyOnly } from "ol/events/condition";
import { WKT } from "ol/format";
import { DragBox, Draw, Modify, Select } from "ol/interaction";
import VectorLayer from "ol/layer/Vector";
import { Stroke, Style } from "ol/style";
import { useMemo, useState } from "react";
import { Button, ButtonGroup, Col, Row } from "react-bootstrap";
import { useLocation, useNavigate } from "react-router-dom";
import { useAppContext } from "../../components/App/AppProvider.tsx";
import {
  IconCreate,
  IconEdit,
  IconOldeb,
  IconTransformGeometrie,
} from "../../components/Icon/Icon.tsx";
import ToolbarButton from "../../components/Map/ToolbarButton.tsx";
import { hasDroit } from "../../droits.tsx";
import TYPE_DROIT from "../../enums/DroitEnum.tsx";
import url, { getFetchOptions } from "../../module/fetch.tsx";
import { useToastContext } from "../../module/Toast/ToastProvider.tsx";
import { URLS } from "../../routes.tsx";
import THEMATIQUE from "../../enums/ThematiqueEnum.tsx";
import VoletButtonListeDocumentThematique from "../../components/ListeDocumentThematique/VoletButtonListeDocumentThematique.tsx";
import { refreshLayerGeoserver } from "../../components/Map/MapUtils.tsx";
import Volet from "../../components/Volet/Volet.tsx";
import TooltipCustom from "../../components/Tooltip/Tooltip.tsx";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import DeleteButtonWithModal from "../../components/Button/DeleteButtonWithModal.tsx";
import OldebUpdate from "./OldebUpdate.tsx";

export const useToolbarOldebContext = ({
  map,
  workingLayer,
  dataOldebLayer,
}: {
  map: Map;
  workingLayer: VectorLayer;
  dataOldebLayer: VectorLayer;
}) => {
  const navigate = useNavigate();
  const location = useLocation();
  const { success: successToast } = useToastContext();
  const { error: errorToast } = useToastContext();
  const [editOldebs, setEditOldebs] = useState<string | null>(null);

  function closeEdit() {
    workingLayer.getSource().clear();
    setEditOldebs(null);
  }

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
            localStorage.setItem(
              "mapContent",
              JSON.stringify({
                wkt: new WKT().writeFeature(event.feature),
                epsg: map.getView().getProjection().getCode(),
              }),
            );
            navigate(URLS.OLDEB_CREATE, {
              state: {
                ...location.state,
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

    const selectCtrl = new Select({
      toggleCondition: platformModifierKeyOnly,
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
      if (!platformModifierKeyOnly(e.mapBrowserEvent)) {
        selectCtrl.getFeatures().clear();
      }
      const boxExtent = dragBoxCtrl.getGeometry().getExtent();

      const boxFeatures = dataOldebLayer
        ?.getSource()
        .getFeaturesInExtent(boxExtent);

      selectCtrl.getFeatures().extend(boxFeatures);
    });

    const editGeometrieCtrl = new Modify({
      source: dataOldebLayer.getSource(),
    });
    editGeometrieCtrl.on("modifyend", (event) => {
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
                url`/api/oldeb/${feature.getProperties().elementId}/geometry`,
                getFetchOptions({
                  method: "PATCH",
                  headers: { "Content-Type": "application/json" },
                  body: JSON.stringify({
                    oldebId: feature.getProperties().elementId,
                    oldebGeometrie: `SRID=${map.getView().getProjection().getCode().split(":").pop()};${new WKT().writeFeature(feature)}`,
                  }),
                }),
              ).then((res) => {
                if (res.status === 200) {
                  successToast("Géométrie modifiée");
                  refreshLayerGeoserver(map);
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

    const editSelectCtrl = new Draw({
      source: workingLayer.getSource(),
      type: "Point",
    });
    editSelectCtrl.on("drawstart", async () => {
      // on clear le workingLayer avant de dessiner un nouveau point
      workingLayer.getSource().clear();
    });

    editSelectCtrl.on("drawend", async (event) => {
      const pointGeom = event.feature.getGeometry();

      const workingSource = workingLayer.getSource();
      if (workingSource) {
        workingSource.removeFeature(event.feature);
      }
      const tolerance = 5;
      const pixel = map.getPixelFromCoordinate(pointGeom.getCoordinates());
      const featuresAtPixel: any[] = [];
      map.forEachFeatureAtPixel(
        pixel,
        (feature, layer) => {
          if (layer === dataOldebLayer) {
            featuresAtPixel.push({
              oldebId: feature.getProperties().elementId,
              properties: feature.getProperties().propertiesToDisplay,
            });
          }
        },
        {
          hitTolerance: tolerance,
        },
      );
      if (featuresAtPixel.length > 0) {
        setEditOldebs(JSON.stringify(featuresAtPixel));
      } else {
        workingLayer.getSource().clear();
        errorToast("Aucune OLDEB trouvée");
      }
    });

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

    function toggleEditGeometrie(active = false) {
      const idx = map?.getInteractions().getArray().indexOf(editGeometrieCtrl);
      if (active) {
        if (idx === -1) {
          map.addInteraction(editGeometrieCtrl);
        }
      } else {
        map.removeInteraction(editGeometrieCtrl);
      }
    }

    function toggleEdit(active = false) {
      const idx = map?.getInteractions().getArray().indexOf(editSelectCtrl);
      if (active) {
        if (idx === -1) {
          map.addInteraction(editSelectCtrl);
        }
      } else {
        map.removeInteraction(editSelectCtrl);
      }
    }

    const tools = {
      "edit-geometrie-oldeb": {
        action: toggleEditGeometrie,
      },
      "create-oldeb": {
        action: toggleCreate,
      },
      "edit-oldeb": {
        action: toggleEdit,
      },
    };

    return tools;
  }, [map, dataOldebLayer]);

  return {
    tools,
    editOldebs,
    closeEdit,
  };
};

const OldebMapToolbar = ({
  toggleTool: toggleToolCallback,
  activeTool,
  editOldebs,
  closeEdit,
  dataOldebLayer,
  map,
}: {
  toggleTool: (toolId: string) => void;
  activeTool: string;
  editOldebs: string | null;
  closeEdit: () => void;
  dataOldebLayer: VectorLayer;
  map: Map;
}) => {
  const { user } = useAppContext();

  const [oldebIdModifie, setOldebIdModifie] = useState(null);

  let oldebsArray: { oldebId: string; properties: string }[] = [];
  if (editOldebs) {
    oldebsArray = JSON.parse(editOldebs);
  }

  return (
    <ButtonGroup>
      <VoletButtonListeDocumentThematique
        codeThematique={THEMATIQUE.OLDEBS}
        titreVolet="Liste des documents liés aux OLDEB"
      />
      {hasDroit(user, TYPE_DROIT.OLDEB_C) && (
        <ToolbarButton
          toolName={"create-oldeb"}
          toolIcon={<IconCreate />}
          toolLabelTooltip={"Créer une OLDEB"}
          toggleTool={toggleToolCallback}
          activeTool={activeTool}
        />
      )}
      {hasDroit(user, TYPE_DROIT.OLDEB_U) && (
        <ToolbarButton
          toolName={"edit-geometrie-oldeb"}
          toolIcon={<IconTransformGeometrie />}
          toolLabelTooltip={"Modifier la géométrie d'une OLDEB"}
          toggleTool={toggleToolCallback}
          activeTool={activeTool}
        />
      )}
      {(hasDroit(user, TYPE_DROIT.OLDEB_U) ||
        hasDroit(user, TYPE_DROIT.OLDEB_D)) && (
        <ToolbarButton
          toolName={"edit-oldeb"}
          toolIcon={<IconEdit />}
          toolLabelTooltip={"Modifier une OLDEB"}
          toggleTool={toggleToolCallback}
          activeTool={activeTool}
        />
      )}

      <Volet
        handleClose={() => closeEdit()}
        show={oldebsArray.length > 0 && oldebIdModifie === null}
        className="w-auto"
      >
        <PageTitle
          icon={<IconOldeb />}
          displayReturnButton={false}
          title={"Modifier une OLDEB"}
        />
        {oldebsArray.map((oldebs) => (
          <div key={oldebs.oldebId} className="card bg-secondary mb-3 rounded">
            <div className="card-body">
              <Row className="justify-content-center align-items-center">
                <Col
                  xs={8}
                  className="card-text"
                  dangerouslySetInnerHTML={{ __html: oldebs.properties }}
                />
                <Col
                  xs={1}
                  className="d-flex justify-content-center align-items-center"
                >
                  <TooltipCustom
                    tooltipText={"Modifier cette OLDEB"}
                    tooltipId={oldebs.oldebId}
                  >
                    <Button
                      variant={"link"}
                      className={"p-0 m-0 text-decoration-none text-info"}
                      onClick={() => setOldebIdModifie(oldebs.oldebId)}
                    >
                      <IconEdit />
                    </Button>
                  </TooltipCustom>
                </Col>
                <Col className="d-flex justify-content-center align-items-center">
                  <TooltipCustom
                    tooltipText={"Supprimer cette OLDEB"}
                    tooltipId={oldebs.oldebId}
                  >
                    <DeleteButtonWithModal
                      path={`/api/oldeb/${oldebs.oldebId}`}
                      disabled={!hasDroit(user, TYPE_DROIT.OLDEB_D)}
                      title={false}
                      variant="link"
                      className="text-decoration-none text-danger"
                      reload={() => {
                        closeEdit();
                        dataOldebLayer.getSource()?.refresh();
                        refreshLayerGeoserver(map);
                      }}
                    />
                  </TooltipCustom>
                </Col>
              </Row>
            </div>
          </div>
        ))}
      </Volet>

      <Volet
        handleClose={() => setOldebIdModifie(null)}
        show={oldebIdModifie !== null}
        className="w-auto"
      >
        <OldebUpdate
          oldebIdCarte={oldebIdModifie}
          onClose={() => {
            closeEdit();
            dataOldebLayer.getSource()?.refresh();
            refreshLayerGeoserver(map);
            setOldebIdModifie(null);
          }}
        />
      </Volet>
    </ButtonGroup>
  );
};

OldebMapToolbar.displayName = "OldebMapToolbar";

export default OldebMapToolbar;

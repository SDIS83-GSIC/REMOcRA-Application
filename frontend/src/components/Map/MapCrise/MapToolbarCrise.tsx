import { WKT } from "ol/format";
import { fromExtent } from "ol/geom/Polygon";
import { Draw, Modify } from "ol/interaction";
import Map from "ol/Map";
import { Fill, Stroke, Style } from "ol/style";
import CircleStyle from "ol/style/Circle";
import { forwardRef, Key, useEffect, useMemo, useState } from "react";
import { Button, Col, Dropdown, Row } from "react-bootstrap";
import { hasDroit, isAuthorized } from "../../../droits.tsx";
import SOUS_TYPE_TYPE_GEOMETRIE from "../../../enums/Signalement/SousTypeTypeGeometrie.tsx";
import TYPE_DROIT from "../../../enums/DroitEnum.tsx";
import url, { getFetchOptions } from "../../../module/fetch.tsx";
import { useToastContext } from "../../../module/Toast/ToastProvider.tsx";
import AddTitleForm, {
  getInitialValue,
  prepareVariables,
  ValidationSchema,
} from "../../../pages/ModuleCrise/Crise/AddTitleForm.tsx";
import CreateListDocument from "../../../pages/ModuleCrise/Document/createListDocument.tsx";
import CreateEvenement from "../../../pages/ModuleCrise/Evenement/createEvenement.tsx";
import CreateListEvenement from "../../../pages/ModuleCrise/Evenement/CreateListEvenement.tsx";
import { useAppContext } from "../../App/AppProvider.tsx";
import { useGet } from "../../Fetch/useFetch.tsx";
import {
  IconCamera,
  IconDocument,
  IconEvent,
  IconLine,
  IconList,
  IconMoveObjet,
  IconCriseRapportPersonnalise,
  IconPoint,
  IconPolygon,
} from "../../Icon/Icon.tsx";
import EditModal from "../../Modal/EditModal.tsx";
import useModal from "../../Modal/ModalUtils.tsx";
import TooltipCustom from "../../Tooltip/Tooltip.tsx";
import Volet from "../../Volet/Volet.tsx";
import { desactiveMoveMap, refreshLayerGeoserver } from "../MapUtils.tsx";
import ToolbarButton from "../ToolbarButton.tsx";
import { TooltipMapEditEvenement } from "../TooltipsMap.tsx";
import ToponymieTypeBarre from "../ToponymieTypeBarre.tsx";
import ExecuteCriseRapportPersonnalise from "../../../pages/ModuleCrise/Document/ExecuteCriseRapportPersonnalise.tsx";

const drawStyle = new Style({
  fill: new Fill({
    color: "rgba(255, 255, 255, 0.2)",
  }),
  stroke: new Stroke({
    color: "rgba(0, 0, 0, 0.5)",
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

export const useToolbarCriseContext = ({
  map,
  workingLayer,
  dataEvenementLayer,
}: {
  map: any;
  workingLayer: any;
  dataEvenementLayer: any;
}) => {
  const [showCreateEvent, setShowCreateEvent] = useState(false);
  const [showListEvent, setShowListEvent] = useState(false);
  const [showListDocument, setShowListDocument] = useState(false);
  const { success: successToast, error: errorToast } = useToastContext();
  const [showPersonalReports, setShowPersonalReports] = useState(false);
  const [geometryReportCode, setGeometryReportCode] = useState<string>("");

  const handleCloseEvent = () => {
    setShowListDocument(false);
    setShowListEvent(false);
    setShowCreateEvent(false);
    setShowPersonalReports(false);
    workingLayer.getSource().clear();
    setGeometryElement(null);
  };

  const [showTracee, setShowTracee] = useState(false);
  const handleCloseTracee = () => setShowTracee(false);
  const [listeEventId] = useState<string[]>([]);
  const [sousTypeElement, setSousTypeElement] = useState<string | null>(null);
  const [geometryElement, setGeometryElement] = useState<string | null>(null);
  const [reportGeometryElement, setReportGeometryElement] = useState<
    Record<string, string>
  >({});
  const [wkt, setWkt] = useState<string | null>(null); // état pour le wkt

  useEffect(() => {
    if (geometryReportCode && wkt) {
      setReportGeometryElement((prev) => {
        return { ...(prev ?? {}), [geometryReportCode]: wkt };
      });
    }
  }, [geometryReportCode, wkt]);

  const tools = useMemo(() => {
    if (!map) {
      return {};
    }

    /**
     * Fonction pour modifier la géométrie d'un évènement sur la carte
     */
    const moveCtrl = new Modify({
      source: dataEvenementLayer.getSource(),
    });
    moveCtrl.on("modifyend", (event) => {
      if (!event.features || event.features.getLength() !== 1) {
        dataEvenementLayer.getSource().clear();
        dataEvenementLayer.getSource().refresh();
        refreshLayerGeoserver(map);
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
                url`/api/crise/${feature.getProperties().elementId}/geometry`,
                getFetchOptions({
                  method: "PATCH",
                  headers: { "Content-Type": "application/json" },
                  body: JSON.stringify({
                    eventId: feature.getProperties().elementId,
                    eventGeometrie: `SRID=${map.getView().getProjection().getCode().split(":").pop()};${new WKT().writeFeature(feature)}`,
                  }),
                }),
              ).then((res) => {
                if (res.status === 200) {
                  successToast("Géométrie modifiée");
                }
              });
            } else {
              dataEvenementLayer.getSource().clear();
              dataEvenementLayer.getSource().refresh();
              errorToast(text);
            }
          })
          .catch((reason) => {
            dataEvenementLayer.getSource().clear();
            dataEvenementLayer.getSource().refresh();
            errorToast(reason);
          });
      });
      refreshLayerGeoserver(map);
    });

    /**
     * Fonction pour dessiner des types de géométries
     */
    function createDrawInteraction(
      geometryType: string,
      isReport: boolean = false,
    ) {
      const drawCtrl = new Draw({
        source: workingLayer.getSource(),
        type: geometryType,
        style: (feature) => {
          const featureType = feature.getGeometry()?.getType();
          if (featureType === geometryType) {
            return drawStyle;
          }
        },
      });
      drawCtrl.on("drawend", async (event) => {
        const wkt = `SRID=${map.getView().getProjection().getCode().split(":").pop()};${new WKT().writeFeature(event.feature)}`;

        setGeometryElement(wkt);
        setWkt(wkt);

        if (!isReport) {
          setShowCreateEvent(true);
        }
      });
      drawCtrl.on("drawstart", async () => {
        // Avant de redessiner un point, on supprime les autres points
        if (!isReport) {
          workingLayer.getSource().clear();
        }
      });
      return drawCtrl;
    }

    /**
     * Fonction pour activer ou désactiver une interaction
     */
    function toggleInteraction(active: boolean = false, draw: Draw | Modify) {
      const idx = map?.getInteractions().getArray().indexOf(draw);
      if (active) {
        if (idx === -1) {
          map.addInteraction(draw);
        }
        if (draw instanceof Modify) {
          desactiveMoveMap(map);
        }
      } else if (!active && idx !== -1) {
        map.removeInteraction(draw);
      }
    }

    function toggleReportInteraction(
      active: boolean = false,
      draw: Draw | Modify,
    ) {
      const idx = map?.getInteractions().getArray().indexOf(draw);
      if (active) {
        if (idx === -1) {
          map.addInteraction(draw);
        }
        if (draw instanceof Modify) {
          desactiveMoveMap(map);
        }
      }
    }

    const drawPoint = createDrawInteraction("Point");
    const drawPolygon = createDrawInteraction("Polygon"); // remplit
    const drawLineString = createDrawInteraction("LineString");

    const drawReportPoint = createDrawInteraction("Point", true);
    const drawReportPolygon = createDrawInteraction("Polygon", true);
    const drawReportLineString = createDrawInteraction("LineString", true);

    function toggleCreateReportPoint(active = false) {
      toggleReportInteraction(active, drawReportPoint);
    }
    function toggleCreateReportPolygon(active = false) {
      toggleReportInteraction(active, drawReportPolygon);
    }
    function toggleCreateReportLinestring(active = false) {
      toggleReportInteraction(active, drawReportLineString);
    }

    /**
     * Permet de dessiner un point pour la création des évènements
     */
    function toggleCreatePointEvenement(active = false) {
      toggleInteraction(active, drawPoint);
    }

    /**
     * Permet de dessiner un Polygon pour la création des évènements
     */
    function toggleCreatePolygonEvenement(active = false) {
      toggleInteraction(active, drawPolygon);
    }

    /**
     * Permet de dessiner une linestring pour la création des évènements
     */
    function toggleCreateLinestringEvenement(active = false) {
      toggleInteraction(active, drawLineString);
    }

    /**
     * Permet de déplacer un évènement
     */
    function toggleMoveEvent(active = false) {
      toggleInteraction(active, moveCtrl);
    }

    const tools = {
      "create-report-point": {
        action: toggleCreateReportPoint,
      },
      "create-report-polygon": {
        action: toggleCreateReportPolygon,
      },
      "create-report-linestring": {
        action: toggleCreateReportLinestring,
      },

      "create-point": {
        action: toggleCreatePointEvenement,
      },
      "create-polygon": {
        action: toggleCreatePolygonEvenement,
      },
      "create-linestring": {
        action: toggleCreateLinestringEvenement,
      },
      "move-event": {
        action: toggleMoveEvent,
      },
    };
    return tools;
  }, [dataEvenementLayer, errorToast, map, successToast, workingLayer]);

  return {
    tools,
    handleCloseEvent,
    showCreateEvent,
    showListEvent,
    showListDocument,
    setShowListDocument,
    setShowListEvent,
    setShowCreateEvent,
    handleCloseTracee,
    showTracee,
    geometryElement,
    reportGeometryElement,
    listeEventId,
    setSousTypeElement,
    sousTypeElement,
    showPersonalReports,
    setShowPersonalReports,
    setGeometryReportCode,
  };
};

const MapToolbarCrise = forwardRef(
  ({
    map,
    state,
    criseId,
    geometryElement,
    reportGeometryElement,
    handleCloseEvent,
    showCreateEvent,
    showListEvent,
    dataCriseLayer,
    showListDocument,
    toggleTool: toggleToolCallback,
    activeTool,
    setSousTypeElement,
    setShowListDocument,
    setShowListEvent,
    setShowCreateEvent,
    setShowPersonalReports,
    setGeometryReportCode,
    showPersonalReports,
    variant = "primary",
  }: {
    map?: Map;
    workingLayer: any;
    criseId: string;
    state: string;
    disabledEditEvent: boolean;
    activeTool: string;
    handleCloseEvent: () => void;
    showCreateEvent: boolean;
    showListEvent: boolean;
    showListDocument: boolean;
    showPersonalReports: boolean;
    setGeometryElement: (object: object) => void;
    setReportGeometryElement: (object: object) => void;
    setShowListDocument: (b: boolean) => void;
    setShowListEvent: (b: boolean) => void;
    setShowCreateEvent: (b: boolean) => void;
    setGeometryReportCode: (code: string) => void;
    setShowPersonalReports: (b: boolean) => void;
    geometryElement: string | null;
    reportGeometryElement: any;
    toggleTool: (toolId: string) => void;
    dataCriseLayer: any;
    setSousTypeElement: (object: object) => void;
    variant: string;
  }) => {
    const { user } = useAppContext();
    const { visible, show, close } = useModal();

    const getMapGeometry = (): string => {
      return (
        `SRID=${map!.getView().getProjection().getCode().split(":").pop()};` +
        new WKT().writeGeometry(fromExtent(map!.getView().calculateExtent()))
      );
    };

    const captureMap = (): File => {
      map!.updateSize();
      const canvas = document.createElement("canvas");
      const size = map!.getSize() as [number, number];

      canvas.width = size[0];
      canvas.height = size[1];

      const ctx = canvas.getContext("2d");

      document
        .querySelectorAll<HTMLCanvasElement>(".ol-layer canvas")
        .forEach((c) => {
          if (c.width > 0) {
            ctx!.globalAlpha = parseFloat(
              c.parentElement?.style.opacity || "1",
            );
            ctx!.drawImage(c, 0, 0, canvas.width, canvas.height);
          }
        });

      // Convertir le canvas en Blob (image PNG)
      const dataURL = canvas.toDataURL("image/png");
      const byteString = atob(dataURL.split(",")[1]); // Décoder base64
      const mimeString = dataURL.split(",")[0].split(":")[1].split(";")[0];
      const listBits = new Uint8Array(byteString.length);

      for (let i = 0; i < byteString.length; i++) {
        listBits[i] = byteString.charCodeAt(i);
      }

      const blob = new Blob([listBits], { type: mimeString });
      return new File([blob], "capture.png", { type: "image/png" });
    };

    const [evenementSousCategorieId, setEvenementSousCategorieId] = useState<
      string | undefined
    >(undefined);

    return (
      <Row>
        <Col xs={"auto"}>
          {hasDroit(user, TYPE_DROIT.CRISE_C) && (
            <>
              {/* déplacer évènement */}
              <ToolbarButton
                toolName={"move-event"}
                toolIcon={<IconMoveObjet />}
                toolLabelTooltip={"Déplacer un événement"}
                toggleTool={toggleToolCallback}
                activeTool={activeTool}
                variant={variant}
              />
              {/* Evènements (création) */}
              <TooltipCustom
                tooltipId="crise-create-event"
                tooltipText="Créer un nouvel événement"
              >
                <Button
                  className="m-2"
                  onClick={() => {
                    setShowCreateEvent(!showCreateEvent);
                  }}
                  variant={variant}
                >
                  <IconEvent />
                </Button>
              </TooltipCustom>
            </>
          )}
          {/* Evènements (liste) */}
          <TooltipCustom
            tooltipId="crise-list-event"
            tooltipText="Afficher la liste des événements"
          >
            <Button
              className="m-2"
              onClick={() => {
                setShowListEvent(!showListEvent);
              }}
              variant={variant}
            >
              <IconList />
            </Button>
          </TooltipCustom>

          {/* documents */}
          <TooltipCustom
            tooltipId="crise-list-docs"
            tooltipText="Afficher la liste des documents"
          >
            <Button
              className="m-2"
              onClick={() => {
                setShowListDocument(!showListDocument);
              }}
              variant={variant}
            >
              <IconDocument />
            </Button>
          </TooltipCustom>

          <TooltipCustom
            tooltipId="crise-custom-report"
            tooltipText="Exectuer des rapports personnalisés"
          >
            <Button
              className="m-2"
              onClick={() => {
                setShowPersonalReports(!showPersonalReports);
              }}
              variant={variant}
            >
              <IconCriseRapportPersonnalise />
            </Button>
          </TooltipCustom>

          {isAuthorized(user, [TYPE_DROIT.CRISE_C, TYPE_DROIT.CRISE_U]) && (
            <TooltipCustom
              tooltipId="crise-capture"
              tooltipText="Enregistre une capture"
            >
              <Button
                className="m-2"
                onClick={() => {
                  show();
                }}
                variant={variant}
              >
                <IconCamera />
              </Button>
            </TooltipCustom>
          )}
        </Col>
        <Col xs={"auto"}>
          {/* gestion toponymies */}
          <ToponymieTypeBarre map={map} criseId={criseId} />
        </Col>
        {hasDroit(user, TYPE_DROIT.CRISE_C) && (
          <Col xs={"auto"}>
            <DropdownTypeSousType
              criseId={criseId}
              variant={variant}
              setTypeEvenement={setEvenementSousCategorieId}
              toggleToolCallback={toggleToolCallback}
              setSousTypeElement={setSousTypeElement}
            />
          </Col>
        )}
        <Volet
          handleClose={handleCloseEvent}
          show={showListEvent}
          className="w-auto"
        >
          <CreateListEvenement
            state={state}
            criseIdentifiant={criseId}
            mapType={map}
          />
        </Volet>

        <Volet
          handleClose={handleCloseEvent}
          show={showListDocument}
          className="w-auto"
        >
          <CreateListDocument
            map={map}
            criseIdentifiant={criseId}
            onSubmit={handleCloseEvent}
          />
        </Volet>

        <Volet
          handleClose={handleCloseEvent}
          show={showPersonalReports}
          className="w-auto"
        >
          <ExecuteCriseRapportPersonnalise
            geometry={reportGeometryElement}
            onGeometrySelect={(geometryType: string, geometryCode: string) => {
              setGeometryReportCode(geometryCode);
              toggleToolCallback("create-report-" + geometryType.toLowerCase());
            }}
          />
        </Volet>

        <Volet
          handleClose={handleCloseEvent}
          show={showCreateEvent}
          className="w-auto"
        >
          <CreateEvenement
            geometrieEvenement={geometryElement}
            EvenementSousCategorieId={evenementSousCategorieId}
            criseId={criseId}
            state={state}
            onSubmit={() => {
              dataCriseLayer.getSource().refresh();
              refreshLayerGeoserver(map);
              handleCloseEvent();
            }}
          />
        </Volet>

        <TooltipMapEditEvenement
          state={state}
          map={map}
          dataEvenementLayer={dataCriseLayer}
          disabled={false}
          criseId={criseId}
        />

        <EditModal
          closeModal={close}
          canModify={true}
          query={url`/api/crise/${criseId}/screen`}
          submitLabel={"Valider"}
          visible={visible}
          isMultipartFormData={true}
          header={null}
          validationSchema={ValidationSchema}
          onSubmit={close}
          prepareVariables={(values) =>
            prepareVariables(captureMap(), values, getMapGeometry())
          }
          getInitialValues={getInitialValue}
        >
          <AddTitleForm />
        </EditModal>
      </Row>
    );
  },
);

MapToolbarCrise.displayName = "MapToolbarCrise";

export default MapToolbarCrise;

const DropdownTypeSousType = ({
  criseId,
  variant,
  setTypeEvenement,
  toggleToolCallback,
  setSousTypeElement,
}: {
  criseId: string;
  variant: string;
  setTypeEvenement: (evenementSousCategorieId: string) => void;
  toggleToolCallback: (evenementSousCategorieGeometrie: string) => void;
  setSousTypeElement: (evenementSousCategorieId: object) => void;
}) => {
  const typeWithSousType = useGet(
    url`/api/crise/${criseId}/evenement/type-sous-type`,
  )?.data;

  return (
    <Dropdown>
      <Dropdown.Toggle className="m-2" id={"dropdown-"} variant={variant}>
        {"Dessiner"}
      </Dropdown.Toggle>
      <Dropdown.Menu>
        {typeWithSousType?.map(
          (
            e: {
              evenementCategorieLibelle: {
                toString: () => any;
              };
              listSousType: any[];
            },
            key: Key | null | undefined,
          ) => {
            return (
              <Row xs={"auto"} className={"m-2"} key={key}>
                <Dropdown>
                  <Dropdown.Toggle id={"dropdown-"} variant={variant}>
                    {e.evenementCategorieLibelle?.toString()}
                  </Dropdown.Toggle>
                  <Dropdown.Menu>
                    {e.listSousType.map(
                      (
                        soustype: {
                          evenementSousCategorieGeometrie: string;
                          evenementSousCategorieId: any;
                          evenementSousCategorieLibelle: any;
                        },
                        key: Key | null | undefined,
                      ) => {
                        let icon;
                        switch (soustype.evenementSousCategorieGeometrie) {
                          case SOUS_TYPE_TYPE_GEOMETRIE.POINT:
                            icon = <IconPoint />;
                            break;
                          case SOUS_TYPE_TYPE_GEOMETRIE.LINESTRING:
                            icon = <IconLine />;
                            break;
                          case SOUS_TYPE_TYPE_GEOMETRIE.POLYGON:
                            icon = <IconPolygon />;
                            break;
                        }
                        return (
                          <Dropdown.Item
                            onClick={() => {
                              setTypeEvenement(
                                soustype.evenementSousCategorieId,
                              );
                              toggleToolCallback(
                                "create-" +
                                  soustype.evenementSousCategorieGeometrie.toLowerCase(),
                              );
                              setSousTypeElement(
                                soustype.evenementSousCategorieId,
                              );
                            }}
                            key={key}
                          >
                            {icon} {soustype?.evenementSousCategorieLibelle}
                          </Dropdown.Item>
                        );
                      },
                    )}
                  </Dropdown.Menu>
                </Dropdown>
              </Row>
            );
          },
        )}
      </Dropdown.Menu>
    </Dropdown>
  );
};

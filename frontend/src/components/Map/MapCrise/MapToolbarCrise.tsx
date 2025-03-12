import Map from "ol/Map";
import { forwardRef, Key, useMemo, useState } from "react";
import { Button, Col, Dropdown } from "react-bootstrap";
import { WKT } from "ol/format";
import { Draw, Modify } from "ol/interaction";
import { Style, Fill, Stroke } from "ol/style";
import CircleStyle from "ol/style/Circle";
import {
  IconDocument,
  IconEvent,
  IconLine,
  IconList,
  IconMoveObjet,
  IconPoint,
  IconPolygon,
  IconSelect,
} from "../../Icon/Icon.tsx";
import Volet from "../../Volet/Volet.tsx";
import ToolbarButton from "../ToolbarButton.tsx";
import CreateEvenement from "../../../pages/ModuleCrise/Evenement/createEvenement.tsx";
import { useGet } from "../../Fetch/useFetch.tsx";
import url, { getFetchOptions } from "../../../module/fetch.tsx";
import SOUS_TYPE_TYPE_GEOMETRIE from "../../../enums/Adresse/SousTypeTypeGeometrie.tsx";
import { TooltipMapEditEvenement } from "../TooltipsMap.tsx";
import CreateListEvenement from "../../../pages/ModuleCrise/Evenement/CreateListEvenement.tsx";
import CreateListDocument from "../../../pages/ModuleCrise/Document/createListDocument.tsx";
import { useToastContext } from "../../../module/Toast/ToastProvider.tsx";
import ToponymieTypeBarre from "../ToponymieTypeBarre.tsx";

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
}) => {
  const [showCreateEvent, setShowCreateEvent] = useState(false);
  const [showListEvent, setShowListEvent] = useState(false);
  const [showListDocument, setShowListDocument] = useState(false);
  const { success: successToast, error: errorToast } = useToastContext();

  const handleCloseEvent = () => {
    setShowListDocument(false);
    setShowListEvent(false);
    setShowCreateEvent(false);
    workingLayer.getSource().clear();
  };

  const [showTracee, setShowTracee] = useState(false);
  const handleCloseTracee = () => setShowTracee(false);
  const [listeEventId] = useState<string[]>([]);
  const [sousTypeElement, setSousTypeElement] = useState<string | null>(null);
  const [geometryElement, setGeometryElement] = useState<string | null>(null);

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
    });

    /**
     * Fonction pour dessiner des types de géométries
     */
    function createDrawInteraction(geometryType: string) {
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
        setGeometryElement(
          `SRID=${map.getView().getProjection().getCode().split(":").pop()};${new WKT().writeFeature(event.feature)}`,
        );
        setShowCreateEvent(true);
      });
      drawCtrl.on("drawstart", async () => {
        // Avant de redessiner un point, on supprime les autres points
        workingLayer.getSource().clear();
      });
      return drawCtrl;
    }

    /**
     * Fonction pour activer ou désactiver une interaction
     */
    function toggleInteraction(active: boolean = false, draw: Draw | Modify) {
      const idx = map?.getInteractions().getArray().indexOf(draw);
      if (active && idx === -1) {
        map.addInteraction(draw);
      } else if (!active && idx !== -1) {
        map.removeInteraction(draw);
      }
    }

    const drawPoint = createDrawInteraction("Point");
    const drawPolygon = createDrawInteraction("Polygon"); // remplit
    const drawLineString = createDrawInteraction("LineString");

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
    listeEventId,
    setSousTypeElement,
    sousTypeElement,
  };
};

const MapToolbarCrise = forwardRef(
  ({
    map,
    state,
    criseId,
    geometryElement,
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
    setShowListDocument: (b: boolean) => void;
    setShowListEvent: (b: boolean) => void;
    setShowCreateEvent: (b: boolean) => void;
    geometryElement: string | null;
    toggleTool: (toolId: string) => void;
    dataCriseLayer: any;
    setSousTypeElement: (object: object) => void;
  }) => {
    const typeWithSousType = useGet(
      url`/api/crise/${criseId}/evenement/type-sous-type`,
    )?.data;

    // Récupère les géométries des communes associées à une crise,
    // puis les utilise pour ajuster la vue de la carte en fonction de l'étendue des géométries.
    const CommuneGeometrie = useGet(
      url`/api/crise/${criseId}/getCommuneGeometrie`,
    )?.data;

    if (CommuneGeometrie) {
      const geometries = CommuneGeometrie.map((geo: any) =>
        geo.split(";").pop(),
      );

      const wktString = "GEOMETRYCOLLECTION(" + geometries.join(",") + ")";
      const feature = new WKT()
        .readFeature(wktString)
        .getGeometry()
        ?.getExtent();

      if (geometries) {
        map?.getView().fit(feature, {
          padding: [50, 50, 50, 50],
          maxZoom: 20,
        });
      }
    }

    const [typeEvenement, setTypeEvenement] = useState<string | undefined>(
      undefined,
    );
    return (
      <>
        <ToolbarButton
          toolName={"select-crise"}
          toolIcon={<IconSelect />}
          toolLabelTooltip={"Sélectionner"}
          toggleTool={toggleToolCallback}
          activeTool={activeTool}
        />

        {/* Evènements (création) */}
        <Button
          className="me-2"
          onClick={() => {
            setShowCreateEvent(!showCreateEvent);
          }}
        >
          <IconEvent />
        </Button>

        {/* Evènements (liste) */}
        <Button
          className="me-2"
          onClick={() => {
            setShowListEvent(!showListEvent);
          }}
        >
          <IconList />
        </Button>

        {/* documents */}
        <Button
          className="me-2"
          onClick={() => {
            setShowListDocument(!showListDocument);
          }}
        >
          <IconDocument />
        </Button>

        {/* déplacer évènement */}
        <ToolbarButton
          toolName={"move-event"}
          toolIcon={<IconMoveObjet />}
          toolLabelTooltip={"Déplacer un événement"}
          toggleTool={toggleToolCallback}
          activeTool={activeTool}
        />

        {/* gestion toponymies */}
        <ToponymieTypeBarre map={map} criseId={criseId} />

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
            criseIdentifiant={criseId}
            onSubmit={handleCloseEvent}
          />
        </Volet>

        <Volet
          handleClose={handleCloseEvent}
          show={showCreateEvent}
          className="w-auto"
        >
          <CreateEvenement
            geometrieEvenement={geometryElement}
            typeEvenement={typeEvenement}
            criseId={criseId}
            state={state}
            onSubmit={() => {
              dataCriseLayer.getSource().refresh();
              handleCloseEvent();
            }}
          />
        </Volet>

        <Dropdown>
          <Dropdown.Toggle id={"dropdown-"}>{"Edition"}</Dropdown.Toggle>

          <Dropdown.Menu>
            <Dropdown>
              <Dropdown.Toggle id={"dropdown-"}>{"Dessiner"}</Dropdown.Toggle>
              <Dropdown.Menu>
                {typeWithSousType?.map(
                  (
                    e: {
                      criseCategorieLibelle: {
                        toString: () => any;
                      };
                      listSousType: any[];
                    },
                    key: Key | null | undefined,
                  ) => {
                    return (
                      <Col xs={"auto"} className={"py-2"} key={key}>
                        <Dropdown>
                          <Dropdown.Toggle id={"dropdown-"}>
                            {e.criseCategorieLibelle?.toString()}
                          </Dropdown.Toggle>

                          <Dropdown.Menu>
                            {e.listSousType.map(
                              (
                                soustype: {
                                  typeCriseCategorieGeometrie: string;
                                  typeCriseCategorieId: any;
                                  typeCriseCategorieLibelle: any;
                                },
                                key: Key | null | undefined,
                              ) => {
                                let icon;
                                switch (soustype.typeCriseCategorieGeometrie) {
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
                                        soustype.typeCriseCategorieId,
                                      );
                                      toggleToolCallback(
                                        "create-" +
                                          soustype.typeCriseCategorieGeometrie.toLowerCase(),
                                      );
                                      setSousTypeElement(
                                        soustype.typeCriseCategorieId,
                                      );
                                    }}
                                    key={key}
                                  >
                                    {icon} {soustype?.typeCriseCategorieLibelle}
                                  </Dropdown.Item>
                                );
                              },
                            )}
                          </Dropdown.Menu>
                        </Dropdown>
                      </Col>
                    );
                  },
                )}
              </Dropdown.Menu>
            </Dropdown>
          </Dropdown.Menu>
        </Dropdown>

        <TooltipMapEditEvenement
          state={state}
          map={map}
          dataEvenementLayer={dataCriseLayer}
          disabled={false}
          criseId={criseId}
        />
      </>
    );
  },
);

MapToolbarCrise.displayName = "MapToolbarCrise";

export default MapToolbarCrise;

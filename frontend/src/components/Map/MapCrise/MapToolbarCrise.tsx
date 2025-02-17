import Map from "ol/Map";
import { forwardRef, Key, useMemo, useState } from "react";
import { Col, Dropdown } from "react-bootstrap";
import { WKT } from "ol/format";
import { Draw } from "ol/interaction";
import { Style, Fill, Stroke } from "ol/style";
import CircleStyle from "ol/style/Circle";
import {
  IconEvent,
  IconLine,
  IconPoint,
  IconPolygon,
  IconSelect,
} from "../../Icon/Icon.tsx";
import Volet from "../../Volet/Volet.tsx";
import ToolbarButton from "../ToolbarButton.tsx";
import CreateEvenement from "../../../pages/ModuleCrise/Evenement/createEvenement.tsx";
import { useGet } from "../../Fetch/useFetch.tsx";
import url from "../../../module/fetch.tsx";
import SOUS_TYPE_TYPE_GEOMETRIE from "../../../enums/Adresse/SousTypeTypeGeometrie.tsx";
import { TooltipMapEditEvenement } from "../TooltipsMap.tsx";

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

export const useToolbarCriseContext = ({ map, workingLayer }) => {
  const [showCreateEvent, setShowEvent] = useState(false);

  const handleCloseEvent = () => {
    setShowEvent(false), workingLayer.getSource().clear();
  };

  const [showTracee, setShowTracee] = useState(false);
  const handleCloseTracee = () => setShowTracee(false);
  const [showCreateElement, setShowCreateElement] = useState(false);
  const [listePeiId] = useState<string[]>([]);
  const [listeEventId] = useState<string[]>([]);
  const [sousTypeElement, setSousTypeElement] = useState<string | null>(null);
  const [geometryElement, setGeometryElement] = useState<string | null>(null);

  const tools = useMemo(() => {
    if (!map) {
      return {};
    }

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
        setShowEvent(true);
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
    function toggleInteraction(active: boolean = false, draw: Draw) {
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
     * Permet d'ouvrir ou fermer le volet de création d'évenement
     */
    function toggleCreateEvent(active = false) {
      if (active) {
        // bouton enclenché
        setGeometryElement(null);
        setShowEvent(true);
      } else {
        setShowEvent(false);
      }
    }

    const tools = {
      "create-event-project": {
        action: toggleCreateEvent,
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
    };
    return tools;
  }, [map, workingLayer]);

  return {
    tools,
    handleCloseEvent,
    showCreateEvent,
    handleCloseTracee,
    showTracee,
    setShowCreateElement,
    listePeiId,
    geometryElement,
    listeEventId,
    setSousTypeElement,
    showCreateElement,
    sousTypeElement,
    setShowEvent,
  };
};

const MapToolbarCrise = forwardRef(
  ({
    map,
    criseId,
    disabledEditEvent,
    geometryElement,
    handleCloseEvent,
    showCreateEvent,
    dataCriseLayer,
    toggleTool: toggleToolCallback,
    activeTool,
    setSousTypeElement,
  }: {
    map?: Map;
    dataPeiLayer: any;
    dataEventLayer: any;
    workingLayer: any;
    criseId: string;
    disabledEditEvent: boolean;
    calcul: () => void;
    pointPeiProjet: string[];
    clear: () => void;
    dataPeiProjetLayer: any;
    handleCloseEvent: () => void;
    showCreateEvent: boolean;
    handleCloseTracee: () => void;
    showTracee: () => void;
    setShowEvent: () => void;
    listePeiId: string[];
    geometryElement: string | null;
    listeEventId: string[];
    toggleTool: (toolId: string) => void;
    activeTool: string;
    dataCriseLayer: any;
    setSousTypeElement: (object: object) => void;
    sousTypeElement: string | null;
  }) => {
    const typeWithSousType = useGet(
      url`/api/crise/${criseId}/evenement/type-sous-type`,
    )?.data;

    const [typeEvenement, setTypeEvenement] = useState<string | null>(null);

    return (
      <>
        <ToolbarButton
          toolName={"select-crise"}
          toolIcon={<IconSelect />}
          toolLabelTooltip={"Sélectionner"}
          toggleTool={toggleToolCallback}
          activeTool={activeTool}
        />
        <ToolbarButton
          toolName={"create-event-project"}
          toolIcon={<IconEvent />}
          toolLabelTooltip={"Créer un évenement"}
          toggleTool={toggleToolCallback}
          activeTool={activeTool}
          disabled={disabledEditEvent}
        />

        <Volet
          handleClose={handleCloseEvent}
          show={showCreateEvent}
          className="w-auto"
        >
          <CreateEvenement
            geometrieEvenement={geometryElement}
            typeEvenement={typeEvenement} // changer le nom
            criseId={criseId}
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

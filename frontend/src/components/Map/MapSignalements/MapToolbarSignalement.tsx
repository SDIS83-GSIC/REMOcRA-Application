import { Feature } from "ol";
import { platformModifierKeyOnly } from "ol/events/condition";
import { WKT } from "ol/format";
import { DragBox, Draw, Select } from "ol/interaction";
import Map from "ol/Map";
import { Fill, Stroke, Style } from "ol/style";
import CircleStyle from "ol/style/Circle";
import { useMemo, useState } from "react";
import { Button, Col, Dropdown, Row } from "react-bootstrap";
import { object } from "yup";
import { SignalementElementEntity } from "../../../Entities/SignalementElementEntity.tsx";
import SOUS_TYPE_TYPE_GEOMETRIE from "../../../enums/Signalement/SousTypeTypeGeometrie.tsx";
import url from "../../../module/fetch.tsx";
import Signalement from "../../../pages/Signalement/Signalement.tsx";
import CreatElementSignalement from "../../../pages/Signalement/CreateElementSignalement.tsx";
import { URLS } from "../../../routes.tsx";
import CreateButton from "../../Button/CreateButton.tsx";
import { useGet } from "../../Fetch/useFetch.tsx";
import MyFormik from "../../Form/MyFormik.tsx";
import {
  IconDelete,
  IconLine,
  IconPoint,
  IconPolygon,
  IconSelect,
} from "../../Icon/Icon.tsx";
import TooltipCustom from "../../Tooltip/Tooltip.tsx";
import Volet from "../../Volet/Volet.tsx";
import { refreshLayerGeoserver } from "../MapUtils.tsx";
import ToolbarButton from "../ToolbarButton.tsx";
import { TooltipMapSignalement } from "../TooltipsMap.tsx";
import THEMATIQUE from "../../../enums/ThematiqueEnum.tsx";
import VoletButtonListeDocumentThematique from "../../ListeDocumentThematique/VoletButtonListeDocumentThematique.tsx";

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

export const useToolbarSignalementContext = ({ map, workingLayer }) => {
  const [showCreateElement, setShowCreateElement] = useState(false);
  const [listSignalementElement, setListSignalementElement] = useState<
    SignalementElementEntity[]
  >([]);
  const [geometryElement, setGeometryElement] = useState<string | null>(null);
  const [sousTypeElement, setSousTypeElement] = useState<string | null>(null);
  const handleCloseElement = () => {
    const lastFeature = workingLayer.getSource().getFeatures()[
      workingLayer.getSource().getFeatures().length - 1
    ];
    workingLayer.getSource().removeFeature(lastFeature);
    setShowCreateElement(false);
  };
  const [showCreateSignalement, setShowCreateSignalement] = useState(false);
  const handleCloseSignalement = () => {
    setShowCreateSignalement(false);
  };
  const [selectedFeatures, setSelectedFeatures] = useState([]);

  function supprimerFeature() {
    if (selectedFeatures.length > 0) {
      // Suppression de l'objet visible
      selectedFeatures.forEach((f) => {
        workingLayer.getSource().removeFeature(f);
      });
      // Suppression de l'objet passé au formulaire
      setListSignalementElement((prevList) =>
        prevList.filter(
          (signalement) =>
            !selectedFeatures.some(
              (feature) =>
                new WKT().writeFeature(feature) === signalement.geometryString,
            ),
        ),
      );
    }
  }

  const tools = useMemo(() => {
    if (!map) {
      return {};
    }

    //POINT
    const createElementPointCtrl = new Draw({
      source: workingLayer.getSource(),
      type: "Point",
      style: (feature) => {
        const geometryType = feature.getGeometry().getType();
        if (geometryType === "Point") {
          return drawStyle;
        }
      },
    });
    createElementPointCtrl.on("drawend", async (event) => {
      setGeometryElement(new WKT().writeFeature(event.feature));
      setShowCreateElement(true);
    });
    //POLYGON
    const createElementPolygonCtrl = new Draw({
      source: workingLayer.getSource(),
      type: "Polygon",
      style: (feature) => {
        const geometryType = feature.getGeometry().getType();
        if (geometryType === "Polygon") {
          return drawStyle;
        }
      },
    });
    createElementPolygonCtrl.on("drawend", async (event) => {
      setGeometryElement(new WKT().writeFeature(event.feature));
      setShowCreateElement(true);
    });
    //LINESTRING
    const createElementLinestringCtrl = new Draw({
      source: workingLayer.getSource(),
      type: "LineString",
    });

    createElementLinestringCtrl.on("drawend", async (event) => {
      setGeometryElement(new WKT().writeFeature(event.feature));
      setShowCreateElement(true);
    });

    const selectCtrl = new Select({
      layers: [workingLayer],
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
    dragBoxCtrl.on("boxend", function (e) {
      const selectedCtrlFeatures = selectCtrl.getFeatures().getArray();

      if (!platformModifierKeyOnly(e.mapBrowserEvent)) {
        selectCtrl.getFeatures().clear();
      }
      const boxExtent = dragBoxCtrl.getGeometry().getExtent();
      const boxFeatures = workingLayer
        .getSource()
        .getFeaturesInExtent(boxExtent);

      selectCtrl.getFeatures().extend(boxFeatures);

      if (selectedCtrlFeatures.length > 0) {
        setSelectedFeatures(selectedCtrlFeatures);
      } else {
        setSelectedFeatures([]);
      }
    });

    function toggleSelectDraw(active = false) {
      const idx1 = map?.getInteractions().getArray().indexOf(selectCtrl);
      const idx2 = map?.getInteractions().getArray().indexOf(dragBoxCtrl);
      if (active) {
        if (idx1 === -1 && idx2 === -1) {
          map.addInteraction(selectCtrl);
          map.addInteraction(dragBoxCtrl);
          if (
            selectCtrl.getFeatures().getArray().length !==
            selectedFeatures.length
          ) {
            setSelectedFeatures(selectCtrl.getFeatures().getArray());
          }
        }
      } else {
        map.removeInteraction(selectCtrl);
        map.removeInteraction(dragBoxCtrl);
        setSelectedFeatures([]);
      }
    }

    /**
     * Permet de dessiner un point pour la création des alertes
     */
    function toggleCreatePointSignalement(active = false) {
      const idx = map
        ?.getInteractions()
        .getArray()
        .indexOf(createElementPointCtrl);
      if (active) {
        if (idx === -1) {
          map.addInteraction(createElementPointCtrl);
        }
      } else {
        map.removeInteraction(createElementPointCtrl);
      }
    }

    /**
     * Permet de dessiner un Polygon pour la création des alertes
     */
    function toggleCreatePolygonSignalement(active = false) {
      const idx = map
        ?.getInteractions()
        .getArray()
        .indexOf(createElementPolygonCtrl);
      if (active) {
        if (idx === -1) {
          map.addInteraction(createElementPolygonCtrl);
        }
      } else {
        map.removeInteraction(createElementPolygonCtrl);
      }
    }

    /**
     * Permet de dessiner une linestring pour la création des alertes
     */
    function toggleCreateLinestringSignalement(active = false) {
      const idx = map
        ?.getInteractions()
        .getArray()
        .indexOf(createElementLinestringCtrl);
      if (active) {
        if (idx === -1) {
          map.addInteraction(createElementLinestringCtrl);
        }
      } else {
        map.removeInteraction(createElementLinestringCtrl);
      }
    }

    const tools = {
      "select-draw": {
        action: toggleSelectDraw,
        actionPossibleEnDeplacement: false,
      },
      "create-point": {
        action: toggleCreatePointSignalement,
      },
      "create-polygon": {
        action: toggleCreatePolygonSignalement,
      },
      "create-linestring": {
        action: toggleCreateLinestringSignalement,
      },
    };

    return tools;
  }, [map, selectedFeatures.length, workingLayer]);

  return {
    tools,
    showCreateElement,
    setShowCreateElement,
    handleCloseElement,
    showCreateSignalement,
    setShowCreateSignalement,
    handleCloseSignalement,
    supprimerFeature,
    selectedFeatures,
    listSignalementElement,
    setListSignalementElement,
    geometryElement,
    setSousTypeElement,
    sousTypeElement,
  };
};

const MapToolbarSignalement = ({
  map,
  dataSignalementLayer,
  handleCloseElement,
  showCreateElement,
  setShowCreateElement,
  handleCloseSignalement,
  showCreateSignalement,
  setShowCreateSignalement,
  toggleTool: toggleToolCallback,
  activeTool,
  supprimerFeature,
  selectedFeatures,
  workingLayer,
  listSignalementElement,
  setListSignalementElement,
  geometryElement,
  setSousTypeElement,
  sousTypeElement,
}: {
  map?: Map;
  dataSignalementLayer: any;
  handleCloseElement: () => void;
  showCreateElement: boolean;
  setShowCreateElement: () => void;
  handleCloseSignalement: () => void;
  showCreateSignalement: boolean;
  setShowCreateSignalement: () => void;
  toggleTool: (toolId: string) => void;
  activeTool: string;
  supprimerFeature: () => void;
  selectedFeatures: Feature[];
  workingLayer: any;
  listSignalementElement: SignalementElementEntity[];
  setListSignalementElement: (SignalementElementEntity) => void;
  geometryElement: string;
  setSousTypeElement: () => void;
  sousTypeElement: string;
}) => {
  const typeWithSousType = useGet(url`/api/signalements/type-sous-type`)?.data;

  return (
    typeWithSousType && (
      <Row>
        <Col xs={"auto"}>
          <VoletButtonListeDocumentThematique
            codeThematique={THEMATIQUE.SIGNALEMENTS}
            titreVolet="Liste des documents liés aux signalements"
          />
          <ToolbarButton
            toolName={"select-draw"}
            toolIcon={<IconSelect />}
            toolLabelTooltip={
              "Sélectionner des éléments (non validés uniquement)"
            }
            toggleTool={toggleToolCallback}
            activeTool={activeTool}
          />
          <TooltipCustom
            tooltipId="suppressionSignalement"
            tooltipText={
              selectedFeatures.length === 0
                ? "Aucun élément sélectionné"
                : "Supprimer le(s) " +
                  selectedFeatures.length +
                  "élément(s) sélectionné(s)"
            }
          >
            <Button
              variant={"outline-danger"}
              onClick={supprimerFeature}
              disabled={selectedFeatures.length === 0}
              className={"m-2"}
            >
              <IconDelete />
            </Button>
          </TooltipCustom>
        </Col>
        {typeWithSousType?.map((e, key) => {
          return (
            <Col xs={"auto"} className={"py-2"} key={key}>
              <Dropdown>
                <Dropdown.Toggle id={"dropdown-" + e.signalementTypeElementCode}>
                  {e.signalementTypeElementLibelle?.toString()}
                </Dropdown.Toggle>

                <Dropdown.Menu>
                  {e.listSousType.map((soustype, key) => {
                    let icon;
                    switch (soustype.signalementSousTypeElementTypeGeom) {
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
                          toggleToolCallback(
                            "create-" +
                              soustype.signalementSousTypeElementTypeGeom.toLowerCase(),
                          );
                          setSousTypeElement(soustype.signalementSousTypeElementId);
                        }}
                        key={key}
                      >
                        {icon} {soustype?.signalementSousTypeElementLibelle}
                      </Dropdown.Item>
                    );
                  })}
                </Dropdown.Menu>
              </Dropdown>
            </Col>
          );
        })}
        <Col xs={"auto"} className={"ms-auto"}>
          <CreateButton
            classnames={"m-2"}
            onClick={() => {
              setShowCreateSignalement(true);
            }}
            title={"Ajouter un signalement"}
            disabled={listSignalementElement?.length <= 0}
          />
        </Col>
        <Volet
          handleClose={handleCloseElement}
          show={showCreateElement}
          className="w-auto"
          backdrop={true}
        >
          <CreatElementSignalement
            srid={map.getView().getProjection().getCode().split(":").pop()}
            layer={workingLayer}
            geometryString={geometryElement}
            onClick={(element: SignalementElementEntity) => {
              setListSignalementElement((data) => {
                return [...data, element];
              });
              dataSignalementLayer.getSource().refresh();
              refreshLayerGeoserver(map);
              setShowCreateElement(false);
            }}
            sousTypeElement={sousTypeElement}
          />
        </Volet>

        <Volet
          handleClose={handleCloseSignalement}
          show={showCreateSignalement}
          className="w-auto"
          backdrop={true}
        >
          <MyFormik
            initialValues={getInitialValues(listSignalementElement)}
            validationSchema={validationSchema}
            isPost={true}
            submitUrl={`/api/signalements/create`}
            prepareVariables={(values) => prepareVariables(values)}
            redirectUrl={URLS.SIGNALEMENTS}
            onSubmit={() => {
              dataSignalementLayer.getSource().refresh();
              refreshLayerGeoserver(map);
              setListSignalementElement([]);
              workingLayer.getSource().clear();
              setShowCreateSignalement(false);
            }}
          >
            {/* j'envoie la liste d'élément juste pour les afficher */}
            <Signalement
              listeElement={listSignalementElement}
              typeWithSousType={typeWithSousType}
            />
          </MyFormik>
        </Volet>
        <TooltipMapSignalement map={map} />
      </Row>
    )
  );
};

export const validationSchema = object({});
export const prepareVariables = (values: {
  listSignalementElement: SignalementElementEntity[];
}) => {
  return {
    ...values,
    listSignalementElement: values.listSignalementElement.map((element) => ({
      geometry: `SRID=${element.srid};${element.geometryString}`,
      anomalies: element.anomalies,
      description: element.description,
      sousType: element.sousType,
    })),
  };
};

export const getInitialValues = (listSignalementElement) => ({
  listSignalementElement: listSignalementElement,
});
MapToolbarSignalement.displayName = "MapToolbarSignalement";

export default MapToolbarSignalement;

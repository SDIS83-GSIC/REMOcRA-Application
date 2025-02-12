import { DragBox, Draw, Select } from "ol/interaction";
import Map from "ol/Map";
import { Fill, Stroke, Style } from "ol/style";
import CircleStyle from "ol/style/Circle";
import { useMemo, useState } from "react";
import { Button, Col, Dropdown, Row } from "react-bootstrap";
import { shiftKeyOnly } from "ol/events/condition";
import { Feature } from "ol";
import { WKT } from "ol/format";
import { object } from "yup";
import {
  IconDelete,
  IconLine,
  IconPoint,
  IconPolygon,
  IconSelect,
} from "../../Icon/Icon.tsx";
import Volet from "../../Volet/Volet.tsx";
import url from "../../../module/fetch.tsx";
import { useGet } from "../../Fetch/useFetch.tsx";
import SOUS_TYPE_TYPE_GEOMETRIE from "../../../enums/Adresse/SousTypeTypeGeometrie.tsx";
import ToolbarButton from "../ToolbarButton.tsx";
import CreatElementAdresse from "../../../pages/Adresse/CreateElementAdresse.tsx";
import Adresse from "../../../pages/Adresse/Adresse.tsx";
import { AdresseElementEntity } from "../../../Entities/AdresseElementEntity.tsx";
import { URLS } from "../../../routes.tsx";
import MyFormik from "../../Form/MyFormik.tsx";
import CreateButton from "../../Button/CreateButton.tsx";
import TooltipCustom from "../../Tooltip/Tooltip.tsx";

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

export const useToolbarAdresseContext = ({ map, workingLayer }) => {
  const [showCreateElement, setShowCreateElement] = useState(false);
  const [listAdresseElement, setListAdresseElement] = useState<
    AdresseElementEntity[]
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
  const [showCreateAdresse, setShowCreateAdresse] = useState(false);
  const handleCloseAdresse = () => {
    setShowCreateAdresse(false);
  };
  const [selectedFeatures, setSelectedFeatures] = useState([]);

  function supprimerFeature() {
    if (selectedFeatures.length > 0) {
      // Suppression de l'objet visible
      selectedFeatures.forEach((f) => {
        workingLayer.getSource().removeFeature(f);
      });
      // Suppression de l'objet passé au formulaire
      setListAdresseElement((prevList) =>
        prevList.filter(
          (adresse) =>
            !selectedFeatures.some(
              (feature) =>
                new WKT().writeFeature(feature) === adresse.geometryString,
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

      if (!shiftKeyOnly(e.mapBrowserEvent)) {
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
    function toggleCreatePointAdresse(active = false) {
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
    function toggleCreatePolygonAdresse(active = false) {
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
    function toggleCreateLinestringAdresse(active = false) {
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
      },
      "create-point": {
        action: toggleCreatePointAdresse,
      },
      "create-polygon": {
        action: toggleCreatePolygonAdresse,
      },
      "create-linestring": {
        action: toggleCreateLinestringAdresse,
      },
    };

    return tools;
  }, [map, selectedFeatures.length, workingLayer]);

  return {
    tools,
    showCreateElement,
    setShowCreateElement,
    handleCloseElement,
    showCreateAdresse,
    setShowCreateAdresse,
    handleCloseAdresse,
    supprimerFeature,
    selectedFeatures,
    listAdresseElement,
    setListAdresseElement,
    geometryElement,
    setSousTypeElement,
    sousTypeElement,
  };
};

const MapToolbarAdresse = ({
  map,
  dataAdresseLayer,
  handleCloseElement,
  showCreateElement,
  setShowCreateElement,
  handleCloseAdresse,
  showCreateAdresse,
  setShowCreateAdresse,
  toggleTool: toggleToolCallback,
  activeTool,
  supprimerFeature,
  selectedFeatures,
  workingLayer,
  listAdresseElement,
  setListAdresseElement,
  geometryElement,
  setSousTypeElement,
  sousTypeElement,
}: {
  map?: Map;
  dataAdresseLayer: any;
  handleCloseElement: () => void;
  showCreateElement: boolean;
  setShowCreateElement: () => void;
  handleCloseAdresse: () => void;
  showCreateAdresse: boolean;
  setShowCreateAdresse: () => void;
  toggleTool: (toolId: string) => void;
  activeTool: string;
  supprimerFeature: () => void;
  selectedFeatures: Feature[];
  workingLayer: any;
  listAdresseElement: AdresseElementEntity[];
  setListAdresseElement: (AdresseElementEntity) => void;
  geometryElement: string;
  setSousTypeElement: () => void;
  sousTypeElement: string;
}) => {
  const typeWithSousType = useGet(url`/api/adresses/type-sous-type`)?.data;

  return (
    typeWithSousType && (
      <Row>
        <Col xs={"auto"}>
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
            tooltipId="suppressionAdresse"
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
            >
              <IconDelete />
            </Button>
          </TooltipCustom>
        </Col>
        {typeWithSousType?.map((e, key) => {
          return (
            <Col xs={"auto"} className={"py-2"} key={key}>
              <Dropdown>
                <Dropdown.Toggle id={"dropdown-" + e.adresseTypeElementCode}>
                  {e.adresseTypeElementLibelle?.toString()}
                </Dropdown.Toggle>

                <Dropdown.Menu>
                  {e.listSousType.map((soustype, key) => {
                    let icon;
                    switch (soustype.adresseSousTypeElementTypeGeom) {
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
                              soustype.adresseSousTypeElementTypeGeom.toLowerCase(),
                          );
                          setSousTypeElement(soustype.adresseSousTypeElementId);
                        }}
                        key={key}
                      >
                        {icon} {soustype?.adresseSousTypeElementLibelle}
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
            onClick={() => {
              setShowCreateAdresse(true);
            }}
            title={"Ajouter une adresse"}
            disabled={listAdresseElement?.length <= 0}
          />
        </Col>
        <Volet
          handleClose={handleCloseElement}
          show={showCreateElement}
          className="w-auto"
          backdrop={true}
        >
          <CreatElementAdresse
            srid={map.getView().getProjection().getCode().split(":").pop()}
            layer={workingLayer}
            geometryString={geometryElement}
            onClick={(element: AdresseElementEntity) => {
              setListAdresseElement((data) => {
                return [...data, element];
              });
              dataAdresseLayer.getSource().refresh();
              setShowCreateElement(false);
            }}
            sousTypeElement={sousTypeElement}
          />
        </Volet>

        <Volet
          handleClose={handleCloseAdresse}
          show={showCreateAdresse}
          className="w-auto"
          backdrop={true}
        >
          <MyFormik
            initialValues={getInitialValues(listAdresseElement)}
            validationSchema={validationSchema}
            isPost={true}
            submitUrl={`/api/adresses/create`}
            prepareVariables={(values) => prepareVariables(values)}
            redirectUrl={URLS.ADRESSE}
            onSubmit={() => {
              dataAdresseLayer.getSource().refresh();
              setListAdresseElement([]);
              workingLayer.getSource().clear();
              setShowCreateAdresse(false);
            }}
          >
            {/* j'envoie la liste d'élément juste pour les afficher */}
            <Adresse
              listeElement={listAdresseElement}
              typeWithSousType={typeWithSousType}
            />
          </MyFormik>
        </Volet>
      </Row>
    )
  );
};

export const validationSchema = object({});
export const prepareVariables = (values) => values;

export const getInitialValues = (listAdresseElement) => ({
  listAdresseElement: listAdresseElement,
});
MapToolbarAdresse.displayName = "MapToolbarAdresse";

export default MapToolbarAdresse;

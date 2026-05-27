import { Feature } from "ol";
import { platformModifierKeyOnly } from "ol/events/condition";
import { WKT } from "ol/format";
import { Geometry } from "ol/geom";
import { DragBox, Draw, Interaction, Select } from "ol/interaction";
import VectorLayer from "ol/layer/Vector";
import OLMap from "ol/Map";
import { Fill, Stroke, Style } from "ol/style";
import CircleStyle from "ol/style/Circle";
import { useMemo, useState } from "react";
import { Button, Dropdown, Row } from "react-bootstrap";
import { object } from "yup";
import { SignalementElementEntity } from "../../../Entities/SignalementElementEntity.tsx";
import SOUS_TYPE_TYPE_GEOMETRIE from "../../../enums/Signalement/SousTypeTypeGeometrie.tsx";
import THEMATIQUE from "../../../enums/ThematiqueEnum.tsx";
import url from "../../../module/fetch.tsx";
import CreatElementSignalement from "../../../pages/Signalement/CreateElementSignalement.tsx";
import Signalement from "../../../pages/Signalement/Signalement.tsx";
import { URLS } from "../../../routes.tsx";
import CreateButton from "../../Button/CreateButton.tsx";
import { useGet } from "../../Fetch/useFetch.tsx";
import { setDocumentInFormData } from "../../Form/FormDocuments.tsx";
import MyFormik from "../../Form/MyFormik.tsx";
import {
  IconDelete,
  IconLine,
  IconPoint,
  IconPolygon,
  IconSelect,
} from "../../Icon/Icon.tsx";
import VoletButtonListeDocumentThematique from "../../ListeDocumentThematique/VoletButtonListeDocumentThematique.tsx";
import TooltipCustom from "../../Tooltip/Tooltip.tsx";
import Volet from "../../Volet/Volet.tsx";
import { refreshLayerGeoserver } from "../MapUtils.tsx";
import ToolbarButton from "../ToolbarButton.tsx";
import { TooltipMapSignalement } from "../TooltipsMap.tsx";

const defaultStyle = new Style({
  fill: new Fill({
    color: "#64C0FFFF",
  }),
  stroke: new Stroke({
    color: "#64C0FFFF",
    width: 2,
  }),
  image: new CircleStyle({
    radius: 5,
    stroke: new Stroke({
      color: "#64C0FFFF",
    }),
    fill: new Fill({
      color: "#64C0FFFF",
    }),
  }),
});

export const useToolbarSignalement = ({
  map,
  cartographieSignalementLayer,
}: {
  map: OLMap;
  cartographieSignalementLayer: VectorLayer;
}) => {
  const [featureStyle, setFeatureStyle] = useState(defaultStyle);
  const [selectedFeatures, setSelectedFeatures] = useState<Feature<Geometry>[]>(
    [],
  );
  const [showCreateElement, setShowCreateElement] = useState(false);
  const [sousTypeElement, setSousTypeElement] = useState<string | null>(null);
  const [listSignalementElement, setListSignalementElement] = useState<
    SignalementElementEntity[]
  >([]);
  const [showCreateSignalement, setShowCreateSignalement] = useState(false);
  const [geometryElement, setGeometryElement] = useState<string | null>(null);

  const handleCloseSignalement = () => {
    setShowCreateSignalement(false);
  };

  const handleCloseElement = () => {
    cartographieSignalementLayer
      .getSource()
      ?.removeFeature(
        cartographieSignalementLayer.getSource()?.getFeatures()?.[
          cartographieSignalementLayer.getSource()!.getFeatures().length - 1
        ],
      );
    setShowCreateElement(false);
  };

  function supprimerFeature() {
    if (selectedFeatures.length > 0) {
      // Suppression de l'objet visible
      selectedFeatures.forEach((f) => {
        cartographieSignalementLayer.getSource()?.removeFeature(f);
      });
      // Suppression de l'objet passé au formulaire
      setListSignalementElement((prevList: any[]) =>
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
    const wkt = new WKT();

    const drawPointCtrl = new Draw({
      source: cartographieSignalementLayer.getSource(),
      type: "Point",
    });

    const drawLineCtrl = new Draw({
      source: cartographieSignalementLayer.getSource(),
      type: "LineString",
    });

    const drawShapeCtrl = new Draw({
      source: cartographieSignalementLayer.getSource(),
      type: "Polygon",
    });

    const handleDrawEnd = (event: { feature: Feature<Geometry> }) => {
      setGeometryElement(wkt.writeFeature(event.feature));
      setShowCreateElement(true);
      event.feature.setStyle(featureStyle.clone());
    };

    drawPointCtrl.on("drawend", handleDrawEnd);
    drawLineCtrl.on("drawend", handleDrawEnd);
    drawShapeCtrl.on("drawend", handleDrawEnd);

    const selectCtrl = new Select({
      layers: [cartographieSignalementLayer],
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

      selectCtrl
        .getFeatures()
        .extend(
          cartographieSignalementLayer
            ?.getSource()
            ?.getFeaturesInExtent(dragBoxCtrl.getGeometry().getExtent()) ?? [],
        );

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

    function toggleCtrl(active: boolean, ctrl: Interaction) {
      const idx = map?.getInteractions().getArray().indexOf(ctrl);
      if (active) {
        if (idx === -1) {
          map.addInteraction(ctrl);

          if (
            selectCtrl.getFeatures().getArray().length !==
            selectedFeatures.length
          ) {
            setSelectedFeatures(selectCtrl.getFeatures().getArray());
          }
        }
      } else {
        map.removeInteraction(ctrl);
      }
    }

    function toggleDrawPoint(active = false) {
      toggleCtrl(active, drawPointCtrl);
    }

    function toggleDrawLine(active = false) {
      toggleCtrl(active, drawLineCtrl);
    }

    function toggleDrawShape(active = false) {
      toggleCtrl(active, drawShapeCtrl);
    }

    const tools = {
      "select-draw": {
        action: toggleSelectDraw,
        actionPossibleEnDeplacement: false,
      },
      "draw-point": {
        action: toggleDrawPoint,
      },
      "draw-linestring": {
        action: toggleDrawLine,
      },
      "draw-polygon": {
        action: toggleDrawShape,
      },
    };

    return tools;
  }, [
    map,
    cartographieSignalementLayer,
    featureStyle.clone,
    selectedFeatures.length,
  ]);

  return {
    tools,
    featureStyle,
    setFeatureStyle,
    selectedFeatures,
    setSousTypeElement,
    supprimerFeature,
    listSignalementElement,
    setShowCreateSignalement,
    handleCloseElement,
    showCreateElement,
    setListSignalementElement,
    sousTypeElement,
    setShowCreateElement,
    geometryElement,
    handleCloseSignalement,
    showCreateSignalement,
  };
};

const MapToolbarSignalement = ({
  map,
  toggleTool: toggleToolCallback,
  activeTool,
  selectedFeatures,
  cartographieSignalementLayer,
  setSousTypeElement,
  supprimerFeature,
  listSignalementElement,
  setShowCreateSignalement,
  handleCloseElement,
  showCreateElement,
  setListSignalementElement,
  sousTypeElement,
  setShowCreateElement,
  geometryElement,
  dataSignalementLayer,
  handleCloseSignalement,
  showCreateSignalement,
}: {
  map?: OLMap;
  activeTool: string;
  selectedFeatures: Feature[];
  cartographieSignalementLayer: VectorLayer;
  listSignalementElement: SignalementElementEntity[];
  showCreateElement: boolean;
  sousTypeElement: string | null;
  geometryElement: string;
  dataSignalementLayer: any;
  showCreateSignalement: boolean;
  setSousTypeElement: (sousTypeId: string) => void;
  setShowCreateSignalement: (show: boolean) => void;
  setListSignalementElement: (
    list:
      | SignalementElementEntity[]
      | ((prev: SignalementElementEntity[]) => SignalementElementEntity[]),
  ) => void;
  setShowCreateElement: (show: boolean) => void;
  supprimerFeature: () => void;
  toggleTool: (toolId: string) => void;
  handleCloseElement: () => void;
  handleCloseSignalement: () => void;
}) => {
  const typeWithSousType = useGet(url`/api/signalements/type-sous-type`)?.data;

  return (
    typeWithSousType && (
      <Row>
        <Row xs={"auto"}>
          <VoletButtonListeDocumentThematique
            codeThematique={THEMATIQUE.SIGNALEMENT}
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

          {typeWithSousType?.map(
            (e: {
              signalementTypeElementCode: string;
              signalementTypeElementLibelle: string;
              listSousType: any[];
            }) => {
              return (
                <Dropdown
                  className="py-2 d-flex align-items-center gap-2"
                  key={e.signalementTypeElementCode}
                >
                  <Dropdown.Toggle
                    id={"dropdown-" + e.signalementTypeElementCode}
                  >
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
                              "draw-" +
                                soustype.signalementSousTypeElementTypeGeom.toLowerCase(),
                            );
                            setSousTypeElement(
                              soustype.signalementSousTypeElementId,
                            );
                          }}
                          key={key}
                        >
                          {icon} {soustype?.signalementSousTypeElementLibelle}
                        </Dropdown.Item>
                      );
                    })}
                  </Dropdown.Menu>
                </Dropdown>
              );
            },
          )}

          <CreateButton
            classnames={"m-2"}
            onClick={() => {
              setShowCreateSignalement(true);
            }}
            title={"Ajouter un signalement"}
            disabled={listSignalementElement?.length <= 0}
          />
        </Row>

        <Volet
          handleClose={handleCloseElement}
          show={showCreateElement}
          className="w-auto"
          backdrop={true}
        >
          <CreatElementSignalement
            srid={
              map?.getView()?.getProjection()?.getCode()?.split(":").pop() ?? ""
            }
            layer={cartographieSignalementLayer}
            geometryString={geometryElement}
            onClick={(element: SignalementElementEntity) => {
              setListSignalementElement((data: SignalementElementEntity[]) => {
                return [...data, element];
              });
              dataSignalementLayer?.getSource()?.refresh();
              refreshLayerGeoserver(map);
              setShowCreateElement(false);
            }}
            sousTypeElement={sousTypeElement!}
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
            isMultipartFormData={true}
            submitUrl={`/api/signalements/create`}
            prepareVariables={(values) => prepareVariables(values)}
            redirectUrl={URLS.SIGNALEMENTS}
            onSubmit={() => {
              dataSignalementLayer.getSource().refresh();
              refreshLayerGeoserver(map);
              setListSignalementElement([]);
              cartographieSignalementLayer?.getSource()?.clear();
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
        <TooltipMapSignalement map={map!} />
      </Row>
    )
  );
};

export const validationSchema = object({});

export const prepareVariables = (values: any) => {
  const formData = new FormData();

  setDocumentInFormData(values?.documents ?? [], [], formData);

  formData.append("description", values.description);

  formData.append(
    "listSignalementElement",
    JSON.stringify(
      values.listSignalementElement.map(
        (el: {
          geometryString: any;
          srid: any;
          anomalies: any;
          description: any;
          sousType: any;
        }) => ({
          geometry: {
            wkt: el.geometryString,
            srid: el.srid,
          },
          anomalies: el.anomalies,
          description: el.description,
          sousType: el.sousType,
        }),
      ),
    ),
  );

  return formData;
};

export const getInitialValues = (
  listSignalementElement: SignalementElementEntity[],
) => ({
  listSignalementElement: listSignalementElement,
});

MapToolbarSignalement.displayName = "MapToolbarSignalement2";

export default MapToolbarSignalement;

import { Map } from "ol";
import { shiftKeyOnly } from "ol/events/condition";
import { DragBox, Draw, Select } from "ol/interaction";
import { Fill, Stroke, Style } from "ol/style";
import CircleStyle from "ol/style/Circle";
import { forwardRef, useMemo, useState } from "react";
import { Button, ButtonGroup } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import { TYPE_DROIT } from "../../../Entities/UtilisateurEntity.tsx";
import { hasDroit, isAuthorized } from "../../../droits.tsx";
import TYPE_NATURE_DECI from "../../../enums/TypeNatureDeci.tsx";
import { useToastContext } from "../../../module/Toast/ToastProvider.tsx";
import url, { getFetchOptions } from "../../../module/fetch.tsx";
import CreateIndisponibiliteTemporaire from "../../../pages/IndisponibiliteTemporaire/CreateIndisponibiliteTemporaire.tsx";
import AffecterPeiTourneeMap from "../../../pages/Tournee/AffecterPeiTourneeMap.tsx";
import { URLS } from "../../../routes.tsx";
import { useAppContext } from "../../App/AppProvider.tsx";
import {
  IconCreate,
  IconIndisponibiliteTemporaire,
  IconMoveObjet,
  IconSelect,
  IconTournee,
} from "../../Icon/Icon.tsx";
import TooltipCustom from "../../Tooltip/Tooltip.tsx";
import Volet from "../../Volet/Volet.tsx";
import toggleDeplacerPoint from "../MapUtils.tsx";
import ToolbarButton from "../ToolbarButton.tsx";
import TooltipMapPei from "../TooltipsMap.tsx";

export const useToolbarPeiContext = ({ map, workingLayer, dataPeiLayer }) => {
  const navigate = useNavigate();
  const { success: successToast, error: errorToast } = useToastContext();
  const [listePeiId] = useState<string[]>([]);
  const [listePeiIdTourneePublic, setListePeiIdTourneePublic] = useState<
    string[]
  >([]);
  const [listePeiIdTourneePrive, setListePeiIdTourneePrive] = useState<
    string[]
  >([]);
  const [showCreateIndispoTemp, setShowCreateIndispoTemp] = useState(false);
  const handleCloseIndispoTemp = () => setShowCreateIndispoTemp(false);

  const [showCreateTournee, setShowCreateTournee] = useState(false);
  const handleCloseTournee = () => setShowCreateTournee(false);

  const tools = useMemo(() => {
    if (!map) {
      return {};
    }

    const createCtrl = new Draw({
      source: workingLayer.getSource(),
      type: "Point",
      style: (feature) => {
        const geometryType = feature.getGeometry().getType();
        if (geometryType === "Point") {
          return new Style({
            fill: new Fill({
              color: "rgba(255, 255, 255, 0.2)",
            }),
            stroke: new Stroke({
              color: "rgba(0, 0, 0, 0.5)",
              lineDash: [10, 10],
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
        }
      },
    });
    createCtrl.on("drawend", async (event) => {
      const geometry = event.feature.getGeometry();
      (
        await fetch(
          url`/api/zone-integration/check`,
          getFetchOptions({
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
              wkt: `POINT(${geometry.getFlatCoordinates()[0]} ${geometry.getFlatCoordinates()[1]})`,
              srid: map.getView().getProjection().getCode(),
            }),
          }),
        )
      )
        .text()
        .then((text) => {
          if (text === "true") {
            navigate(URLS.CREATE_PEI, {
              state: {
                coordonneeX: geometry.getFlatCoordinates()[0],
                coordonneeY: geometry.getFlatCoordinates()[1],
                srid: map.getView().getProjection().getCode().split(":").pop(),
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

    const selectCtrl = new Select({});
    const dragBoxCtrl = new DragBox({
      style: new Style({
        stroke: new Stroke({
          color: [0, 0, 255, 1],
        }),
      }),
      minArea: 25,
    });
    dragBoxCtrl.on("boxend", function (e) {
      if (!shiftKeyOnly(e.mapBrowserEvent)) {
        selectCtrl.getFeatures().clear();
      }
      const boxExtent = dragBoxCtrl.getGeometry().getExtent();

      const boxFeatures = dataPeiLayer
        .getSource()
        .getFeaturesInExtent(boxExtent);

      selectCtrl.getFeatures().extend(boxFeatures);

      listePeiId.splice(0, listePeiId.length);
      listePeiIdTourneePrive.splice(0, listePeiIdTourneePrive.length);
      listePeiIdTourneePublic.splice(0, listePeiIdTourneePublic.length);

      const prives: string[] = [];
      const publics: string[] = [];

      selectCtrl.getFeatures().forEach((e) => {
        const point = e.getProperties();
        listePeiId.push(point.pointId);

        if (point.natureDeciCode === TYPE_NATURE_DECI.PRIVE) {
          prives.push(point.pointId);
        } else {
          publics.push(point.pointId);
        }
      });

      // A retirer en 3.1 => ticket #126505
      if (prives.length > 0 && publics.length === 0) {
        setListePeiIdTourneePrive(prives);
      } else if (publics.length > 0 && prives.length === 0) {
        setListePeiIdTourneePublic(publics);
      } else {
        setListePeiIdTourneePrive([]);
        setListePeiIdTourneePublic([]);
      }
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
        listePeiIdTourneePrive.splice(0, listePeiIdTourneePrive.length);
        listePeiIdTourneePublic.splice(0, listePeiIdTourneePublic.length);
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

    const selectPeiCtrl = new Select();

    function toggleDeplacerPei(active = false) {
      toggleDeplacerPoint(
        active,
        selectPeiCtrl,
        map,
        `/api/pei/deplacer/`,
        dataPeiLayer,
        successToast,
        errorToast,
      );
    }

    const tools = {
      "select-pei": {
        action: toggleSelect,
      },
      "create-pei": {
        action: toggleCreate,
      },
      "deplacer-pei": {
        action: toggleDeplacerPei,
      },
    };

    return tools;
  }, [map]);

  function createUpdateTournee() {
    setShowCreateTournee(true);
  }

  function createIndispoTemp() {
    setShowCreateIndispoTemp(true);
  }

  return {
    tools,
    showCreateIndispoTemp,
    handleCloseIndispoTemp,
    listePeiId,
    createIndispoTemp,
    createUpdateTournee,
    showCreateTournee,
    handleCloseTournee,
    listePeiIdTourneePublic,
    listePeiIdTourneePrive,
  };
};

const MapToolbarPei = forwardRef(
  ({
    toggleTool: toggleToolCallback,
    activeTool,
    map,
    dataPeiLayer,
    showCreateIndispoTemp,
    handleCloseIndispoTemp,
    listePeiId,
    createIndispoTemp,
    createUpdateTournee,
    showCreateTournee,
    handleCloseTournee,
    listePeiIdTourneePrive,
    listePeiIdTourneePublic,
  }: {
    toggleTool: (toolId: string) => void;
    activeTool: string;
    map: Map;
    dataPeiLayer: any;
    showCreateIndispoTemp: boolean;
    handleCloseIndispoTemp: () => void;
    listePeiId: string[];
    createIndispoTemp: () => void;
    createUpdateTournee: () => void;
    showCreateTournee: boolean;
    handleCloseTournee: () => void;
    listePeiIdTourneePrive: string[];
    listePeiIdTourneePublic: string[];
  }) => {
    const { user } = useAppContext();

    return (
      <ButtonGroup>
        <ToolbarButton
          toolName={"select-pei"}
          toolIcon={<IconSelect />}
          toolLabelTooltip={"Sélectionner"}
          toggleTool={toggleToolCallback}
          activeTool={activeTool}
        />
        {hasDroit(user, TYPE_DROIT.PEI_C) && (
          <ToolbarButton
            toolName={"create-pei"}
            toolIcon={<IconCreate />}
            toolLabelTooltip={"Créer un PEI"}
            toggleTool={toggleToolCallback}
            activeTool={activeTool}
          />
        )}
        {hasDroit(user, TYPE_DROIT.PEI_DEPLACEMENT_U) && (
          <ToolbarButton
            toolName={"deplacer-pei"}
            toolIcon={<IconMoveObjet />}
            toolLabelTooltip={"Déplacer un PEI"}
            toggleTool={toggleToolCallback}
            activeTool={activeTool}
          />
        )}
        {hasDroit(user, TYPE_DROIT.INDISPO_TEMP_C) && (
          <>
            <Button
              variant="outline-primary"
              onClick={createIndispoTemp}
              className="rounded m-2"
            >
              <IconIndisponibiliteTemporaire />
            </Button>
            <Volet
              handleClose={handleCloseIndispoTemp}
              show={showCreateIndispoTemp}
              className="w-auto"
            >
              <CreateIndisponibiliteTemporaire listePeiId={listePeiId} />
            </Volet>
          </>
        )}
        {hasDroit(user, TYPE_DROIT.TOURNEE_A) && (
          <>
            <TooltipCustom
              tooltipText={"Ajouter des PEI de même nature DECI à une tournée"}
              tooltipId={"affecter-pei-tournee-carte"}
            >
              <Button
                variant="outline-primary"
                onClick={createUpdateTournee}
                className="rounded m-2"
                disabled={
                  listePeiIdTourneePrive.length === 0 &&
                  listePeiIdTourneePublic.length === 0
                }
              >
                <IconTournee />
              </Button>
            </TooltipCustom>
            <Volet
              handleClose={handleCloseTournee}
              show={showCreateTournee}
              className="w-auto"
            >
              <AffecterPeiTourneeMap
                listePeiId={
                  listePeiIdTourneePrive.length !== 0
                    ? listePeiIdTourneePrive
                    : listePeiIdTourneePublic
                }
                isPrive={listePeiIdTourneePrive.length !== 0}
                closeVolet={handleCloseTournee}
              />
            </Volet>
          </>
        )}
        <TooltipMapPei
          map={map}
          dataPeiLayer={dataPeiLayer}
          displayButtonDelete={hasDroit(user, TYPE_DROIT.PEI_D)}
          displayButtonEdit={isAuthorized(user, [
            TYPE_DROIT.PEI_U,
            TYPE_DROIT.PEI_CARACTERISTIQUES_U,
            TYPE_DROIT.PEI_DEPLACEMENT_U,
            TYPE_DROIT.PEI_NUMERO_INTERNE_U,
          ])}
          disabledTooltip={activeTool === "deplacer-pei"}
        />
      </ButtonGroup>
    );
  },
);

MapToolbarPei.displayName = "MapToolbarPei";

export default MapToolbarPei;

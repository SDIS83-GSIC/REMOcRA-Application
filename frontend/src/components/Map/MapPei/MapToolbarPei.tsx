import { Map } from "ol";
import { platformModifierKeyOnly } from "ol/events/condition";
import { WKT } from "ol/format";
import { DragBox, Draw, Modify, Select } from "ol/interaction";
import { Fill, Stroke, Style } from "ol/style";
import CircleStyle from "ol/style/Circle";
import { useMemo, useState } from "react";
import { Button, ButtonGroup, Row } from "react-bootstrap";
import { hasDroit, isAuthorized } from "../../../droits.tsx";
import TYPE_DROIT from "../../../enums/DroitEnum.tsx";
import TYPE_NATURE_DECI from "../../../enums/TypeNatureDeci.tsx";
import { useToastContext } from "../../../module/Toast/ToastProvider.tsx";
import url, { getFetchOptions } from "../../../module/fetch.tsx";
import CreateDebitSimultane from "../../../pages/DebitSimultane/CreateDebitSimultane.tsx";
import CreateIndisponibiliteTemporaire from "../../../pages/IndisponibiliteTemporaire/CreateIndisponibiliteTemporaire.tsx";
import AffecterPeiTourneeMap from "../../../pages/Tournee/AffecterPeiTourneeMap.tsx";
import { useAppContext } from "../../App/AppProvider.tsx";
import {
  IconCreate,
  IconDebitSimultane,
  IconIndisponibiliteTemporaire,
  IconMoveObjet,
  IconSelect,
  IconTournee,
} from "../../Icon/Icon.tsx";
import EditModal from "../../Modal/EditModal.tsx";
import useModal from "../../Modal/ModalUtils.tsx";
import SimpleModal from "../../Modal/SimpleModal.tsx";
import TooltipCustom from "../../Tooltip/Tooltip.tsx";
import Volet from "../../Volet/Volet.tsx";
import toggleDeplacerPoint, { refreshLayerGeoserver } from "../MapUtils.tsx";
import ToolbarButton from "../ToolbarButton.tsx";
import TooltipMapPei from "../TooltipsMap.tsx";

export const useToolbarPeiContext = ({
  map,
  workingLayer,
  dataPeiLayer,
  setShowFormPei,
  setCoordonneesPeiCreate,
}: {
  map: Map;
  setShowFormPei: (v: boolean) => void;
  setCoordonneesPeiCreate: (e: any) => void;
}) => {
  const { visible, show, close, ref } = useModal();

  const { visible: visibleMove, show: showMove, close: closeMove } = useModal();

  const { error: errorToast } = useToastContext();
  const [listePeiId] = useState<string[]>([]);
  const [listePeiTourneePublic, setListePeiTourneePublic] = useState<
    { peiId: string; numeroComplet: string }[]
  >([]);
  const [listePeiTourneePrive, setListePeiTourneePrive] = useState<
    { peiId: string; numeroComplet: string }[]
  >([]);
  const [showCreateIndispoTemp, setShowCreateIndispoTemp] = useState(false);
  const handleCloseIndispoTemp = () => setShowCreateIndispoTemp(false);

  const [showCreateTournee, setShowCreateTournee] = useState(false);
  const handleCloseTournee = () => setShowCreateTournee(false);

  const [showCreateDebitSimultane, setShowCreateDebitSimultane] =
    useState(false);
  const handleCloseDebitSimultane = () => setShowCreateDebitSimultane(false);

  const [listePeiIdDebitSimultane, setListePeiIdDebitSimultane] = useState<
    string[]
  >([]);

  const [typeReseauId, setTypeReseauId] = useState<string>();

  const [peiIdMove, setPeiIdMove] = useState<string | null>(null);

  const [geometrieMove, setGeometrieMove] = useState<string | null>(null);

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
    createCtrl.on("drawstart", async () => {
      // Avant de redessiner un point, on supprime les autres points, le but est d'avoir juste un seul point à la fois.
      workingLayer.getSource().clear();
    });
    createCtrl.on("drawend", async (event) => {
      const feature = event.feature;
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
            setShowFormPei(true);
            setCoordonneesPeiCreate({
              coordonneeX: feature.getGeometry().getFlatCoordinates()[0],
              coordonneeY: feature.getGeometry().getFlatCoordinates()[1],
              srid: map.getView().getProjection().getCode().split(":").pop(),
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
      style: new Style({
        image: new CircleStyle({
          radius: 16,
          stroke: new Stroke({
            color: "rgba(255, 0, 0, 0.7)",
            width: 4,
          }),
        }),
      }),
      toggleCondition: platformModifierKeyOnly,
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

    dragBoxCtrl.on("boxend", function (e) {
      if (!platformModifierKeyOnly(e.mapBrowserEvent)) {
        selectCtrl.getFeatures().clear();
      }
      const boxExtent = dragBoxCtrl.getGeometry().getExtent();

      const boxFeatures = dataPeiLayer
        .getSource()
        .getFeaturesInExtent(boxExtent);

      selectCtrl.getFeatures().extend(boxFeatures);

      listePeiId.splice(0, listePeiId.length);
      listePeiTourneePrive.splice(0, listePeiTourneePrive.length);
      listePeiTourneePublic.splice(0, listePeiTourneePublic.length);

      const prives: { peiId: string; numeroComplet: string }[] = [];
      const publics: { peiId: string; numeroComplet: string }[] = [];

      const peiPrivesDebitSimultane: any[] = [];

      selectCtrl.getFeatures().forEach((e) => {
        const point = e.getProperties();
        if (!listePeiId.includes(point.elementId)) {
          listePeiId.push(point.elementId);
        }

        if (point.natureDeciCode === TYPE_NATURE_DECI.PRIVE) {
          prives.push({
            peiId: point.elementId,
            numeroComplet: point.peiNumeroComplet,
          });

          if (point.pibiTypeReseauId != null) {
            peiPrivesDebitSimultane.push(point);
          }
        } else {
          publics.push({
            peiId: point.elementId,
            numeroComplet: point.peiNumeroComplet,
          });
        }
      });

      const distinctTypeReseau = peiPrivesDebitSimultane
        .map((pei) => pei.pibiTypeReseauId)
        .filter((value, index, self) => self.indexOf(value) === index);

      const hasDebitSimultane = peiPrivesDebitSimultane
        .map((pei) => pei.hasDebitSimultane)
        .find((e) => e === true);

      // Si les PEI ont le même type de réseau et n'ont pas de débit simultané alors on autorise la création
      if (distinctTypeReseau.length === 1 && hasDebitSimultane == null) {
        setListePeiIdDebitSimultane(
          peiPrivesDebitSimultane.map((e) => e.elementId),
        );
        setTypeReseauId(distinctTypeReseau.at(0));
      } else {
        setListePeiIdDebitSimultane([]);
        setTypeReseauId(null);
      }

      // A retirer en 3.1 => ticket #126505
      if (prives.length > 0 && publics.length === 0) {
        setListePeiTourneePrive(prives);
      } else if (publics.length > 0 && prives.length === 0) {
        setListePeiTourneePublic(publics);
      } else {
        setListePeiTourneePrive([]);
        setListePeiTourneePublic([]);
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
        listePeiTourneePrive.splice(0, listePeiTourneePrive.length);
        listePeiTourneePublic.splice(0, listePeiTourneePublic.length);
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

    const modifyCtrl = new Modify({
      features: selectPeiCtrl.getFeatures(),
      source: dataPeiLayer,
      snapToPointer: true,
    });

    function toggleDeplacerPei(active = false) {
      toggleDeplacerPoint(
        active,
        selectPeiCtrl,
        modifyCtrl,
        map,
        (feature, pointId) => {
          setGeometrieMove(feature);
          setPeiIdMove(pointId);
          showMove();
        },
      );

      if (!active) {
        setPeiIdMove(null);
        setGeometrieMove(null);
      }
    }

    const tools = {
      "select-pei": {
        action: toggleSelect,
        actionPossibleEnDeplacement: false,
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

  async function createDebitSimultane() {
    // On regarde si les PEI sélectionné sont à moins de 500 mètres
    (
      await fetch(
        url`/api/debit-simultane/check-distance?${{ listePibiId: JSON.stringify(listePeiIdDebitSimultane) }}`,
        getFetchOptions({
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({}),
        }),
      )
    )
      .text()
      .then((text) => {
        if (text === "true") {
          setShowCreateDebitSimultane(true);
        } else {
          show();
        }
      })
      .catch(() => {
        show();
      });
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
    listePeiTourneePublic,
    listePeiTourneePrive,
    createDebitSimultane,
    handleCloseDebitSimultane,
    showCreateDebitSimultane,
    listePeiIdDebitSimultane,
    typeReseauId,
    ref,
    visible,
    close,
    peiIdMove,
    geometrieMove,
    closeMove,
    visibleMove,
  };
};

const MapToolbarPei = ({
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
  listePeiTourneePrive,
  listePeiTourneePublic,
  dataDebitSimultaneLayer,
  createDebitSimultane,
  handleCloseDebitSimultane,
  showCreateDebitSimultane,
  listePeiIdDebitSimultane,
  typeReseauId,
  closeModal,
  refModal,
  visibleModal,
  setPeiIdUpdate,
  setShowFormPei,
  showFormPei,
  peiIdUpdate,
  disabledTool,
  peiIdMove,
  geometrieMove,
  closeMove,
  visibleMove,
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
  listePeiTourneePrive: { peiId: string; numeroComplet: string }[];
  listePeiTourneePublic: { peiId: string; numeroComplet: string }[];
  dataDebitSimultaneLayer: any;
  createDebitSimultane: () => void;
  handleCloseDebitSimultane: () => void;
  showCreateDebitSimultane: boolean;
  listePeiIdDebitSimultane: string[];
  typeReseauId: string | undefined;
  visibleModal: boolean;
  closeModal: () => void;
  refModal: any;
  setPeiIdUpdate: (v: string) => void;
  setShowFormPei: (v: boolean) => void;
  showFormPei: boolean;
  peiIdUpdate: string;
  disabledTool: (toolId: string) => void;
  peiIdMove: string | null;
  geometrieMove: string | null;
  closeMove: () => void;
  visibleMove: boolean;
}) => {
  const { user } = useAppContext();

  // Pour afficher le *bon* bouton pour accéder à la fiche PEI (lecture, écriture)
  const canEditPei = isAuthorized(user, [
    TYPE_DROIT.PEI_U,
    TYPE_DROIT.PEI_CARACTERISTIQUES_U,
    TYPE_DROIT.PEI_DEPLACEMENT_U,
    TYPE_DROIT.PEI_NUMERO_INTERNE_U,
    TYPE_DROIT.PEI_ADRESSE_C,
  ]);

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
          disabled={showFormPei && !peiIdUpdate}
          toolLabelTooltip={
            showFormPei && peiIdUpdate
              ? "Un formulaire est en cours d'édition"
              : "Créer un PEI"
          }
          toggleTool={toggleToolCallback}
          activeTool={activeTool}
        />
      )}
      {hasDroit(user, TYPE_DROIT.PEI_DEPLACEMENT_U) && (
        <ToolbarButton
          toolName={"deplacer-pei"}
          toolIcon={<IconMoveObjet />}
          toolLabelTooltip={
            showFormPei
              ? "Un formulaire est en cours d'édition"
              : "Déplacer un PEI"
          }
          toggleTool={toggleToolCallback}
          disabled={showFormPei}
          activeTool={activeTool}
        />
      )}
      {hasDroit(user, TYPE_DROIT.INDISPO_TEMP_C) && (
        <>
          <TooltipCustom
            tooltipText={
              showFormPei
                ? "Un formulaire est en cours d'édition"
                : "Créer une indisponibilité temporaire"
            }
            tooltipId={"indispo-temp-carte"}
          >
            <Button
              variant="outline-primary"
              onClick={createIndispoTemp}
              className="rounded m-2"
              disabled={showFormPei}
            >
              <IconIndisponibiliteTemporaire />
            </Button>
          </TooltipCustom>
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
            tooltipText={
              showFormPei
                ? "Un formulaire est en cours d'édition"
                : "Ajouter des PEI de même nature DECI à une tournée"
            }
            tooltipId={"affecter-pei-tournee-carte"}
          >
            <Button
              variant="outline-primary"
              onClick={createUpdateTournee}
              className="rounded m-2"
              disabled={
                (listePeiTourneePrive.length === 0 &&
                  listePeiTourneePublic.length === 0) ||
                showFormPei
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
              listePei={
                listePeiTourneePrive.length !== 0
                  ? listePeiTourneePrive
                  : listePeiTourneePublic
              }
              isPrive={listePeiTourneePrive.length !== 0}
              closeVolet={handleCloseTournee}
            />
          </Volet>
        </>
      )}
      {hasDroit(user, TYPE_DROIT.DEBITS_SIMULTANES_A) && (
        <>
          <TooltipCustom
            tooltipText={
              <>
                <Row className="p-2">
                  <b>Créer un débit simultané</b>
                </Row>
                <Row className="p-2">
                  Les PIBI sélectionnés doivent :
                  <ul>
                    <li>avoir la nature DECI privé </li>
                    <li>avoir le même type de réseau</li>
                    <li>n&apos;avoir aucun débit simultané associé</li>
                    <li>être à moins de 500m</li>
                  </ul>
                </Row>
              </>
            }
            tooltipId={"debit-simultane-carte"}
          >
            <Button
              variant="outline-primary"
              onClick={createDebitSimultane}
              className="rounded m-2"
              disabled={
                listePeiIdDebitSimultane.length < 2 || typeReseauId === null
              }
            >
              <IconDebitSimultane />
            </Button>
          </TooltipCustom>
          <Volet
            handleClose={handleCloseDebitSimultane}
            show={showCreateDebitSimultane}
            className="w-auto"
          >
            <CreateDebitSimultane
              listePibiId={listePeiIdDebitSimultane}
              typeReseauId={typeReseauId!}
              onSubmit={() => {
                dataDebitSimultaneLayer.getSource().refresh();
                dataPeiLayer.getSource().refresh();
                refreshLayerGeoserver(map);
                handleCloseDebitSimultane();
              }}
            />
          </Volet>
          <SimpleModal
            closeModal={closeModal}
            content={"Tous les PEI doivent être à moins de 500 mètres."}
            header={"Impossible de créer un débit simultané"}
            ref={refModal}
            visible={visibleModal}
          />
        </>
      )}
      <TooltipMapPei
        map={map}
        dataPeiLayer={dataPeiLayer}
        displayButtonDelete={hasDroit(user, TYPE_DROIT.PEI_D)}
        displayButtonEdit={canEditPei}
        displayButtonSeeFichePei={
          hasDroit(user, TYPE_DROIT.PEI_R) && !canEditPei
        }
        disabledTooltip={activeTool === "deplacer-pei"}
        displayButtonEditDebitSimultane={hasDroit(
          user,
          TYPE_DROIT.DEBITS_SIMULTANES_A,
        )}
        setShowFormPei={setShowFormPei}
        setPeiIdUpdate={setPeiIdUpdate}
        dataDebitSimultaneLayer={dataDebitSimultaneLayer}
        showFormPei={showFormPei}
        disabledCreateButton={() => disabledTool("create-pei")}
      />
      {peiIdMove && geometrieMove && (
        <EditModal
          closeModal={() => {
            dataPeiLayer.getSource().refresh();
            closeMove();
          }}
          canModify={canEditPei}
          query={url`/api/pei/deplacer/${peiIdMove}`}
          submitLabel={"Valider"}
          visible={visibleMove}
          header={"Déplacer le PEI"}
          onSubmit={() => {
            dataPeiLayer.getSource().refresh();
            refreshLayerGeoserver(map);
          }}
          prepareVariables={(values) => ({
            geometry: values.geometrie,
          })}
          getInitialValues={() => ({
            geometrie: geometrieMove,
          })}
        >
          <p>Voulez-vous déplacer le PEI ?</p>
        </EditModal>
      )}
    </ButtonGroup>
  );
};

MapToolbarPei.displayName = "MapToolbarPei";

export default MapToolbarPei;

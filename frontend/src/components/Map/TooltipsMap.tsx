import { Feature, Map, Overlay } from "ol";
import { WKT } from "ol/format";
import { ReactNode, Ref, useEffect, useRef, useState } from "react";
import { Button, Col, Popover, Row } from "react-bootstrap";
import { useLocation, useNavigate } from "react-router-dom";
import COLUMN_INDISPONIBILITE_TEMPORAIRE from "../../enums/ColumnIndisponibiliteTemporaireEnum.tsx";
import TYPE_POINT_CARTE from "../../enums/TypePointCarteEnum.tsx";
import UpdatePeiProjet from "../../pages/CouvertureHydraulique/PeiProjet/UpdatePeiProjet.tsx";
import UpdateDebitSimultane from "../../pages/DebitSimultane/UpdateDebitSimultane.tsx";
import ListIndisponibiliteTemporaire from "../../pages/IndisponibiliteTemporaire/ListIndisponibiliteTemporaire.tsx";
import FicheResume from "../../pages/Pei/FicheResume/FicheResume.tsx";
import ListTournee from "../../pages/Tournee/ListTournee.tsx";
import { URLS } from "../../routes.tsx";
import DeleteButtonWithModal from "../Button/DeleteButtonWithModal.tsx";
import {
  IconClose,
  IconEdit,
  IconIndisponibiliteTemporaire,
  IconOeil,
  IconSee,
  IconTournee,
  IconVisite,
} from "../Icon/Icon.tsx";
import Volet from "../Volet/Volet.tsx";
import TooltipCustom from "../Tooltip/Tooltip.tsx";
import UpdatePeiPrescrit from "../../pages/PeiPrescrit/UpdatePeiPrescrit.tsx";
import CustomLinkButton from "../Button/CustomLinkButton.tsx";
import UpdatePermis from "../../pages/Permis/UpdatePermis.tsx";
import UpdateEvenement from "../../pages/ModuleCrise/Evenement/UpdateEvenement.tsx";
import { hasDroit } from "../../droits.tsx";
import TYPE_DROIT from "../../enums/DroitEnum.tsx";
import { useAppContext } from "../App/AppProvider.tsx";
import { refreshLayerGeoserver } from "./MapUtils.tsx";

/**
 * Permet d'afficher une tooltip sur la carte lorsque l'utilisateur clique sur un point
 * @param map : la carte
 * @returns la tooltip
 */
const TooltipMapPei = ({
  map,
  displayButtonEdit,
  displayButtonSeeFichePei,
  displayButtonDelete,
  dataPeiLayer,
  disabledTooltip = false,
  displayButtonEditDebitSimultane,
  dataDebitSimultaneLayer,
  setShowFormPei,
  setPeiIdUpdate,
}: {
  map: Map;
  displayButtonEdit: boolean;
  displayButtonSeeFichePei: boolean;
  displayButtonDelete: boolean;
  dataPeiLayer: any;
  disabledTooltip: boolean;
  displayButtonEditDebitSimultane: boolean;
  dataDebitSimultaneLayer: any;
  setPeiIdUpdate: (v: string) => void;
  setShowFormPei: (v: boolean) => void;
}) => {
  const ref = useRef(null);
  const navigate = useNavigate();
  const location = useLocation();
  const { featureSelect, overlay } = useTooltipMap({ ref: ref, map: map });

  const [showFichePei, setShowFichePei] = useState(false);
  const handleCloseFichePei = () => setShowFichePei(false);

  const [showIndispoTemp, setShowIndispoTemp] = useState(false);
  const handleCloseIndispoTemp = () => setShowIndispoTemp(false);

  const [showTournee, setShowTournee] = useState(false);
  const handleCloseTournee = () => setShowTournee(false);

  const [showUpdateDebitSimultane, setShowUpdateDebitSimultane] =
    useState(false);
  const handleCloseUpdateDebitSimultane = () =>
    setShowUpdateDebitSimultane(false);

  const elementId = featureSelect?.getProperties().elementId;
  const { user } = useAppContext();

  return (
    <div ref={ref}>
      {featureSelect?.getProperties().typeElementCarte ===
      TYPE_POINT_CARTE.PEI ? (
        <Tooltip
          disabled={disabledTooltip}
          featureSelect={featureSelect}
          overlay={overlay}
          displayButtonEdit={displayButtonEdit}
          onClickEdit={() => {
            setShowFormPei(true);
            setPeiIdUpdate(elementId);
          }}
          labelEdit={"Modifier le PEI"}
          displayButtonDelete={displayButtonDelete}
          deletePath={`/api/pei/delete/` + elementId}
          onClickDelete={() => {
            dataPeiLayer.getSource().refresh();
            refreshLayerGeoserver(map);
            overlay?.setPosition(undefined);
          }}
          labelDelete={
            featureSelect?.getProperties().hasTournee
              ? "Impossible de supprimer un PEI affecté à une tournée"
              : "Supprimer le PEI"
          }
          displayButtonSee={true}
          onClickSee={() => setShowFichePei(true)}
          labelSee={"Voir la fiche de résumé du PEI"}
          autreActionBouton={
            <>
              {displayButtonSeeFichePei && (
                <Col className="p-1" xs={"auto"}>
                  <TooltipCustom
                    tooltipText={"Voir la fiche complète du PEI"}
                    tooltipId={"fichepei"}
                  >
                    <CustomLinkButton
                      pathname={URLS.UPDATE_PEI(elementId)}
                      variant="primary"
                    >
                      <IconOeil />
                    </CustomLinkButton>
                  </TooltipCustom>
                </Col>
              )}
              {hasDroit(user, TYPE_DROIT.VISITE_R) && (
                <Col className="p-1" xs={"auto"}>
                  <TooltipCustom
                    tooltipText={"Voir les visites du PEI"}
                    tooltipId={"visite"}
                  >
                    <CustomLinkButton
                      pathname={URLS.VISITE(elementId)}
                      variant="warning"
                    >
                      <IconVisite />
                    </CustomLinkButton>
                  </TooltipCustom>
                </Col>
              )}
              {featureSelect?.getProperties().hasIndispoTemp && (
                <Col className="p-1" xs={"auto"}>
                  <TooltipCustom
                    tooltipText={"Voir les indisponibilités temporaires du PEI"}
                    tooltipId={"indispo-temp-carte"}
                  >
                    <Button
                      variant="warning"
                      onClick={() => {
                        setShowIndispoTemp(true);
                        overlay?.setPosition(undefined);
                      }}
                    >
                      <IconIndisponibiliteTemporaire />
                    </Button>
                  </TooltipCustom>
                </Col>
              )}
              {featureSelect?.getProperties().hasTournee &&
                hasDroit(user, TYPE_DROIT.TOURNEE_R) && (
                  <Col className="p-1" xs={"auto"}>
                    <TooltipCustom
                      tooltipText={"Voir les tournées associées"}
                      tooltipId={"tournees-carte"}
                    >
                      <Button
                        variant="warning"
                        onClick={() => {
                          setShowTournee(true);
                          overlay?.setPosition(undefined);
                        }}
                      >
                        <IconTournee />
                      </Button>
                    </TooltipCustom>
                  </Col>
                )}
              <Volet
                handleClose={handleCloseFichePei}
                show={showFichePei}
                className="w-auto"
              >
                <FicheResume
                  peiId={elementId}
                  titre={
                    "Fiche Résumé du PEI " +
                    featureSelect?.getProperties().peiNumeroComplet
                  }
                />
              </Volet>
              <Volet
                handleClose={() => {
                  handleCloseIndispoTemp();

                  navigate(
                    {
                      pathname: location.pathname,
                      search: "",
                    },
                    { replace: true, state: location.state },
                  );
                }}
                show={showIndispoTemp}
                className="w-auto"
                backdrop={true}
              >
                <ListIndisponibiliteTemporaire
                  peiId={elementId}
                  colonnes={[COLUMN_INDISPONIBILITE_TEMPORAIRE.MOTIF]}
                />
              </Volet>
              <Volet
                handleClose={() => {
                  handleCloseTournee();

                  navigate(
                    {
                      pathname: location.pathname,
                      search: "",
                    },
                    { replace: true, state: location.state },
                  );
                }}
                show={showTournee}
                className="w-auto"
                backdrop={true}
              >
                <ListTournee peiId={elementId} />
              </Volet>
            </>
          }
        />
      ) : featureSelect?.getProperties().typeElementCarte ===
        TYPE_POINT_CARTE.DEBIT_SIMULTANE ? (
        <>
          <Tooltip
            featureSelect={featureSelect}
            overlay={overlay}
            displayButtonEdit={displayButtonEditDebitSimultane}
            onClickEdit={() => setShowUpdateDebitSimultane(true)}
            displayButtonDelete={displayButtonEditDebitSimultane}
            onClickDelete={() => {
              dataDebitSimultaneLayer.getSource().refresh();
              refreshLayerGeoserver(map);
              overlay?.setPosition(undefined);
            }}
            deletePath={`/api/debit-simultane/delete/` + elementId}
          />

          <Volet
            handleClose={() => {
              handleCloseUpdateDebitSimultane();
            }}
            show={showUpdateDebitSimultane}
            className="w-50"
          >
            <UpdateDebitSimultane
              debitSimultaneId={elementId}
              onSubmit={() => {
                dataDebitSimultaneLayer.getSource().refresh();
                refreshLayerGeoserver(map);
                handleCloseUpdateDebitSimultane();
              }}
              typeReseauId={featureSelect?.getProperties().typeReseauId}
              coordonneeX={
                featureSelect?.getProperties().geometry.flatCoordinates[0]
              }
              coordonneeY={
                featureSelect?.getProperties().geometry.flatCoordinates[1]
              }
              srid={map.getView().getProjection().getCode().split(":")[1]}
            />
          </Volet>
        </>
      ) : (
        ""
      )}
    </div>
  );
};

export default TooltipMapPei;

const Tooltip = ({
  disabled = false,
  featureSelect,
  overlay,

  displayButtonEdit = false,
  onClickEdit,
  hrefEdit,
  labelEdit = "Modifier l'élément",

  displayButtonDelete = false,
  onClickDelete,
  deletePath,
  labelDelete = "Supprimer l'élément",

  displayButtonSee = false,
  onClickSee,
  labelSee = "Voir l'élément",

  href = undefined,
  autreActionBouton,
}: {
  disabled: boolean;
  featureSelect: Feature | undefined;
  overlay: Overlay | undefined;
  displayButtonEdit?: boolean;
  hrefEdit?: string;
  onClickEdit?: () => void;
  labelEdit: string;

  displayButtonDelete?: boolean;
  deletePath: string;
  onClickDelete?: () => void;
  labelDelete: string;

  displayButtonSee?: boolean;
  onClickSee?: () => void;
  labelSee: string;

  href?: string;
  autreActionBouton: ReactNode | undefined;
}) => {
  return (
    <>
      {featureSelect?.getProperties().elementId && !disabled && (
        <Popover
          id="popover"
          placement="bottom"
          arrowProps={{
            style: {
              display: "none",
            },
          }}
        >
          <Popover.Header>
            <Row>
              <Col>Informations</Col>
              <Col className="ms-auto" xs={"auto"}>
                <Button
                  variant="link"
                  onClick={() => overlay?.setPosition(undefined)}
                >
                  <IconClose />
                </Button>
              </Col>
            </Row>
          </Popover.Header>
          <Popover.Body>
            <div
              dangerouslySetInnerHTML={{
                __html: featureSelect.getProperties().propertiesToDisplay,
              }}
            />

            <Row className="mt-3">
              <Col className="ms-auto" xs={"auto"}>
                <Row>
                  {displayButtonSee && (
                    <Col className="p-1" xs={"auto"}>
                      <TooltipCustom
                        tooltipText={labelSee}
                        tooltipId={"fiche-resume-carte"}
                      >
                        <CustomLinkButton
                          variant="primary"
                          pathname={href!}
                          onClick={onClickSee}
                        >
                          <IconSee />
                        </CustomLinkButton>
                      </TooltipCustom>
                    </Col>
                  )}
                  {displayButtonEdit && (
                    <Col className="p-1" xs={"auto"}>
                      <TooltipCustom
                        tooltipText={labelEdit}
                        tooltipId={"edit-carte"}
                      >
                        <CustomLinkButton
                          variant="info"
                          className={"text-white"}
                          onClick={onClickEdit}
                          pathname={hrefEdit!}
                        >
                          <IconEdit />
                        </CustomLinkButton>
                      </TooltipCustom>
                    </Col>
                  )}
                  {displayButtonDelete && (
                    <Col className="p-1" xs={"auto"}>
                      <TooltipCustom
                        tooltipText={labelDelete}
                        tooltipId={"supprimer-carte"}
                      >
                        <DeleteButtonWithModal
                          path={deletePath}
                          disabled={
                            !displayButtonDelete ||
                            featureSelect?.getProperties().hasTournee
                          }
                          title={false}
                          reload={onClickDelete}
                        />
                      </TooltipCustom>
                    </Col>
                  )}
                  {autreActionBouton && autreActionBouton}
                </Row>
              </Col>
            </Row>
          </Popover.Body>
        </Popover>
      )}
    </>
  );
};

export const TooltipMapEditPeiProjet = ({
  map,
  etudeId,
  disabledEditPeiProjet = false,
  dataPeiProjetLayer,
  disabled,
}: {
  map: Map;
  etudeId: string;
  disabledEditPeiProjet: boolean;
  dataPeiProjetLayer: any;
  disabled: boolean;
}) => {
  const ref = useRef(null);
  const [showUpdatePeiProjet, setShowUpdatePeiProjet] = useState(false);
  const handleCloseUpdatePeiProjet = () => setShowUpdatePeiProjet(false);

  const { featureSelect, overlay } = useTooltipMap({
    ref: ref,
    map: map,
    disabled: disabled,
  });
  const displayEditDeleteButton =
    !disabledEditPeiProjet &&
    featureSelect?.getProperties().typeElementCarte === "PEI_PROJET" &&
    featureSelect?.getProperties().elementId != null;

  if (disabled) {
    overlay?.setPosition(undefined);
  }
  return (
    <>
      <div ref={ref}>
        <Tooltip
          featureSelect={featureSelect}
          overlay={overlay}
          onClickEdit={() => setShowUpdatePeiProjet(true)}
          displayButtonEdit={displayEditDeleteButton}
          displayButtonDelete={displayEditDeleteButton}
          onClickDelete={() => {
            dataPeiProjetLayer.getSource().refresh();
            refreshLayerGeoserver(map);
            overlay?.setPosition(undefined);
          }}
          deletePath={
            "/api/couverture-hydraulique/pei-projet/" +
            featureSelect?.getProperties().elementId
          }
          disabled={disabled}
        />
      </div>
      <Volet
        handleClose={handleCloseUpdatePeiProjet}
        show={showUpdatePeiProjet}
        className="w-auto"
      >
        <UpdatePeiProjet
          etudeId={etudeId}
          peiProjetId={featureSelect?.getProperties().elementId}
          coordonneeX={
            featureSelect?.getProperties().geometry.getFlatCoordinates()[0]
          }
          coordonneeY={
            featureSelect?.getProperties().geometry.getFlatCoordinates()[1]
          }
          srid={map.getView().getProjection().getCode().split(":").pop()}
          onSubmit={() => {
            handleCloseUpdatePeiProjet();
            overlay?.setPosition(undefined);
          }}
        />
      </Volet>
    </>
  );
};

export const TooltipMapEditEvenement = ({
  map,
  disabled,
  criseId,
  state,
}: {
  map: Map;
  disabled: boolean;
  criseId: string;
  state: string;
}) => {
  const ref = useRef(null);
  const [showUpdateEvenement, setShowUpdateEvenement] = useState(false);
  const [isReadOnly, setIsReadOnly] = useState(false);

  const handleCloseUpdateEvenement = () => setShowUpdateEvenement(false);
  const { featureSelect, overlay } = useTooltipMap({
    ref: ref,
    map: map,
    disabled: disabled,
  });
  const eventId = featureSelect?.getProperties().elementId;
  const displayButton = featureSelect?.getProperties().elementId != null;
  const srid = map.getView().getProjection().getCode().split(":")[1];

  if (disabled) {
    overlay?.setPosition(undefined);
  }

  return (
    <div ref={ref}>
      <Tooltip
        featureSelect={featureSelect}
        overlay={overlay}
        onClickEdit={() => {
          setShowUpdateEvenement(true), setIsReadOnly(false);
        }}
        displayButtonEdit={displayButton}
        displayButtonSee={displayButton}
        onClickSee={() => {
          setShowUpdateEvenement(true), setIsReadOnly(true);
        }}
      />
      <Volet
        handleClose={handleCloseUpdateEvenement}
        show={showUpdateEvenement}
        className="w-auto"
      >
        <UpdateEvenement
          state={state}
          readOnly={isReadOnly}
          criseId={criseId}
          evenementId={eventId}
          geometrieEvenement={`SRID=${srid};${featureSelect && new WKT().writeFeature(featureSelect)}`}
          onSubmit={() => {
            handleCloseUpdateEvenement();
            overlay?.setPosition(undefined);
          }}
        />
      </Volet>
    </div>
  );
};

export const TooltipMapEditPeiPrescrit = ({
  map,
  disabledEditPeiPrescrit = false,
  dataPeiPrescritLayer,
  disabled,
}: {
  map: Map;
  disabledEditPeiPrescrit: boolean;
  dataPeiPrescritLayer: any;
  disabled: boolean;
}) => {
  const ref = useRef(null);
  const [showUpdatePeiPrescrit, setShowUpdatePeiPrescrit] = useState(false);
  const handleCloseUpdatePeiPrescrit = () => setShowUpdatePeiPrescrit(false);

  const { featureSelect, overlay } = useTooltipMap({
    ref: ref,
    map: map,
    disabled: disabled,
  });
  const displayEditDeleteButton =
    !disabledEditPeiPrescrit &&
    featureSelect?.getProperties().typeElementCarte === "PEI_PRESCRIT" &&
    featureSelect?.getProperties().elementId != null;

  if (disabled) {
    overlay?.setPosition(undefined);
  }
  return (
    <div ref={ref}>
      <Tooltip
        featureSelect={featureSelect}
        overlay={overlay}
        onClickEdit={() => {
          setShowUpdatePeiPrescrit(true), overlay?.setPosition(undefined);
        }}
        displayButtonEdit={displayEditDeleteButton}
        displayButtonDelete={displayEditDeleteButton}
        onClickDelete={() => {
          dataPeiPrescritLayer.getSource().refresh();
          refreshLayerGeoserver(map);
          overlay?.setPosition(undefined);
        }}
        deletePath={
          "/api/pei-prescrit/" + featureSelect?.getProperties().elementId
        }
        disabled={disabled}
      />
      <Volet
        handleClose={handleCloseUpdatePeiPrescrit}
        show={showUpdatePeiPrescrit}
        className="w-auto"
      >
        <UpdatePeiPrescrit
          peiPrescritId={featureSelect?.getProperties().elementId}
          coordonneeX={
            featureSelect?.getProperties().geometry.getFlatCoordinates()[0]
          }
          coordonneeY={
            featureSelect?.getProperties().geometry.getFlatCoordinates()[1]
          }
          srid={map.getView().getProjection().getCode().split(":")[1]}
          onSubmit={() => {
            handleCloseUpdatePeiPrescrit();
            dataPeiPrescritLayer.getSource().refresh();
            overlay?.setPosition(undefined);
          }}
        />
      </Volet>
    </div>
  );
};

export const TooltipMapEditPermis = ({
  map,
  disabledEditPermis = false,
  dataPermisLayer,
  disabled,
  hasRightToInteract = false,
}: {
  map: Map;
  disabledEditPermis: boolean;
  dataPermisLayer: any;
  disabled: boolean;
  hasRightToInteract: boolean;
}) => {
  const ref = useRef(null);
  const [showUpdatePermis, setShowUpdatePermis] = useState(false);
  const handleCloseUpdatePermis = () => setShowUpdatePermis(false);
  const [showPermisReadOnly, setShowPermisReadOnly] = useState(false);
  const handleClosePermisReadOnly = () => setShowPermisReadOnly(false);

  const { featureSelect, overlay } = useTooltipMap({
    ref: ref,
    map: map,
    disabled: disabled,
  });
  const displayEditDeleteButton =
    hasRightToInteract &&
    !disabledEditPermis &&
    featureSelect?.getProperties().typeElementCarte === "PERMIS" &&
    featureSelect?.getProperties().elementId != null;
  if (disabled) {
    overlay?.setPosition(undefined);
  }
  return (
    <div ref={ref}>
      <Tooltip
        featureSelect={featureSelect}
        overlay={overlay}
        onClickEdit={() => setShowUpdatePermis(true)}
        displayButtonEdit={displayEditDeleteButton}
        displayButtonDelete={displayEditDeleteButton}
        onClickDelete={() => {
          dataPermisLayer.getSource().refresh();
          refreshLayerGeoserver(map);
          overlay?.setPosition(undefined);
        }}
        deletePath={"/api/permis/" + featureSelect?.getProperties().elementId}
        disabled={disabled}
        displayButtonSee={true}
        onClickSee={() => setShowPermisReadOnly(true)}
      />
      <Volet
        handleClose={handleCloseUpdatePermis}
        show={showUpdatePermis}
        className="w-auto"
      >
        <UpdatePermis
          permisId={featureSelect?.getProperties().elementId}
          coordonneeX={
            featureSelect?.getProperties().geometry.getFlatCoordinates()[0]
          }
          coordonneeY={
            featureSelect?.getProperties().geometry.getFlatCoordinates()[1]
          }
          srid={map.getView().getProjection().getCode().split(":")[1]}
          onSubmit={() => {
            handleCloseUpdatePermis();
            overlay?.setPosition(undefined);
          }}
          readOnly={false}
        />
      </Volet>
      <Volet
        handleClose={handleClosePermisReadOnly}
        show={showPermisReadOnly}
        className="w-auto"
      >
        <UpdatePermis
          permisId={featureSelect?.getProperties().elementId}
          coordonneeX={
            featureSelect?.getProperties().geometry.getFlatCoordinates()[0]
          }
          coordonneeY={
            featureSelect?.getProperties().geometry.getFlatCoordinates()[1]
          }
          srid={map.getView().getProjection().getCode().split(":")[1]}
          readOnly={true}
        />
      </Volet>
    </div>
  );
};

export const TooltipMapAdresse = ({ map }: { map: Map }) => {
  const ref = useRef(null);

  const { featureSelect, overlay } = useTooltipMap({
    ref: ref,
    map: map,
  });

  return (
    <div ref={ref}>
      <Tooltip
        featureSelect={featureSelect}
        overlay={overlay}
        displayButtonEdit={false}
        displayButtonDelete={false}
        displayButtonSee={false}
      />
    </div>
  );
};

/**
 * Permet d'observer quel point est cliqué par l'utilisateur
 */
const useTooltipMap = ({
  ref,
  map,
  disabled = false,
}: {
  ref: Ref<HTMLDivElement>;
  map: Map;
}) => {
  const [featureSelect, setFeatureSelect] = useState<Feature | null>(null);
  const [overlay, setOverlay] = useState<Overlay | undefined>(
    new Overlay({
      positioning: "bottom-center",
      stopEvent: true,
    }),
  );

  useEffect(() => {
    if (map && ref.current != null && !disabled) {
      map.on("singleclick", (event) => {
        const pixel = map.getEventPixel(event.originalEvent);

        map.forEachFeatureAtPixel(pixel, function (feature) {
          const coordinate = event.coordinate;
          setFeatureSelect(feature);

          const over = new Overlay({
            element: ref.current,
            positioning: "bottom-center",
            position: coordinate,
            autoPan: {
              animation: {
                duration: 250,
              },
            },
          });

          map.addOverlay(over);
          setOverlay(over);
        });
      });
    }
  }, [map, ref, disabled]);

  return { featureSelect, overlay };
};

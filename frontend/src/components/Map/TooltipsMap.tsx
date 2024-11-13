import { Feature, Map, Overlay } from "ol";
import { ReactNode, Ref, useEffect, useRef, useState } from "react";
import { Button, Col, Popover, Row } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import UpdatePeiProjet from "../../pages/CouvertureHydraulique/PeiProjet/UpdatePeiProjet.tsx";
import { URLS } from "../../routes.tsx";
import DeleteButtonWithModale from "../Button/DeleteButtonWithModale.tsx";
import { IconClose, IconEdit, IconSee } from "../Icon/Icon.tsx";
import Volet from "../Volet/Volet.tsx";
import FicheResume from "../../pages/Pei/FicheResume/FicheResume.tsx";

/**
 * Permet d'afficher une tooltip sur la carte lorsque l'utilisateur clique sur un point
 * @param map : la carte
 * @returns la tooltip
 */
const TooltipMapPei = ({
  map,
  displayButtonEdit,
  displayButtonDelete,
  dataPeiLayer,
  disabledTooltip = false,
}: {
  map: Map;
  displayButtonEdit: boolean;
  displayButtonDelete: boolean;
  dataPeiLayer: any;
  disabledTooltip: boolean;
}) => {
  const ref = useRef(null);
  const navigate = useNavigate();
  const { featureSelect, overlay } = useTooltipMap({ ref: ref, map: map });

  const [showFichePei, setShowFichePei] = useState(false);
  const handleCloseFichePei = () => setShowFichePei(false);

  const peiId = featureSelect?.getProperties().pointId;
  return (
    <div ref={ref}>
      <Tooltip
        featureSelect={featureSelect}
        overlay={overlay}
        displayButtonEdit={displayButtonEdit}
        deletePath={`/api/pei/delete/` + peiId}
        displayButtonDelete={displayButtonDelete}
        onClickEdit={() => navigate(URLS.UPDATE_PEI(peiId))}
        onClickDelete={() => {
          dataPeiLayer.getSource().refresh();
          overlay?.setPosition(undefined);
        }}
        disabled={disabledTooltip}
        autreActionBouton={
          <Button variant="primary" onClick={() => setShowFichePei(true)}>
            <IconSee />
          </Button>
        }
      />
      <Volet
        handleClose={handleCloseFichePei}
        show={showFichePei}
        className="w-auto"
      >
        <FicheResume peiId={peiId} />
      </Volet>
    </div>
  );
};

export default TooltipMapPei;

const Tooltip = ({
  featureSelect,
  overlay,
  displayButtonEdit = false,
  onClickEdit,
  displayButtonDelete = false,
  onClickDelete,
  deletePath,
  disabled = false,
  href = undefined,
  autreActionBouton,
}: {
  featureSelect: Feature | undefined;
  overlay: Overlay | undefined;
  displayButtonEdit?: boolean;
  onClickEdit?: () => void;
  displayButtonDelete: boolean;
  onClickDelete?: () => void;
  deletePath: string;
  disabled: boolean;
  href: string;
  autreActionBouton: ReactNode | undefined;
}) => {
  return (
    <>
      {featureSelect?.getProperties().pointId && !disabled && (
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
              <Col>Information</Col>
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
            {Object.entries(featureSelect.getProperties()).map(
              ([key, value]) =>
                key !== "geometry" && (
                  <div key={key}>
                    <span className="fw-bold">{key}</span> : {value}{" "}
                  </div>
                ),
            )}
            <Row className="mt-3">
              <Col className="ms-auto" xs={"auto"}>
                <Row>
                  {autreActionBouton && (
                    <Col className="p-1" xs={"auto"}>
                      {autreActionBouton}
                    </Col>
                  )}
                  {displayButtonEdit && (
                    <Col className="p-1" xs={"auto"}>
                      <Button
                        variant="info"
                        className={"text-white"}
                        onClick={onClickEdit}
                        href={href}
                      >
                        <IconEdit />
                      </Button>
                    </Col>
                  )}
                  {displayButtonDelete && (
                    <Col className="p-1" xs={"auto"}>
                      <DeleteButtonWithModale
                        path={deletePath}
                        disabled={!displayButtonDelete}
                        title={false}
                        reload={onClickDelete}
                      />
                    </Col>
                  )}
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
    featureSelect?.getProperties().typePointCarte === "PEI_PROJET" &&
    featureSelect?.getProperties().pointId != null;

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
            overlay?.setPosition(undefined);
          }}
          deletePath={
            "/api/couverture-hydraulique/pei-projet/" +
            featureSelect?.getProperties().pointId
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
          peiProjetId={featureSelect?.getProperties().pointId}
          coordonneeX={
            featureSelect?.getProperties().geometry.getFlatCoordinates()[0]
          }
          coordonneeY={
            featureSelect?.getProperties().geometry.getFlatCoordinates()[1]
          }
          srid={map.getView().getProjection().getCode().split(":")[1]}
          onSubmit={() => {
            handleCloseUpdatePeiProjet();
            overlay?.setPosition(undefined);
          }}
        />
      </Volet>
    </>
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
      stopEvent: false,
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
            stopEvent: false,
          });

          map.addOverlay(over);
          setOverlay(over);
        });
      });
    }
  }, [map, ref, disabled]);

  return { featureSelect, overlay };
};

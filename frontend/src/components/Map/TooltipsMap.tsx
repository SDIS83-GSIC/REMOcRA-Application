import { Feature, Map, Overlay } from "ol";
import { useEffect, useRef, useState } from "react";
import { Button, Col, Popover, Row } from "react-bootstrap";
import UpdatePeiProjet from "../../pages/CouvertureHydraulique/PeiProjet/UpdatePeiProjet.tsx";
import { IconClose, IconEdit } from "../Icon/Icon.tsx";
import Volet from "../Volet/Volet.tsx";
import DeleteButtonWithModale from "../Button/DeleteButtonWithModale.tsx";

/**
 * Permet d'afficher une tooltip sur la carte lorsque l'utilisateur clique sur un point
 * @param map : la carte
 * @returns la tooltip
 */
const TooltipMapInfo = ({ map }: { map: Map }) => {
  const ref = useRef(null);
  const { featureSelect, overlay } = useTooltipMap({ ref: ref, map: map });

  return (
    <div ref={ref}>
      <Tooltip featureSelect={featureSelect} overlay={overlay} />
    </div>
  );
};

export default TooltipMapInfo;

const Tooltip = ({
  featureSelect,
  overlay,
  displayButtonEdit = false,
  onClickEdit,
  displayButtonDelete = false,
  onClickDelete,
  deletePath,
}: {
  featureSelect: Feature | undefined;
  overlay: Overlay | undefined;
  displayButtonEdit?: boolean;
  onClickEdit?: () => void;
  displayButtonDelete: boolean;
  onClickDelete?: () => void;
  deletePath: string;
}) => {
  return (
    <>
      {featureSelect?.getProperties().pointId && (
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
            {displayButtonEdit && (
              <Row className="mt-3">
                <Col className="ms-auto" xs={"auto"}>
                  <Button variant="primary" onClick={onClickEdit}>
                    <IconEdit /> Modifier
                  </Button>
                </Col>
              </Row>
            )}
            {displayButtonDelete && (
              <Row className="mt-3">
                <Col className="ms-auto" xs={"auto"}>
                  <DeleteButtonWithModale
                    path={deletePath}
                    disabled={!displayButtonDelete}
                    title={true}
                    reload={onClickDelete}
                  />
                </Col>
              </Row>
            )}
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
}: {
  map: Map;
  etudeId: string;
  disabledEditPeiProjet: boolean;
  dataPeiProjetLayer: any;
}) => {
  const ref = useRef(null);
  const [showUpdatePeiProjet, setShowUpdatePeiProjet] = useState(false);
  const handleCloseUpdatePeiProjet = () => setShowUpdatePeiProjet(false);

  const { featureSelect, overlay } = useTooltipMap({ ref: ref, map: map });
  const displayEditDeleteButton =
    !disabledEditPeiProjet &&
    featureSelect?.getProperties().typePointCarte === "PEI_PROJET" &&
    featureSelect?.getProperties().pointId != null;
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
const useTooltipMap = ({ ref, map }) => {
  const [featureSelect, setFeatureSelect] = useState<Feature | null>(null);
  const [overlay, setOverlay] = useState<Overlay | undefined>(
    new Overlay({
      positioning: "bottom-center",
      stopEvent: false,
    }),
  );

  useEffect(() => {
    if (map && ref.current != null) {
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
  }, [map, ref]);

  return { featureSelect, overlay };
};

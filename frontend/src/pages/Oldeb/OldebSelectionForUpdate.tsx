import { Map as OLMap } from "ol";
import VectorLayer from "ol/layer/Vector";
import { Button, CloseButton, Col, Row } from "react-bootstrap";
import { useAppContext } from "../../components/App/AppProvider.tsx";
import DeleteButtonWithModal from "../../components/Button/DeleteButtonWithModal.tsx";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { IconEdit, IconOldeb } from "../../components/Icon/Icon.tsx";
import { refreshLayerGeoserver } from "../../components/Map/MapUtils.tsx";
import TooltipCustom from "../../components/Tooltip/Tooltip.tsx";
import { hasDroit } from "../../droits.tsx";
import TYPE_DROIT from "../../enums/DroitEnum.tsx";

interface OldebSelectionForUpdateProps {
  editOldebs: string | null;
  onClose: () => void;
  onEdit: (id: string) => void;
  selectedOldebId: string | null;
  setSelectedOldebId: (id: string | null) => void;
  dataOldebLayer: VectorLayer;
  map: OLMap | undefined;
  closeEdit: () => void;
}

const OldebSelectionForUpdate = ({
  editOldebs,
  onClose,
  onEdit,
  selectedOldebId,
  setSelectedOldebId,
  dataOldebLayer,
  map,
  closeEdit,
}: OldebSelectionForUpdateProps) => {
  const { user } = useAppContext();
  let oldebsArray: { oldebId: string; properties: string }[] = [];
  if (editOldebs) {
    oldebsArray = JSON.parse(editOldebs);
  }

  return (
    <div className="bg-light p-3 h-100 d-flex flex-column">
      <div className="d-flex justify-content-between align-items-center mb-2 flex-shrink-0">
        <PageTitle
          icon={<IconOldeb />}
          displayReturnButton={false}
          title={"Modifier une OLDEB"}
        />
        <CloseButton aria-label="Fermer" onClick={onClose} />
      </div>
      <div className="overflow-auto flex-grow-1">
        {oldebsArray.map((oldebs) => (
          <div
            key={oldebs.oldebId}
            className={`card bg-secondary mb-3 rounded${selectedOldebId === oldebs.oldebId ? " border-primary border-3" : ""}`}
            onMouseEnter={() => setSelectedOldebId(oldebs.oldebId)}
            onMouseLeave={() => setSelectedOldebId(null)}
          >
            <div className="card-body">
              <Row className="justify-content-center align-items-center">
                <Col
                  xs={8}
                  className="card-text"
                  dangerouslySetInnerHTML={{ __html: oldebs.properties }}
                />
                <Col
                  xs={1}
                  className="d-flex justify-content-center align-items-center"
                >
                  <TooltipCustom
                    tooltipText={"Modifier cette OLDEB"}
                    tooltipId={oldebs.oldebId}
                  >
                    <Button
                      variant={"link"}
                      className={"p-0 m-0 text-decoration-none text-info"}
                      onClick={() => {
                        onEdit(oldebs.oldebId);
                        setSelectedOldebId(oldebs.oldebId);
                      }}
                    >
                      <IconEdit />
                    </Button>
                  </TooltipCustom>
                </Col>
                <Col className="d-flex justify-content-center align-items-center">
                  <TooltipCustom
                    tooltipText={"Supprimer cette OLDEB"}
                    tooltipId={oldebs.oldebId}
                  >
                    <DeleteButtonWithModal
                      path={`/api/oldeb/${oldebs.oldebId}`}
                      disabled={!hasDroit(user, TYPE_DROIT.OLDEB_D)}
                      title={false}
                      header={"Suppression d'une OLDEB"}
                      content={`Voulez-vous supprimer cette OLDEB ?`}
                      variant="link"
                      className="text-decoration-none text-danger"
                      reload={() => {
                        closeEdit();
                        dataOldebLayer.getSource()?.refresh();
                        refreshLayerGeoserver(map);
                      }}
                    />
                  </TooltipCustom>
                </Col>
              </Row>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default OldebSelectionForUpdate;

import Map from "ol/Map";
import { WKT } from "ol/format";
import { Button, Col, Container, Row } from "react-bootstrap";
import { useAppContext } from "../../../components/App/AppProvider.tsx";
import CreateButton from "../../../components/Button/CreateButton.tsx";
import { IconExport, IconLocation } from "../../../components/Icon/Icon.tsx";
import EditModal from "../../../components/Modal/EditModal.tsx";
import useModal from "../../../components/Modal/ModalUtils.tsx";
import QueryTable, {
  useFilterContext,
} from "../../../components/Table/QueryTable.tsx";
import {
  ButtonType,
  TYPE_BUTTON,
} from "../../../components/Table/TableActionColumn.tsx";
import { ActionColumn } from "../../../components/Table/columns.tsx";
import { hasDroit, isAuthorized } from "../../../droits.tsx";
import TYPE_DROIT from "../../../enums/DroitEnum.tsx";
import url from "../../../module/fetch.tsx";
import { shortenString } from "../../../utils/fonctionsUtils.tsx";
import formatDateTime from "../../../utils/formatDateUtils.tsx";
import AddDocForm, {
  getInitialValue,
  prepareVariables,
  ValidationSchema,
} from "./AddDocForm.tsx";
import filterValuesToVariable from "./FilterCriseDocument.tsx";

const ListDocument = ({
  criseId,
  onSubmit,
  map,
}: {
  criseId: string;
  onSubmit: any;
  map: Map;
}) => {
  const { visible, show, close } = useModal();
  const { user } = useAppContext();
  const listeButton: ButtonType[] = [];

  const showEventLocation = (geometry: string) => {
    const geom = new WKT().readFeature(geometry.split(";").pop());
    map?.getView().fit(geom.get("geometry"));
  };

  if (hasDroit(user, TYPE_DROIT.CRISE_U)) {
    listeButton.push({
      row: (row: any) => {
        return row;
      },
      type: TYPE_BUTTON.DELETE,
      textEnable: "Supprimer le document",
      pathname: url`/api/crise/documents/supprimer/`,
      classEnable: "danger",
    });
  }

  listeButton.push({
    row: (row: any) => {
      return row;
    },
    classEnable: "info",
    route: (documentId) => url`/api/documents/telecharger/${documentId}`,
    type: TYPE_BUTTON.BUTTON,
    icon: <IconExport />,
    textEnable: "Télécharger",
  });

  return (
    <Container>
      {isAuthorized(user, [TYPE_DROIT.CRISE_C, TYPE_DROIT.CRISE_U]) && (
        <Row>
          <Col xs={12} className="d-flex justify-content-end">
            <CreateButton
              title={"Ajouter un document"}
              classnames={"my-2"}
              onClick={() => {
                show();
              }}
            />
          </Col>
        </Row>
      )}
      <QueryTable
        query={url`/api/crise/documents/getAllFromCrise/${criseId}`}
        columns={[
          {
            Header: "Origine",
            accessor: "type",
          },
          {
            Header: "Libellé",
            accessor: "documentNomFichier",
            sortField: "documentNomFichier",
            Cell: (value) => {
              return <div>{shortenString(value.value, 20)}</div>;
            },
          },
          {
            Header: "Mise à jour le",
            accessor: "documentDate",
            sortField: "documentDate",
            Cell: (value) => {
              return (
                <div>{value?.value != null && formatDateTime(value.value)}</div>
              );
            },
          },

          {
            Header: "Localiser",
            accessor: "documentGeometrie",
            Cell: (value) => {
              return (
                <>
                  <Button
                    disabled={value.value == null}
                    style={{ backgroundColor: "transparent", border: "none" }}
                    className={"text-warning"}
                    onClick={() => {
                      showEventLocation(value.value);
                    }}
                  >
                    <IconLocation />
                  </Button>
                </>
              );
            },
          },
          ActionColumn({
            Header: "Actions",
            accessor: "documentId",
            buttons: listeButton,
          }),
        ]}
        idName={"tableModuleDocument"}
        filterValuesToVariable={filterValuesToVariable}
        filterContext={useFilterContext({
          documentDate: undefined,
          documentNomFichier: undefined,
          type: undefined,
        })}
      />

      {/* affichage fenêtre d'ajout de doc */}
      <EditModal
        closeModal={close}
        canModify={true}
        query={url`/api/crise/document/addDocument/${criseId}`}
        submitLabel={"Valider"}
        visible={visible}
        isMultipartFormData={true}
        header={null}
        validationSchema={ValidationSchema}
        onSubmit={onSubmit}
        prepareVariables={(values) => prepareVariables(values)}
        getInitialValues={() => getInitialValue()}
      >
        <AddDocForm />
      </EditModal>
    </Container>
  );
};

export default ListDocument;

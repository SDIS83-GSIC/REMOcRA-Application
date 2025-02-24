import { Button, Container } from "react-bootstrap";
import { ActionColumn } from "../../../components/Table/columns.tsx";
import { IconExport } from "../../../components/Icon/Icon.tsx";
import QueryTable, {
  useFilterContext,
} from "../../../components/Table/QueryTable.tsx";
import url from "../../../module/fetch.tsx";
import formatDateTime from "../../../utils/formatDateUtils.tsx";
import { shortenString } from "../../../utils/fonctionsUtils.tsx";
import useModal from "../../../components/Modal/ModalUtils.tsx";
import EditModal from "../../../components/Modal/EditModal.tsx";
import { hasDroit } from "../../../droits.tsx";
import TYPE_DROIT from "../../../enums/DroitEnum.tsx";
import { useAppContext } from "../../../components/App/AppProvider.tsx";
import UtilisateurEntity from "../../../Entities/UtilisateurEntity.tsx";
import {
  ButtonType,
  TYPE_BUTTON,
} from "../../../components/Table/TableActionColumn.tsx";
import CreateButton from "../../../components/Button/CreateButton.tsx";
import AddDocForm, {
  getInitialValue,
  prepareVariables,
  ValidationSchema,
} from "./AddDocForm.tsx";
import filterValuesToVariable from "./FilterCriseDocument.tsx";

const ListDocument = ({
  criseId,
  onSubmit,
}: {
  criseId: string;
  onSubmit: any;
}) => {
  const { visible, show, close } = useModal();
  const { user }: { user: UtilisateurEntity } = useAppContext();
  const listeButton: ButtonType[] = [];

  if (hasDroit(user, TYPE_DROIT.CRISE_U)) {
    listeButton.push({
      row: (row) => {
        return row;
      },
      type: TYPE_BUTTON.DELETE,
      textEnable: "Clore la crise",
      pathname: url`/api/crise/documents/supprimer/`,
      classEnable: "danger",
    });
  }

  return (
    <Container>
      <CreateButton
        title={"Ajouter un document"}
        onClick={() => {
          show();
        }}
      />

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
            Header: "Télécharger",
            accessor: "documentId",
            Cell: (value) => {
              return (
                <div>
                  <Button
                    style={{ backgroundColor: "transparent", border: "none" }}
                    className={"text-warning"}
                    href={url`/api/documents/telecharger/` + value.value}
                  >
                    <IconExport />
                  </Button>
                </div>
              );
            },
          },
          ActionColumn({
            Header: "Supprimer",
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

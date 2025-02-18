import { ReactNode } from "react";
import { Button, Col } from "react-bootstrap";
import url from "../../module/fetch.tsx";
import AddRemoveComponent from "../AddRemoveComponent/AddRemoveComponent.tsx";
import { FileInput } from "./Form.tsx";

export type Document = {
  documentId: string | null;
  documentNomFichier: string;
  data: File;
};

/**
 * Formulaire utilisé pour ajouter ou supprimer des documents
 * La variable dans formik doit s'appeler obligatoirement "documents"
 * @param documents liste des documents
 * @param setFieldValue fonction formik
 * @param defaultOtherProperties si l'object document n'a pas qu'un id, un nom, alors on envoie un objet contenant les autres propriétés à définir par défaut
 * @param otherFormParam si l'object document n'a pas qu'un id, un nom alors on fournit l'autre partie du formulaire à répéter
 */
const FormDocuments = ({
  documents,
  setFieldValue,
  otherFormParam,
  defaultOtherProperties,
  disabled,
  readOnly,
}: {
  documents: Document[];
  defaultOtherProperties: any;
  setFieldValue: (champ: string, newValue: any | undefined) => void;
  otherFormParam: (index: number, listeElements: any[]) => ReactNode;
  disabled: boolean;
  readOnly: boolean;
}) => {
  function formDocumentsToRepeat(index: number, listeElements: any[]) {
    return (
      <>
        {listeElements[index].documentId != null ? (
          <Col>
            <Button
              variant="link"
              href={
                url`/api/documents/telecharger/` +
                listeElements[index].documentId
              }
            >
              {listeElements[index].documentNomFichier}
            </Button>
          </Col>
        ) : (
          <Col>{listeElements[index].documentNomFichier}</Col>
        )}
        {otherFormParam && otherFormParam(index, listeElements)}
      </>
    );
  }

  return (
    <>
      <FileInput
        name="documentCourant"
        accept="*.*"
        label="Gérer les documents"
        required={false}
        onChange={(e) => {
          documents.push({
            documentNomFichier: e.target.files[0].name,
            data: e.target.files[0],
            ...defaultOtherProperties,
          });
          setFieldValue("documents", documents);
        }}
        disabled={disabled}
        readOnly={readOnly}
      />
      <AddRemoveComponent
        name="documents"
        createComponentToRepeat={formDocumentsToRepeat}
        listeElements={documents ?? []}
        canAdd={false}
        disabled={disabled}
        readOnly={readOnly}
      />
    </>
  );
};

export function setDocumentInFormData(
  documentsApresModif: Document[],
  documentsAvantModif: Document[],
  formData: FormData,
) {
  documentsApresModif.map((e) => {
    if (e.data != null) {
      formData.append("document_" + e.documentNomFichier, e.data);
    }
  });

  formData.append("documents", JSON.stringify(documentsApresModif));
  formData.append(
    "listeDocsToRemove",
    JSON.stringify(
      documentsAvantModif
        ?.filter(
          (e) =>
            !documentsApresModif
              .map((e) => e.documentId)
              .includes(e.documentId),
        )
        .filter((e) => e != null)
        .map((e) => e.documentId) ?? [],
    ),
  );
}

export default FormDocuments;

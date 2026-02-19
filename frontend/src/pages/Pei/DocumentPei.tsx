import { Container } from "react-bootstrap";
import Loading from "../../components/Elements/Loading/Loading.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import url from "../../module/fetch.tsx";

const DocumentPei = ({
  peiId,
  titre = null,
}: {
  peiId: string;
  titre?: string | null;
}) => {
  const documentsPei = useGet(url`/api/documents/pei/${peiId}`);

  type DocumentType = {
    documentId: string;
    documentNomFichier: string;
    isPhotoPei: boolean;
  };
  if (!documentsPei.isResolved) {
    return <Loading />;
  }

  const LinkDocument = ({
    documentId,
    documentNomFichier,
  }: {
    documentId: string;
    documentNomFichier: string;
  }) => {
    return (
      <a
        className="fs-6"
        href={url`/api/documents/telecharger/${documentId}`}
        target="_blank"
        rel="noopener noreferrer"
      >
        {documentNomFichier}
      </a>
    );
  };

  return (
    <>
      {titre && <h1>{titre}</h1>}
      <Container>
        {documentsPei.data &&
          (() => {
            const docs = documentsPei.data;
            const photoDoc = docs.find((doc: DocumentType) => doc.isPhotoPei);
            const otherDocs = docs.filter(
              (doc: DocumentType) => !doc.isPhotoPei,
            );
            return (
              <>
                {photoDoc && (
                  <div className="p-3 mb-3 bg-light border rounded d-flex align-items-center gap-2">
                    <span className="fw-bold fs-6">Photo du PEI :</span>
                    <LinkDocument
                      documentId={photoDoc.documentId}
                      documentNomFichier={photoDoc.documentNomFichier}
                    />
                  </div>
                )}
                {otherDocs.length > 0 && (
                  <ul className="fs-6">
                    {otherDocs.map((doc: DocumentType) => (
                      <li key={doc.documentId}>
                        <LinkDocument
                          documentId={doc.documentId}
                          documentNomFichier={doc.documentNomFichier}
                        />
                      </li>
                    ))}
                  </ul>
                )}
              </>
            );
          })()}
      </Container>
    </>
  );
};
export default DocumentPei;

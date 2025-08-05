import { Button, Col, Image, Row, Table } from "react-bootstrap";
import { classNames } from "@react-pdf-viewer/core";
import url from "../../module/fetch.tsx";
import { LinkType } from "../../pages/Accueil/AccueilPrive.tsx";
import { URLS } from "../../routes.tsx";
import formatDateTime, { formatDate } from "../../utils/formatDateUtils.tsx";
import CustomLinkButton from "../Button/CustomLinkButton.tsx";
import { IconExport } from "../Icon/Icon.tsx";

/**
 * Permet d'afficher un module dans la page d'accueil
 */

const ModuleRemocra = ({
  moduleId,
  type,
  titre,
  image,
  contenuHtml,
  listeLink,
  listeDocument,
}: ModuleRemocra) => {
  return (
    <Col className="bg-light border rounded">
      <div className="fw-bold text-center p-2">{titre}</div>
      <Row>
        {image && (
          <Col md="3">
            <Image src={image} alt={titre} fluid />
          </Col>
        )}
        <Col>
          {(type === TypeModuleRemocra.DOCUMENT ||
            type === TypeModuleRemocra.COURRIER) && (
            <ModuleDocumentCourrier
              listeDocument={listeDocument}
              moduleId={moduleId}
              moduleType={type}
            />
          )}
          {listeLink && <BuildLinks listeLink={listeLink} />}
          {contenuHtml && (
            <div dangerouslySetInnerHTML={{ __html: contenuHtml }} />
          )}
        </Col>
      </Row>
    </Col>
  );
};

const ModuleDocumentCourrier = ({
  listeDocument,
  moduleId,
  moduleType,
}: {
  listeDocument: {
    id: string;
    libelle: string;
    date: Date;
  }[];
  moduleId: string;
  moduleType: TypeModuleRemocra;
}) => {
  const isTypeDocument = moduleType === TypeModuleRemocra.DOCUMENT;
  const majWidth = isTypeDocument ? 100 : 150;

  return (
    <>
      <Table bordered striped>
        <thead>
          <tr>
            <th>Libellé</th>
            <th width={majWidth}>Mise à jour</th>
            <th width={50} />
          </tr>
        </thead>
        <tbody>
          {listeDocument?.map((e, index) => {
            return (
              <tr key={index}>
                <td>{e.libelle}</td>
                <td width={majWidth}>
                  {e.date &&
                    (isTypeDocument
                      ? formatDate(e.date)
                      : formatDateTime(e.date))}
                </td>
                <td width={50} className="p-0 m-0">
                  <Button
                    variant={"link"}
                    className={classNames(
                      "p-0 m-0",
                      "text-decoration-none text-primary",
                    )}
                    href={
                      moduleType === TypeModuleRemocra.DOCUMENT
                        ? url`/api/document-habilitable/telecharger/` + e.id
                        : url`/api/documents/telecharger/` + e.id
                    }
                  >
                    <IconExport />
                  </Button>
                </td>
              </tr>
            );
          })}
        </tbody>
      </Table>
      <CustomLinkButton
        variant="link"
        pathname={URLS.LIST_MODULE_DOCUMENT_COURRIER(moduleType, moduleId)}
      >
        Voir plus
      </CustomLinkButton>
    </>
  );
};

const BuildLinks = ({ listeLink }: { listeLink: LinkType[] }) => {
  return (
    <>
      {listeLink.map((e, key) => {
        if (e.aLeDroit === true) {
          return (
            <div key={key}>
              <CustomLinkButton className="text-underline" pathname={e.link}>
                {e.label}
              </CustomLinkButton>
            </div>
          );
        }
      })}
    </>
  );
};

export default ModuleRemocra;

type ModuleRemocra = {
  moduleId: string;
  type: TypeModuleRemocra;
  titre: string;
  image: string;
  contenuHtml: string;
  listeLink: LinkType[] | undefined;
  listeDocument: {
    id: string;
    libelle: string;
    date: Date;
  }[];
};

export enum TypeModuleRemocra {
  DECI = "DECI",
  COUVERTURE_HYDRAULIQUE = "COUVERTURE_HYDRAULIQUE",
  CARTOGRAPHIE_PERSONNALISEE = "CARTOGRAPHIE_PERSONNALISEE",
  OLDEBS = "OLDEBS",
  PERMIS = "PERMIS",
  RCI = "RCI",
  DFCI = "DFCI",
  ADRESSES = "ADRESSES",
  RISQUES = "RISQUES",
  ADMIN = "ADMIN",
  COURRIER = "COURRIER",
  DOCUMENT = "DOCUMENT",
  RAPPORT_PERSONNALISE = "RAPPORT_PERSONNALISE",
  PEI_PRESCRIT = "PEI_PRESCRIT",
  PERSONNALISE = "PERSONNALISE",
  DASHBOARD = "DASHBOARD",
  OPERATIONS_DIVERSES = "OPERATIONS_DIVERSES",
  CRISE = "CRISE",
}

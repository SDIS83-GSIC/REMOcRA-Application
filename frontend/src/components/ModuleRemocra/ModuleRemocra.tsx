import { Button, Col, Image, Row, Table } from "react-bootstrap";
import url from "../../module/fetch.tsx";
import { LinkType } from "../../pages/Accueil/Accueil.tsx";
import { URLS } from "../../routes.tsx";
import formatDateTime from "../../utils/formatDateUtils.tsx";
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
    <div className="bg-light p-2 border rounded">
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
    </div>
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
  return (
    <>
      <Table bordered striped>
        <thead>
          <tr>
            <th>Libellé</th>
            <th>Date de dernière mise à jour</th>
            <th />
          </tr>
        </thead>
        <tbody>
          {listeDocument?.map((e, index) => {
            return (
              <tr key={index}>
                <td>{e.libelle}</td>
                <td>{e.date && formatDateTime(e.date)}</td>
                <td>
                  <Button
                    className={"text-warning"}
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
  CARTOGRAPHIE = "CARTOGRAPHIE",
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

import { Col, Image, Row } from "react-bootstrap";
import { LinkType } from "../../pages/Accueil/Accueil.tsx";

/**
 * Permet d'afficher un module dans la page d'accueil
 */

const ModuleRemocra = ({
  titre,
  image,
  contenuHtml,
  listeLink,
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
          {listeLink && <BuildLinks listeLink={listeLink} />}
          {contenuHtml && (
            <div dangerouslySetInnerHTML={{ __html: contenuHtml }} />
          )}
        </Col>
      </Row>
    </div>
  );
};

const BuildLinks = ({ listeLink }: { listeLink: LinkType[] }) => {
  return (
    <>
      {listeLink.map((e, key) => {
        if (e.aLeDroit === true) {
          return (
            <div key={key}>
              <a href={e.link}>{e.label}</a>
            </div>
          );
        }
      })}
    </>
  );
};

export default ModuleRemocra;

type ModuleRemocra = {
  titre: string;
  image: string;
  contenuHtml: string;
  listeLink: LinkType[] | undefined;
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
  PERSONNALISE = "PERSONNALISE",
}

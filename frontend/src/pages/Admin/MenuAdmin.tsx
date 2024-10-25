import { Col, Container, Row } from "react-bootstrap";
import { URLS } from "../../routes.tsx";

const MenuAdmin = () => {
  return (
    <Container>
      <Row>
        <Col className="bg-light p-2 border rounded">
          <div className="fw-bold text-center p-2">Gérer les nomenclatures</div>
          <a href={URLS.LIST_TYPE_CANALISATION}>Types de canalisations</a>
          <br />
          <a href={URLS.LIST_DIAMETRE}>Types de diamètres</a>
          <br />
          <a href={URLS.LIST_TYPE_PENA_ASPIRATION}>
            Types de dispositifs d&apos;aspiration
          </a>
          <br />
          <a href={URLS.LIST_DOMAINE}>Domaines</a>
          <br />
          <a href={URLS.LIST_TYPE_ETUDE}>Types d&apos;études</a>
          <br />
          <a href={URLS.LIST_MARQUE_PIBI}>Marques de PIBI</a>
          <br />
          <a href={URLS.LIST_MATERIAU}>Matériaux</a>
          <br />
          <a href={URLS.LIST_MODELE_PIBI}>Modèles PIBI</a>
          <br />
          <a href={URLS.LIST_NATURE}>Natures de PEI</a>
          <br />
          <a href={URLS.LIST_NATURE_DECI}>Natures DECI</a>
          <br />
          <a href={URLS.LIST_NIVEAU}>Niveaux</a>
          <br />
          <a href={URLS.LIST_ORGANISME}>Organismes</a>
          <br />
          <a href={URLS.LIST_TYPE_RESEAU}>Types de réseaux</a>
          <br />
        </Col>
        {/* TODO à adapter quand on fera la page des droits */}
        <Col className="bg-light p-2 border rounded mx-2">
          <div className="fw-bold text-center p-2">Gérer les droits</div>
          <a href={URLS.LIST_PROFIL_ORGANSIME}>Profils d&apos;organismes</a>
          <br />
          <a href={URLS.LIST_PROFIL_UTILISATEUR}>Profils d&apos;utilisateurs</a>
          <br />
          <a href={URLS.LIST_TYPE_ORGANSIME}>Types d&apos;organismes</a>
          <br />
        </Col>
      </Row>
    </Container>
  );
};

export default MenuAdmin;

import { Col, Container, Row } from "react-bootstrap";
import { URLS } from "../../routes.tsx";

const MenuAdmin = () => {
  return (
    <Container>
      <Row>
        <Col className="bg-light p-2 border rounded mx-2">
          <div className="fw-bold text-center p-2">Administration générale</div>

          <a href={URLS.ADMIN_PARAMETRE}>Paramètres applicatifs</a>
          <br />
          <a href={URLS.ADMIN_FICHE_RESUME}>
            Configurer la fiche de résumé des PEI
          </a>
          <br />
          <a href={URLS.ADMIN_ACCUEIL}>Configurer la page d&apos;accueil</a>
          <br />
          <a href={URLS.LIST_ZONE_INTEGRATION}>Zones de compétence</a>
          <br />
          <a href={URLS.LIST_RAPPORT_PERSONNALISE}>Rapports personnalisés</a>
          <br />
          <a href={URLS.TASK}>Paramétrage des traitements</a>
          <br />
          <a href={URLS.ADMIN_IMPORT_RESSOURCES}>Import des ressources</a>
          <br />
        </Col>

        <Col className="bg-light p-2 border rounded mx-2">
          <div className="fw-bold text-center p-2">Gérer les droits</div>
          <a href={URLS.PROFIL_DROIT_LIST}>Groupes de fonctionnalités</a>
          <br />
          <a href={URLS.LIEN_DROIT_LIST}>Attribution des fonctionnalités</a>
          <br />
          <a href={URLS.LIST_TYPE_ORGANISME}>Types d&apos;organismes</a>
          <br />
          <a href={URLS.LIST_PROFIL_ORGANISME}>Profils d&apos;organismes</a>
          <br />
          <a href={URLS.LIST_PROFIL_UTILISATEUR}>Profils d&apos;utilisateurs</a>
          <br />
          <a href={URLS.LIEN_PROFIL_FONCTIONNALITE_LIST}>
            Liens profils / groupes de fonctionnalités
          </a>
          <br />
          <br />
          <a href={URLS.LIST_ORGANISME}>Organismes</a>
          <br />
          <a href={URLS.LIST_UTILISATEUR}>Utilisateurs</a>
          <br />
          <a href={URLS.COUCHES_LIST}>Couches cartographiques</a>
          <br />
        </Col>

        <Col className="bg-light p-2 border rounded">
          <div className="fw-bold text-center p-2">Gérer les nomenclatures</div>

          <a href={URLS.LIST_DOMAINE}>Domaines</a>
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
          <a href={URLS.LIST_ROLE_CONTACT}>Rôles des contacts</a>
          <br />
          <a href={URLS.LIST_THEMATIQUE}>Thématiques</a>
          <br />
          <a href={URLS.LIST_TYPE_CANALISATION}>Types de canalisations</a>
          <br />
          <a href={URLS.LIST_DIAMETRE}>Types de diamètres</a>
          <br />
          <a href={URLS.LIST_TYPE_PENA_ASPIRATION}>
            Types de dispositifs d&apos;aspiration
          </a>
          <br />
          <a href={URLS.LIST_TYPE_ETUDE}>Types d&apos;études</a>
          <br />
          <a href={URLS.LIST_TYPE_RESEAU}>Types de réseaux</a>
          <br />
        </Col>
      </Row>

      <Row className="mt-2">
        <Col className="bg-light p-2 border rounded">
          <div className="fw-bold text-center p-2">
            Gestionnaires &amp; sites
          </div>
          <a href={URLS.LIST_GESTIONNAIRE}>Gestionnaires</a>
          <br />
          <a href={URLS.LIST_SITE}>Sites</a>
          <br />
        </Col>
      </Row>
    </Container>
  );
};

export default MenuAdmin;

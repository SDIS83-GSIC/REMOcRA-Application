import { Col, Container, Nav, Row } from "react-bootstrap";
import CustomLinkButton from "../../components/Button/CustomLinkButton.tsx";
import { URLS } from "../../routes.tsx";

const MenuAdmin = () => {
  return (
    <Container>
      <Row>
        <Col className="bg-light p-2 border rounded mx-2">
          <div className="fw-bold text-center p-2">Administration générale</div>
          <Nav className="flex-column">
            <Nav.Item>
              <CustomLinkButton
                className="text-underline text-start"
                href={URLS.ADMIN_PARAMETRE}
              >
                Paramètres applicatifs
              </CustomLinkButton>
            </Nav.Item>
            <Nav.Item>
              <CustomLinkButton
                className="text-underline text-start"
                href={URLS.ADMIN_FICHE_RESUME}
              >
                Configurer la fiche de résumé des PEI
              </CustomLinkButton>
            </Nav.Item>
            <Nav.Item>
              <CustomLinkButton
                className="text-underline text-start"
                href={URLS.ADMIN_ACCUEIL}
              >
                Configurer la page d&apos;accueil
              </CustomLinkButton>
            </Nav.Item>
            <Nav.Item>
              <CustomLinkButton
                className="text-underline text-start"
                href={URLS.LIST_ZONE_INTEGRATION}
              >
                Zones de compétence
              </CustomLinkButton>
            </Nav.Item>
            <Nav.Item>
              <CustomLinkButton
                className="text-underline text-start"
                href={URLS.LIST_RAPPORT_PERSONNALISE}
              >
                Rapports personnalisés
              </CustomLinkButton>
            </Nav.Item>
            <Nav.Item>
              <CustomLinkButton
                className="text-underline text-start"
                href={URLS.TASK}
              >
                Paramétrage des traitements
              </CustomLinkButton>
            </Nav.Item>
            <Nav.Item>
              <CustomLinkButton
                className="text-underline text-start"
                href={URLS.ADMIN_IMPORT_RESSOURCES}
              >
                Import des ressources
              </CustomLinkButton>
            </Nav.Item>
          </Nav>
        </Col>

        <Col className="bg-light p-2 border rounded mx-2">
          <div className="fw-bold text-center p-2">Gérer les droits</div>

          <Nav className="flex-column">
            <Nav.Item>
              <CustomLinkButton
                className="text-underline text-start"
                href={URLS.PROFIL_DROIT_LIST}
              >
                Groupes de fonctionnalités
              </CustomLinkButton>
            </Nav.Item>
            <Nav.Item>
              <CustomLinkButton
                className="text-underline text-start"
                href={URLS.LIEN_DROIT_LIST}
              >
                Attribution des fonctionnalités
              </CustomLinkButton>
            </Nav.Item>
            <Nav.Item>
              <CustomLinkButton
                className="text-underline text-start"
                href={URLS.LIST_TYPE_ORGANISME}
              >
                Types d&apos;organismes
              </CustomLinkButton>
            </Nav.Item>
            <Nav.Item>
              <CustomLinkButton
                className="text-underline text-start"
                href={URLS.LIST_PROFIL_ORGANISME}
              >
                Profils d&apos;organismes
              </CustomLinkButton>
            </Nav.Item>
            <Nav.Item>
              <CustomLinkButton
                className="text-underline text-start"
                href={URLS.LIST_PROFIL_UTILISATEUR}
              >
                Profils d&apos;utilisateurs
              </CustomLinkButton>
            </Nav.Item>
            <Nav.Item>
              <CustomLinkButton
                className="text-underline text-start"
                href={URLS.LIEN_PROFIL_FONCTIONNALITE_LIST}
              >
                Liens profils / groupes de fonctionnalités
              </CustomLinkButton>
            </Nav.Item>
            <br />
            <Nav.Item>
              <CustomLinkButton
                className="text-underline text-start"
                href={URLS.LIST_ORGANISME}
              >
                Organismes
              </CustomLinkButton>
            </Nav.Item>
            <Nav.Item>
              <CustomLinkButton
                className="text-underline text-start"
                href={URLS.LIST_UTILISATEUR}
              >
                Utilisateurs
              </CustomLinkButton>
            </Nav.Item>
            <Nav.Item>
              <CustomLinkButton
                className="text-underline text-start"
                href={URLS.COUCHES_LIST}
              >
                Couches cartographiques
              </CustomLinkButton>
            </Nav.Item>

            <br />
            <Nav.Item>
              <CustomLinkButton
                className="text-underline text-start"
                href={URLS.LIEN_TYPE_ORGANISME_DROIT_API}
              >
                Attribution des droits API pour les types organismes
              </CustomLinkButton>
            </Nav.Item>
          </Nav>
        </Col>

        <Col className="bg-light p-2 border rounded mx-2">
          <div className="fw-bold text-center p-2">Gérer les nomenclatures</div>

          <Nav className="flex-column">
            <Nav.Item>
              <CustomLinkButton
                className="text-underline text-start"
                href={URLS.LIST_DOMAINE}
              >
                Domaines
              </CustomLinkButton>
            </Nav.Item>
            <Nav.Item>
              <CustomLinkButton
                className="text-underline text-start"
                href={URLS.LIST_MARQUE_PIBI}
              >
                Marques de PIBI
              </CustomLinkButton>
            </Nav.Item>
            <Nav.Item>
              <CustomLinkButton
                className="text-underline text-start"
                href={URLS.LIST_MATERIAU}
              >
                Matériaux
              </CustomLinkButton>
            </Nav.Item>
            <Nav.Item>
              <CustomLinkButton
                className="text-underline text-start"
                href={URLS.LIST_MODELE_PIBI}
              >
                Modèles PIBI
              </CustomLinkButton>
            </Nav.Item>
            <Nav.Item>
              <CustomLinkButton
                className="text-underline text-start"
                href={URLS.LIST_NATURE}
              >
                Natures de PEI
              </CustomLinkButton>
            </Nav.Item>
            <Nav.Item>
              <CustomLinkButton
                className="text-underline text-start"
                href={URLS.LIST_NATURE_DECI}
              >
                Natures DECI
              </CustomLinkButton>
            </Nav.Item>
            <Nav.Item>
              <CustomLinkButton
                className="text-underline text-start"
                href={URLS.LIST_NIVEAU}
              >
                Niveaux
              </CustomLinkButton>
            </Nav.Item>
            <Nav.Item>
              <CustomLinkButton
                className="text-underline text-start"
                href={URLS.LIST_ROLE_CONTACT}
              >
                Rôles des contacts
              </CustomLinkButton>
            </Nav.Item>
            <Nav.Item>
              <CustomLinkButton
                className="text-underline text-start"
                href={URLS.LIST_THEMATIQUE}
              >
                Thématiques
              </CustomLinkButton>
            </Nav.Item>
            <Nav.Item>
              <CustomLinkButton
                className="text-underline text-start"
                href={URLS.LIST_TYPE_CANALISATION}
              >
                Types de canalisations
              </CustomLinkButton>
            </Nav.Item>
            <Nav.Item>
              <CustomLinkButton
                className="text-underline text-start"
                href={URLS.LIST_DIAMETRE}
              >
                Types de diamètres
              </CustomLinkButton>
            </Nav.Item>
            <Nav.Item>
              <CustomLinkButton
                className="text-underline text-start"
                href={URLS.LIST_TYPE_PENA_ASPIRATION}
              >
                Types de dispositifs d&apos;aspiration
              </CustomLinkButton>
            </Nav.Item>
            <Nav.Item>
              <CustomLinkButton
                className="text-underline text-start"
                href={URLS.LIST_TYPE_ETUDE}
              >
                Types d&apos;études
              </CustomLinkButton>
            </Nav.Item>
            <Nav.Item>
              <CustomLinkButton
                className="text-underline text-start"
                href={URLS.LIST_TYPE_RESEAU}
              >
                Types de réseaux
              </CustomLinkButton>
            </Nav.Item>
          </Nav>
        </Col>
      </Row>

      <Row className="mt-2">
        <Col className="bg-light p-2 border rounded mx-2">
          <div className="fw-bold text-center p-2">
            Gestionnaires &amp; sites
          </div>

          <Nav className="flex-column">
            <Nav.Item>
              <CustomLinkButton
                className="text-underline text-start"
                href={URLS.LIST_GESTIONNAIRE}
              >
                Gestionnaires
              </CustomLinkButton>
            </Nav.Item>
            <Nav.Item>
              <CustomLinkButton
                className="text-underline text-start"
                href={URLS.LIST_SITE}
              >
                Sites
              </CustomLinkButton>
            </Nav.Item>
          </Nav>
        </Col>
        <Col className="bg-light p-2 border rounded mx-2">
          <div className="fw-bold text-center p-2">Tableaux de bord</div>

          <Nav className="flex-column">
            {/*  TODO type droit DASHBOARD_A */}
            <a href={URLS.DASHBOARD_ADMIN_QUERY}>
              Édition des requêtes et composants associés
            </a>
            <br />
            <a href={URLS.DASHBOARD_ADMIN_DASHBOARD}>
              Édition des tableaux de bord et profils associés
            </a>
            <br />
          </Nav>
        </Col>
      </Row>
    </Container>
  );
};

export default MenuAdmin;

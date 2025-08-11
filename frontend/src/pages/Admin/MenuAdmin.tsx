import { Col, Container, Nav, Row } from "react-bootstrap";
import CustomLinkButton from "../../components/Button/CustomLinkButton.tsx";
import { URLS } from "../../routes.tsx";
import { hasDroit, isAuthorized } from "../../droits.tsx";
import { useAppContext } from "../../components/App/AppProvider.tsx";
import TYPE_DROIT from "../../enums/DroitEnum.tsx";

const MenuAdmin = () => {
  const { user } = useAppContext();

  const hasDroitAdministrationGenerale = isAuthorized(user, [
    TYPE_DROIT.ADMIN_COURRIER,
    TYPE_DROIT.ADMIN_PARAM_APPLI,
    TYPE_DROIT.ADMIN_PARAM_APPLI_MOBILE,
    TYPE_DROIT.ADMIN_ZONE_COMPETENCE,
    TYPE_DROIT.ADMIN_RAPPORTS_PERSO,
    TYPE_DROIT.ADMIN_PARAM_TRAITEMENTS,
    TYPE_DROIT.ADMIN_ANOMALIES,
  ]);
  const hasDroitGererLesDroits = isAuthorized(user, [
    TYPE_DROIT.ADMIN_GROUPE_UTILISATEUR,
    TYPE_DROIT.ADMIN_DROITS,
    TYPE_DROIT.ADMIN_UTILISATEURS_A,
    TYPE_DROIT.ADMIN_COUCHE_CARTOGRAPHIQUE,
    TYPE_DROIT.ADMIN_API,
  ]);
  const hasDroitGererLesNomenclatures = isAuthorized(user, [
    TYPE_DROIT.ADMIN_NOMENCLATURE,
    TYPE_DROIT.ADMIN_ROLE_CONTACT,
    TYPE_DROIT.ADMIN_PARAM_APPLI,
    TYPE_DROIT.ADMIN_TYPE_ETUDE,
  ]);
  const hasDroitGestionnairesEtSites = isAuthorized(user, [
    TYPE_DROIT.GEST_SITE_A,
    TYPE_DROIT.GEST_SITE_R,
  ]);
  const hasDroitTableauxDeBord = hasDroit(user, TYPE_DROIT.DASHBOARD_A);

  return (
    <Container>
      {(hasDroitAdministrationGenerale ||
        hasDroitGererLesDroits ||
        hasDroitGererLesNomenclatures) && (
        <Row>
          {hasDroitAdministrationGenerale && (
            <Col className="bg-light p-2 border rounded mx-2">
              <div className="fw-bold text-center p-2">
                Administration générale
              </div>
              <Nav className="flex-column">
                {(hasDroit(user, TYPE_DROIT.ADMIN_PARAM_APPLI) ||
                  hasDroit(user, TYPE_DROIT.ADMIN_PARAM_APPLI_MOBILE)) && (
                  <Nav.Item>
                    <CustomLinkButton
                      className="text-underline text-start"
                      pathname={URLS.ADMIN_PARAMETRE}
                    >
                      Paramètres applicatifs
                    </CustomLinkButton>
                  </Nav.Item>
                )}
                {hasDroit(user, TYPE_DROIT.ADMIN_PARAM_APPLI) && (
                  <Nav.Item>
                    <CustomLinkButton
                      className="text-underline text-start"
                      pathname={URLS.ADMIN_FICHE_RESUME}
                    >
                      Configurer la fiche de résumé des PEI
                    </CustomLinkButton>
                  </Nav.Item>
                )}
                {hasDroit(user, TYPE_DROIT.ADMIN_PARAM_APPLI) && (
                  <Nav.Item>
                    <CustomLinkButton
                      className="text-underline text-start"
                      pathname={URLS.ADMIN_ACCUEIL}
                    >
                      Configurer la page d&apos;accueil
                    </CustomLinkButton>
                  </Nav.Item>
                )}
                {hasDroit(user, TYPE_DROIT.ADMIN_ZONE_COMPETENCE) && (
                  <Nav.Item>
                    <CustomLinkButton
                      className="text-underline text-start"
                      pathname={URLS.LIST_ZONE_INTEGRATION}
                    >
                      Zones de compétence
                    </CustomLinkButton>
                  </Nav.Item>
                )}
                {hasDroit(user, TYPE_DROIT.ADMIN_RAPPORTS_PERSO) && (
                  <Nav.Item>
                    <CustomLinkButton
                      className="text-underline text-start"
                      pathname={URLS.LIST_RAPPORT_PERSONNALISE}
                    >
                      Rapports personnalisés
                    </CustomLinkButton>
                  </Nav.Item>
                )}
                {hasDroit(user, TYPE_DROIT.ADMIN_COURRIER) && (
                  <Nav.Item>
                    <CustomLinkButton
                      className="text-underline text-start"
                      pathname={URLS.LIST_MODELE_COURRIER}
                    >
                      Modèles de courrier
                    </CustomLinkButton>
                  </Nav.Item>
                )}
                {hasDroit(user, TYPE_DROIT.ADMIN_PARAM_TRAITEMENTS) && (
                  <>
                    <Nav.Item>
                      <CustomLinkButton
                        className="text-underline text-start"
                        pathname={URLS.TASK}
                      >
                        Paramétrage des traitements
                      </CustomLinkButton>
                    </Nav.Item>
                    <Nav.Item>
                      <CustomLinkButton
                        className="text-underline text-start"
                        pathname={URLS.LIST_TASK_SPECIFIQUE}
                      >
                        Paramétrage des traitements spécifiques
                      </CustomLinkButton>
                    </Nav.Item>
                  </>
                )}
                {hasDroit(user, TYPE_DROIT.ADMIN_PARAM_APPLI) && (
                  <Nav.Item>
                    <CustomLinkButton
                      className="text-underline text-start"
                      pathname={URLS.ADMIN_IMPORT_RESSOURCES}
                    >
                      Import des ressources
                    </CustomLinkButton>
                  </Nav.Item>
                )}
                {hasDroit(user, TYPE_DROIT.ADMIN_ANOMALIES) && (
                  <Nav.Item>
                    <CustomLinkButton
                      className="text-underline text-start"
                      pathname={URLS.LIST_ANOMALIE}
                    >
                      Anomalies
                    </CustomLinkButton>
                  </Nav.Item>
                )}
              </Nav>
            </Col>
          )}

          {hasDroitGererLesDroits && (
            <Col className="bg-light p-2 border rounded mx-2">
              <div className="fw-bold text-center p-2">Gérer les droits</div>

              <Nav className="flex-column">
                {hasDroit(user, TYPE_DROIT.ADMIN_GROUPE_UTILISATEUR) && (
                  <Nav.Item>
                    <CustomLinkButton
                      className="text-underline text-start"
                      pathname={URLS.PROFIL_DROIT_LIST}
                    >
                      Groupes de fonctionnalités
                    </CustomLinkButton>
                  </Nav.Item>
                )}
                {hasDroit(user, TYPE_DROIT.ADMIN_DROITS) && (
                  <Nav.Item>
                    <CustomLinkButton
                      className="text-underline text-start"
                      pathname={URLS.LIEN_DROIT_LIST}
                    >
                      Attribution des fonctionnalités
                    </CustomLinkButton>
                  </Nav.Item>
                )}
                {hasDroit(user, TYPE_DROIT.ADMIN_GROUPE_UTILISATEUR) && (
                  <Nav.Item>
                    <CustomLinkButton
                      className="text-underline text-start"
                      pathname={URLS.LIST_TYPE_ORGANISME}
                    >
                      Types d&apos;organismes
                    </CustomLinkButton>
                  </Nav.Item>
                )}
                {hasDroit(user, TYPE_DROIT.ADMIN_GROUPE_UTILISATEUR) && (
                  <Nav.Item>
                    <CustomLinkButton
                      className="text-underline text-start"
                      pathname={URLS.LIST_PROFIL_ORGANISME}
                    >
                      Profils d&apos;organismes
                    </CustomLinkButton>
                  </Nav.Item>
                )}
                {hasDroit(user, TYPE_DROIT.ADMIN_GROUPE_UTILISATEUR) && (
                  <Nav.Item>
                    <CustomLinkButton
                      className="text-underline text-start"
                      pathname={URLS.LIST_PROFIL_UTILISATEUR}
                    >
                      Profils d&apos;utilisateurs
                    </CustomLinkButton>
                  </Nav.Item>
                )}
                {hasDroit(user, TYPE_DROIT.ADMIN_GROUPE_UTILISATEUR) && (
                  <>
                    <Nav.Item>
                      <CustomLinkButton
                        className="text-underline text-start"
                        pathname={URLS.LIEN_PROFIL_FONCTIONNALITE_LIST}
                      >
                        Liens profils / groupes de fonctionnalités
                      </CustomLinkButton>
                    </Nav.Item>
                    <br />
                  </>
                )}
                {hasDroit(user, TYPE_DROIT.ADMIN_UTILISATEURS_A) && (
                  <Nav.Item>
                    <CustomLinkButton
                      className="text-underline text-start"
                      pathname={URLS.LIST_ORGANISME}
                    >
                      Organismes
                    </CustomLinkButton>
                  </Nav.Item>
                )}
                {hasDroit(user, TYPE_DROIT.ADMIN_UTILISATEURS_A) && (
                  <Nav.Item>
                    <CustomLinkButton
                      className="text-underline text-start"
                      pathname={URLS.LIST_UTILISATEUR}
                    >
                      Utilisateurs
                    </CustomLinkButton>
                  </Nav.Item>
                )}
                {hasDroit(user, TYPE_DROIT.ADMIN_COUCHE_CARTOGRAPHIQUE) && (
                  <>
                    <Nav.Item>
                      <CustomLinkButton
                        className="text-underline text-start"
                        pathname={URLS.COUCHES_LIST}
                      >
                        Couches cartographiques
                      </CustomLinkButton>
                    </Nav.Item>
                    <br />
                  </>
                )}
                {hasDroit(user, TYPE_DROIT.ADMIN_API) && (
                  <Nav.Item>
                    <CustomLinkButton
                      className="text-underline text-start"
                      pathname={URLS.LIEN_TYPE_ORGANISME_DROIT_API}
                    >
                      Attribution des droits API pour les types organismes
                    </CustomLinkButton>
                  </Nav.Item>
                )}
              </Nav>
            </Col>
          )}

          {hasDroitGererLesNomenclatures && (
            <Col className="bg-light p-2 border rounded mx-2">
              <div className="fw-bold text-center p-2">
                Gérer les nomenclatures
              </div>

              <Nav className="flex-column">
                {hasDroit(user, TYPE_DROIT.ADMIN_NOMENCLATURE) && (
                  <Nav.Item>
                    <CustomLinkButton
                      className="text-underline text-start"
                      pathname={URLS.LIST_ADRESSE_TYPE_ANOMALIE}
                    >
                      Adresse - Type d&apos;anomalie
                    </CustomLinkButton>
                  </Nav.Item>
                )}
                {hasDroit(user, TYPE_DROIT.ADMIN_NOMENCLATURE) && (
                  <Nav.Item>
                    <CustomLinkButton
                      className="text-underline text-start"
                      pathname={URLS.LIST_ADRESSE_TYPE_ELEMENT}
                    >
                      Adresse - Type d&apos;élément
                    </CustomLinkButton>
                  </Nav.Item>
                )}
                {hasDroit(user, TYPE_DROIT.ADMIN_NOMENCLATURE) && (
                  <Nav.Item>
                    <CustomLinkButton
                      className="text-underline text-start"
                      pathname={URLS.LIST_ADRESSE_SOUS_TYPE_ELEMENT}
                    >
                      Adresse - Sous type d&apos;élément
                    </CustomLinkButton>
                  </Nav.Item>
                )}
                {hasDroit(user, TYPE_DROIT.ADMIN_NOMENCLATURE) && (
                  <Nav.Item>
                    <CustomLinkButton
                      className="text-underline text-start"
                      pathname={URLS.LIST_ANOMALIE_CATEGORIE}
                    >
                      Catégories d&apos;anomalies
                    </CustomLinkButton>
                  </Nav.Item>
                )}
                {hasDroit(user, TYPE_DROIT.ADMIN_NOMENCLATURE) && (
                  <Nav.Item>
                    <CustomLinkButton
                      className="text-underline text-start"
                      pathname={URLS.LIST_DIAMETRE}
                    >
                      Diamètres
                    </CustomLinkButton>
                  </Nav.Item>
                )}
                {hasDroit(user, TYPE_DROIT.ADMIN_NOMENCLATURE) && (
                  <Nav.Item>
                    <CustomLinkButton
                      className="text-underline text-start"
                      pathname={URLS.LIST_DOMAINE}
                    >
                      Domaines
                    </CustomLinkButton>
                  </Nav.Item>
                )}
                {hasDroit(user, TYPE_DROIT.ADMIN_NOMENCLATURE) && (
                  <Nav.Item>
                    <CustomLinkButton
                      className="text-underline text-start"
                      pathname={URLS.LIST_FONCTION_CONTACT}
                    >
                      Fonctions des contacts
                    </CustomLinkButton>
                  </Nav.Item>
                )}
                {hasDroit(user, TYPE_DROIT.ADMIN_NOMENCLATURE) && (
                  <Nav.Item>
                    <CustomLinkButton
                      className="text-underline text-start"
                      pathname={URLS.LIST_MARQUE_PIBI}
                    >
                      Marques de PIBI
                    </CustomLinkButton>
                  </Nav.Item>
                )}
                {hasDroit(user, TYPE_DROIT.ADMIN_NOMENCLATURE) && (
                  <Nav.Item>
                    <CustomLinkButton
                      className="text-underline text-start"
                      pathname={URLS.LIST_MATERIAU}
                    >
                      Matériaux
                    </CustomLinkButton>
                  </Nav.Item>
                )}
                {hasDroit(user, TYPE_DROIT.ADMIN_NOMENCLATURE) && (
                  <Nav.Item>
                    <CustomLinkButton
                      className="text-underline text-start"
                      pathname={URLS.LIST_MODELE_PIBI}
                    >
                      Modèles PIBI
                    </CustomLinkButton>
                  </Nav.Item>
                )}
                {hasDroit(user, TYPE_DROIT.ADMIN_NOMENCLATURE) && (
                  <Nav.Item>
                    <CustomLinkButton
                      className="text-underline text-start"
                      pathname={URLS.LIST_NATURE}
                    >
                      Natures de PEI
                    </CustomLinkButton>
                  </Nav.Item>
                )}
                {hasDroit(user, TYPE_DROIT.ADMIN_NOMENCLATURE) && (
                  <Nav.Item>
                    <CustomLinkButton
                      className="text-underline text-start"
                      pathname={URLS.LIST_NATURE_DECI}
                    >
                      Natures DECI
                    </CustomLinkButton>
                  </Nav.Item>
                )}
                {hasDroit(user, TYPE_DROIT.ADMIN_NOMENCLATURE) && (
                  <Nav.Item>
                    <CustomLinkButton
                      className="text-underline text-start"
                      pathname={URLS.LIST_NIVEAU}
                    >
                      Niveaux
                    </CustomLinkButton>
                  </Nav.Item>
                )}
                {hasDroit(user, TYPE_DROIT.ADMIN_ROLE_CONTACT) && (
                  <Nav.Item>
                    <CustomLinkButton
                      className="text-underline text-start"
                      pathname={URLS.LIST_ROLE_CONTACT}
                    >
                      Rôles des contacts
                    </CustomLinkButton>
                  </Nav.Item>
                )}
                {hasDroit(user, TYPE_DROIT.ADMIN_PARAM_APPLI) && (
                  <Nav.Item>
                    <CustomLinkButton
                      className="text-underline text-start"
                      pathname={URLS.LIST_THEMATIQUE}
                    >
                      Thématiques
                    </CustomLinkButton>
                  </Nav.Item>
                )}
                {hasDroit(user, TYPE_DROIT.ADMIN_NOMENCLATURE) && (
                  <Nav.Item>
                    <CustomLinkButton
                      className="text-underline text-start"
                      pathname={URLS.LIST_TYPE_CANALISATION}
                    >
                      Types de canalisations
                    </CustomLinkButton>
                  </Nav.Item>
                )}
                {hasDroit(user, TYPE_DROIT.ADMIN_NOMENCLATURE) && (
                  <Nav.Item>
                    <CustomLinkButton
                      className="text-underline text-start"
                      pathname={URLS.LIST_TYPE_PENA_ASPIRATION}
                    >
                      Types de dispositifs d&apos;aspiration
                    </CustomLinkButton>
                  </Nav.Item>
                )}
                {hasDroit(user, TYPE_DROIT.ADMIN_NOMENCLATURE) && (
                  <Nav.Item>
                    <CustomLinkButton
                      className="text-underline text-start"
                      pathname={URLS.LIST_TYPE_ENGIN}
                    >
                      Types d&apos;engins
                    </CustomLinkButton>
                  </Nav.Item>
                )}
                {hasDroit(user, TYPE_DROIT.ADMIN_TYPE_ETUDE) && (
                  <Nav.Item>
                    <CustomLinkButton
                      className="text-underline text-start"
                      pathname={URLS.LIST_TYPE_ETUDE}
                    >
                      Types d&apos;études
                    </CustomLinkButton>
                  </Nav.Item>
                )}
                {hasDroit(user, TYPE_DROIT.ADMIN_NOMENCLATURE) && (
                  <Nav.Item>
                    <CustomLinkButton
                      className="text-underline text-start"
                      pathname={URLS.LIST_TYPE_RESEAU}
                    >
                      Types de réseaux
                    </CustomLinkButton>
                  </Nav.Item>
                )}
              </Nav>
              {hasDroit(user, TYPE_DROIT.ADMIN_NOMENCLATURE) && (
                <Nav.Item>
                  <CustomLinkButton
                    className="text-underline text-start"
                    pathname={URLS.LIST_TYPE_CRISE}
                  >
                    Types de crise
                  </CustomLinkButton>
                </Nav.Item>
              )}
              {hasDroit(user, TYPE_DROIT.ADMIN_NOMENCLATURE) && (
                <Nav.Item>
                  <CustomLinkButton
                    className="text-underline text-start"
                    pathname={URLS.LIST_CRISE_CATEGORIE}
                  >
                    Catégories de crise
                  </CustomLinkButton>
                </Nav.Item>
              )}
            </Col>
          )}
        </Row>
      )}

      {(hasDroitGestionnairesEtSites || hasDroitTableauxDeBord) && (
        <Row className="mt-2">
          {hasDroitGestionnairesEtSites && (
            <Col className="bg-light p-2 border rounded mx-2">
              <div className="fw-bold text-center p-2">
                Gestionnaires &amp; sites
              </div>

              <Nav className="flex-column">
                <Nav.Item>
                  <CustomLinkButton
                    className="text-underline text-start"
                    pathname={URLS.LIST_GESTIONNAIRE}
                  >
                    Gestionnaires
                  </CustomLinkButton>
                </Nav.Item>
                <Nav.Item>
                  <CustomLinkButton
                    className="text-underline text-start"
                    pathname={URLS.LIST_SITE}
                  >
                    Sites
                  </CustomLinkButton>
                </Nav.Item>
              </Nav>
            </Col>
          )}
          {hasDroitTableauxDeBord && (
            <Col className="bg-light p-2 border rounded mx-2">
              <div className="fw-bold text-center p-2">Tableaux de bord</div>

              <Nav className="flex-column">
                <Nav.Item>
                  <CustomLinkButton
                    pathname={URLS.DASHBOARD_ADMIN_QUERY}
                    className="text-underline text-start"
                  >
                    Édition des requêtes et composants associés
                  </CustomLinkButton>
                </Nav.Item>
                <Nav.Item>
                  <CustomLinkButton
                    pathname={URLS.DASHBOARD_ADMIN_DASHBOARD}
                    className="text-underline text-start"
                  >
                    Édition des tableaux de bord et profils associés
                  </CustomLinkButton>
                </Nav.Item>
              </Nav>
            </Col>
          )}
        </Row>
      )}
    </Container>
  );
};

export default MenuAdmin;

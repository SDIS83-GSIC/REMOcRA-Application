import { Alert, Button, Col, Row } from "react-bootstrap";
import UtilisateurEntity from "../../Entities/UtilisateurEntity.tsx";
import { useAppContext } from "../../components/App/AppProvider.tsx";
import Loading from "../../components/Elements/Loading/Loading.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import Header from "../../components/Header/Header.tsx";
import ModuleRemocra, {
  TypeModuleRemocra,
} from "../../components/ModuleRemocra/ModuleRemocra.tsx";
import { hasDroit } from "../../droits.tsx";
import TYPE_DROIT from "../../enums/DroitEnum.tsx";
import url from "../../module/fetch.tsx";
import { URLS } from "../../routes.tsx";
import SquelettePage from "../SquelettePage.tsx";

const Accueil = () => {
  const { user }: { user: UtilisateurEntity } = useAppContext();

  // On récupère les modules
  const modulesState = useGet(url`/api/modules/`);

  const messagePeiLongueIndispoState = useGet(
    url`/api/message-pei-longue-indispo/`,
  );

  if (!modulesState.isResolved) {
    return <Loading />;
  }
  const { data }: { data: Module[] } = modulesState;
  const mapColonneRow = Object.groupBy(
    data,
    ({ moduleColonne }) => moduleColonne,
  );

  return (
    <SquelettePage navbar={<Header />} fluid={false} banner={true}>
      {messagePeiLongueIndispoState?.data && (
        <Row>
          <Alert variant="danger">
            <Alert.Heading>PEI indisponibles</Alert.Heading>
            {messagePeiLongueIndispoState?.data.message}
            <Button
              variant="link"
              href={URLS.MESSAGE_PEI_LONGUE_INDISPO_LISTE_PEI}
            >
              Voir plus
            </Button>
          </Alert>
        </Row>
      )}
      <Row>
        {Object.entries(mapColonneRow).map(([key, values]) => (
          <Col key={key}>
            {Array.from(values).map((e) => {
              const listeLink = getLinks(e.moduleType, user);
              return (
                (listeLink?.find((e) => e.aLeDroit === true) != null ||
                  e.moduleContenuHtml != null ||
                  e.moduleType === TypeModuleRemocra.DOCUMENT ||
                  e.moduleType === TypeModuleRemocra.COURRIER) && (
                  <Row key={e.moduleId} className="m-3">
                    <ModuleRemocra
                      moduleId={e.moduleId}
                      type={e.moduleType}
                      titre={e.moduleTitre}
                      image={e.moduleLinkImage}
                      contenuHtml={e.moduleContenuHtml}
                      listeLink={listeLink}
                      listeDocument={e.listeDocument}
                    />
                  </Row>
                )
              );
            })}
          </Col>
        ))}
      </Row>
    </SquelettePage>
  );
};

/**
 * Fonction à alimenter.
 * Permet de récupérer une liste de liens d'une module.
 * @param typeModuleRemocra type du module
 * @returns
 */
function getLinks(
  typeModuleRemocra: TypeModuleRemocra,
  user: UtilisateurEntity,
): LinkType[] | undefined {
  switch (typeModuleRemocra) {
    case TypeModuleRemocra.DECI:
      return [
        {
          aLeDroit: hasDroit(user, TYPE_DROIT.PEI_R),
          label: "Gestion des points d'eau",
          link: URLS.ACCES_RAPIDE,
        },
        {
          aLeDroit: hasDroit(user, TYPE_DROIT.DECLARATION_PEI),
          label: "Déclarer un PEI",
          link: URLS.DECLARATION_PEI,
        },
        {
          aLeDroit: hasDroit(user, TYPE_DROIT.IMPORT_CTP_PEI_DEPLACEMENT_U),
          label: "Import CTP",
          link: URLS.IMPORT_CTP,
        },
        {
          aLeDroit: hasDroit(user, TYPE_DROIT.COURRIER_C),
          label: "Générer un courrier",
          link: URLS.GENERER_COURRIER(TypeModuleRemocra.DECI),
        },
      ];
    case TypeModuleRemocra.COUVERTURE_HYDRAULIQUE:
      return [
        {
          aLeDroit: hasDroit(user, TYPE_DROIT.ETUDE_R),
          label: "Liste des études",
          link: URLS.LIST_ETUDE,
        },
      ];
    case TypeModuleRemocra.CARTOGRAPHIE:
      return [
        {
          aLeDroit: hasDroit(user, TYPE_DROIT.CARTOGRAPHIES_E),
          label: "Cartographie personnalisée",
          link: URLS.CARTOGRAPHIE_PERSONNALISEE,
        },
      ];
    case TypeModuleRemocra.RCI:
      return [
        {
          aLeDroit:
            hasDroit(user, TYPE_DROIT.RCCI_A) ||
            hasDroit(user, TYPE_DROIT.RCCI_R),
          label: "Carte des départs de feu",
          link: URLS.RCCI_MAP,
        },
      ];
    case TypeModuleRemocra.OPERATIONS_DIVERSES:
      return [
        {
          aLeDroit: hasDroit(user, TYPE_DROIT.OPERATIONS_DIVERSES_E),
          label: "Historique des opérations",
          link: URLS.HISTORIQUE_OPERATIONS,
        },
        {
          aLeDroit: hasDroit(user, TYPE_DROIT.OPERATIONS_DIVERSES_E),
          label: "Résultats d'exécutions",
          link: URLS.RESULTATS_EXECUTION,
        },
      ];
    case TypeModuleRemocra.DFCI:
      return [
        {
          aLeDroit: hasDroit(user, TYPE_DROIT.DFCI_R),
          label: "Carte DFCI",
          link: URLS.CARTE_DFCI,
        },
      ];
    case TypeModuleRemocra.OLDEBS:
      return [
        {
          aLeDroit:
            hasDroit(user, TYPE_DROIT.OLDEB_C) ||
            hasDroit(user, TYPE_DROIT.OLDEB_D) ||
            hasDroit(user, TYPE_DROIT.OLDEB_R) ||
            hasDroit(user, TYPE_DROIT.OLDEB_U),
          label: "Liste des Obligations Légales de Débroussaillement",
          link: URLS.OLDEB_LIST,
        },
      ];
    case TypeModuleRemocra.PERMIS:
      return [
        {
          aLeDroit: hasDroit(user, TYPE_DROIT.PERMIS_R),
          label: "Carte des Permis",
          link: URLS.CARTE_PERMIS,
        },
      ];
    case TypeModuleRemocra.ADRESSES:
      return [
        {
          aLeDroit: hasDroit(user, TYPE_DROIT.ADRESSES_C),
          label: "Carte des adresses",
          link: URLS.ADRESSE,
        },
      ];
    case TypeModuleRemocra.RISQUES:
      return;
    case TypeModuleRemocra.CRISE:
      return [
        {
          aLeDroit: hasDroit(user, TYPE_DROIT.CRISE_R),
          label: "Liste des crises",
          link: URLS.LIST_CRISES,
        },
      ];
    case TypeModuleRemocra.ADMIN:
      return [
        {
          aLeDroit: hasDroit(user, TYPE_DROIT.ADMIN_DROITS),
          label: "Administrer l'application",
          link: URLS.MODULE_ADMIN,
        },
        {
          aLeDroit: hasDroit(user, TYPE_DROIT.DOCUMENTS_R),
          label: "Liste des documents habilitables",
          link: URLS.LIST_DOCUMENT_HABILITABLE,
        },
      ];
    case TypeModuleRemocra.COURRIER:
      return;
    case TypeModuleRemocra.DOCUMENT:
      return;
    case TypeModuleRemocra.RAPPORT_PERSONNALISE:
      return [
        {
          aLeDroit: hasDroit(user, TYPE_DROIT.RAPPORT_PERSONNALISE_E),
          label: "Exécuter un rapport personnalisé",
          link: URLS.EXECUTER_RAPPORT_PERSONNALISE,
        },
      ];
    case TypeModuleRemocra.PEI_PRESCRIT:
      return [
        {
          aLeDroit: hasDroit(user, TYPE_DROIT.PEI_PRESCRIT_A),
          label: "Carte des points d'eau prescrits",
          link: URLS.PEI_PRESCRIT,
        },
      ];
    case TypeModuleRemocra.PERSONNALISE:
      return;
    case TypeModuleRemocra.DASHBOARD:
      return [
        {
          aLeDroit:
            hasDroit(user, TYPE_DROIT.DASHBOARD_A) ||
            hasDroit(user, TYPE_DROIT.DASHBOARD_R),
          label: "Accéder au tableau de bord",
          link: URLS.DASHBOARD_LIST,
        },
      ];
  }
}

/**
 * label : label affiché du lien
 * link : page référente
 * aLeDroit : permet de savoir si l'utilisateur a les droits pour accéder à ce lien
 */
export type LinkType = {
  label: string;
  link: string;
  aLeDroit: boolean;
};

type Module = {
  moduleId: string;
  moduleType: string;
  moduleTitre: string;
  moduleLinkImage: string;
  moduleColonne: number;
  moduleLigne: number;
  moduleContenuHtml: string;
  listeDocument: {
    id: string;
    libelle: string;
    date: Date;
  }[];
};

export default Accueil;

import { Col, Row } from "react-bootstrap";
import Loading from "../../components/Elements/Loading/Loading.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import Header from "../../components/Header/Header.tsx";
import ModuleRemocra, {
  TypeModuleRemocra,
} from "../../components/ModuleRemocra/ModuleRemocra.tsx";
import url from "../../module/fetch.tsx";
import { URLS } from "../../routes.tsx";
import UtilisateurEntity from "../../Entities/UtilisateurEntity.tsx";
import { useAppContext } from "../../components/App/AppProvider.tsx";
import { hasDroit } from "../../droits.tsx";
import SquelettePage from "../SquelettePage.tsx";
import TYPE_DROIT from "../../enums/DroitEnum.tsx";

const Accueil = () => {
  const { user }: { user: UtilisateurEntity } = useAppContext();
  // On récupère les modules
  const modulesState = useGet(url`/api/modules/`);

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
                  <Row className="m-3">
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
          link: URLS.PEI,
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
    case TypeModuleRemocra.OLDEBS:
    case TypeModuleRemocra.PERMIS:
    case TypeModuleRemocra.RCI:
    case TypeModuleRemocra.DFCI:
    case TypeModuleRemocra.ADRESSES:
    case TypeModuleRemocra.RISQUES:
    case TypeModuleRemocra.ADMIN:
      return [
        {
          aLeDroit: hasDroit(user, TYPE_DROIT.ADMIN_DROITS),
          label: "Administrer l'application",
          link: URLS.MODULE_ADMIN,
        },
        {
          aLeDroit:
            hasDroit(user, TYPE_DROIT.GEST_SITE_R) ||
            hasDroit(user, TYPE_DROIT.GEST_SITE_A),
          label: "Liste des gestionnaires",
          link: URLS.LIST_GESTIONNAIRE,
        },
        {
          aLeDroit:
            hasDroit(user, TYPE_DROIT.GEST_SITE_R) ||
            hasDroit(user, TYPE_DROIT.GEST_SITE_A),
          label: "Liste des sites",
          link: URLS.LIST_SITE,
        },
        {
          aLeDroit: hasDroit(user, TYPE_DROIT.DOCUMENTS_R),
          label: "Liste des blocs documents",
          link: URLS.LIST_BLOC_DOCUMENT,
        },
      ];
    case TypeModuleRemocra.COURRIER:
    case TypeModuleRemocra.DOCUMENT:
    case TypeModuleRemocra.RAPPORT_PERSONNALISE:
      return [
        {
          aLeDroit: hasDroit(user, TYPE_DROIT.RAPPORT_PERSONNALISE_E),
          label: "Exécuter un rapport personnalisé",
          link: URLS.EXECUTER_RAPPORT_PERSONNALISE,
        },
      ];
    case TypeModuleRemocra.PERSONNALISE:
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

import { Col, Container, Row } from "react-bootstrap";
import Loading from "../../components/Elements/Loading/Loading.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import Header from "../../components/Header/Header.tsx";
import ModuleRemocra, {
  TypeModuleRemocra,
} from "../../components/ModuleRemocra/ModuleRemocra.tsx";
import url from "../../module/fetch.tsx";
import { URLS } from "../../routes.tsx";

const Accueil = () => {
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
    <>
      <Header />
      <Container>
        <Row>
          {Object.entries(mapColonneRow).map(([key, values]) => (
            <Col key={key}>
              {Array.from(values).map((e) => {
                const listeLink = getLinks(e.moduleType);
                return (
                  (listeLink?.find((e) => e.aLeDroit === true) != null ||
                    e.moduleContenuHtml != null) && (
                    <Row className="m-3">
                      <ModuleRemocra
                        titre={e.moduleTitre}
                        image={e.moduleLinkImage}
                        contenuHtml={e.moduleContenuHtml}
                        listeLink={listeLink}
                      />
                    </Row>
                  )
                );
              })}
            </Col>
          ))}
        </Row>
      </Container>
    </>
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
): LinkType[] | undefined {
  // TODO prendre en compte les droits de l'utilisateur pour chaque lien

  switch (typeModuleRemocra) {
    case TypeModuleRemocra.DECI:
      return [
        {
          aLeDroit: true, // TODO droit
          label: "Gestion des points d'eau",
          link: URLS.PEI,
        },
      ];
    case TypeModuleRemocra.CARTOGRAPHIE:
    case TypeModuleRemocra.OLDEBS:
    case TypeModuleRemocra.PERMIS:
    case TypeModuleRemocra.RCI:
    case TypeModuleRemocra.DFCI:
    case TypeModuleRemocra.ADRESSES:
    case TypeModuleRemocra.RISQUES:
    case TypeModuleRemocra.ADMIN:
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
  moduleType: string;
  moduleTitre: string;
  moduleLinkImage: string;
  moduleColonne: number;
  moduleLigne: number;
  moduleContenuHtml: string;
};

export default Accueil;

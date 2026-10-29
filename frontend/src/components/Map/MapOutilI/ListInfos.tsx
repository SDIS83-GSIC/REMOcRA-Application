import { useEffect, useState } from "react";
import { Container } from "react-bootstrap";
import AccordionCustom, {
  useAccordionState,
} from "../../../components/Accordion/Accordion.tsx";
import { useAppContext } from "../../App/AppProvider.tsx";
import { CoucheMetadata } from "./ShowInfoVolet.tsx";

type FeatureProperties = Record<
  string,
  string | number | boolean | null | undefined
>;
type GeoJsonFeature = { id?: string; properties?: FeatureProperties };
type FeatureCollection = { id?: string; features?: GeoJsonFeature[] };
type AccordionItem = { header: string; content: JSX.Element };

function findMetadataById(
  coucheMetadata: CoucheMetadata[],
  coucheId: string,
): CoucheMetadata | null {
  return (
    coucheMetadata.find((metadata) => metadata.coucheId === coucheId) ?? null
  );
}

// Remplace les #clé# du style par leur valeur, et supprime la ligne si la valeur est vide.
function resolveTemplatePlaceholders(
  template: string,
  properties: FeatureProperties,
): string {
  return template.replace(/^.*#(.*?)#.*$/gm, (line, key) => {
    const value = properties[key];
    if (value === undefined || value === null || value === "") {
      return "";
    }
    return line.replace(`#${key}#`, String(value));
  });
}

// Transforme le texte (déjà résolu) en éléments React en interprétant [b]/[i]/[u]/[br].
function renderStyledTemplate(text: string): (JSX.Element | string)[] {
  const tokens = text.split(/(\[\/?(?:b|i|u|br)\])/);
  const openTags: string[] = [];
  const elements: (JSX.Element | string)[] = [];
  let buffer: (JSX.Element | string)[] = [];

  const flushBuffer = () => {
    if (buffer.length === 0) {
      return;
    }
    let wrapped: (JSX.Element | string)[] = buffer;
    for (let i = openTags.length - 1; i >= 0; i--) {
      const key = `${elements.length}-${i}`;
      switch (openTags[i]) {
        case "b":
          wrapped = [<b key={key}>{wrapped}</b>];
          break;
        case "i":
          wrapped = [<i key={key}>{wrapped}</i>];
          break;
        case "u":
          wrapped = [<u key={key}>{wrapped}</u>];
          break;
      }
    }
    elements.push(...wrapped);
    buffer = [];
  };

  for (const token of tokens) {
    if (token === "[br]") {
      flushBuffer();
      elements.push(<br key={elements.length} />);
    } else if (token === "[b]" || token === "[i]" || token === "[u]") {
      flushBuffer();
      openTags.push(token.slice(1, -1));
    } else if (token === "[/b]" || token === "[/i]" || token === "[/u]") {
      flushBuffer();
      openTags.pop();
    } else if (token !== "") {
      buffer.push(token);
    }
  }
  flushBuffer();
  return elements;
}

// Applique le style complet (texte + mise en forme) sur les propriétés d'une feature.
function applyStyleTemplate(
  template: string,
  properties: FeatureProperties,
): JSX.Element {
  const resolved = resolveTemplatePlaceholders(template, properties);
  return <div>{renderStyledTemplate(resolved)}</div>;
}

// Affiche toutes les propriétés brutes d'une feature (vue superadmin).
function renderRawProperties(properties: FeatureProperties): JSX.Element {
  return (
    <div>
      {Object.keys(properties).map((key, idx) => (
        <div key={idx}>
          <b>{key.replace(/_/g, " ")}</b>:{" "}
          {JSON.stringify(properties[key] ?? "Non renseigné").replace(/"/g, "")}
        </div>
      ))}
    </div>
  );
}

// Récupère le nom de couche à partir de l'id GeoServer (format "nomCouche.fid", parfois
// préfixé par un workspace "workspace:nomCouche.fid").
function extractLayerNameFromFeatureId(featureId: string | undefined): string {
  const id = featureId ?? "";
  const withoutWorkspace = id.includes(":") ? id.split(":").at(-1)! : id;
  const lastDotIndex = withoutWorkspace.lastIndexOf(".");
  return lastDotIndex === -1
    ? withoutWorkspace
    : withoutWorkspace.slice(0, lastDotIndex);
}

// Regroupe les features par couche source réelle (via le nom de couche dans feature.id).
// S'il n'y a qu'une seule couche (pas d'agrégat), on obtient un seul groupe.
function groupFeaturesByType(features: GeoJsonFeature[]): GeoJsonFeature[][] {
  const groups = new Map<string, GeoJsonFeature[]>();
  for (const feature of features) {
    const layerName = extractLayerNameFromFeatureId(feature?.id);
    if (!groups.has(layerName)) {
      groups.set(layerName, []);
    }
    groups.get(layerName)!.push(feature);
  }
  return [...groups.values()];
}

// Fusionne les propriétés de toutes les features (garde la 1ère valeur non vide par clé).
// Sert de socle commun pour compléter les données partagées dans chaque accordéon.
function mergePropertiesFirstWins(
  features: GeoJsonFeature[],
): FeatureProperties {
  const merged: FeatureProperties = {};
  for (const feature of features) {
    const properties = feature?.properties ?? {};
    for (const key of Object.keys(properties)) {
      const value = properties[key];
      if (
        merged[key] === undefined &&
        value !== undefined &&
        value !== null &&
        value !== ""
      ) {
        merged[key] = value;
      }
    }
  }
  return merged;
}

// Construit les accordéons pour une couche (ou un agrégat de couches).
// Ex: 1 commune + 3 réseaux au même endroit => 3 accordéons, la commune étant répétée dans chacun.
function buildAccordionItemsForLayer({
  featureCollection,
  coucheMetadata,
  isSuperAdmin,
}: {
  featureCollection: FeatureCollection;
  coucheMetadata: CoucheMetadata[];
  isSuperAdmin: boolean;
}): AccordionItem[] {
  const features = featureCollection?.features ?? [];
  const coucheId = featureCollection?.id;

  const metadata = coucheId ? findMetadataById(coucheMetadata, coucheId) : null;
  const style = metadata?.coucheMetadataStyle;
  const libelle = metadata?.coucheLibelle || null;

  // Si pas de métadonnée, on ignore complètement cette couche, sauf pour le superadmin
  if ((!metadata && !isSuperAdmin) || features.length === 0) {
    return [];
  }

  const baseHeader =
    libelle || features[0]?.id?.split(".")?.[0] || coucheId || "Inconnu";

  const groups = groupFeaturesByType(features);
  const commonProperties = mergePropertiesFirstWins(features);

  // Le nombre d'accordéons = la taille du plus grand groupe (ex: 3 réseaux => 3 accordéons),
  // pas le total de toutes les features (ex: 1 commune + 3 réseaux => 3, pas 4).
  const accordionCount = Math.max(1, ...groups.map((group) => group.length));

  return Array.from({ length: accordionCount }, (_, accordionIdx) => {
    // Pour chaque accordéon : une feature par groupe (celle à cet index, ou la 1ère du groupe si trop court,).
    const effectiveProperties = groups.reduce<FeatureProperties>(
      (acc, group) => {
        const feature = group[accordionIdx] ?? group[0];
        return { ...acc, ...(feature?.properties ?? {}) };
      },
      { ...commonProperties },
    );

    const header =
      accordionCount > 1
        ? `${baseHeader} (${accordionIdx + 1}/${accordionCount})`
        : baseHeader;

    if (isSuperAdmin) {
      return { header, content: renderRawProperties(effectiveProperties) };
    }

    if (style) {
      return {
        header,
        content: applyStyleTemplate(style, effectiveProperties),
      };
    }

    return {
      header,
      content: <div>Aucune métadonnée à afficher paramétrée</div>,
    };
  });
}

const ListInfos = ({
  data,
  coucheMetadata,
}: {
  data: FeatureCollection[];
  coucheMetadata: CoucheMetadata[];
}) => {
  const { user } = useAppContext();

  const [tableau, setTableau] = useState<AccordionItem[]>([]);
  const { activesKeys, handleShowClose } = useAccordionState(
    Array(6).fill(false),
  );

  useEffect(() => {
    const isSuperAdmin = Boolean(user?.isSuperAdmin);

    const generatedTableau = data.flatMap((featureCollection) =>
      buildAccordionItemsForLayer({
        featureCollection,
        coucheMetadata,
        isSuperAdmin,
      }),
    );

    setTableau(generatedTableau);
  }, [data, coucheMetadata, user?.isSuperAdmin]);

  return (
    <Container>
      {tableau.length === 0 ? (
        <div>Aucune donnée</div>
      ) : (
        <AccordionCustom
          list={tableau}
          activesKeys={activesKeys}
          handleShowClose={handleShowClose}
        />
      )}
    </Container>
  );
};

export default ListInfos;

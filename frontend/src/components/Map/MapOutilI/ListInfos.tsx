import { useEffect, useState } from "react";
import { Container } from "react-bootstrap";
import AccordionCustom, {
  useAccordionState,
} from "../../../components/Accordion/Accordion.tsx";
import { useAppContext } from "../../App/AppProvider.tsx";
import { CoucheMetadata } from "./ShowInfoVolet.tsx";

function findMetadataById(coucheMetadata: CoucheMetadata[], typeId: string) {
  for (const metadata of coucheMetadata) {
    if (metadata.coucheId === typeId) {
      return metadata;
    }
  }
  return null;
}

const ListInfos = ({
  data,
  coucheMetadata,
}: {
  data: any[];
  coucheMetadata: CoucheMetadata[];
}) => {
  const { user } = useAppContext();

  const [tableau, setTableau] = useState<
    { header: string; content: JSX.Element }[]
  >([]);
  const { activesKeys, handleShowClose } = useAccordionState(
    Array(6).fill(false),
  );

  useEffect(() => {
    // fonction de parsing du style
    const parseStyledContent = (
      template: string,
      properties: Record<string, any>,
    ) => {
      // Étape 1 — Supprime toute la ligne si la valeur est vide, null ou undefined
      const tmp = template.replace(/^.*#(.*?)#.*$/gm, (line, key) => {
        const value = properties[key];
        if (value === undefined || value === "" || value === null) {
          return ""; // supprime toute la ligne
        }
        return line.replace(`#${key}#`, value);
      });

      // Étape 2 — Découpe en crochets
      const crochets = tmp.split(/(\[\/?(?:b|i|u|br)\])/);
      const stack: string[] = [];
      const elements: (JSX.Element | string)[] = [];

      let current: (JSX.Element | string)[] = [];

      const flushCurrent = () => {
        if (current.length === 0) {
          return;
        }
        if (stack.length === 0) {
          elements.push(...current);
        } else {
          let content = current;
          for (let i = stack.length - 1; i >= 0; i--) {
            const key = elements.length + "-" + i;

            switch (stack[i]) {
              case "b":
                content = [<strong key={key}>{content}</strong>];
                break;
              case "i":
                content = [<em key={key}>{content}</em>];
                break;
              case "u":
                content = [<u key={key}>{content}</u>];
                break;
            }
          }
          elements.push(...content);
        }
        current = [];
      };

      for (const style of crochets) {
        if (style === "[br]") {
          flushCurrent();
          elements.push(<br key={elements.length} />);
        } else if (style === "[b]" || style === "[i]" || style === "[u]") {
          flushCurrent();
          stack.push(style.slice(1, -1));
        } else if (style === "[/b]" || style === "[/i]" || style === "[/u]") {
          flushCurrent();
          stack.pop();
        } else {
          if (style !== "") {
            current.push(style);
          }
        }
      }

      flushCurrent();
      return elements;
    };

    let generatedTableau = data.flatMap((featureCollection) => {
      const features = featureCollection?.features || [];
      const id = featureCollection?.id;

      // Cherche le metadata correspondant à la couche
      const metadata =
        coucheMetadata && id ? findMetadataById(coucheMetadata, id) : null;
      const style = metadata?.coucheMetadataStyle;
      const libelle = metadata?.coucheLibelle || null;

      // Si pas de métadonnée, on ignore complètement cette couche, sauf pour le superadmin
      if ((!metadata && !user?.isSuperAdmin) || !features.length) {
        return [];
      }

      return features.map((feature: any) => {
        const properties = feature?.properties ?? {};
        // Utilise le libellé comme header
        const header =
          libelle || feature?.id?.split(".")?.[0] || id || "Inconnu";

        if (style && !user?.isSuperAdmin) {
          return {
            header,
            content: <div>{parseStyledContent(style, properties)}</div>,
          };
        }

        if (user?.isSuperAdmin) {
          // si on est super admin, on affiche tout
          return {
            header,
            content: Object.keys(properties).map((key, idx) => (
              <div key={idx}>
                <strong>{key.replace(/_/g, " ")}</strong>:{" "}
                {JSON.stringify(properties[key] ?? "Non renseigné").replace(
                  /"/g,
                  "",
                )}
              </div>
            )),
          };
        }

        return {
          header,
          content: "Aucune donnée",
        };
      });
    });

    // Si generatedTableau contient un objet "Aucune donnée" et qu'il y a d'autres données, on supprime cet objet
    if (generatedTableau.length > 1) {
      generatedTableau = generatedTableau.filter(
        (item) => !(item.header === "Aucune donnée" && item.content === ""),
      );
    }

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

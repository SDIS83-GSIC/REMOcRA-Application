import { useEffect, useState } from "react";
import { Container } from "react-bootstrap";
import AccordionCustom, {
  useAccordionState,
} from "../../../components/Accordion/Accordion.tsx";
import { useGet } from "../../Fetch/useFetch.tsx";
import url from "../../../module/fetch.tsx";

function findStyleById(coucheStyle: any[], typeId: string): string | null {
  for (const style of coucheStyle) {
    if (style.layerId === typeId) {
      return style.layerStyle;
    }
  }
  return null; // Si aucun match trouvé
}

const ListInfos = ({ data }: { data: any[] }) => {
  const coucheStyle = useGet(url`/api/admin/couche/get-all-styles`)?.data;
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
      // Étape 1 — Remplacement des #clé# par la valeur
      const tmp = template.replace(/#(.*?)#/g, (_, key) => {
        return Object.prototype.hasOwnProperty.call(properties, key)
          ? properties[key]
          : "";
      });

      // Étape 2 — Découpe en crochets
      const crochets = tmp.split(/(\[\/?(?:b|i|u|br)\])/);
      const stack: string[] = [];
      const elements: (JSX.Element | string)[] = [];

      let current: (JSX.Element | string)[] = [];

      const flushCurrent = () => {
        if (stack.length === 0) {
          elements.push(...current);
        } else {
          // Appliquer les styles
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
          stack.push(style.slice(1, -1)); // [b] => b
        } else if (style === "[/b]" || style === "[/i]" || style === "[/u]") {
          flushCurrent();
          stack.pop();
        } else {
          current.push(style);
        }
      }

      flushCurrent(); // flush final
      return elements;
    };

    let generatedTableau = data.flatMap((featureCollection) => {
      const features = featureCollection?.features || [];
      const id = featureCollection?.id;

      if (!features.length) {
        return [
          {
            header: "Aucune donnée",
            content: "",
          },
        ];
      }

      const style = coucheStyle && id ? findStyleById(coucheStyle, id) : null;

      return features.map((feature: any) => {
        const properties = feature?.properties ?? {};
        const header = feature?.id?.split(".")?.[0] ?? "Inconnu";

        if (style) {
          return {
            header,
            content: <div>{parseStyledContent(style, properties)}</div>,
          };
        }

        // affichage brut
        return {
          header,
          content: Object.keys(properties).map((key, idx) => (
            <div key={idx}>
              <strong>{key.replace(/_/g, " ")}</strong>:{" "}
              {JSON.stringify(properties[key]).replace(/"/g, "")}
            </div>
          )),
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
  }, [data, coucheStyle]);

  return (
    <Container>
      <AccordionCustom
        list={tableau}
        activesKeys={activesKeys}
        handleShowClose={handleShowClose}
      />
    </Container>
  );
};

export default ListInfos;

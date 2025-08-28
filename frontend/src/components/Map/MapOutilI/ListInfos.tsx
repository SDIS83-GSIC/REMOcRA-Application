import { useEffect, useState } from "react";
import { Container } from "react-bootstrap";
import AccordionCustom, {
  useAccordionState,
} from "../../../components/Accordion/Accordion.tsx";

const ListInfos = ({ data }: { data: any[] }) => {
  const [tableau, setTableau] = useState<
    { header: string; content: JSX.Element }[]
  >([]);
  const { activesKeys, handleShowClose } = useAccordionState(
    Array(6).fill(false),
  );

  useEffect(() => {
    let generatedTableau = data.flatMap((featureCollection) => {
      const features = featureCollection?.features || [];

      if (features.length === 0) {
        return [
          {
            header: "Aucune donnée",
            content: "",
          },
        ];
      }

      return features.map((feature: any) => {
        const properties = feature.properties || {};
        return {
          header: feature.id.split(".")[0],
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
  }, [data]);

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

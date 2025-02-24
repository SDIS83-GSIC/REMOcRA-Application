import { Container } from "react-bootstrap";
import { ReactNode } from "react";
import { JSX } from "react/jsx-runtime";
import AccordionCustom, {
  useAccordionState,
} from "../../../components/Accordion/Accordion.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import url from "../../../module/fetch.tsx";
import MessageElement from "../../../components/message/messageElement.tsx";

const Evenement = ({ criseId }: { criseId: string }) => {
  const getEvents = useGet(url`/api/crise/${criseId}/evenement`)?.data;
  const { activesKeys, handleShowClose } = useAccordionState(
    Array(6).fill(false),
  );

  function shortenString(str: string, maxLength: number): string {
    if (str.length > maxLength) {
      return str.substring(0, maxLength) + "...";
    } else {
      return str;
    }
  }

  const tableau:
    | { header: string; content: ReactNode }[]
    | { header: string; content: JSX.Element }[] = [];
  getEvents?.map((e: { evenementLibelle: string }) => {
    tableau.push({
      header: shortenString(e.evenementLibelle, 35),
      content: <MessageElement />,
    });
  });

  return (
    <Container>
      <AccordionCustom
        activesKeys={activesKeys}
        list={tableau}
        handleShowClose={handleShowClose}
      />
    </Container>
  );
};

export default Evenement;

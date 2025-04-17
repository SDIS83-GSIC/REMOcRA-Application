import { ReactNode, useState } from "react";
import { Accordion } from "react-bootstrap";

/**
 * Composant Accordion qui permet d'avoir plusieurs sections
 *
 * @param {list} list - liste des sections avec leur nom et leur contenu
 * @param {Function} handleShowClose - Fonction qui permet de fermer / ouvrir une section (elle doit venir de useAccordionState)
 * @param {list} activesKeys - Liste des sections qui doivent être actives
 */
const AccordionCustom = ({
  list,
  handleShowClose,
  activesKeys,
  classNameBody,
}: AccordionType) => {
  return (
    <Accordion
      alwaysOpen
      defaultActiveKey={activesKeys}
      activeKey={activesKeys}
    >
      {list.map(({ header, content }, key) => (
        <Accordion.Item eventKey={key.toString()} key={key}>
          <Accordion.Header onClick={() => handleShowClose(key)}>
            {header}
          </Accordion.Header>
          <Accordion.Body className={classNameBody}>{content}</Accordion.Body>
        </Accordion.Item>
      ))}
    </Accordion>
  );
};

export default AccordionCustom;

type AccordionType = {
  list: { header: string; content: ReactNode }[];
  handleShowClose: (index: number) => void;
  activesKeys: string[];
  classNameBody?: string;
};

/**
 * Permet d'observer le state d'un accordion
 * @param initialeOpenState une liste de boolean : un booléen par section (true = ouvert, false = fermé)
 * @returns l'état courant de openState, la fonction handleShowClose qui permet de fermer / ouvrir une section et les indexes des sections ouvertes
 */
export function useAccordionState(initialeOpenState = [true]) {
  const [openState, setOpenState] = useState(initialeOpenState);
  const [activesKeys, setActivesKeys] = useState(
    initialeOpenState
      .map((e, i) => (e === true ? i.toString() : ""))
      .filter(String),
  );

  function handleShowClose(index: number) {
    openState[index] = !openState[index];
    setOpenState(openState);
    getActivesKeys();
  }

  function getActivesKeys() {
    setActivesKeys(
      openState.map((e, i) => (e === true ? i.toString() : "")).filter(String),
    );
  }

  // Force l'ouverture
  function show(index: number) {
    openState[index] = true;
    setOpenState(openState);
    getActivesKeys();
  }

  return { openState, handleShowClose, activesKeys, show };
}

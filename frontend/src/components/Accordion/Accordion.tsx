import { ReactNode } from "react";
import { Accordion } from "react-bootstrap";

const AccordionCustom = ({ list }: AccordionType) => {
  return (
    <Accordion alwaysOpen defaultActiveKey="0">
      {list.map(({ header, content }, key) => (
        <Accordion.Item eventKey={key.toString()} key={key}>
          <Accordion.Header>{header}</Accordion.Header>
          <Accordion.Body>{content}</Accordion.Body>
        </Accordion.Item>
      ))}
    </Accordion>
  );
};

export default AccordionCustom;

type AccordionType = {
  list: { header: string; content: ReactNode }[];
};

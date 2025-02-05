import { Accordion, Col, Row } from "react-bootstrap";
import { AdresseElementEntity } from "../../Entities/AdresseElementEntity.tsx";
import { FormContainer, TextAreaInput } from "../../components/Form/Form.tsx";
import SubmitFormButtons from "../../components/Form/SubmitFormButtons.tsx";

const Adresse = ({
  listeElement,
}: {
  listeElement: AdresseElementEntity[];
}) => {
  return (
    <>
      <h3>Récap des éléments de l&apos;adresse</h3>
      <Accordion>
        {listeElement.map((e, index) => {
          return (
            <>
              <Accordion.Item eventKey={index.toString()}>
                <Accordion.Header>
                  {e.geometryString.split("(", 1)}
                </Accordion.Header>
                <Accordion.Body>
                  <Row>
                    <Col>Anomalies</Col>
                    <Col>Descrption</Col>
                  </Row>
                  <Row>
                    <Col>
                      <ul>
                        {e.anomalies.map((anomalie, key) => {
                          return <li key={key}>{anomalie}</li>;
                        })}
                      </ul>
                    </Col>
                    <Col>{e.description}</Col>
                  </Row>
                </Accordion.Body>
              </Accordion.Item>
            </>
          );
        })}
      </Accordion>
      <FormContainer>
        <Row>
          <Col>
            <TextAreaInput
              name={"description"}
              label={"Description"}
              required={false}
            />
          </Col>
        </Row>
        <Row>
          <Col>
            <SubmitFormButtons />
          </Col>
        </Row>
      </FormContainer>
    </>
  );
};

export default Adresse;

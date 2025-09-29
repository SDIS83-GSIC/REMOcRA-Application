import { Accordion, Col, Row } from "react-bootstrap";
import { SignalementElementEntity } from "../../Entities/SignalementElementEntity.tsx";
import { FormContainer, TextAreaInput } from "../../components/Form/Form.tsx";
import SubmitFormButtons from "../../components/Form/SubmitFormButtons.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import url from "../../module/fetch.tsx";

const Signalement = ({
  listeElement,
  typeWithSousType,
}: {
  listeElement: SignalementElementEntity[];
  typeWithSousType: any;
}) => {
  const listeAnomalie = useGet(url`/api/signalements/type-anomalie`)?.data;
  const listeSousType = typeWithSousType.flatMap((type) => type.listSousType);

  return (
    <>
      <h3>Récap des éléments du signalement</h3>
      <Accordion>
        {listeElement.map((e, index) => {
          return (
            <>
              <Accordion.Item eventKey={index.toString()}>
                <Accordion.Header>
                  {
                    listeSousType.find(
                      (sousType) =>
                        sousType.signalementSousTypeElementId === e.sousType,
                    ).signalementSousTypeElementLibelle
                  }
                </Accordion.Header>
                <Accordion.Body>
                  <Row>
                    <Col>Anomalies</Col>
                    <Col>Description</Col>
                  </Row>
                  <Row>
                    <Col>
                      <ul>
                        {e.anomalies.map((anomalie, key) => {
                          return (
                            <li key={key}>
                              {
                                listeAnomalie?.find(
                                  (ano) =>
                                    anomalie ===
                                    ano.signalementTypeAnomalieCode,
                                ).signalementTypeAnomalieLibelle
                              }
                            </li>
                          );
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

export default Signalement;

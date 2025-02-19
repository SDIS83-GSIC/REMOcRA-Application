import { Accordion, Col, Row } from "react-bootstrap";
import { AdresseElementEntity } from "../../Entities/AdresseElementEntity.tsx";
import { FormContainer, TextAreaInput } from "../../components/Form/Form.tsx";
import SubmitFormButtons from "../../components/Form/SubmitFormButtons.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import url from "../../module/fetch.tsx";

const Adresse = ({
  listeElement,
  typeWithSousType,
}: {
  listeElement: AdresseElementEntity[];
  typeWithSousType: any;
}) => {
  const listeAnomalie = useGet(url`/api/adresses/type-anomalie`)?.data;
  const listeSousType = typeWithSousType.flatMap((type) => type.listSousType);

  return (
    <>
      <h3>Récap des éléments de l&apos;adresse</h3>
      <Accordion>
        {listeElement.map((e, index) => {
          return (
            <>
              <Accordion.Item eventKey={index.toString()}>
                <Accordion.Header>
                  {
                    listeSousType.find(
                      (sousType) =>
                        sousType.adresseSousTypeElementId === e.sousType,
                    ).adresseSousTypeElementLibelle
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
                                    anomalie === ano.adresseTypeAnomalieCode,
                                ).adresseTypeAnomalieLibelle
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

export default Adresse;

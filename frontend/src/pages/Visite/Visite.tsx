import Form from "react-bootstrap/Form";
import { Button, Col, Container, Row, Table } from "react-bootstrap";
import { ReactNode, useState } from "react";
import classNames from "classnames";
import { useParams } from "react-router-dom";
import { IconOverview } from "../../components/Icon/Icon.tsx";
import AccordionCustom from "../../components/Accordion/Accordion.tsx";
import url from "../../module/fetch.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import { VisiteCompleteEntity } from "../../Entities/VisiteEntity.tsx";
import formatDateTime from "../../utils/formatDateUtils.tsx";

const Visite = () => {
  const { peiId } = useParams();

  const [currentVisite, setCurrentVisite] =
    useState<VisiteCompleteEntity>(null);

  const listeVisite = useGet(
    url`/api/visite/getVisiteWithAnomalies/` + peiId,
    {},
  );

  if (!listeVisite.isResolved) {
    return;
  }

  const listeVoletsAccordion: { header: string; content: ReactNode }[] = [];

  if (currentVisite != null) {
    const groupedListeAnomalies = currentVisite.listeAnomalie
      ? Object.groupBy(
          currentVisite.listeAnomalie,
          (item: { anomalieCategorieLibelle: string }) =>
            item.anomalieCategorieLibelle,
        )
      : [];

    Object.entries(groupedListeAnomalies).map(([categorie, values]) =>
      listeVoletsAccordion.push({
        header: categorie, // TODO : Ajouter une indication du nombre d'anomalie cochée
        content: values?.map((element) => (
          <Form.Check
            key={element.anomalieId}
            type="checkbox"
            disabled={true}
            checked={true}
            label={
              <span
                className={classNames(
                  element.poidsAnomalieValIndispoTerrestre === 5 && "fw-bold",
                  element.poidsAnomalieValIndispoHbe === 5 &&
                    "text-decoration-underline",
                )}
              >
                {element.anomalieLibelle}
              </span>
            }
          />
        )),
      }),
    );
  }

  return (
    <Container className="p-0 m-0">
      <Row>
        <Col xs="5">
          <Table striped bordered size="sm">
            <thead>
              <tr>
                <th>Date</th>
                <th>Type</th>
                <th>Agent</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {listeVisite.data.map((element, index) => (
                <tr
                  key={index}
                  className={
                    currentVisite &&
                    element.visiteId === currentVisite.visiteId &&
                    "table-info"
                  }
                >
                  <td>{formatDateTime(element.visiteDate)}</td>
                  <td>{element.visiteTypeVisite}</td>
                  <td>{element.visiteAgent1}</td>
                  <td>
                    <Button
                      size="sm"
                      onClick={() => {
                        setCurrentVisite(element);
                      }}
                    >
                      <IconOverview />
                    </Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        </Col>
        {/* Visualisation d'une visite déja existante */}
        {currentVisite && (
          <Col xs="7">
            <div>
              <h3>Consultation d&apos;une visite </h3>
              <AccordionCustom
                list={[
                  {
                    header: "Informations générales",
                    content: (
                      <>
                        <div>
                          <div>
                            Date et Heure :{" "}
                            {formatDateTime(currentVisite.visiteDate)}
                          </div>
                          <div>
                            TypeVisite : {currentVisite.visiteTypeVisite}
                          </div>
                          <div>
                            <Form.Check
                              type="checkbox"
                              disabled
                              checked={currentVisite.ctrlDebitPression != null}
                              label="Contrôle débit et pression (CDP)"
                            />
                          </div>
                          <Row>
                            <Col>Agent1 : {currentVisite.visiteAgent1}</Col>
                            <Col>Agent2 : {currentVisite.visiteAgent2}</Col>
                          </Row>
                        </div>
                      </>
                    ),
                  },
                  {
                    header: "Mesures",
                    content: (
                      <>
                        {currentVisite.ctrlDebitPression != null && (
                          <div>
                            <div>
                              Débit à 1 bar (m3/h) :{" "}
                              {
                                currentVisite.ctrlDebitPression
                                  .visiteCtrlDebitPressionDebit
                              }
                            </div>
                            <div>
                              Pression dynamique au débit nominal (bar) :{" "}
                              {
                                currentVisite.ctrlDebitPression
                                  .visiteCtrlDebitPressionPressionDyn
                              }
                            </div>
                            <div>
                              Pression statique (bar) :{" "}
                              {
                                currentVisite.ctrlDebitPression
                                  .visiteCtrlDebitPressionPression
                              }
                            </div>
                          </div>
                        )}
                        {currentVisite.ctrlDebitPression == null && (
                          <p>
                            Aucune mesure de débit/pression n&apos;a été
                            effectuée lors de cette visite
                          </p>
                        )}
                      </>
                    ),
                  },
                  {
                    header: "Points d'attention",
                    content: (
                      <div>
                        {listeVoletsAccordion.length > 0 ? (
                          <AccordionCustom list={listeVoletsAccordion} />
                        ) : (
                          <p>
                            Aucune anomalie n&apos;a été retenue lors de cette
                            visite
                          </p>
                        )}
                      </div>
                    ),
                  },
                  {
                    header: "Observations",
                    content: <p>{currentVisite.visiteObservation}</p>,
                  },
                ]}
              />
            </div>
          </Col>
        )}
      </Row>
    </Container>
  );
};

export default Visite;

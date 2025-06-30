import classnames from "classnames";
import { ReactNode, useState } from "react";
import { Button, Col, Container, Row, Table } from "react-bootstrap";
import Form from "react-bootstrap/Form";
import { useParams } from "react-router-dom";
import AccordionCustom, {
  useAccordionState,
} from "../../components/Accordion/Accordion.tsx";
import { useAppContext } from "../../components/App/AppProvider.tsx";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import CreateButton from "../../components/Button/CreateButton.tsx";
import MyFormik from "../../components/Form/MyFormik.tsx";
import {
  IconDelete,
  IconOverview,
  IconVisite,
} from "../../components/Icon/Icon.tsx";
import DeleteModal from "../../components/Modal/DeleteModal.tsx";
import useModal from "../../components/Modal/ModalUtils.tsx";
import { hasDroit } from "../../droits.tsx";
import { CtrlDebitPressionEntity } from "../../Entities/CtrlDebitPressionEntity.tsx";
import { VisiteCompleteEntity } from "../../Entities/VisiteEntity.tsx";
import TYPE_DROIT from "../../enums/DroitEnum.tsx";
import TYPE_PEI from "../../enums/TypePeiEnum.tsx";
import referenceTypeVisite, {
  TYPE_VISITE,
} from "../../enums/TypeVisiteEnum.tsx";
import url from "../../module/fetch.tsx";
import formatDateTime from "../../utils/formatDateUtils.tsx";
import VisiteForm, {
  getInitialValues,
  prepareVariables,
} from "./VisiteForm.tsx";

const Visite = () => {
  const { peiId } = useParams();
  const { user } = useAppContext();

  const { visible, show, close, ref } = useModal();

  const [currentVisite, setCurrentVisite] =
    useState<VisiteCompleteEntity>(null);
  const [newVisite, setNewVisite] = useState<boolean>(false); // Permet de différencier la lecture de la création d'un visite pour l'affichage

  const { handleShowClose, activesKeys } = useAccordionState([
    true,
    false,
    false,
    false,
  ]);

  const visiteInformations = useGet(
    url`/api/visite/getVisiteWithAnomalies/` + peiId,
    {},
  );

  const listeAnomaliesAssignable = useGet(
    url`/api/anomalie/getAssignablesAnomalies/` + peiId,
    {},
  );

  if (!visiteInformations.isResolved) {
    return;
  }

  const listeVisite = visiteInformations.data.listVisite;
  const typePei = visiteInformations.data.typePei;
  const numeroComplet = visiteInformations.data.numeroComplet;
  const commune = visiteInformations.data.commune;
  const lastCDP: CtrlDebitPressionEntity = {
    ctrlVisiteId: null,
    ctrlDebit: visiteInformations.data.lastCDP?.visiteCtrlDebitPressionDebit,
    ctrlPression:
      visiteInformations.data.lastCDP?.visiteCtrlDebitPressionPression,
    ctrlPressionDyn:
      visiteInformations.data.lastCDP?.visiteCtrlDebitPressionPressionDyn,
  };

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
                className={classnames(
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

  const hasRightToDelete = (visite: VisiteCompleteEntity) => {
    switch (visite.visiteTypeVisite) {
      case TYPE_VISITE.CTP:
        return hasDroit(user, TYPE_DROIT.VISITE_CTP_D);
      case TYPE_VISITE.NP:
        return hasDroit(user, TYPE_DROIT.VISITE_NP_D);
      case TYPE_VISITE.RECEPTION:
        return hasDroit(user, TYPE_DROIT.VISITE_RECEP_D);
      case TYPE_VISITE.ROP:
        return hasDroit(user, TYPE_DROIT.VISITE_RECO_D);
      case TYPE_VISITE.RECO_INIT:
        return hasDroit(user, TYPE_DROIT.VISITE_RECO_INIT_D);
    }
  };

  const hasRightToCreate = () => {
    if (listeVisite.length === 0) {
      return hasDroit(user, TYPE_DROIT.VISITE_RECEP_C);
    } else if (listeVisite.length === 1) {
      return hasDroit(user, TYPE_DROIT.VISITE_RECO_INIT_C);
    } else if (listeVisite.length >= 2) {
      return (
        hasDroit(user, TYPE_DROIT.VISITE_CONTROLE_TECHNIQUE_C) ||
        hasDroit(user, TYPE_DROIT.VISITE_RECO_C) ||
        hasDroit(user, TYPE_DROIT.VISITE_NON_PROGRAMME_C)
      );
    }
  };
  return (
    <Container fluid className={"px-5"}>
      <PageTitle
        title={`Visites du PEI ${numeroComplet} - ${commune}`}
        icon={<IconVisite />}
        right={
          hasRightToCreate() && (
            <CreateButton
              title={"Ajouter une visite"}
              onClick={() => {
                setNewVisite(true);
                setCurrentVisite(null);
              }}
            />
          )
        }
      />
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
              {listeVisite.map((element, index) => (
                <tr
                  key={index}
                  className={
                    currentVisite &&
                    element.visiteId === currentVisite.visiteId &&
                    "table-info"
                  }
                >
                  <td className={"text-nowrap"}>
                    {formatDateTime(element.visiteDate)}
                  </td>
                  <td>
                    {
                      referenceTypeVisite.find(
                        (e) => e.code === element.visiteTypeVisite,
                      )?.libelle
                    }
                  </td>
                  <td className={"text-nowrap"}>{element.visiteAgent1}</td>
                  <td>
                    <Button
                      variant="link"
                      className={classnames("p-0 m-0 text-decoration-none")}
                      onClick={() => {
                        setCurrentVisite(element);
                        setNewVisite(false);
                      }}
                    >
                      <IconOverview />
                    </Button>
                    {index === 0 && hasRightToDelete(element) && (
                      <>
                        <Button
                          variant={"link"}
                          className={
                            "p-0 m-0 ps-1 text-decoration-none text-danger"
                          }
                          onClick={show}
                        >
                          <IconDelete />
                        </Button>

                        <DeleteModal
                          visible={visible}
                          closeModal={close}
                          query={url`/api/visite/` + element.visiteId}
                          ref={ref}
                          onDelete={() => {
                            visiteInformations.reload();
                            listeAnomaliesAssignable.reload();
                          }}
                        />
                      </>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        </Col>
        {/* Visualisation d'une visite déja existante */}
        {currentVisite && !newVisite && (
          <Col xs="7">
            <div>
              <h3>Consultation d&apos;une visite </h3>
              <AccordionCustom
                activesKeys={activesKeys}
                handleShowClose={handleShowClose}
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
                            Type de visite : {currentVisite.visiteTypeVisite}
                          </div>
                          {typePei === TYPE_PEI.PIBI && (
                            <div>
                              <Form.Check
                                type="checkbox"
                                disabled
                                checked={
                                  currentVisite.ctrlDebitPression != null
                                }
                                label="Contrôle débit et pression (CDP)"
                              />
                            </div>
                          )}
                          <Row>
                            <Col>Agent1 : {currentVisite.visiteAgent1}</Col>
                            <Col>Agent2 : {currentVisite.visiteAgent2}</Col>
                          </Row>
                        </div>
                      </>
                    ),
                  },
                  ...(typePei === TYPE_PEI.PIBI
                    ? [
                        {
                          header: "Mesures",
                          content: (
                            <>
                              {currentVisite.ctrlDebitPression != null && (
                                <div>
                                  <div>
                                    Pression statique (bar) :{" "}
                                    {
                                      currentVisite.ctrlDebitPression
                                        .visiteCtrlDebitPressionPression
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
                                    Débit à 1 bar (m3/h) :{" "}
                                    {
                                      currentVisite.ctrlDebitPression
                                        .visiteCtrlDebitPressionDebit
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
                      ]
                    : []),
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
        {/* Création d'une nouvelle visite */}
        {!currentVisite && newVisite && (
          <Col>
            <div>
              <h3>Création d&apos;une visite</h3>
              <MyFormik
                initialValues={getInitialValues(
                  peiId,
                  listeAnomaliesAssignable.data,
                  lastCDP,
                )}
                isPost={false}
                submitUrl={`/api/visite/createVisite`}
                prepareVariables={(values) => prepareVariables(values)}
                onSubmit={() => window.location.reload()}
              >
                <VisiteForm
                  nbVisite={listeVisite.length}
                  typePei={typePei}
                  listeAnomaliesAssignable={listeAnomaliesAssignable.data}
                  user={user}
                />
              </MyFormik>
            </div>
          </Col>
        )}
      </Row>
    </Container>
  );
};

export default Visite;

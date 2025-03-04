import { useState } from "react";
import { Button, Col, Container, Row } from "react-bootstrap";
import { Outlet, useOutletContext, useParams } from "react-router-dom";
import AccordionCustom, {
  useAccordionState,
} from "../../components/Accordion/Accordion.tsx";
import DeleteButton from "../../components/Button/DeleteButton.tsx";
import Loading from "../../components/Elements/Loading/Loading.tsx";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import FilterInput from "../../components/Filter/FilterInput.tsx";
import MultiSelectFilterFromList from "../../components/Filter/MultiSelectFilterFromList.tsx";
import MyFormik from "../../components/Form/MyFormik.tsx";
import Header from "../../components/Header/Header.tsx";
import { IconAdd, IconDocument } from "../../components/Icon/Icon.tsx";
import QueryTable, {
  useFilterContext,
} from "../../components/Table/QueryTable.tsx";
import url, { getFetchOptions } from "../../module/fetch.tsx";
import { useToastContext } from "../../module/Toast/ToastProvider.tsx";
import { URLS } from "../../routes.tsx";
import GenererForm, {
  DynamicFormWithParametre,
} from "../../utils/buildDynamicForm.tsx";
import SquelettePage from "../SquelettePage.tsx";
import {
  getInitialValues,
  prepareVariables,
  validationSchema,
} from "./Courrier.tsx";
import filterValuesToVariable from "./FilterDestinataire.tsx";

type ContextType = {
  urlCourrier: {
    url: string;
    modeleCourrierId: string;
    courrierReference: string;
  } | null;
};

const GenereCourrier = () => {
  const { typeModule } = useParams();
  const [urlCourrier, setUrlCourrier] = useState(null);
  const { activesKeys, handleShowClose } = useAccordionState([true, false]);

  // On récupère tous les courriers avec leurs paramètres
  const modeleCourrierState = useGet(
    url`/api/courriers/parametres?${{ typeModule: typeModule }}`,
  );

  if (!modeleCourrierState.isResolved) {
    return <Loading />;
  }

  const { data }: { data: DynamicFormWithParametre[] } = modeleCourrierState;

  return (
    <SquelettePage navbar={<Header />}>
      <Container fluid>
        <PageTitle icon={<IconDocument />} title={"Générer un courrier"} />
        <AccordionCustom
          activesKeys={activesKeys}
          list={[
            {
              header: "Générer le courrier",
              content: (
                <Row>
                  <Col xs={12} lg={4}>
                    <MyFormik
                      initialValues={getInitialValues()}
                      validationSchema={validationSchema}
                      isPost={true}
                      submitUrl={`/api/courriers`}
                      prepareVariables={(values) => prepareVariables(values)}
                      redirectUrl={URLS.VIEW_COURRIER(typeModule)}
                      onSubmit={(url) => setUrlCourrier(url)}
                    >
                      <GenererForm
                        listeWithParametre={data}
                        contexteLibelle="Modèle de courrier"
                        reference={true}
                      />
                    </MyFormik>
                  </Col>
                  {urlCourrier && (
                    <Col xs={12} lg={8}>
                      <Outlet context={{ urlCourrier } satisfies ContextType} />
                    </Col>
                  )}
                </Row>
              ),
            },
            {
              header: "Notifier le courrier",
              content: urlCourrier ? (
                <ListDestinataire
                  urlCourrier={urlCourrier.url}
                  modeleCourrierId={urlCourrier.modeleCourrierId}
                  courrierReference={urlCourrier.courrierReference}
                />
              ) : (
                <Row>Veuillez générer le courrier avant de notifier.</Row>
              ),
            },
          ]}
          handleShowClose={handleShowClose}
        />
      </Container>
    </SquelettePage>
  );
};

export function useUrlCourrier() {
  return useOutletContext<ContextType>();
}

export default GenereCourrier;

const ListDestinataire = ({
  urlCourrier,
  modeleCourrierId,
  courrierReference,
}: {
  urlCourrier: string;
  modeleCourrierId: string;
  courrierReference: string;
}) => {
  const [listeDestinataire, setListeDestinataire] = useState<
    {
      destinataireId: string;
      nomDestinataire: string;
      emailDestinataire: string;
      typeDestinataire: string;
    }[]
  >([]);

  const { success: successToast, error: errorToast } = useToastContext();

  async function notifier() {
    (
      await fetch(
        url`/api/courriers/create/`,
        getFetchOptions({
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            modeleCourrierId: modeleCourrierId,
            nomDocument: urlCourrier.split("courrierName=")[1],
            listeDestinataire: listeDestinataire,
            courrierReference: courrierReference,
          }),
        }),
      )
    )
      .text()
      .then(() => {
        successToast("Destinataires notifiés");
      })
      .catch((reason: string) => {
        errorToast(reason);
      });
  }

  return (
    <Row>
      <Col xs={12} lg={6}>
        <QueryTable
          query={url`/api/courriers/destinataires`}
          filterContext={useFilterContext({})}
          columns={[
            {
              Header: "Type",
              accessor: "typeDestinataire",
              sortField: "typeDestinataire",
              Filter: (
                <MultiSelectFilterFromList
                  name={"listeTypeDestinataire"}
                  listIdCodeLibelle={[
                    {
                      id: "UTILISATEUR",
                      code: "UTILISATEUR",
                      libelle: "Utilisateur",
                    },
                    {
                      id: "ORGANISME",
                      code: "ORGANISME",
                      libelle: "Organisme",
                    },
                    {
                      id: "CONTACT_ORGANISME",
                      code: "CONTACT_ORGANISME",
                      libelle: "Contact d'organisme",
                    },
                    {
                      id: "CONTACT_GESTIONNAIRE",
                      code: "CONTACT_GESTIONNAIRE",
                      libelle: "Contact de gestionnaire",
                    },
                  ]}
                />
              ),
            },
            {
              Header: "Nom",
              accessor: "nomDestinataire",
              sortField: "nomDestinataire",
              Filter: <FilterInput type="text" name="nomDestinataire" />,
            },
            {
              Header: "Email",
              accessor: "emailDestinataire",
              sortField: "emailDestinataire",
              Filter: <FilterInput type="text" name="emailDestinataire" />,
            },
            {
              Header: "Fonction",
              accessor: "fonctionDestinataire",
              sortField: "fonctionDestinataire",
              Filter: <FilterInput type="text" name="fonctionDestinataire" />,
            },
            {
              accessor: ({
                destinataireId,
                nomDestinataire,
                emailDestinataire,
                typeDestinataire,
              }) => {
                return {
                  destinataireId,
                  nomDestinataire,
                  emailDestinataire,
                  typeDestinataire,
                };
              },
              Cell: (value) => {
                return (
                  <Button
                    variant="link"
                    className="btn-link text-decoration-none fw-bold text-nowrap"
                    disabled={listeDestinataire
                      .map((e) => e.destinataireId)
                      .includes(value.value.destinataireId)}
                    onClick={() => {
                      setListeDestinataire((liste) => [
                        ...liste,
                        {
                          destinataireId: value.value.destinataireId,
                          nomDestinataire: value.value.nomDestinataire,
                          emailDestinataire: value.value.emailDestinataire,
                          typeDestinataire: value.value.typeDestinataire,
                        },
                      ]);
                    }}
                  >
                    <IconAdd /> Ajouter
                  </Button>
                );
              },
            },
          ]}
          filterValuesToVariable={filterValuesToVariable}
          idName={"tableDestinataireId"}
        />
      </Col>
      <Col xs={12} lg={6}>
        <Row>
          <Col>
            <h3>Destinataires sélectionnés</h3>
          </Col>
          <Col lg={2}>
            <Button className="mb-2" onClick={notifier}>
              Notifier
            </Button>
          </Col>
        </Row>
        <div className="bg-light p-2 border rounded">
          {listeDestinataire.length === 0 ? (
            <div>Aucun destinataire sélectionné</div>
          ) : (
            listeDestinataire.map((e, index) => (
              <Row className="mt-2" key={index}>
                <Col>
                  <b>Nom</b> : {e?.nomDestinataire}
                </Col>
                <Col>
                  <b>Email</b> : {e?.emailDestinataire}
                </Col>
                <Col lg={2}>
                  <DeleteButton
                    title={"Supprimer"}
                    onClick={() =>
                      setListeDestinataire((liste) =>
                        liste.filter(
                          (item) => item.destinataireId !== e.destinataireId,
                        ),
                      )
                    }
                  />
                </Col>
              </Row>
            ))
          )}
        </div>
      </Col>
    </Row>
  );
};

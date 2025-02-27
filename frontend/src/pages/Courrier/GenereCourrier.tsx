import { useState } from "react";
import { Col, Container, Row } from "react-bootstrap";
import { Outlet, useOutletContext } from "react-router-dom";
import AccordionCustom, {
  useAccordionState,
} from "../../components/Accordion/Accordion.tsx";
import Loading from "../../components/Elements/Loading/Loading.tsx";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import FilterInput from "../../components/Filter/FilterInput.tsx";
import MultiSelectFilterFromList from "../../components/Filter/MultiSelectFilterFromList.tsx";
import MyFormik from "../../components/Form/MyFormik.tsx";
import Header from "../../components/Header/Header.tsx";
import { IconDocument } from "../../components/Icon/Icon.tsx";
import QueryTable, {
  useFilterContext,
} from "../../components/Table/QueryTable.tsx";
import url from "../../module/fetch.tsx";
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

type ContextType = { urlCourrier: { url: string } | null };

const GenereCourrier = () => {
  const [urlCourrier, setUrlCourrier] = useState(null);
  const { activesKeys, handleShowClose } = useAccordionState([true, false]);

  // On récupère tous les courriers avec leurs paramètres
  const modeleCourrierState = useGet(url`/api/courriers/parametres`);

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
                      redirectUrl={URLS.VIEW_COURRIER}
                      onSubmit={(url) => setUrlCourrier(url)}
                    >
                      <GenererForm
                        listeWithParametre={data}
                        contexteLibelle="Modèle de courrier"
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
                <ListDestinataire />
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

const ListDestinataire = () => {
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
                      code: "Utilisateur",
                      libelle: "Utilisateur",
                    },
                    {
                      id: "ORGANISME",
                      code: "Organisme",
                      libelle: "Organisme",
                    },
                    {
                      id: "CONTACT_ORGANISME",
                      code: "Contact d'organisme",
                      libelle: "Contact d'organisme",
                    },
                    {
                      id: "CONTACT_GESTIONNAIRE",
                      code: "Contact de gestionnaire",
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
          ]}
          filterValuesToVariable={filterValuesToVariable}
          idName={"tableDestinataireId"}
        />
      </Col>
      <Col xs={12} lg={4}>
        TODO ajouter les destinataires sélectionnés
      </Col>
    </Row>
  );
};

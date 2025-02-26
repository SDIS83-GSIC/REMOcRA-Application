import { useState } from "react";
import { Col, Container, Row } from "react-bootstrap";
import { Outlet, useOutletContext } from "react-router-dom";
import Loading from "../../components/Elements/Loading/Loading.tsx";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import MyFormik from "../../components/Form/MyFormik.tsx";
import Header from "../../components/Header/Header.tsx";
import { IconDocument } from "../../components/Icon/Icon.tsx";
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

type ContextType = { urlCourrier: { url: string } | null };

const GenereCourrier = () => {
  const [urlCourrier, setUrlCourrier] = useState(null);

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
        <Row>
          <Col xs={12} lg={4}>
            <MyFormik
              initialValues={getInitialValues()}
              validationSchema={validationSchema}
              isPost={true}
              submitUrl={`/api/courriers`} // TODO à implémenter
              prepareVariables={(values) => {
                const value = prepareVariables(values, data);
                return value;
              }}
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
      </Container>
    </SquelettePage>
  );
};

export function useUrlCourrier() {
  return useOutletContext<ContextType>();
}

export default GenereCourrier;

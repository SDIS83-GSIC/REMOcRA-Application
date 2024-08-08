import { Formik } from "formik";
import { useEffect, useState } from "react";
import { Col, Container, Row } from "react-bootstrap";
import { Outlet, useOutletContext } from "react-router-dom";
import Loading from "../../components/Elements/Loading/Loading.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import { useMyFormik } from "../../components/Form/MyFormik.tsx";
import { ModeleCourrierWithParametres } from "../../Entities/CourrierEntity.tsx";
import url from "../../module/fetch.tsx";
import { URLS } from "../../routes.tsx";
import Courrier, {
  getInitialValues,
  prepareVariables,
  validationSchema,
} from "./Courrier.tsx";

type ContextType = { urlCourrier: { url: string } | null };

const GenereCourrier = () => {
  const [urlCourrier, setUrlCourrier] = useState(null);

  // Ici on a besoin de l'url du courrier généré et donc de la réponse
  const { submitState, errorMessage } = useMyFormik(
    `/api/courriers/`,
    true,
    null,
    "Génération du courrier effectué avec succès",
    "Impossible de générer le courrier",
    URLS.VIEW_COURRIER,
    false,
  );

  useEffect(() => {
    setUrlCourrier(submitState.data);
  }, [submitState, setUrlCourrier]);

  // On récupère tous les courriers avec leurs paramètres
  const modeleCourrierState = useGet(url`/api/courriers/parametres`);

  if (!modeleCourrierState.isResolved) {
    return <Loading />;
  }

  const { data }: { data: ModeleCourrierWithParametres[] } =
    modeleCourrierState;

  return (
    <>
      <Formik
        enableReinitialize={true}
        initialValues={getInitialValues}
        validationSchema={validationSchema}
        onSubmit={async (values, { setSubmitting }) => {
          const variables = prepareVariables(values);
          try {
            await submitState.run(variables);
          } finally {
            setSubmitting(false);
          }
        }}
      >
        <Container>
          {errorMessage && errorMessage}
          <Row className="p-2">
            <Col>
              <Courrier initialesValues={data} />
            </Col>
            {urlCourrier && (
              <Col>
                <Outlet context={{ urlCourrier } satisfies ContextType} />
              </Col>
            )}
          </Row>
        </Container>
      </Formik>
    </>
  );
};

export function useUrlCourrier() {
  return useOutletContext<ContextType>();
}

export default GenereCourrier;

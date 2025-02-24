import { useState } from "react";
import { Container, Row } from "react-bootstrap";
import { useOutletContext } from "react-router-dom";
import Loading from "../../components/Elements/Loading/Loading.tsx";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import MyFormik from "../../components/Form/MyFormik.tsx";
import { IconDocument } from "../../components/Icon/Icon.tsx";
import url from "../../module/fetch.tsx";
import GenererForm, {
  DynamicFormWithParametre,
} from "../../utils/buildDynamicForm.tsx";
import {
  getInitialValues,
  prepareVariables,
  validationSchema,
} from "./Courrier.tsx";

type ContextType = { urlCourrier: { url: string } | null };

const GenereCourrier = () => {
  const [, setUrlCourrier] = useState(null);

  // On récupère tous les courriers avec leurs paramètres
  const modeleCourrierState = useGet(url`/api/courriers/parametres`);

  if (!modeleCourrierState.isResolved) {
    return <Loading />;
  }

  const { data }: { data: DynamicFormWithParametre[] } = modeleCourrierState;

  return (
    <Container fluid>
      <PageTitle icon={<IconDocument />} title={"Générer un courrier"} />
      <Row>
        <MyFormik
          initialValues={getInitialValues()}
          validationSchema={validationSchema}
          isPost={false}
          submitUrl={`/api/courrier/generer`} // TODO à implémenter
          prepareVariables={(values) => {
            const value = prepareVariables(values, data);
            return value;
          }}
          onSubmit={(url) => setUrlCourrier(url)}
        >
          <GenererForm
            listeWithParametre={data}
            contexteLibelle="Modèle de courrier"
          />
        </MyFormik>
      </Row>
    </Container>
  );
};

export function useUrlCourrier() {
  return useOutletContext<ContextType>();
}

export default GenereCourrier;

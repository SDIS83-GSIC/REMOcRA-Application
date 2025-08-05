import { useFormikContext } from "formik";
import { Button, Col, Container, Row } from "react-bootstrap";
import { object } from "yup";
import Loading from "../../../components/Elements/Loading/Loading.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { FormContainer } from "../../../components/Form/Form.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconEtude } from "../../../components/Icon/Icon.tsx";

type TraceeCouvertureType = {
  listePeiId: string[];
  listePeiProjetId: string[];
  useReseauImporte: boolean;
  useReseauImporteWithReseauCourant: boolean;
};

export const getInitialValues = (
  listePeiId: string[],
  listePeiProjetId: string[],
) => ({
  useReseauImporte: false,
  useReseauImporteWithReseauCourant: false,
  listePeiId: listePeiId,
  listePeiProjetId: listePeiProjetId,
  isSubmit: false,
});

export const prepareVariables = (values: TraceeCouvertureType) => ({
  useReseauImporte: values.useReseauImporte,
  useReseauImporteWithReseauCourant: values.useReseauImporteWithReseauCourant,
  listePeiId: values.listePeiId,
  listePeiProjetId: values.listePeiProjetId,
});

export const validationSchema = object({});

const TraceeCouvertureForm = ({
  etudeId,
  listePeiId,
  listePeiProjetId,
  closeVolet,
}: {
  etudeId: string;
  listePeiId: string[];
  listePeiProjetId: string[];
  closeVolet: () => void;
}) => {
  return (
    <Container>
      <PageTitle
        icon={<IconEtude />}
        title={"Lancer une simulation"}
        displayReturnButton={false}
      />
      <MyFormik
        initialValues={getInitialValues(listePeiId, listePeiProjetId)}
        validationSchema={validationSchema}
        isPost={false}
        isMultipartFormData={false}
        submitUrl={`/api/couverture-hydraulique/calcul/` + etudeId}
        prepareVariables={(values) => prepareVariables(values)}
        onSubmit={closeVolet}
      >
        <Form />
      </MyFormik>
    </Container>
  );
};

const Form = () => {
  const { setFieldValue, submitCount } = useFormikContext();
  return (
    <>
      <FormContainer>
        <div className="text-center">
          Souhaitez-vous calculer la couverture hydraulique de ces PEI
          <br /> sur le réseau routier commun ou sur le réseau routier
          précédemment importé ?
        </div>
        <Row className="mt-3 text-center">
          <Col>
            <Button
              type="submit"
              variant="primary"
              onClick={() => {
                setFieldValue("useReseauImporte", false);
              }}
              disabled={submitCount > 0}
            >
              Commun
            </Button>
          </Col>
        </Row>
        <Row className="mt-3 text-center">
          <Col>
            <Button
              type="submit"
              variant="primary"
              onClick={() => {
                setFieldValue("useReseauImporte", true);
              }}
              disabled={submitCount > 0}
            >
              Importé
            </Button>
          </Col>
        </Row>
        <Row className="mt-3 text-center">
          <Col>
            <Button
              type="submit"
              variant="primary"
              onClick={() => {
                setFieldValue("useReseauImporteWithReseauCourant", true);
              }}
              disabled={submitCount > 0}
            >
              Commun et importé
            </Button>
          </Col>
        </Row>
        {submitCount > 0 && <Loading />}
      </FormContainer>
    </>
  );
};

export default TraceeCouvertureForm;

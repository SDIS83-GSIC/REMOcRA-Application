import { PropsWithChildren } from "react";
import { Card, Col, Container, Row } from "react-bootstrap";
import { boolean, object } from "yup";
import PageTitle from "../../../../components/Elements/PageTitle/PageTitle.tsx";
import MyFormik from "../../../../components/Form/MyFormik.tsx";
import { IconInfo } from "../../../../components/Icon/Icon.tsx";
import url from "../../../../module/fetch.tsx";
import ImporterCadastre from "./ImporterCadastre.tsx";
import RelancerCalculDispo from "./RelancerCalculDispo.tsx";
import RelancerCalculNumerotation from "./RelancerCalculNumerotation.tsx";

const ExecuteTasksManuelles = () => {
  return (
    <Container>
      <PageTitle
        icon={<IconInfo />}
        title="Exécuter des traitements manuellement"
      />
      <Row>
        <Col>
          <CardTask
            title="Relancer le calcul de disponibilité"
            apiUrl={url`/api/admin/relancer-calcul-dispo`}
            prepareVariables={prepareVariablesCalculDispoNumero}
            initialValues={initialValuesCalculDispoNumero}
            validationSchema={validationSchemaCalculDispoNumero}
          >
            <RelancerCalculDispo />
          </CardTask>
        </Col>
        <Col>
          <CardTask
            title="Relancer le calcul de la numérotation"
            apiUrl={url`/api/admin/relancer-calcul-numerotation`}
            prepareVariables={prepareVariablesCalculDispoNumero}
            initialValues={initialValuesCalculDispoNumero}
            validationSchema={validationSchemaCalculDispoNumero}
          >
            <RelancerCalculNumerotation />
          </CardTask>
        </Col>
      </Row>
      <Row>
        <Col>
          <CardTask
            title="Import du cadastre"
            apiUrl={url`/api/admin/importer-cadastre`}
            prepareVariables={() => ({})}
            initialValues={{}}
            validationSchema={object({})}
          >
            <ImporterCadastre />
          </CardTask>
        </Col>
      </Row>
    </Container>
  );
};

const prepareVariablesCalculDispoNumero = (values: {
  eventTracabilite: boolean;
  eventNexSis: boolean;
}) => {
  return {
    eventTracabilite: values.eventTracabilite,
    eventNexSis: values.eventNexSis,
  };
};

const initialValuesCalculDispoNumero = {
  eventTracabilite: false,
  eventNexSis: false,
};

const validationSchemaCalculDispoNumero = object({
  eventTracabilite: boolean().required(),
  eventNexSis: boolean().required(),
});

const CardTask = ({
  title,
  apiUrl,
  prepareVariables,
  initialValues,
  validationSchema,
  children,
}: PropsWithChildren<{
  title: string;
  apiUrl: string;
  prepareVariables: (values: any) => any;
  initialValues: any;
  validationSchema: any;
}>) => {
  return (
    <Card
      className="mb-4 shadow"
      style={{ borderRadius: "1rem", overflow: "hidden" }}
    >
      <Card.Header className="bg-primary text-white fs-5">{title}</Card.Header>
      <Card.Body className="bg-secondary">
        <MyFormik
          submitUrl={apiUrl}
          prepareVariables={(values) => {
            return prepareVariables(values);
          }}
          validationSchema={validationSchema}
          isPost
          initialValues={initialValues}
          onSubmit={() => {}}
        >
          {children}
        </MyFormik>
      </Card.Body>
    </Card>
  );
};

export default ExecuteTasksManuelles;

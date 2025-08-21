import { ReactNode } from "react";
import { Card, Col, Container, Row } from "react-bootstrap";
import { boolean, object } from "yup";
import PageTitle from "../../../../components/Elements/PageTitle/PageTitle.tsx";
import MyFormik from "../../../../components/Form/MyFormik.tsx";
import { IconInfo } from "../../../../components/Icon/Icon.tsx";
import url from "../../../../module/fetch.tsx";
import RelancerCalculDispo from "./RelancerCalculDispo.tsx";

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
          >
            <RelancerCalculDispo />
          </CardTask>
        </Col>
        {/** TODO recalcul numérotation */}
      </Row>
    </Container>
  );
};

const CardTask = ({
  title,
  apiUrl,
  children,
}: {
  title: string;
  apiUrl: string;
  children: ReactNode;
}) => {
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
            return {
              eventTracabilite: values.eventTracabilite,
              eventNexSis: values.eventNexSis,
            };
          }}
          validationSchema={object({
            eventTracabilite: boolean().required(),
            eventNexSis: boolean().required(),
          })}
          isPost
          initialValues={{ eventTracabilite: false, eventNexSis: false }}
          onSubmit={() => {}}
        >
          {children}
        </MyFormik>
      </Card.Body>
    </Card>
  );
};

export default ExecuteTasksManuelles;

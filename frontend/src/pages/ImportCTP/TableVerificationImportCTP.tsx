import { useFormikContext } from "formik";
import React from "react";
import { Button, Col, Container, Row, Table } from "react-bootstrap";
import { useLocation, useNavigate } from "react-router-dom";
import { object } from "yup";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { FormContainer } from "../../components/Form/Form.tsx";
import MyFormik from "../../components/Form/MyFormik.tsx";
import { IconValidation } from "../../components/Icon/Icon.tsx";
import { URLS } from "../../routes.tsx";

export const getInitialValues = (initialValues) => ({
  bilanVerifications: initialValues.result.bilanVerifications,
});

export const validationSchema = object({});

export const prepareVariables = (values) => ({
  bilanVerifications: values.bilanVerifications ?? null,
});

const VerificationImportCTP = () => {
  const { state } = useLocation();

  let initialValues;
  if (state) {
    initialValues = state;
    // On vide le state
    window.history.replaceState(null, "");
  }

  return (
    <Container>
      <PageTitle
        icon={<IconValidation />}
        title={"Validation de l'import CTP"}
      />
      <MyFormik
        initialValues={getInitialValues(initialValues)}
        validationSchema={validationSchema}
        isPost={true}
        submitUrl={`/api/importctp/enregistrement`}
        prepareVariables={(values) => prepareVariables(values)}
        redirectUrl={URLS.ACCUEIL}
      >
        <ResultatsVerificationImportCTP />
      </MyFormik>
    </Container>
  );
};

export default VerificationImportCTP;

const ResultatsVerificationImportCTP = () => {
  const { values } = useFormikContext();
  const navigate = useNavigate();

  const nbCTValides = values.bilanVerifications.filter(
    (e) => e.bilanStyle === "OK" || e.bilanStyle === "WARNING",
  ).length;
  const nbCTValidesWarn = values.bilanVerifications.filter(
    (e) => e.bilanStyle === "WARNING",
  ).length;
  const nbCTRejetes = values.bilanVerifications.filter(
    (e) => e.bilanStyle === "ERREUR" || e.bilanStyle === "INFO",
  ).length;
  const nbCTRejetesNR = values.bilanVerifications.filter(
    (e) => e.bilanStyle === "INFO",
  ).length;

  return (
    <FormContainer>
      <Row>
        <Col>
          <Table bordered>
            <thead>
              <tr>
                <th>N°Ligne</th>
                <th>Code INSEE</th>
                <th>N° interne du PEI</th>
                <th>Date du CT</th>
                <th>Bilan du contrôle</th>
              </tr>
            </thead>
            <tbody>
              {values.bilanVerifications?.map((element, index) => (
                <tr
                  key={index}
                  className={
                    element.bilanStyle === "ERROR"
                      ? "table-danger"
                      : element.bilanStyle === "WARNING"
                        ? "table-warning"
                        : "table-success"
                  }
                >
                  <td>{element.numeroLigne}</td>
                  <td>{element.codeInsee}</td>
                  <td>{element.numeroInterne}</td>
                  <td>{element.dateCtp}</td>
                  {element.warnings && element.warnings.length > 0 ? (
                    <td>
                      <ul>
                        {element.warnings.map((warning, index) => (
                          <li key={index}>{warning}</li>
                        ))}
                      </ul>
                    </td>
                  ) : (
                    <td>{element.bilan}</td>
                  )}
                </tr>
              ))}
            </tbody>
          </Table>
        </Col>
      </Row>
      <Row>
        <p>Nombre de CT valides : {nbCTValides}</p>
        <p>Dont : {nbCTValidesWarn} CT valides avec avertissements</p>
        <p>Nombre de CT rejetés : {nbCTRejetes}</p>
        <p>Dont : {nbCTRejetesNR} CT rejetés car non renseignés</p>
      </Row>
      <Row>
        <Col>
          <Button onClick={() => navigate(URLS.ACCUEIL)}>Annuler</Button>
        </Col>
        <Col>
          <Button type="submit" variant="primary">
            Importer les visites
          </Button>
        </Col>
      </Row>
    </FormContainer>
  );
};

import { useFormikContext } from "formik";
import { Col, Container, Row, Table } from "react-bootstrap";
import { useLocation } from "react-router-dom";
import { object } from "yup";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { FormContainer } from "../../components/Form/Form.tsx";
import MyFormik from "../../components/Form/MyFormik.tsx";
import SubmitFormButtons from "../../components/Form/SubmitFormButtons.tsx";
import { IconValidation } from "../../components/Icon/Icon.tsx";
import { URLS } from "../../routes.tsx";
import { calculerBilan } from "../../utils/fonctionsUtils.tsx";

type BilanVerification = {
  numeroLigne: number;
  numeroPei: string;
  dateReleve: string;
  bilanStyle: string;
  warnings?: string[];
  infos?: string[];
};

type VerificationFormValues = {
  bilanVerifications: BilanVerification[];
};

type VerificationImportState = {
  result: {
    bilanVerifications: BilanVerification[];
  };
  from?: string;
};

export const getInitialValues = (
  initialValues: VerificationImportState,
): VerificationFormValues => ({
  bilanVerifications: initialValues.result.bilanVerifications,
});

export const validationSchema = object({});

export const prepareVariables = (values: VerificationFormValues) => ({
  bilanVerifications: values.bilanVerifications ?? null,
});

const VerificationImportPei = () => {
  const { state } = useLocation();

  let initialValues;
  if (state) {
    initialValues = state;
    window.history.replaceState({ from: state.from }, "");
  }

  return (
    <Container>
      <PageTitle
        icon={<IconValidation />}
        title={"Validation de l'import des positions des PEI"}
      />
      <MyFormik
        initialValues={getInitialValues(initialValues)}
        validationSchema={validationSchema}
        isPost={true}
        submitUrl={`/api/maj-positions-pei/enregistrement`}
        prepareVariables={(values) => prepareVariables(values)}
        redirectUrl={URLS.MAJ_POSITIONS_PEI}
      >
        <ResultatsVerificationImportPei />
      </MyFormik>
    </Container>
  );
};

export default VerificationImportPei;

const ResultatsVerificationImportPei = () => {
  const { values } = useFormikContext();
  const data = calculerBilan(values);
  return (
    <FormContainer>
      <Row>
        <Col>
          <Table bordered>
            <thead>
              <tr>
                <th>N°Ligne</th>
                <th>N° du PEI</th>
                <th>Date de relevé</th>
                <th>Bilan du contrôle</th>
              </tr>
            </thead>
            <tbody>
              {values.bilanVerifications?.map(
                (element: BilanVerification, index: number) => (
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
                    <td>{element.numeroPei}</td>
                    <td>{element.dateReleve}</td>
                    {element.warnings && element.warnings.length > 0 ? (
                      <td>
                        <ul>
                          {element.warnings.map((warning, idx) => (
                            <li key={idx}>{warning}</li>
                          ))}
                        </ul>
                      </td>
                    ) : (
                      <td>
                        {element.infos && element.infos.length > 0 ? (
                          <ul>
                            {element.infos.map(
                              (info: string, index: number) => (
                                <li key={index}>{info}</li>
                              ),
                            )}
                          </ul>
                        ) : (
                          <span>Aucune information disponible</span>
                        )}
                      </td>
                    )}
                  </tr>
                ),
              )}
            </tbody>
          </Table>
        </Col>
      </Row>
      <Row>
        <p>Nombre de PEI valides : {data.nbValides}</p>
        <p>Dont : {data.nbValidesWarn} PEI valides avec avertissements</p>
        <p>Nombre de PEI rejetés : {data.nbRejetes}</p>
        <p>Dont : {data.nbRejetesNR} PEI rejetés car non renseignés</p>
      </Row>
      <Row>
        <Col>
          <SubmitFormButtons
            returnLink={false}
            submitTitle="Importer les positions"
          />
        </Col>
      </Row>
    </FormContainer>
  );
};

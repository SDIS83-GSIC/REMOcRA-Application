import { useFormikContext } from "formik";
import { Col, Container, Row, Table } from "react-bootstrap";
import { useLocation } from "react-router-dom";
import { object } from "yup";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { FormContainer } from "../../../components/Form/Form.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import SubmitFormButtons from "../../../components/Form/SubmitFormButtons.tsx";
import { IconValidation } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";

type ImportError = {
  line: number;
  message: string;
};

export const getInitialValues = (
  initialValues: any,
): { bilanVerifications: ImportError[]; utilisateurList: any[] } => {
  return {
    bilanVerifications: (initialValues?.result?.errors ?? []).map((e: any) => ({
      line: e.line,
      message: e.message,
    })),
    utilisateurList: initialValues?.result?.utilisateurList ?? [],
  };
};

export const validationSchema = object({});

export const prepareVariables = (values: any) => {
  return values.utilisateurList ?? null;
};

const VerificationImportUtilisateur = () => {
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
        title={"Validation de l'import des utilisateurs"}
      />
      <MyFormik
        initialValues={getInitialValues(initialValues)}
        validationSchema={validationSchema}
        isPost={true}
        submitUrl={`/api/utilisateur/importer-utilisateur`}
        prepareVariables={(values) => prepareVariables(values)}
        redirectUrl={URLS.LIST_UTILISATEUR}
      >
        <ResultatsVerificationImportUser />
      </MyFormik>
    </Container>
  );
};

export default VerificationImportUtilisateur;

const ResultatsVerificationImportUser = () => {
  const { values } = useFormikContext<{ bilanVerifications: ImportError[] }>();

  // Regroupement par ligne
  const erreursLigne = (values?.bilanVerifications ?? []).reduce<
    Record<number, string[]>
  >((acc, error) => {
    if (!acc[error.line]) {
      acc[error.line] = [];
    }
    acc[error.line].push(error.message);
    return acc;
  }, {});

  const groupedEntries = Object.entries(erreursLigne).sort(
    ([a], [b]) => Number(a) - Number(b),
  );

  return (
    <FormContainer>
      <Row>
        <Col>
          <Table bordered>
            <thead>
              <tr>
                <th>Lignes</th>
                <th>Erreurs</th>
              </tr>
            </thead>
            <tbody>
              {groupedEntries.length === 0 ? (
                <tr>
                  <td colSpan={2}>Aucune erreur</td>
                </tr>
              ) : (
                groupedEntries.map(([line, messages]) => (
                  <tr key={line}>
                    <td>{line}</td>
                    <td>
                      <ul className="list-unstyled ps-3">
                        {messages.map((msg, index) => (
                          <li key={index}>{msg}</li>
                        ))}
                      </ul>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </Table>
        </Col>
      </Row>

      <Row>
        <Col>
          <SubmitFormButtons
            disabledValide={groupedEntries.length !== 0}
            submitTitle={"Importer les utilisateurs"}
          />
        </Col>
      </Row>
    </FormContainer>
  );
};

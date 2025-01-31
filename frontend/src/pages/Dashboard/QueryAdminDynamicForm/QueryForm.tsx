import { Alert, Button, Card, Col } from "react-bootstrap";
import {
  FormContainer,
  TextAreaInput,
  TextInput,
} from "../../../components/Form/Form.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import url from "../../../module/fetch.tsx";
import { IconInfo } from "../../../components/Icon/Icon.tsx";
import { formatData, QueryParam } from "../Constants.tsx";

type QueryFormProps = {
  activeQuery: QueryParam;
  setActiveQuery: any;
  setQueryData: any;
  setAvailableOptions: any;
};

const QueryForm = (props: QueryFormProps) => {
  const urlApiQuery = url`/api/dashboard/validate-query`;

  const updateData = (data: any) => {
    props.setActiveQuery({
      ...props.activeQuery,
      title: data.queryTitle,
      query: data.querySql,
    });
    const dataFormatted = formatData(data);
    props.setQueryData(dataFormatted);
    props.setAvailableOptions(Object.keys(dataFormatted[0]));
  };

  const getInitialValues = (activeQuery: QueryParam) => ({
    query: activeQuery.query,
    queryTitle: activeQuery.title,
  });

  return (
    <Card bg="secondary" className="m-3">
      <Card.Body>
        <MyFormik
          initialValues={getInitialValues(props.activeQuery)}
          isPost={true}
          submitUrl={urlApiQuery}
          onSubmit={(values) => updateData(values)}
          successToastMessage="La requête est valide"
        >
          <FormContainer>
            <TextInput
              required={false}
              name="queryTitle"
              label="Titre de la requête"
            />
            <TextAreaInput
              name="query"
              label="Requête SQL :"
              required={false}
            />
            <Col>
              <Alert
                variant="info"
                className="mt-2 mb-2 text-muted d-flex align-items-center"
              >
                <span>
                  {" "}
                  {IconInfo()} Tester la requête pour enregistrer les
                  modifications sur le titre et/ou la requête .{" "}
                </span>
              </Alert>

              <Button type="submit" variant={"info"} className="mt-3">
                Tester la requête
              </Button>
            </Col>
          </FormContainer>
        </MyFormik>
      </Card.Body>
    </Card>
  );
};

export default QueryForm;

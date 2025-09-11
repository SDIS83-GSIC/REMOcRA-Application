import Container from "react-bootstrap/Container";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { IconEdit } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import GroupeFonctionnalitesForm, {
  getInitialValues,
  prepareValues,
  validationSchema,
} from "./GroupeFonctionnalitesForm.tsx";

const GroupeFonctionnalitesCreate = () => {
  return (
    <Container>
      <PageTitle
        title="Création d'un groupe de fonctionnalités"
        icon={<IconEdit />}
      />
      <MyFormik
        initialValues={getInitialValues()}
        prepareVariables={(values) => prepareValues(values)}
        validationSchema={validationSchema}
        submitUrl={`/api/groupe-fonctionnalites/create`}
        isPost={true}
        redirectUrl={URLS.GROUPE_FONCTIONNALITES_LIST}
        onSubmit={() => true}
      >
        <GroupeFonctionnalitesForm />
      </MyFormik>
    </Container>
  );
};

export default GroupeFonctionnalitesCreate;
